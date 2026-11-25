package api;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import java.util.List;
import model.Projeto;
import service.ProjetoService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Api {

    public static void main(String[] args) {

        ProjetoService service =
            new ProjetoService();

        try {
            service.carregar();
        } catch (Exception e) {
            System.err.println("Aviso: Arquivo de dados inicial não carregado: " + e.getMessage());
        }

        var app = Javalin.create(config -> {

        	config.staticFiles.add("src/main/resources/public", Location.EXTERNAL);

            config.routes.get(
                "/api/projetos",
                ctx -> {

                    List<Projeto> projetos =
                        service.listar();

                    ctx.json(projetos);
                }
            );

            config.routes.get("/api/projetos/{id}", ctx -> {
                int id = ctx.pathParamAsClass("id", Integer.class).get();
                Projeto projeto = service.buscarPorId(id);
                if (projeto == null) {
                    ctx.status(404).result("Projeto não encontrado.");
                    return;
                }
                ctx.json(projeto);
            });

            config.routes.post("/api/projetos", ctx -> {
                Projeto novo = ctx.bodyAsClass(Projeto.class);
                String erro = validar(novo);
                if (erro != null) {
                    ctx.status(400).result(erro);
                    return;
                }

                int proximoId = 1;
                for (Projeto p : service.listar()) {
                    if (p.getId() >= proximoId) {
                        proximoId = p.getId() + 1;
                    }
                }
                novo.setId(proximoId);

                service.adicionar(novo);
                service.salvar();
                log("CRIADO", "id=" + novo.getId() + " nome=" + novo.getNome());
                ctx.status(201).json(novo);
            });

            config.routes.put("/api/projetos/{id}", ctx -> {
                int id = ctx.pathParamAsClass("id", Integer.class).get();
                Projeto atualizado = ctx.bodyAsClass(Projeto.class);
                atualizado.setId(id);

                String erro = validar(atualizado);
                if (erro != null) {
                    ctx.status(400).result(erro);
                    return;
                }
                if (!service.alterar(atualizado)) {
                    ctx.status(404).result("Projeto não encontrado.");
                    return;
                }
                service.salvar();
                log("ALTERADO", "id=" + id);
                ctx.json(atualizado);
            });

            config.routes.delete("/api/projetos/{id}", ctx -> {
                int id = ctx.pathParamAsClass("id", Integer.class).get();
                if (!service.removerPorId(id)) {
                    ctx.status(404).result("Projeto não encontrado.");
                    return;
                }
                service.salvar();
                log("EXCLUIDO", "id=" + id);
                ctx.status(204);
            });

            config.routes.after("/api/*", ctx ->
            log("ACESSO", ctx.method() + " " + ctx.path() + " -> " + ctx.statusCode() + " (" + ctx.ip() + ")")
        );

            config.routes.post("/api/login", ctx -> {
                var dados = ctx.bodyAsClass(java.util.Map.class);
                boolean ok = "admin".equals(dados.get("usuario"))
                          && "123456".equals(dados.get("senha"));
                ctx.status(ok ? 200 : 401);
            });

        }).start(7070);
    }

    private static final int DESCRICAO_MINIMA = 5;
    private static final List<String> STATUS_VALIDOS =
        List.of("Planejado", "Em desenvolvimento", "Concluído");

    private static String validar(Projeto p) {
        if (p.getNome() == null || p.getNome().isBlank()) {
            return "Informe o nome.";
        }
        if (p.getDescricao() == null || p.getDescricao().trim().length() < DESCRICAO_MINIMA) {
            return "A descrição precisa ter pelo menos " + DESCRICAO_MINIMA + " caracteres.";
        }
        if (p.getCategoria() == null || p.getCategoria().isBlank()) {
            return "Informe a categoria.";
        }
        if (!STATUS_VALIDOS.contains(p.getStatus())) {
            return "Status inválido.";
        }
        return null;
    }

    private static final DateTimeFormatter HORA =
    	    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    	private static final Path ARQUIVO_LOG = Path.of("dados", "log.csv");

    	private static synchronized void log(String tipo, String detalhe) {
    	    String data = LocalDateTime.now().format(HORA);
    	    System.out.println("[" + data + "] " + tipo + " " + detalhe);

    	    try {
    	        Files.createDirectories(ARQUIVO_LOG.getParent());

    	        String linha = data + ";" + tipo + ";" + detalhe.replace(";", ",") + "\n";
    	        if (Files.notExists(ARQUIVO_LOG)) {
    	            linha = "data_hora;tipo;detalhe\n" + linha;
    	        }

    	        Files.writeString(ARQUIVO_LOG, linha, StandardCharsets.UTF_8,
    	            StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    	    } catch (IOException e) {
    	        System.err.println("Não foi possível gravar o log: " + e.getMessage());
    	    }
    	}
}

