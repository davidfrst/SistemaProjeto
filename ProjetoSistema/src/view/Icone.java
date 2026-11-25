package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Icon;

public class Icone implements Icon {

    public enum Tipo {
        PASTA, CASA, ETIQUETA, INFO, CHECK, RELOGIO, BARRAS, GLOBO, CODIGO, CELULAR, GRADE, SOL, LUA
    }

    private final Tipo tipo;
    private final int tamanho;
    private final Color cor;

    public Icone(Tipo tipo, int tamanho, Color cor) {
        this.tipo = tipo;
        this.tamanho = tamanho;
        this.cor = cor;
    }

    @Override
    public int getIconWidth() {
        return tamanho;
    }

    @Override
    public int getIconHeight() {
        return tamanho;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.translate(x, y);
        double escala = tamanho / 24.0;
        g2.scale(escala, escala);
        g2.setColor(cor);
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        desenhar(g2);
        g2.dispose();
    }

    private void desenhar(Graphics2D g2) {
        switch (tipo) {
            case PASTA -> {
                Path2D p = new Path2D.Double();
                p.moveTo(3, 6);
                p.lineTo(9, 6);
                p.lineTo(11, 8);
                p.lineTo(21, 8);
                p.lineTo(21, 19);
                p.lineTo(3, 19);
                p.closePath();
                g2.draw(p);
            }
            case CASA -> {
                Path2D telhado = new Path2D.Double();
                telhado.moveTo(3, 11);
                telhado.lineTo(12, 3);
                telhado.lineTo(21, 11);
                g2.draw(telhado);
                Path2D corpo = new Path2D.Double();
                corpo.moveTo(5.5, 9.5);
                corpo.lineTo(5.5, 20);
                corpo.lineTo(18.5, 20);
                corpo.lineTo(18.5, 9.5);
                g2.draw(corpo);
            }
            case ETIQUETA -> {
                Path2D p = new Path2D.Double();
                p.moveTo(4, 4);
                p.lineTo(12, 4);
                p.lineTo(20, 12);
                p.lineTo(12, 20);
                p.lineTo(4, 12);
                p.closePath();
                g2.draw(p);
                g2.fill(new Ellipse2D.Double(7, 7, 3, 3));
            }
            case INFO -> {
                g2.draw(new Ellipse2D.Double(3, 3, 18, 18));
                g2.draw(new Line2D.Double(12, 11, 12, 17));
                g2.fill(new Ellipse2D.Double(11, 6.5, 2, 2));
            }
            case CHECK -> {
                g2.draw(new Ellipse2D.Double(3, 3, 18, 18));
                Path2D v = new Path2D.Double();
                v.moveTo(8, 12.5);
                v.lineTo(10.8, 15.3);
                v.lineTo(16, 9.2);
                g2.draw(v);
            }
            case RELOGIO -> {
                g2.draw(new Ellipse2D.Double(3, 3, 18, 18));
                Path2D ponteiros = new Path2D.Double();
                ponteiros.moveTo(12, 7);
                ponteiros.lineTo(12, 12);
                ponteiros.lineTo(15.5, 14);
                g2.draw(ponteiros);
            }
            case BARRAS -> {
                g2.setStroke(new BasicStroke(2.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(new Line2D.Double(6.5, 19, 6.5, 12));
                g2.draw(new Line2D.Double(12, 19, 12, 5));
                g2.draw(new Line2D.Double(17.5, 19, 17.5, 9));
            }
            case GLOBO -> {
                g2.draw(new Ellipse2D.Double(3, 3, 18, 18));
                g2.draw(new Ellipse2D.Double(8, 3, 8, 18));
                g2.draw(new Line2D.Double(3, 12, 21, 12));
            }
            case CODIGO -> {
                Path2D esquerda = new Path2D.Double();
                esquerda.moveTo(8, 7);
                esquerda.lineTo(3, 12);
                esquerda.lineTo(8, 17);
                g2.draw(esquerda);
                Path2D direita = new Path2D.Double();
                direita.moveTo(16, 7);
                direita.lineTo(21, 12);
                direita.lineTo(16, 17);
                g2.draw(direita);
                g2.draw(new Line2D.Double(14, 5, 10, 19));
            }
            case CELULAR -> {
                g2.draw(new RoundRectangle2D.Double(7, 3, 10, 18, 3, 3));
                g2.draw(new Line2D.Double(11, 17.5, 13, 17.5));
            }
            case GRADE -> {
                g2.draw(new RoundRectangle2D.Double(4, 4, 6, 6, 2, 2));
                g2.draw(new RoundRectangle2D.Double(14, 4, 6, 6, 2, 2));
                g2.draw(new RoundRectangle2D.Double(4, 14, 6, 6, 2, 2));
                g2.draw(new RoundRectangle2D.Double(14, 14, 6, 6, 2, 2));
            }
            case SOL -> {
                g2.draw(new Ellipse2D.Double(8, 8, 8, 8));
                for (int i = 0; i < 8; i++) {
                    double angulo = Math.PI / 4 * i;
                    double cos = Math.cos(angulo);
                    double sen = Math.sin(angulo);
                    g2.draw(new Line2D.Double(12 + 7 * cos, 12 + 7 * sen, 12 + 10 * cos, 12 + 10 * sen));
                }
            }
            case LUA -> {
                Area lua = new Area(new Ellipse2D.Double(4, 4, 16, 16));
                lua.subtract(new Area(new Ellipse2D.Double(9, 1, 15, 15)));
                g2.fill(lua);
            }
        }
    }
}
