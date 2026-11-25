const API = "/api/projetos";

const form = document.getElementById("form-projeto");
const lista = document.getElementById("lista");
const vazio = document.getElementById("vazio");
const mensagem = document.getElementById("mensagem");
const busca = document.getElementById("busca");
const btnExcluir = document.getElementById("btn-excluir");

const BADGE_STATUS = {
  "Planejado": "bg-label-warning",
  "Em desenvolvimento": "bg-label-info",
  "Concluído": "bg-label-success",
};

let projetos = [];
let selecionado = null;

function avisar(texto, erro = false) {
  mensagem.textContent = texto;
  mensagem.classList.toggle("erro", erro);
}

async function carregar() {
  try {
    const resposta = await fetch(API);
    if (!resposta.ok) throw new Error("HTTP " + resposta.status);
    projetos = await resposta.json();
    desenhar();
    atualizarDashboard();
    atualizarCategorias();
  } catch (e) {
    avisar("Não foi possível carregar os projetos. A API está rodando na porta 7070?", true);
  }
}

function desenhar() {

  const termo = busca.value.trim().toLowerCase();
  const visiveis = projetos.filter((p) =>
    [p.nome, p.categoria, p.status].some((v) => String(v ?? "").toLowerCase().includes(termo))
  );

  if (!visiveis.some((p) => p.id === selecionado)) selecionado = null;

  lista.replaceChildren();
  vazio.textContent = projetos.length === 0
    ? "Nenhum projeto cadastrado. Preencha o formulário para criar o primeiro."
    : "Nenhum projeto encontrado para essa pesquisa.";
  vazio.hidden = visiveis.length > 0;

  for (const p of visiveis) {
    const tr = document.createElement("tr");
    tr.classList.toggle("table-active", p.id === selecionado);
    tr.addEventListener("click", () => selecionar(p.id));

    for (const campo of ["id", "nome", "categoria"]) {
      const td = document.createElement("td");
      td.textContent = p[campo];
      tr.append(td);
    }

    const tdStatus = document.createElement("td");
    const badge = document.createElement("span");
    badge.className = "badge rounded-pill " + (BADGE_STATUS[p.status] ?? "bg-label-secondary");
    badge.textContent = p.status;
    tdStatus.append(badge);
    tr.append(tdStatus);

    lista.append(tr);
  }

  btnExcluir.disabled = selecionado === null;
}

function selecionar(id) {
  selecionado = selecionado === id ? null : id;
  desenhar();
}

busca.addEventListener("input", desenhar);

form.addEventListener("reset", () => {
  selecionado = null;
  desenhar();
});

form.addEventListener("submit", async (evento) => {
  evento.preventDefault();

  const projeto = {
    nome: document.getElementById("nome").value.trim(),
    descricao: document.getElementById("descricao").value.trim(),
    categoria: document.getElementById("categoria").value,
    status: document.getElementById("status").value,
  };

  try {
    const resposta = await fetch(API, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(projeto),
    });
    if (!resposta.ok) throw new Error("HTTP " + resposta.status);
    form.reset();
    avisar("Projeto salvo.");
    carregar();
  } catch (e) {
    avisar("Não foi possível salvar o projeto.", true);
  }
});

btnExcluir.addEventListener("click", () => {
  if (selecionado === null) return;
  if (confirm("Excluir o projeto selecionado?")) excluir(selecionado);
});

async function excluir(id) {
  try {
    const resposta = await fetch(`${API}/${id}`, { method: "DELETE" });
    if (!resposta.ok) throw new Error("HTTP " + resposta.status);
    selecionado = null;
    avisar("Projeto excluído.");
    carregar();
  } catch (e) {
    avisar("Não foi possível excluir o projeto.", true);
  }
}

document.getElementById("btn-sair").addEventListener("click", () => {
  sessionStorage.removeItem("usuario");
  location.replace("login.html");
});

carregar();

setInterval(carregar, 5000);

const btnTema = document.getElementById("btn-tema");
const iconeTema = document.getElementById("icone-tema");

function aplicarTema(tema) {
  document.documentElement.setAttribute("data-bs-theme", tema);
  localStorage.setItem("tema", tema);
  if (iconeTema) {
    iconeTema.className = tema === "dark"
      ? "icon-base ri ri-sun-line"
      : "icon-base ri ri-moon-line";
  }
  if (typeof atualizarDashboard === "function") {
    atualizarDashboard();
  }
}

aplicarTema(localStorage.getItem("tema") || "light");

btnTema?.addEventListener("click", () => {
  const atual = document.documentElement.getAttribute("data-bs-theme");
  aplicarTema(atual === "dark" ? "light" : "dark");
});

const secaoDashboard = document.getElementById("secao-dashboard");
const secaoProjetos = document.getElementById("secao-projetos");
const secaoCategorias = document.getElementById("secao-categorias");
const secaoInformacoes = document.getElementById("secao-informacoes");

const menuDashboard = document.getElementById("menu-dashboard");
const menuProjetos = document.getElementById("menu-projetos");
const menuCategorias = document.getElementById("menu-categorias");
const menuInformacoes = document.getElementById("menu-informacoes");

function abrirTela(tela) {
  secaoDashboard.hidden = tela !== "dashboard";
  secaoProjetos.hidden = tela !== "projetos";
  secaoCategorias.hidden = tela !== "categorias";
  secaoInformacoes.hidden = tela !== "informacoes";

  menuDashboard.classList.toggle("active", tela === "dashboard");
  menuProjetos.classList.toggle("active", tela === "projetos");
  menuCategorias.classList.toggle("active", tela === "categorias");
  menuInformacoes.classList.toggle("active", tela === "informacoes");

  sessionStorage.setItem("abaAtiva", tela);
  history.replaceState(null, "", `#${tela}`);

  if (tela === "dashboard") atualizarDashboard();
  if (tela === "categorias") atualizarCategorias();
}

document.getElementById("link-dashboard")?.addEventListener("click", (e) => {
  e.preventDefault();
  abrirTela("dashboard");
});

document.getElementById("link-projetos")?.addEventListener("click", (e) => {
  e.preventDefault();
  abrirTela("projetos");
});

document.getElementById("link-categorias")?.addEventListener("click", (e) => {
  e.preventDefault();
  abrirTela("categorias");
});

document.getElementById("link-informacoes")?.addEventListener("click", (e) => {
  e.preventDefault();
  abrirTela("informacoes");
});

document.getElementById("btn-ir-projetos")?.addEventListener("click", () => {
  abrirTela("projetos");
});

const abasValidas = ["dashboard", "projetos", "categorias", "informacoes"];
const abaSalva = location.hash.replace("#", "") || sessionStorage.getItem("abaAtiva");
abrirTela(abasValidas.includes(abaSalva) ? abaSalva : "projetos");

let categoriaFiltro = "Todas";

function selecionarFiltroCategoria(cat) {
  categoriaFiltro = cat;

  document.querySelectorAll("#filtros-categorias button").forEach((btn) => {
    const ativo = btn.dataset.filtro === cat;
    btn.className = ativo ? "btn btn-sm btn-primary" : "btn btn-sm btn-outline-secondary";
  });

  atualizarCategorias();
}

document.querySelectorAll("#filtros-categorias button").forEach((btn) => {
  btn.addEventListener("click", () => selecionarFiltroCategoria(btn.dataset.filtro));
});

document.querySelectorAll(".card-categoria").forEach((card) => {
  card.addEventListener("click", () => selecionarFiltroCategoria(card.dataset.categoria));
});

document.getElementById("btn-novo-na-categoria")?.addEventListener("click", () => {
  abrirTela("projetos");
  if (categoriaFiltro !== "Todas") {
    document.getElementById("categoria").value = categoriaFiltro;
  }
  document.getElementById("nome").focus();
});

function atualizarCategorias() {
  for (const cat of ["Web", "Software", "Mobile", "Outro"]) {
    const el = document.getElementById(`cat-qtd-${cat}`);
    if (el) {
      el.textContent = projetos.filter((p) => p.categoria === cat).length;
    }
  }

  const tbody = document.getElementById("lista-categorias");
  const vazioCat = document.getElementById("vazio-categorias");
  if (!tbody || !vazioCat) return;

  const filtrados = categoriaFiltro === "Todas"
    ? projetos
    : projetos.filter((p) => p.categoria === categoriaFiltro);

  tbody.replaceChildren();
  vazioCat.hidden = filtrados.length > 0;

  for (const p of filtrados) {
    const tr = document.createElement("tr");

    for (const campo of ["id", "nome", "descricao", "categoria"]) {
      const td = document.createElement("td");
      td.textContent = p[campo] ?? "";
      tr.append(td);
    }

    const tdStatus = document.createElement("td");
    const badge = document.createElement("span");
    badge.className = "badge rounded-pill " + (BADGE_STATUS[p.status] ?? "bg-label-secondary");
    badge.textContent = p.status;
    tdStatus.append(badge);
    tr.append(tdStatus);

    tbody.append(tr);
  }
}

let graficoCategorias = null;

function atualizarDashboard() {
  const total = projetos.length;
  const concluidos = projetos.filter((p) => p.status === "Concluído").length;
  const andamento = projetos.filter((p) => p.status === "Em desenvolvimento").length;
  const planejados = projetos.filter((p) => p.status === "Planejado").length;

  document.getElementById("stat-total").textContent = total;
  document.getElementById("stat-concluidos").textContent = concluidos;
  document.getElementById("stat-andamento").textContent = andamento;
  document.getElementById("stat-planejados").textContent = planejados;

  const categorias = ["Web", "Software", "Mobile", "Outro"];
  const valores = categorias.map((cat) => projetos.filter((p) => p.categoria === cat).length);
  desenharGraficoPizza(categorias, valores, total);

  const listaRecentes = document.getElementById("lista-recentes");
  listaRecentes.replaceChildren();

  const recentes = [...projetos].slice(-5).reverse();
  if (recentes.length === 0) {
    const tr = document.createElement("tr");
    const td = document.createElement("td");
    td.colSpan = 3;
    td.className = "text-body-secondary";
    td.textContent = "Nenhum projeto cadastrado ainda.";
    tr.append(td);
    listaRecentes.append(tr);
    return;
  }

  for (const p of recentes) {
    const tr = document.createElement("tr");

    const tdNome = document.createElement("td");
    tdNome.textContent = p.nome;

    const tdCat = document.createElement("td");
    tdCat.textContent = p.categoria;

    const tdStatus = document.createElement("td");
    const badge = document.createElement("span");
    badge.className = "badge rounded-pill " + (BADGE_STATUS[p.status] ?? "bg-label-secondary");
    badge.textContent = p.status;
    tdStatus.append(badge);

    tr.append(tdNome, tdCat, tdStatus);
    listaRecentes.append(tr);
  }
}

function desenharGraficoPizza(categorias, valores, total) {
  const vazioEl = document.getElementById("grafico-vazio");
  const boxEl = document.getElementById("box-grafico");
  const canvas = document.getElementById("grafico-categorias");
  if (!canvas || typeof Chart === "undefined") return;

  if (total === 0) {
    vazioEl.hidden = false;
    boxEl.hidden = true;
    return;
  }

  vazioEl.hidden = true;
  boxEl.hidden = false;

  const ehEscuro = document.documentElement.getAttribute("data-bs-theme") === "dark";
  const corTexto = ehEscuro ? "#e2e8f0" : "#566a7f";
  const corBorda = ehEscuro ? "#1b212e" : "#ffffff";

  if (graficoCategorias) {
    graficoCategorias.data.datasets[0].data = valores;
    graficoCategorias.data.datasets[0].borderColor = corBorda;
    graficoCategorias.options.plugins.legend.labels.color = corTexto;
    graficoCategorias.update();
    return;
  }

  graficoCategorias = new Chart(canvas, {
    type: "pie",
    data: {
      labels: categorias,
      datasets: [{
        data: valores,
        backgroundColor: ["#163a70", "#2b6cb0", "#0ea5e9", "#64748b"],
        borderColor: corBorda,
        borderWidth: 2
      }]
    },
    options: {
      responsive: true,
      plugins: {
        legend: {
          position: "bottom",
          labels: { color: corTexto }
        }
      }
    }
  });
}
