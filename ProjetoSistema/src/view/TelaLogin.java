package view;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import client.ApiClient;
import net.miginfocom.swing.MigLayout;

public class TelaLogin extends JFrame {

    private final ApiClient api = new ApiClient();
    private final JTextField campoUsuario = new JTextField();
    private final JPasswordField campoSenha = new JPasswordField();
    private final JButton botaoEntrar = Ui.botaoPrimario("Entrar");
    private final JLabel mensagem = new JLabel(" ");

    public TelaLogin() {
        setTitle("Entrar | Sistema de Projetos");
        setSize(1280, 760);
        setMinimumSize(new Dimension(600, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel fundo = new JPanel(new MigLayout("insets 24, fill", "[grow][380!][grow]", "[grow][][grow]"));
        fundo.setBackground(Tema.FUNDO);

        JPanel cartao = Ui.cartao("insets 32, wrap 1, fillx", "[grow,fill]", "");

        JLabel titulo = new JLabel("Sistema de Projetos");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
        titulo.setForeground(Tema.TEXTO);
        cartao.add(titulo, "gapbottom 4");

        cartao.add(Ui.rotulo("Entre com seu usuário e senha."), "gapbottom 20");

        JLabel lblUsuario = new JLabel("Usuário");
        lblUsuario.setForeground(Tema.TEXTO);
        lblUsuario.setFont(lblUsuario.getFont().deriveFont(13f));
        cartao.add(lblUsuario, "gapbottom 4");
        cartao.add(campoUsuario, "growx, h 40!, gapbottom 14");

        JLabel lblSenha = new JLabel("Senha");
        lblSenha.setForeground(Tema.TEXTO);
        lblSenha.setFont(lblSenha.getFont().deriveFont(13f));
        cartao.add(lblSenha, "gapbottom 4");
        cartao.add(campoSenha, "growx, h 40!, gapbottom 18");

        botaoEntrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botaoEntrar.addActionListener(e -> entrar());
        cartao.add(botaoEntrar, "growx, h 40!");

        mensagem.setForeground(Tema.PERIGO);
        mensagem.setFont(mensagem.getFont().deriveFont(12f));
        cartao.add(mensagem, "gaptop 10");

        fundo.add(cartao, "cell 1 1, growx");
        setContentPane(fundo);

        getRootPane().setDefaultButton(botaoEntrar);
    }

    private void entrar() {
        String usuario = campoUsuario.getText().trim();
        String senha = new String(campoSenha.getPassword());

        if (usuario.isEmpty() || senha.isEmpty()) {
            mensagem.setText("Informe o usuário e a senha.");
            return;
        }

        mensagem.setText(" ");
        botaoEntrar.setEnabled(false);

        Ui.emSegundoPlano(() -> api.login(usuario, senha), ok -> {
            botaoEntrar.setEnabled(true);
            if (ok) {
                dispose();
                new TelaPrincipal().setVisible(true);
            } else {
                mensagem.setText("Usuário ou senha inválidos.");
                campoSenha.setText("");
                campoSenha.requestFocus();
            }
        }, erro -> {
            botaoEntrar.setEnabled(true);
            mensagem.setText(erro);
        });
    }

    public static void main(String[] args) {
        Tema.instalar();
        SwingUtilities.invokeLater(() -> new TelaLogin().setVisible(true));
    }
}
