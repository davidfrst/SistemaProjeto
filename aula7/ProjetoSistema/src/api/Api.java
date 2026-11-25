package api;

import io.javalin.Javalin;

import java.util.List;

import model.Projeto;
import service.ProjetoService;

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

            config.routes.get("/", ctx -> {

                ctx.result(
                    "API Sistema de Projetos"
                );

            });

            config.routes.get(
                "/api/projetos",
                ctx -> {

                    List<Projeto> projetos =
                        service.listar();

                    ctx.json(projetos);
                }
            );

        }).start(7070);
    }
}
