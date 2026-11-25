package view;

import java.awt.Color;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.UIManager;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

public final class Tema {

    private static final Preferences PREFERENCIAS = Preferences.userNodeForPackage(Tema.class);
    private static boolean escuro = PREFERENCIAS.getBoolean("escuro", true);

    public static Color FUNDO, LATERAL, SUPERFICIE, CABECALHO, BORDA, TEXTO, TEXTO_SUAVE;
    public static Color PRIMARIA, PERIGO, CODIGO, AVATAR_FUNDO, AVATAR_TEXTO;

    public static final Color AZUL = new Color(0x4C8DF6);
    public static final Color CIANO = new Color(0x29B6E8);
    public static final Color VERDE = new Color(0x34C97A);
    public static final Color AMBAR = new Color(0xF0B429);

    static {
        definirCores();
    }

    private Tema() {
    }

    public static boolean isEscuro() {
        return escuro;
    }

    public static void alternar() {
        escuro = !escuro;
        PREFERENCIAS.putBoolean("escuro", escuro);
        instalar();
    }

    public static void instalar() {
        definirCores();
        FlatLaf.setGlobalExtraDefaults(Map.of(
            "@background", hex(FUNDO),
            "@foreground", hex(TEXTO),
            "@accentColor", "#2F6BDB"
        ));
        if (escuro) {
            FlatDarkLaf.setup();
        } else {
            FlatLightLaf.setup();
        }

        UIManager.put("Button.arc", 10);
        UIManager.put("Component.arc", 10);
        UIManager.put("TextComponent.arc", 10);
    }

    private static void definirCores() {
        PRIMARIA = new Color(0x17376E);
        PERIGO = new Color(0xE5484D);
        if (escuro) {
            FUNDO = new Color(0x111722);
            LATERAL = new Color(0x1C2333);
            SUPERFICIE = new Color(0x1D2434);
            CABECALHO = new Color(0x232B3D);
            BORDA = new Color(0x2A3245);
            TEXTO = new Color(0xE5E9F0);
            TEXTO_SUAVE = new Color(0x8B95A7);
            CODIGO = new Color(0xE0608A);
            AVATAR_FUNDO = new Color(0x25314D);
            AVATAR_TEXTO = new Color(0x7FA8F5);
        } else {
            FUNDO = new Color(0xF3F5FA);
            LATERAL = new Color(0xF3F5FA);
            SUPERFICIE = new Color(0xFFFFFF);
            CABECALHO = new Color(0xF6F7FB);
            BORDA = new Color(0xE3E6EE);
            TEXTO = new Color(0x3B4152);
            TEXTO_SUAVE = new Color(0x8A91A5);
            CODIGO = new Color(0xD63384);
            AVATAR_FUNDO = new Color(0xDDE3F0);
            AVATAR_TEXTO = new Color(0x17376E);
        }
    }

    private static String hex(Color cor) {
        return String.format("#%06X", cor.getRGB() & 0xFFFFFF);
    }

    public static Color fundoStatus(String status) {
        String s = status == null ? "" : status;
        if (escuro) {
            return switch (s) {
                case "Concluído" -> new Color(0x1B4332);
                case "Em desenvolvimento" -> new Color(0x153A50);
                case "Planejado" -> new Color(0x4A3A1F);
                default -> BORDA;
            };
        }
        return switch (s) {
            case "Concluído" -> new Color(0xDFF6DD);
            case "Em desenvolvimento" -> new Color(0xD7F0FB);
            case "Planejado" -> new Color(0xFFF0CC);
            default -> BORDA;
        };
    }

    public static Color textoStatus(String status) {
        String s = status == null ? "" : status;
        if (escuro) {
            return switch (s) {
                case "Concluído" -> new Color(0x34C97A);
                case "Em desenvolvimento" -> new Color(0x29B6E8);
                case "Planejado" -> new Color(0xF0B429);
                default -> TEXTO_SUAVE;
            };
        }
        return switch (s) {
            case "Concluído" -> new Color(0x2F9E44);
            case "Em desenvolvimento" -> new Color(0x129AD1);
            case "Planejado" -> new Color(0xD98E00);
            default -> TEXTO_SUAVE;
        };
    }

    public static Color fundoMetodo(String metodo) {
        return switch (metodo) {
            case "GET" -> escuro ? new Color(0x1B4332) : new Color(0xDFF6DD);
            case "DELETE" -> new Color(0xFFE3E6);
            default -> escuro ? new Color(0x273A5E) : new Color(0xE0E9FB);
        };
    }

    public static Color textoMetodo(String metodo) {
        return switch (metodo) {
            case "GET" -> escuro ? new Color(0x34C97A) : new Color(0x2F9E44);
            case "DELETE" -> new Color(0xE5484D);
            default -> escuro ? new Color(0x8AB4FF) : new Color(0x2F6BDB);
        };
    }
}
