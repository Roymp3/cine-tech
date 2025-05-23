<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
   <link rel ="stylesheet" href="index.css">
   <link rel="styleshhet" href="/paginaPesquisa/paginaPesquisa.css">
   <link rel="preconnect" href="https://fonts.googleapis.com">
       <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
       <link href="https://fonts.googleapis.com/css2?family=Inria+Sans:ital,wght@0,300;0,400;0,700;1,300;1,400;1,700&display=swap" rel="stylesheet">
 <link rel="stylesheet" href="./menuLateral/menuLateral.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <meta charset="UTF-8">
    <title>Resultado da Busca</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f8f8f8;
            margin: 0;
            padding: 0;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }
        .page-content {
            flex: 1;
            display: flex;
            flex-direction: column;
            min-height: calc(100vh - 80px); /* Altura total menos altura do footer */
        }
        .container {
            width: 80%;
            margin: auto;
            flex: 1;
            padding-bottom: 40px;
        }
        .filme-card {
            background-color: white;
            border-radius: 8px;
            padding: 16px;
            margin-bottom: 20px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            display: flex;
            gap: 20px;
            cursor: pointer; /* Indica que o elemento é clicável */
            transition: transform 0.2s ease, box-shadow 0.2s ease; /* Transição suave */
        }
        .filme-card:hover {
            transform: translateY(-3px); /* Efeito de elevação */
            box-shadow: 0 5px 15px rgba(0,0,0,0.2); /* Sombra mais pronunciada */
        }
        .filme-card img {
            width: 150px;
            height: 200px;
            object-fit: cover;
            border-radius: 4px;
        }
        .filme-info {
            flex: 1;
        }
        h2 {
            text-align: center;
            margin: 30px 0;
        }
        #sinopse-p {
            margin-top: 3%;
        }
        #genero-p {
           margin-top: 1%;
        }
        h3{
            font-size: 1.3em;
        }
        footer {
            width: 100%;
            height: 80px;
            background-color: #494850;
            padding: 15px 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            color: white;
            box-sizing: border-box;
            margin-top: auto;
        }
        footer p {
            text-align: center;
            font-size: 13px;
            color: rgba(255, 255, 255, 0.8);
            margin: 0;
        }
        .rede-sociais {
            display: flex;
            gap: 15px;
        }
        .rede-sociais img {
            width: 20px;
            height: 20px;
        }
        .menu {
            display: flex;
            gap: 15px;
        }
        .menu a {
            color: white;
            text-decoration: none;
            font-size: 14px;
        }        .empty-results {
            text-align: center;
            padding: 50px 0;
            font-size: 18px;
            color: #666;
            min-height: 300px;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
        }
        .search-tip {
            margin-top: 15px;
            font-size: 16px;
            color: #888;
        }
        .results-count {
            margin-bottom: 20px;
            color: #666;
            font-style: italic;
        }
    </style>
</head>
<body>
<div class="page-content">
    <header>
        <div class="logo">
            <img src="assets/images/pageCriarConta/iconCinetech.svg">
            <a href="http://localhost:8080/" style="text-decoration:none"> <h1>CineTech</h1> </a>
        </div>        <div class="search-bar">
            <form action="/pesquisaFilme" method="get">
                <input type="text" placeholder="Pesquisar filmes ou séries" name="search" value="${param.search}" />
                <button type="submit" style="border:none; background-color: transparent"><i class="fas fa-search"></i></button>
            </form>
        </div>
        <div id="ocultar" class="user-controls">
            <i class="far fa-bell"></i>
            <i onclick="abrirMenuLateral()" class="fas fa-bars"></i>
        </div>

        <div id="menuLateral"></div>
    </header>
      <div class="container">
        <h2>
            <c:choose>
                <c:when test="${not empty param.search}">
                    Resultados da Busca: "${param.search}"
                </c:when>
                <c:otherwise>
                    Resultados da Busca
                </c:otherwise>
            </c:choose>
        </h2>

        <c:choose>
            <c:when test="${not empty filmes}">
                <p class="results-count">${filmes.size()} filme(s) encontrado(s)</p>
                <c:forEach var="filme" items="${filmes}">
                    <div class="filme-card" onclick="window.location.href='PaginaReview/filme.html?id=${filme.id}'">
                        <c:if test="${not empty filme.bannerEncoded}">
                            <img src="data:image/jpeg;base64,${filme.bannerEncoded}" alt="Banner do filme"/>
                        </c:if>
                        <div class="filme-info">
                            <h3>${filme.nome}</h3>
                            <p id="genero-p"> <strong>Gênero:</strong> ${filme.genero}</p>
                            <p id="sinopse-p">  <strong>Sinopse:</strong> ${filme.sinopse}</p>
                        </div>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="empty-results">
                    <p>Nenhum filme encontrado para "${param.search}".</p>
                    <p class="search-tip">Tente pesquisar por outro termo ou verifique a ortografia.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<footer>
    <div class="rede-sociais">
        <img src="../assets/images/imgfilme/twitter.svg">
        <img src="../assets/images/imgfilme/youtube.svg">
        <img src="../assets/images/imgfilme/facebook.svg">
        <img src="../assets/images/imgfilme/instagram 1.svg">
    </div>
    <p>© CineTech 2024 - 2025. Todos os direitos reservados. </p>
    <div class="menu">
        <a>About</a>
        <a>Home</a>
        <a>Contact</a>
    </div>
</footer>

<script src="index.js"></script>
<script>
// Adicione evento de clique a todos os cartões de filme
document.addEventListener('DOMContentLoaded', function() {
    const filmeCards = document.querySelectorAll('.filme-card');
    
    filmeCards.forEach(card => {
        card.style.cursor = 'pointer';
    });
    
    // Certificando-se que o footer está sempre no fim da página
    function ajustarAlturaPagina() {
        const pageContent = document.querySelector('.page-content');
        const windowHeight = window.innerHeight;
        const footerHeight = document.querySelector('footer').offsetHeight;
        
        // Garante altura mínima para empurrar o footer para baixo
        pageContent.style.minHeight = (windowHeight - footerHeight) + 'px';
    }
    
    // Executa no carregamento e redimensionamento da janela
    ajustarAlturaPagina();
    window.addEventListener('resize', ajustarAlturaPagina);
});
</script>
</body>
</html>
