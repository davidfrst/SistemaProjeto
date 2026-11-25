package view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import model.Projeto;
import net.miginfocom.swing.MigLayout;

class PainelCategorias extends PainelBase {

    private static final String[] CATEGORIAS = { "Web", "Software", "Mobile", "Outro" };

    private final Map<String, JLabel> contadores = new LinkedHashMap<>();
    private final Map<String, Ui.Chip> chips = new LinkedHashMap<>();
    private String filtroAtual = "Todas";
    private List<Projeto> cacheProjetos = new ArrayList<>();

    private final DefaultTableModel modelo = new DefaultTableModel(
        new Object[] { "ID", "NOME", "DESCRIÇÃO", "CATEGORIA", "STATUS" }, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    PainelCategorias(Runnable aoAbrirProjetos) {
        setLayout(new MigLayout("insets 24, wrap 1, fill", "[grow,fill]", "[][grow,fill][]"));

        JPanel linhaCards = new JPanel(new MigLayout("insets 0, fillx", "[grow,fill]16[grow,fill]16[grow,fill]16[grow,fill]", ""));
        linhaCards.setOpaque(false);
        linhaCards.add(cardCategoria("Web", Icone.Tipo.GLOBO, Tema.AZUL));
        linhaCards.add(cardCategoria("Software", Icone.Tipo.CODIGO, Tema.CIANO));
        linhaCards.add(cardCategoria("Mobile", Icone.Tipo.CELULAR, Tema.VERDE));
        linhaCards.add(cardCategoria("Outro", Icone.Tipo.GRADE, Tema.AMBAR));
        add(linhaCards, "growx, gapbottom 16");

        JPanel cardTabela = Ui.cartao("insets 20, wrap 1, fill", "[grow,fill]", "[][grow,fill]");

        JPanel barraFiltros = new JPanel(new MigLayout("insets 0, fillx", "[]8[]8[]8[]8[][grow][]", ""));
        barraFiltros.setOpaque(false);

        adicionarChip(barraFiltros, "Todas");
        for (String cat : CATEGORIAS) {
            adicionarChip(barraFiltros, cat);
        }

        JButton btnNovo = Ui.botaoPrimario("+ Novo Projeto");
        btnNovo.addActionListener(e -> aoAbrirProjetos.run());
        barraFiltros.add(btnNovo, "skip 1, h 34!");
        cardTabela.add(barraFiltros, "growx, gapbottom 14");

        JTable tabela = new JTable(modelo);
        Ui.estilizarTabela(tabela, 4);
        tabela.getColumnModel().getColumn(0).setMaxWidth(70);
        cardTabela.add(Ui.rolagem(tabela), "grow");

        add(cardTabela, "grow");
        add(aviso);

        selecionarFiltro("Todas");
        atualizar();
    }

    private JPanel cardCategoria(String nome, Icone.Tipo icone, Color cor) {
        JPanel card = Ui.cartao("insets 18", "[grow][]", "");
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel textos = new JPanel(new MigLayout("insets 0, wrap 1", "[grow]", ""));
        textos.setOpaque(false);
        textos.add(Ui.rotulo(nome));

        JLabel qtd = new JLabel("0");
        qtd.setFont(qtd.getFont().deriveFont(Font.BOLD, 22f));
        qtd.setForeground(Tema.TEXTO);
        contadores.put(nome, qtd);
        textos.add(qtd);

        card.add(textos, "growx");
        card.add(new Ui.Tile(icone, cor, 44));

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selecionarFiltro(nome);
            }
        });
        return card;
    }

    private void adicionarChip(JPanel barra, String nome) {
        Ui.Chip chip = new Ui.Chip(nome, () -> selecionarFiltro(nome));
        chips.put(nome, chip);
        barra.add(chip);
    }

    private void selecionarFiltro(String filtro) {
        filtroAtual = filtro;
        chips.forEach((nome, chip) -> chip.setSelecionado(nome.equals(filtro)));
        desenharTabela();
    }

    private void desenharTabela() {
        modelo.setRowCount(0);
        for (Projeto p : cacheProjetos) {
            if ("Todas".equals(filtroAtual) || filtroAtual.equals(p.getCategoria())) {
                modelo.addRow(new Object[] {
                    p.getId(), p.getNome(), p.getDescricao(), p.getCategoria(), p.getStatus()
                });
            }
        }
    }

    @Override
    public void atualizar() {
        emSegundoPlano(api::listar, projetos -> {
            avisar("", false);
            cacheProjetos = projetos;
            for (String cat : CATEGORIAS) {
                long qtd = projetos.stream().filter(p -> cat.equals(p.getCategoria())).count();
                JLabel lbl = contadores.get(cat);
                if (lbl != null) {
                    lbl.setText(String.valueOf(qtd));
                }
            }
            desenharTabela();
        });
    }
}
