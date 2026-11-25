package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.util.Arrays;

import javax.swing.JComponent;

class GraficoPizza extends JComponent {

    static final Color[] CORES = {
        new Color(0x1E4A8C), new Color(0x2F78C4), new Color(0x12A4E8), new Color(0x6B7A90)
    };

    private int[] valores = new int[CORES.length];

    GraficoPizza() {
        setPreferredSize(new Dimension(240, 240));
        setMinimumSize(new Dimension(120, 120));
    }

    void setValores(int[] novos) {
        this.valores = novos.clone();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int total = Arrays.stream(valores).sum();
        int diametro = Math.min(getWidth(), getHeight()) - 8;
        if (diametro > 0) {
            int x = (getWidth() - diametro) / 2;
            int y = (getHeight() - diametro) / 2;

            if (total == 0) {
                g2.setColor(Tema.BORDA);
                g2.fillOval(x, y, diametro, diametro);
            } else {
                double inicio = 90;
                g2.setStroke(new BasicStroke(2f));
                for (int i = 0; i < valores.length; i++) {
                    if (valores[i] == 0) {
                        continue;
                    }
                    double extensao = -360.0 * valores[i] / total;
                    Arc2D fatia = new Arc2D.Double(x, y, diametro, diametro, inicio, extensao, Arc2D.PIE);
                    g2.setColor(CORES[i]);
                    g2.fill(fatia);
                    g2.setColor(Tema.SUPERFICIE);
                    g2.draw(fatia);
                    inicio += extensao;
                }
            }
        }
        g2.dispose();
    }
}
