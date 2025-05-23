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
        }
        .container {
            width: 80%;
            margin: auto;
        }
        .filme-card {
            background-color: white;
            border-radius: 8px;
            padding: 16px;
            margin-bottom: 20px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            display: flex;
            gap: 20px;
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
        }#sinopse-p {
            margin-top: 3%;

        }#genero-p {
           margin-top: 1%;

         }h3{
            font-size: 1.3em;
         }
    </style>
</head>
<body>
<header>
    <div class="logo">
        <img src="assets/images/pageCriarConta/iconCinetech.svg">
        <a href="http://localhost:8080/" style="text-decoration:none"> <h1>CineTech</h1> </a>
    </div>
    <div class="search-bar">
        <form action="/pesquisaFilme" method="get">
            <input type="text" placeholder="Pesquisar filmes ou séries" name="search" />
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
    <h2>Resultados da Busca</h2>

    <c:choose>
        <c:when test="${not empty filmes}">
            <c:forEach var="filme" items="${filmes}">
                <div class="filme-card">
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
            <p style="text-align: center;">Nenhum filme encontrado.</p>
        </c:otherwise>
    </c:choose>
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

</body>
<script src="index.js"></script>
</html>
