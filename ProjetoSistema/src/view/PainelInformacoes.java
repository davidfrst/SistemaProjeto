package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

class PainelInformacoes extends JPanel implements Atualizavel {

    PainelInformacoes() {
        setBackground(Tema.FUNDO);
        setLayout(new MigLayout("insets 24, wrap 1, fill", "[grow,fill]", "[][grow,fill]"));

        JPanel linhaTopo = new JPanel(new MigLayout("insets 0, fill", "[50%,fill]16[50%,fill]", "[grow,fill]"));
        linhaTopo.setOpaque(false);

        JPanel cardWeb = Ui.cartao("insets 20, wrap 1", "[grow,fill]", "");
        cardWeb.add(cabecalhoCard(Icone.Tipo.CODIGO, Tema.AZUL, "Frontend (Interface Web)"), "gapbottom 14");
        cardWeb.add(itemTecnologia("HTML5 & CSS3:", "Estrutura das páginas."), "growx, wmin 0, gapbottom 10");
        cardWeb.add(itemTecnologia("Bootstrap 5 & Tema Materio:", "Sistema de grid responsivo e componentes de layout."), "growx, wmin 0, gapbottom 10");
        cardWeb.add(itemTecnologia("JavaScript", ""), "growx, wmin 0, gapbottom 10");
        cardWeb.add(itemTecnologia("Chart.js:", "Geração do gráfico em pizza."), "growx, wmin 0");

        JPanel cardJava = Ui.cartao("insets 20, wrap 1", "[grow,fill]", "");
        cardJava.add(cabecalhoCard(Icone.Tipo.GRADE, Tema.CIANO, "Backend & Aplicação Desktop"), "gapbottom 14");
        cardJava.add(itemTecnologia("Java & Javalin:", "Servidor web."), "growx, wmin 0, gapbottom 10");
        cardJava.add(itemTecnologia("Jackson:", "JSON."), "growx, wmin 0, gapbottom 10");
        cardJava.add(itemTecnologia("Java Swing:", "Interface gráfica desktop integrada à mesma base de dados e sincronizada com a versão web."), "growx, wmin 0, gapbottom 10");
        cardJava.add(itemTecnologia("FlatLaf & MigLayout:", "Aparência moderna."), "growx, wmin 0");

        linhaTopo.add(cardWeb, "grow");
        linhaTopo.add(cardJava, "grow");
        add(linhaTopo, "growx, gapbottom 16");

        JPanel cardApi = Ui.cartao("insets 20, wrap 1, fill", "[grow,fill]", "[][grow,fill]");
        cardApi.add(cabecalhoCard(Icone.Tipo.INFO, Tema.VERDE, "Endpoints da API Utilizados"), "gapbottom 12");

        DefaultTableModel modelo = new DefaultTableModel(
            new Object[] { "MÉTODO", "ROTA", "FUNÇÃO" }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modelo.addRow(new Object[] { "POST", "/api/login", "Autentica o usuário e inicia a sessão" });
        modelo.addRow(new Object[] { "GET", "/api/projetos", "Retorna a lista completa de projetos cadastrados" });
        modelo.addRow(new Object[] { "POST", "/api/projetos", "Cadastra um novo projeto no sistema" });
        modelo.addRow(new Object[] { "DELETE", "/api/projetos/{id}", "Remove o projeto selecionado pelo ID" });

        JTable tabela = new JTable(modelo);
        Ui.estilizarTabela(tabela, -1);
        tabela.getColumnModel().getColumn(0).setMaxWidth(110);
        tabela.getColumnModel().getColumn(0).setCellRenderer(new RenderizadorMetodo());
        cardApi.add(Ui.rolagem(tabela), "grow");

        add(cardApi, "grow");
    }

    private JPanel cabecalhoCard(Icone.Tipo icone, Color cor, String titulo) {
        JPanel topo = new JPanel(new MigLayout("insets 0", "[]12[grow]", ""));
        topo.setOpaque(false);
        topo.add(new Ui.Tile(icone, cor, 36));
        topo.add(Ui.titulo(titulo));
        return topo;
    }

    private JLabel itemTecnologia(String titulo, String descricao) {
        String corTitulo = String.format("#%06X", Tema.TEXTO.getRGB() & 0xFFFFFF);
        String corDesc = String.format("#%06X", Tema.TEXTO_SUAVE.getRGB() & 0xFFFFFF);
        String html = "<html><b style='color:" + corTitulo + "'>" + titulo + "</b>"
            + (descricao.isEmpty() ? "" : " <span style='color:" + corDesc + "'>" + descricao + "</span>")
            + "</html>";
        JLabel lbl = new JLabel(html);
        lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN, 13f));
        return lbl;
    }

    @Override
    public void atualizar() {

    }

    private static class RenderizadorMetodo extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable tabela, Object valor,
                boolean selecionada, boolean foco, int linha, int coluna) {
            JPanel celula = new JPanel(new GridBagLayout());
            celula.setBackground(selecionada ? tabela.getSelectionBackground() : tabela.getBackground());

            GridBagConstraints pos = new GridBagConstraints();
            pos.anchor = GridBagConstraints.WEST;
            pos.weightx = 1;
            pos.insets = new Insets(0, 12, 0, 0);

            String metodo = String.valueOf(valor);
            celula.add(new Ui.Pilula(metodo, Tema.fundoMetodo(metodo), Tema.textoMetodo(metodo)), pos);
            return celula;
        }
    }
}
