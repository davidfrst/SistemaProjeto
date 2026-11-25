package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import net.miginfocom.swing.MigLayout;

public class TelaPrincipal extends JFrame {

    private final CardLayout cartas = new CardLayout();
    private final JPanel conteudo = new JPanel(cartas);
    private final JPanel menu = new JPanel(new MigLayout("insets 0, wrap 1, fillx", "[grow,fill]", ""));
    private final List<ItemMenu> itens = new ArrayList<>();
    private final Timer atualizador;
    private String abaAtual = "Projetos";

    public TelaPrincipal() {
        this("Projetos");
    }

    public TelaPrincipal(String abaInicial) {
        setTitle("Sistema de Projetos");
        setSize(1280, 760);
        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(criarLateral(), BorderLayout.WEST);
        add(criarAreaPrincipal(), BorderLayout.CENTER);

        adicionarTela("Projetos", Icone.Tipo.PASTA, new PainelProjetos());
        adicionarTela("Dashboard", Icone.Tipo.CASA, new PainelDashboard(() -> mostrar("Projetos")));
        adicionarTela("Categorias", Icone.Tipo.ETIQUETA, new PainelCategorias(() -> mostrar("Projetos")));
        adicionarTela("Informações", Icone.Tipo.INFO, new PainelInformacoes());

        mostrar(abaInicial);

        atualizador = new Timer(5000, e -> atualizarAbaAtual());
        atualizador.start();
    }

    public void adicionarTela(String nome, Icone.Tipo icone, JComponent painel) {
        conteudo.add(painel, nome);
        ItemMenu item = new ItemMenu(nome, icone, () -> mostrar(nome));
        itens.add(item);
        menu.add(item);
    }

    private void mostrar(String nome) {
        abaAtual = nome;
        cartas.show(conteudo, nome);
        for (ItemMenu item : itens) {
            item.setSelecionado(item.getNome().equals(nome));
        }
        atualizarAbaAtual();
    }

    private void atualizarAbaAtual() {
        for (Component c : conteudo.getComponents()) {
            if (c.isVisible() && c instanceof Atualizavel atualizavel) {
                atualizavel.atualizar();
            }
        }
    }

    private JPanel criarLateral() {
        JPanel lateral = new JPanel(new MigLayout("insets 24 12 12 12, wrap 1, fillx", "[grow,fill]", ""));
        lateral.setBackground(Tema.LATERAL);
        lateral.setPreferredSize(new Dimension(260, 0));

        JLabel titulo = new JLabel("SISTEMA DE PROJETOS", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 16f));
        titulo.setForeground(Tema.TEXTO);
        lateral.add(titulo, "growx, gapbottom 24");

        menu.setOpaque(false);
        lateral.add(menu, "growx, top, pushy");
        return lateral;
    }

    private JPanel criarAreaPrincipal() {
        JPanel area = new JPanel(new BorderLayout());
        area.setBackground(Tema.FUNDO);
        area.add(criarBarraSuperior(), BorderLayout.NORTH);
        conteudo.setOpaque(false);
        area.add(conteudo, BorderLayout.CENTER);
        return area;
    }

    private JPanel criarBarraSuperior() {
        JPanel barra = new JPanel(new MigLayout("insets 12 24 0 24, fillx", "[grow][][][][]", ""));
        barra.setOpaque(false);

        Icone.Tipo tipoTema = Tema.isEscuro() ? Icone.Tipo.SOL : Icone.Tipo.LUA;
        JButton btnTema = new JButton(new Icone(tipoTema, 18, Tema.TEXTO));
        btnTema.setToolTipText("Alternar modo escuro/claro");
        btnTema.addActionListener(e -> {
            String aba = abaAtual;
            Tema.alternar();
            dispose();
            SwingUtilities.invokeLater(() -> new TelaPrincipal(aba).setVisible(true));
        });

        JLabel usuario = new JLabel("Admin");
        usuario.setForeground(Tema.TEXTO);
        JButton sair = new JButton("Sair");
        sair.addActionListener(e -> {
            dispose();
            new TelaLogin().setVisible(true);
        });

        barra.add(btnTema, "skip 1, w 36!, h 36!, gapright 12");
        barra.add(usuario, "gapright 12");
        barra.add(new Avatar("A"), "w 40!, h 40!, gapright 12");
        barra.add(sair, "h 36!");
        return barra;
    }

    @Override
    public void dispose() {
        if (atualizador != null) {
            atualizador.stop();
        }
        super.dispose();
    }

    public static void main(String[] args) {
        Tema.instalar();
        SwingUtilities.invokeLater(() -> new TelaLogin().setVisible(true));
    }

    private static class ItemMenu extends JPanel {
        private final String nome;
        private boolean selecionado;

        ItemMenu(String nome, Icone.Tipo tipoIcone, Runnable aoClicar) {
            this.nome = nome;
            setLayout(new MigLayout("insets 10 18 10 18", "[]12[grow]", ""));
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel icone = new JLabel(new Icone(tipoIcone, 18, Tema.TEXTO));
            JLabel rotulo = new JLabel(nome);
            rotulo.setForeground(Tema.TEXTO);
            add(icone);
            add(rotulo);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    aoClicar.run();
                }
            });
        }

        String getNome() {
            return nome;
        }

        void setSelecionado(boolean selecionado) {
            this.selecionado = selecionado;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (selecionado) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Tema.PRIMARIA);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    private static class Avatar extends JLabel {
        Avatar(String texto) {
            super(texto, SwingConstants.CENTER);
            setOpaque(false);
            setForeground(Tema.AVATAR_TEXTO);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Tema.AVATAR_FUNDO);
            g2.fillOval(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
