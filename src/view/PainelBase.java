package view;

import java.util.function.Consumer;

import javax.swing.JLabel;
import javax.swing.JPanel;

import client.ApiClient;

abstract class PainelBase extends JPanel implements Atualizavel {

    protected final ApiClient api = new ApiClient();
    protected final JLabel aviso = new JLabel(" ");

    protected PainelBase() {
        setBackground(Tema.FUNDO);
    }

    protected void avisar(String texto, boolean erro) {
        aviso.setText(texto == null || texto.isEmpty() ? " " : texto);
        aviso.setForeground(erro ? Tema.PERIGO : Tema.TEXTO_SUAVE);
    }

    protected <T> void emSegundoPlano(Ui.Chamada<T> chamada, Consumer<T> aoTerminar) {
        Ui.emSegundoPlano(chamada, aoTerminar, mensagem -> avisar(mensagem, true));
    }
}
