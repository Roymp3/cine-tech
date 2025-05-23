<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="pt">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Perfil</title>
    <link rel="stylesheet" href="/PaginaPerfil/usuario.css">
     <link rel="stylesheet" href="/index.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inria+Sans:ital,wght@0,300;0,400;0,700;1,300;1,400;1,700&display=swap" rel="stylesheet">
     <link rel="stylesheet" href="./menuLateral/menuLateral.css">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
</head>
<body>

<header>
    <div class="logo">
        <img src="assets/images/pageCriarConta/iconCinetech.svg">
        <a href="http://localhost:8080/" style="text-decoration:none"> <h1>CineTech</h1> </a>
    </div>

    <div id="ocultar" class="user-controls">
        <i class="far fa-bell"></i>
        <i onclick="abrirMenuLateral()" class="fas fa-bars"></i>
    </div>

    <div id="menuLateral"></div>
</header>


<section class="usuario">
    <div class="perfil-image">
        <img src="PaginaPerfil/imgusuario/Vector.svg" alt="perfil de usuario">
    </div>

    <!-- Exibe o nome do usuário da sessão, ou "Usuário não logado" -->
    <div class="nome" id="nomeUsuario">
        <c:choose>
            <c:when test="${not empty sessionScope.nome}">
                ${sessionScope.nome}
            </c:when>
            <c:otherwise>
                Usuário não logado
            </c:otherwise>
        </c:choose>
    </div>

    <div class="status-perfil">
        <div class="indices">
            <div class="numero">29</div>
            <div class="label">Filmes Vistos</div>
        </div>
        <div class="indices">
            <div class="numero">76</div>
            <div class="label">Favoritos</div>
        </div>
        <div class="indices">
            <div class="numero">45</div>
            <div class="label">Comentários</div>
        </div>
    </div>
</section>

<section class="lista">
    <div class="campo">
        <div class="lista-header">
            <img class="img1" src="PaginaPerfil/imgusuario/lista.svg" alt="lista">
            <h2>Lista de desejos</h2>
        </div>

        <div class="lista-items">
            <div class="lista-item">
                <img class="img" src="PaginaPerfil/imgusuario/minecraft.jpg" alt="minecraft">
                <div class="lista-item-info">
                    <div class="lista-item-titulo">Minecraft</div>
                    <div class="lista-item-ano">2025</div>
                </div>
            </div>

            <div class="lista-item">
                <img class="img" src="PaginaPerfil/imgusuario/flow.jpg" alt="Flow">
                <div class="lista-item-info">
                    <div class="lista-item-titulo">Flow</div>
                    <div class="lista-item-ano">2025</div>
                </div>
            </div>
        </div>
    </div>
</section>

<section class="generos">
    <div class="genres-header">
        <span class="icon">🎬</span>
        <h2>Gêneros Favoritos</h2>
    </div>

    <div class="genero-tags">
        <div class="genero-tag">Terror</div>
        <div class="genero-tag">Comédia</div>
        <div class="genero-tag">Drama</div>
        <div class="genero-tag">Ação</div>
        <div class="genero-tag">Ficção Científica</div>
    </div>
</section>

<section class="conteudo-central">
    <div class="tabs">
        <div class="tab-coluna">
            <div class="tab-active">Favoritos</div>
            <div class="card-container">
                <div class="card">
                    <img src="PaginaPerfil/imgusuario/flow.jpg" alt="Flow">
                    <div class="info">
                        <div class="titulo">Flow</div>
                        <div class="detalhes">
                            <img src="PaginaPerfil/imgusuario/estrela.svg" alt="Estrela" class="estrela-img">
                            9.0 | 2025
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="tab-coluna">
            <div class="tab">Curtidos</div>
            <div class="card-container">
                <div class="card card-vazio" style="display: none;"></div>
            </div>
        </div>

        <div class="tab-coluna">
            <div class="tab">Comentários</div>
            <div class="card-container">
                <div class="card card-vazio" style="display: none;"></div>
            </div>
        </div>
    </div>
</section>

<footer>
    <div class="rede-sociais">
        <img src="PaginaPerfil/imgusuario/twitter.svg" alt="Twitter">
        <img src="PaginaPerfil/imgusuario/youtube.svg" alt="YouTube">
        <img src="PaginaPerfil/imgusuario/facebook.svg" alt="Facebook">
        <img src="PaginaPerfil/imgusuario/instagram%201.svg" alt="Instagram">
    </div>

    <p>© CineTech 2024 - 2025. Todos os direitos reservados. </p>

    <div class="menu">
        <a href="#">About</a>
        <a href="#">Home</a>
        <a href="#">Contact</a>
    </div>
</footer>
<script src="/index.js"></script>
<script src="PaginaPerfil/usuario.js"></script>

</body>
</html>
