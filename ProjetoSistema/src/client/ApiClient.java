package client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.Projeto;

public class ApiClient {

    private static final String SERVIDOR = "http://localhost:7070";
    private static final String PROJETOS = SERVIDOR + "/api/projetos";
    private static final String LOGIN = SERVIDOR + "/api/login";

    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper json = new ObjectMapper();

    public boolean login(String usuario, String senha) throws IOException, InterruptedException {
        String corpo = json.writeValueAsString(Map.of("usuario", usuario, "senha", senha));
        HttpRequest requisicao = HttpRequest.newBuilder(URI.create(LOGIN))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(corpo))
            .build();
        HttpResponse<String> resposta = http.send(requisicao, HttpResponse.BodyHandlers.ofString());
        if (resposta.statusCode() == 200) {
            return true;
        }
        if (resposta.statusCode() == 401) {
            return false;
        }
        verificar(resposta);
        return false;
    }

    public List<Projeto> listar() throws IOException, InterruptedException {
        HttpRequest requisicao = HttpRequest.newBuilder(URI.create(PROJETOS)).GET().build();
        HttpResponse<String> resposta = http.send(requisicao, HttpResponse.BodyHandlers.ofString());
        verificar(resposta);
        return json.readValue(resposta.body(), new TypeReference<List<Projeto>>() {});
    }

    public Projeto cadastrar(Projeto projeto) throws IOException, InterruptedException {
        HttpRequest requisicao = HttpRequest.newBuilder(URI.create(PROJETOS))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json.writeValueAsString(projeto)))
            .build();
        HttpResponse<String> resposta = http.send(requisicao, HttpResponse.BodyHandlers.ofString());
        verificar(resposta);
        return json.readValue(resposta.body(), Projeto.class);
    }

    public void excluir(int id) throws IOException, InterruptedException {
        HttpRequest requisicao = HttpRequest.newBuilder(URI.create(PROJETOS + "/" + id)).DELETE().build();
        HttpResponse<String> resposta = http.send(requisicao, HttpResponse.BodyHandlers.ofString());
        verificar(resposta);
    }

    private void verificar(HttpResponse<String> resposta) throws IOException {
        if (resposta.statusCode() >= 400) {
            String texto = resposta.body();
            throw new IOException(texto == null || texto.isBlank()
                ? "Erro " + resposta.statusCode() + " da API."
                : texto);
        }
    }
}
