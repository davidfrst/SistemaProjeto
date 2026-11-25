package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.net.ConnectException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

import client.ApiClient;
import model.Projeto;
import net.miginfocom.swing.MigLayout;

public class PainelProjetos extends JPanel {

    private static final int DESCRICAO_MINIMA = 5;

    private final ApiClient api = new ApiClient();

    private final JTextField campoNome = new JTextField();
    private final JTextField campoDescricao = new JTextField();
    private final JComboBox<String> comboCategoria =
        new JComboBox<>(new String[] {"Web", "Software", "Mobile", "Outro"});
    private final JComboBox<String> comboStatus =
        new JComboBox<>(new String[] {"Planejado", "Em desenvolvimento", "Concluído"});
    private final JLabel mensagem = new JLabel(" ");

    private final JTextField campoBusca = new JTextField();
    private final DefaultTableModel modelo =
        new DefaultTableModel(new String[] {"ID", "NOME", "CATEGORIA", "STATUS"}, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
    private final JTable tabela = new JTable(modelo);
    private final TableRowSorter<DefaultTableModel> filtro = new TableRowSorter<>(modelo);

    public PainelProjetos() {
        setBackground(Tema.FUNDO);
        setLayout(new MigLayout("insets 24, fill, wrap 1", "[grow,fill]", "[][grow,fill]"));
        add(criarCartaoCadastro());
        add(criarCartaoLista());

        carregar();

        new Timer(5000, e -> carregar()).start();
    }

    private JPanel criarCartaoCadastro() {
        JPanel cartao = cartao("Cadastro de Projeto", "insets 20, fillx, wrap 2", "[grow,fill][grow,fill]");

        cartao.add(rotulo("Nome do Projeto"), "span");
        cartao.add(campoNome, "span, growx, h 40!");
        cartao.add(rotulo("Descrição"), "span");
        cartao.add(campoDescricao, "span, growx, h 40!");
        cartao.add(rotulo("Categoria"));
        cartao.add(rotulo("Status"));
        cartao.add(comboCategoria, "growx, h 40!");
        cartao.add(comboStatus, "growx, h 40!");
        cartao.add(criarBotoes(), "span");
        cartao.add(mensagem, "span");
        return cartao;
    }

    private JPanel criarBotoes() {
        JButton botaoSalvar = new JButton("Salvar");
        botaoSalvar.setBackground(Tema.PRIMARIA);
        botaoSalvar.setForeground(Color.WHITE);
        JButton botaoLimpar = new JButton("Limpar");
        JButton botaoExcluir = new JButton("Excluir");
        botaoExcluir.setForeground(Tema.PERIGO);

        botaoSalvar.addActionListener(e -> salvar());
        botaoLimpar.addActionListener(e -> limpar());
        botaoExcluir.addActionListener(e -> excluir());

        JPanel painel = new JPanel(new MigLayout("insets 0", "[]8[]8[]"));
        painel.setOpaque(false);
        painel.add(botaoSalvar, "h 36!");
        painel.add(botaoLimpar, "h 36!");
        painel.add(botaoExcluir, "h 36!");
        return painel;
    }

    private JPanel criarCartaoLista() {
        JPanel cartao = cartao("Lista de Projetos", "insets 20, fill, wrap 1", "[grow,fill]");

        campoBusca.putClientProperty("JTextField.placeholderText", "Pesquisar projeto...");
        aoDigitar(campoBusca, this::filtrar);
        cartao.add(campoBusca, "growx, h 40!, gapbottom 8");

        configurarTabela();
        JScrollPane rolagem = new JScrollPane(tabela);
        rolagem.getViewport().setBackground(Tema.SUPERFICIE);
        rolagem.setBorder(BorderFactory.createLineBorder(Tema.BORDA));
        cartao.add(rolagem, "grow, push");
        return cartao;
    }

    private void configurarTabela() {
        tabela.setRowSorter(filtro);
        tabela.setRowHeight(40);
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setFillsViewportHeight(true);
        tabela.setBackground(Tema.SUPERFICIE);
        tabela.setForeground(Tema.TEXTO);
        tabela.setSelectionBackground(Tema.PRIMARIA);
        tabela.setSelectionForeground(Color.WHITE);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader cabecalho = tabela.getTableHeader();
        cabecalho.setReorderingAllowed(false);

        tabela.getColumnModel().getColumn(0).setMaxWidth(80);
        tabela.getColumnModel().getColumn(3).setCellRenderer(new RenderizadorStatus());
    }

    private static JPanel cartao(String titulo, String layout, String colunas) {
        JPanel painel = new JPanel(new MigLayout(layout, colunas));
        painel.setBackground(Tema.SUPERFICIE);
        painel.setBorder(BorderFactory.createLineBorder(Tema.BORDA));

        JLabel rotuloTitulo = new JLabel(titulo);
        rotuloTitulo.setFont(rotuloTitulo.getFont().deriveFont(Font.PLAIN, 18f));
        rotuloTitulo.setForeground(Tema.TEXTO);
        painel.add(rotuloTitulo, "span, gapbottom 8");
        return painel;
    }

    private static JLabel rotulo(String texto) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setForeground(Tema.TEXTO_SUAVE);
        rotulo.setFont(rotulo.getFont().deriveFont(12f));
        return rotulo;
    }

    public void carregar() {
        emSegundoPlano(api::listar, this::preencherTabela);
    }

    private void salvar() {
        String nome = campoNome.getText().trim();
        String descricao = campoDescricao.getText().trim();

        if (nome.isEmpty()) {
            avisar("Informe o nome do projeto.", true);
            campoNome.requestFocus();
            return;
        }
        if (descricao.length() < DESCRICAO_MINIMA) {
            avisar("A descrição precisa ter pelo menos " + DESCRICAO_MINIMA + " caracteres.", true);
            campoDescricao.requestFocus();
            return;
        }

        Projeto projeto = new Projeto();
        projeto.setNome(nome);
        projeto.setDescricao(descricao);
        projeto.setCategoria((String) comboCategoria.getSelectedItem());
        projeto.setStatus((String) comboStatus.getSelectedItem());

        emSegundoPlano(() -> api.cadastrar(projeto), criado -> {
            limparCampos();
            avisar("Projeto salvo.", false);
            carregar();
        });
    }

    private void excluir() {
        Integer id = idSelecionado();
        if (id == null) {
            avisar("Selecione um projeto na lista para excluir.", true);
            return;
        }

        int resposta = JOptionPane.showConfirmDialog(this,
            "Excluir o projeto " + id + "?", "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        emSegundoPlano(() -> {
            api.excluir(id);
            return null;
        }, ok -> {
            avisar("Projeto excluído.", false);
            carregar();
        });
    }

    private void limpar() {
        limparCampos();
        avisar("", false);
    }

    private void limparCampos() {
        campoNome.setText("");
        campoDescricao.setText("");
        comboCategoria.setSelectedIndex(0);
        comboStatus.setSelectedIndex(0);
        campoNome.requestFocus();
    }

    private void filtrar() {
        String texto = campoBusca.getText().trim();
        if (texto.isEmpty()) {
            filtro.setRowFilter(null);
        } else {
            filtro.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto)));
        }
    }

    private void preencherTabela(List<Projeto> projetos) {
        Integer selecionado = idSelecionado();
        modelo.setRowCount(0);
        for (Projeto p : projetos) {
            modelo.addRow(new Object[] {p.getId(), p.getNome(), p.getCategoria(), p.getStatus()});
        }
        if (selecionado != null) {
            reselecionar(selecionado);
        }
    }

    private Integer idSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return null;
        }
        return (Integer) modelo.getValueAt(tabela.convertRowIndexToModel(linha), 0);
    }

    private void reselecionar(int id) {
        for (int i = 0; i < modelo.getRowCount(); i++) {
            if ((int) modelo.getValueAt(i, 0) == id) {
                int visual = tabela.convertRowIndexToView(i);
                if (visual >= 0) {
                    tabela.setRowSelectionInterval(visual, visual);
                }
                return;
            }
        }
    }

    private void avisar(String texto, boolean erro) {
        mensagem.setText(texto.isEmpty() ? " " : texto);
        mensagem.setForeground(erro ? Tema.PERIGO : Tema.TEXTO_SUAVE);
    }

    private interface Chamada<T> {
        T executar() throws Exception;
    }

    private <T> void emSegundoPlano(Chamada<T> chamada, Consumer<T> aoTerminar) {
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return chamada.executar();
            }

            @Override
            protected void done() {
                try {
                    aoTerminar.accept(get());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    Throwable causa = e.getCause();
                    if (causa instanceof ConnectException) {
                        avisar("Erro de conexão: a API está rodando na porta 7070?", true);
                    } else {
                        String texto = causa.getMessage();
                        avisar(texto == null ? "Erro inesperado." : texto, true);
                    }
                }
            }
        }.execute();
    }

    private static void aoDigitar(JTextField campo, Runnable acao) {
        campo.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                acao.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                acao.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                acao.run();
            }
        });
    }

    private static class RenderizadorStatus extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable tabela, Object valor,
                boolean selecionada, boolean foco, int linha, int coluna) {
            JPanel celula = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 7));
            celula.setBackground(selecionada ? tabela.getSelectionBackground() : tabela.getBackground());
            celula.add(new Pilula(String.valueOf(valor)));
            return celula;
        }
    }

    private static class Pilula extends JLabel {
        private final Color fundo;

        Pilula(String texto) {
            super(texto);
            this.fundo = Tema.fundoStatus(texto);
            setOpaque(false);
            setForeground(Tema.textoStatus(texto));
            setFont(getFont().deriveFont(Font.PLAIN, 12f));
            setBorder(new EmptyBorder(2, 10, 2, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fundo);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
