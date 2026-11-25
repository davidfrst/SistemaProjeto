package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.ConnectException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import net.miginfocom.swing.MigLayout;

final class Ui {

    private Ui() {
    }

    interface Chamada<T> {
        T executar() throws Exception;
    }

    static JPanel cartao(String layout, String colunas, String linhas) {
        JPanel painel = new JPanel(new MigLayout(layout, colunas, linhas));
        painel.setBackground(Tema.SUPERFICIE);
        painel.setBorder(BorderFactory.createLineBorder(Tema.BORDA));
        return painel;
    }

    static JLabel titulo(String texto) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(rotulo.getFont().deriveFont(Font.PLAIN, 18f));
        rotulo.setForeground(Tema.TEXTO);
        return rotulo;
    }

    static JLabel rotulo(String texto) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setForeground(Tema.TEXTO_SUAVE);
        rotulo.setFont(rotulo.getFont().deriveFont(12f));
        return rotulo;
    }

    static JButton botaoPrimario(String texto) {
        JButton botao = new JButton(texto);
        botao.setBackground(Tema.PRIMARIA);
        botao.setForeground(Color.WHITE);
        return botao;
    }

    static JScrollPane rolagem(JTable tabela) {
        JScrollPane rolagem = new JScrollPane(tabela);
        rolagem.getViewport().setBackground(Tema.SUPERFICIE);
        rolagem.setBorder(BorderFactory.createLineBorder(Tema.BORDA));
        return rolagem;
    }

    static void estilizarTabela(JTable tabela, int colunaStatus) {
        tabela.setRowHeight(42);
        tabela.setShowGrid(false);
        tabela.setShowHorizontalLines(true);
        tabela.setIntercellSpacing(new Dimension(0, 1));
        tabela.setGridColor(Tema.BORDA);
        tabela.setFillsViewportHeight(true);
        tabela.setBackground(Tema.SUPERFICIE);
        tabela.setForeground(Tema.TEXTO);
        tabela.setSelectionBackground(Tema.PRIMARIA);
        tabela.setSelectionForeground(Color.WHITE);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setDefaultRenderer(Object.class, new RenderizadorTexto());
        if (colunaStatus >= 0) {
            tabela.getColumnModel().getColumn(colunaStatus).setCellRenderer(new RenderizadorStatus());
        }

        JTableHeader cabecalho = tabela.getTableHeader();
        cabecalho.setReorderingAllowed(false);
        cabecalho.setDefaultRenderer(new RenderizadorCabecalho());
        cabecalho.setPreferredSize(new Dimension(cabecalho.getPreferredSize().width, 38));
    }

    private static class RenderizadorTexto extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable tabela, Object valor,
                boolean selecionada, boolean foco, int linha, int coluna) {
            super.getTableCellRendererComponent(tabela, valor, selecionada, false, linha, coluna);
            setBorder(new EmptyBorder(0, 12, 0, 12));
            return this;
        }
    }

    private static class RenderizadorCabecalho extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable tabela, Object valor,
                boolean selecionada, boolean foco, int linha, int coluna) {
            super.getTableCellRendererComponent(tabela, valor, false, false, linha, coluna);
            setBackground(Tema.CABECALHO);
            setForeground(Tema.TEXTO_SUAVE);
            setFont(getFont().deriveFont(Font.PLAIN, 11f));
            setBorder(new EmptyBorder(0, 12, 0, 12));
            setHorizontalAlignment(LEFT);
            return this;
        }
    }

    private static class RenderizadorStatus extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable tabela, Object valor,
                boolean selecionada, boolean foco, int linha, int coluna) {
            JPanel celula = new JPanel(new GridBagLayout());
            celula.setBackground(selecionada ? tabela.getSelectionBackground() : tabela.getBackground());

            GridBagConstraints posicao = new GridBagConstraints();
            posicao.anchor = GridBagConstraints.WEST;
            posicao.weightx = 1;
            posicao.insets = new Insets(0, 12, 0, 0);

            String status = String.valueOf(valor);
            celula.add(new Pilula(status, Tema.fundoStatus(status), Tema.textoStatus(status)), posicao);
            return celula;
        }
    }

    static class Pilula extends JLabel {
        private final Color fundo;

        Pilula(String texto, Color fundo, Color letra) {
            super(texto);
            this.fundo = fundo;
            setOpaque(false);
            setForeground(letra);
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

    static class Tile extends JComponent {
        private final Icone icone;
        private final Color fundo;

        Tile(Icone.Tipo tipo, Color acento, int lado) {
            this.icone = new Icone(tipo, lado / 2, acento);
            this.fundo = new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 45);
            setPreferredSize(new Dimension(lado, lado));
            setMinimumSize(new Dimension(lado, lado));
            setMaximumSize(new Dimension(lado, lado));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fundo);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            icone.paintIcon(this, g2, (getWidth() - icone.getIconWidth()) / 2,
                (getHeight() - icone.getIconHeight()) / 2);
            g2.dispose();
        }
    }

    static class Chip extends JPanel {
        private final JLabel rotulo;
        private boolean selecionado;

        Chip(String texto, Runnable aoClicar) {
            setLayout(new BorderLayout());
            setOpaque(false);
            setBorder(new EmptyBorder(6, 14, 6, 14));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            rotulo = new JLabel(texto);
            rotulo.setForeground(Tema.TEXTO);
            add(rotulo, BorderLayout.CENTER);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    aoClicar.run();
                }
            });
        }

        void setSelecionado(boolean selecionado) {
            this.selecionado = selecionado;
            rotulo.setForeground(selecionado ? Color.WHITE : Tema.TEXTO);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (selecionado) {
                g2.setColor(Tema.PRIMARIA);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            } else {
                g2.setColor(Tema.BORDA);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static void aoDigitar(JTextField campo, Runnable acao) {
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

    static <T> void emSegundoPlano(Chamada<T> chamada, Consumer<T> aoTerminar, Consumer<String> aoErro) {
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
                    aoErro.accept(mensagemDeErro(e.getCause()));
                }
            }
        }.execute();
    }

    static String mensagemDeErro(Throwable causa) {
        if (causa instanceof ConnectException) {
            return "Erro de conexão: a API está rodando na porta 7070?";
        }
        String texto = causa == null ? null : causa.getMessage();
        return texto == null || texto.isBlank() ? "Erro inesperado." : texto;
    }
}
