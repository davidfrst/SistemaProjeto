package dao;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import model.Projeto;

public class ProjetoCSV {

    private Path caminho;

    public ProjetoCSV() {

        caminho =
            Path.of("dao/projetos.csv");

    }

    public void salvar(List<Projeto> projetos)
        throws Exception {

        List<String> linhas =  new ArrayList<>();

        linhas.add("id;nome;descricao;categoria;status");

        for (Projeto projeto : projetos) {

            String linha =
                projeto.getId() + ";" +
                projeto.getNome() + ";" +
                projeto.getDescricao() + ";" +
                projeto.getCategoria() + ";" +
                projeto.getStatus();

            linhas.add(linha);
        }

        Files.write(caminho, linhas);
    }

    public List<Projeto> listar()
    throws Exception {

    List<Projeto> projetos =
        new ArrayList<>();

        if (!Files.exists(caminho)) {

    return projetos;

}

    return projetos;
    
}
}