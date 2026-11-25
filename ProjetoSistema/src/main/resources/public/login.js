const API_LOGIN = "/api/login";

const form = document.getElementById("form-login");
const mensagem = document.getElementById("mensagem");
const botao = document.getElementById("btn-entrar");

if (sessionStorage.getItem("usuario")) {
  location.replace("index.html");
}

function avisar(texto, erro = false) {
  mensagem.textContent = texto;
  mensagem.classList.toggle("erro", erro);
}

form.addEventListener("submit", async (evento) => {
  evento.preventDefault();

  const usuario = document.getElementById("usuario").value.trim();
  const senha = document.getElementById("senha").value;

  botao.disabled = true;
  avisar("");

  try {
    const resposta = await fetch(API_LOGIN, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ usuario, senha }),
    });

    if (resposta.status === 401) {
      avisar("Usuário ou senha inválidos.", true);
      return;
    }
    if (!resposta.ok) throw new Error("HTTP " + resposta.status);

    sessionStorage.setItem("usuario", usuario);
    location.replace("index.html");
  } catch (e) {
    avisar("Não foi possível entrar. A API está rodando na porta 7070?", true);
  } finally {
    botao.disabled = false;
  }
});
