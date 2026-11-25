package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import model.Projeto;
import net.miginfocom.swing.MigLayout;

class PainelDashboard extends PainelBase {

    private static final String[] CATEGORIAS = { "Web", "Software", "Mobile", "Outro" };

    private final JLabel lblTotal = valorGrande();
    private final JLabel lblConcluidos = valorGrande();
    private final JLabel lblAndamento = valorGrande();
    private final JLabel lblPlanejados = valorGrande();

    private final GraficoPizza pizza = new GraficoPizza();
    private final DefaultTableModel modeloRecentes = new DefaultTableModel(
        new Object[] { "NOME", "CATEGORIA", "STATUS" }, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    PainelDashboard(Runnable aoAbrirProjetos) {
        setLayout(new MigLayout("insets 24, wrap 1, fill", "[grow,fill]", "[][grow,fill][]"));

        JPanel linhaCards = new JPanel(new MigLayout("insets 0, fillx", "[grow,fill]16[grow,fill]16[grow,fill]16[grow,fill]", ""));
        linhaCards.setOpaque(false);
        linhaCards.add(cardStat(Icone.Tipo.PASTA, Tema.AZUL, lblTotal, "Total de Projetos"));
        linhaCards.add(cardStat(Icone.Tipo.CHECK, Tema.VERDE, lblConcluidos, "Concluídos"));
        linhaCards.add(cardStat(Icone.Tipo.RELOGIO, Tema.CIANO, lblAndamento, "Em Andamento"));
        linhaCards.add(cardStat(Icone.Tipo.BARRAS, Tema.AMBAR, lblPlanejados, "Planejados"));
        add(linhaCards, "growx, gapbottom 16");

        JPanel linhaInferior = new JPanel(new MigLayout("insets 0, fill", "[42%,fill]16[58%,fill]", "[grow,fill]"));
        linhaInferior.setOpaque(false);

        JPanel cardGrafico = Ui.cartao("insets 20, wrap 1, fill", "[grow,fill]", "[][grow,center][]");
        cardGrafico.add(Ui.titulo("Projetos por Categoria"));
        cardGrafico.add(pizza, "center");
        cardGrafico.add(criarLegenda(), "center");

        JPanel cardRecentes = Ui.cartao("insets 20, wrap 1, fill", "[grow,fill]", "[][grow,fill]");
        JPanel topoRecentes = new JPanel(new MigLayout("insets 0, fillx", "[grow][]", ""));
        topoRecentes.setOpaque(false);
        topoRecentes.add(Ui.titulo("Projetos Recentes"));
        JButton btnGerenciar = Ui.botaoPrimario("Gerenciar Projetos");
        btnGerenciar.addActionListener(e -> aoAbrirProjetos.run());
        topoRecentes.add(btnGerenciar, "h 32!");
        cardRecentes.add(topoRecentes, "growx, gapbottom 12");

        JTable tabela = new JTable(modeloRecentes);
        Ui.estilizarTabela(tabela, 2);
        cardRecentes.add(Ui.rolagem(tabela), "grow");

        linhaInferior.add(cardGrafico, "grow");
        linhaInferior.add(cardRecentes, "grow");
        add(linhaInferior, "grow");
        add(aviso);

        atualizar();
    }

    private JPanel cardStat(Icone.Tipo icone, Color cor, JLabel valor, String legenda) {
        JPanel card = Ui.cartao("insets 18", "[]16[grow]", "");
        card.add(new Ui.Tile(icone, cor, 48));

        JPanel textos = new JPanel(new MigLayout("insets 0, wrap 1", "[grow]", ""));
        textos.setOpaque(false);
        textos.add(valor);
        textos.add(Ui.rotulo(legenda));
        card.add(textos, "growx");
        return card;
    }

    private JPanel criarLegenda() {
        JPanel legenda = new JPanel(new MigLayout("insets 4 0 0 0", "[]14[]14[]14[]", ""));
        legenda.setOpaque(false);
        for (int i = 0; i < CATEGORIAS.length; i++) {
            JPanel item = new JPanel(new MigLayout("insets 0", "[]6[]", ""));
            item.setOpaque(false);
            item.add(new Bolinha(GraficoPizza.CORES[i]));
            item.add(Ui.rotulo(CATEGORIAS[i]));
            legenda.add(item);
        }
        return legenda;
    }

    private static JLabel valorGrande() {
        JLabel lbl = new JLabel("0");
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 22f));
        lbl.setForeground(Tema.TEXTO);
        return lbl;
    }

    @Override
    public void atualizar() {
        emSegundoPlano(api::listar, projetos -> {
            avisar("", false);
            int total = projetos.size();
            long concluidos = projetos.stream().filter(p -> "Concluído".equals(p.getStatus())).count();
            long andamento = projetos.stream().filter(p -> "Em desenvolvimento".equals(p.getStatus())).count();
            long planejados = projetos.stream().filter(p -> "Planejado".equals(p.getStatus())).count();

            lblTotal.setText(String.valueOf(total));
            lblConcluidos.setText(String.valueOf(concluidos));
            lblAndamento.setText(String.valueOf(andamento));
            lblPlanejados.setText(String.valueOf(planejados));

            int[] valores = new int[CATEGORIAS.length];
            for (int i = 0; i < CATEGORIAS.length; i++) {
                String cat = CATEGORIAS[i];
                valores[i] = (int) projetos.stream().filter(p -> cat.equals(p.getCategoria())).count();
            }
            pizza.setValores(valores);

            modeloRecentes.setRowCount(0);
            List<Projeto> recentes = new ArrayList<>(projetos);
            Collections.reverse(recentes);
            int limite = Math.min(5, recentes.size());
            for (int i = 0; i < limite; i++) {
                Projeto p = recentes.get(i);
                modeloRecentes.addRow(new Object[] { p.getNome(), p.getCategoria(), p.getStatus() });
            }
        });
    }

    private static class Bolinha extends JComponent {
        private final Color cor;

        Bolinha(Color cor) {
            this.cor = cor;
            setPreferredSize(new java.awt.Dimension(10, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(cor);
            g2.fillOval(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }
}
