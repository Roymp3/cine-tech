document.addEventListener('DOMContentLoaded', function() {
    
    const abas = document.querySelectorAll('.tab, .tab-active');
    
    // Verificar se o usuário é um administrador
    function isAdmin() {
        const emailLogado = sessionStorage.getItem('emailLogado');
        return emailLogado && emailLogado.includes('@cinetech.com');
    }
    
    // Verifica se existem resultados na página
    function verificarResultados() {
        const resultadosContainer = document.querySelector('.movie-cards');
        const resultadosHeading = document.querySelector('.results-heading');
        
        // Se não houver resultados ou se o texto indicar que não há resultados
        if (!resultadosContainer.children.length || resultadosHeading.textContent.includes('Nenhum filme encontrado')) {
            // Adiciona um elemento de aviso se não existir
            if (!document.querySelector('.no-results-message')) {
                const mensagem = document.createElement('div');
                mensagem.className = 'no-results-message';
                mensagem.textContent = 'Nenhum filme encontrado.';
                mensagem.style.textAlign = 'center';
                mensagem.style.padding = '40px 0';
                mensagem.style.color = '#666';
                mensagem.style.fontSize = '18px';
                resultadosContainer.appendChild(mensagem);
            }
        }
    }
    
    // Adicionar botões de administração aos filmes se o usuário for admin
    function adicionarBotoesAdmin() {
        if (!isAdmin()) return;
        
        const movieCards = document.querySelectorAll('.movie-card');
        movieCards.forEach(card => {
            // Verificar se já tem os botões de admin
            if (card.querySelector('.admin-controls')) return;
            
            const filmeId = card.dataset.filmeId;
            if (!filmeId) return;
            
            const adminControls = document.createElement('div');
            adminControls.className = 'admin-controls';
            
            // Botão de editar
            const editBtn = document.createElement('button');
            editBtn.className = 'admin-btn edit-btn';
            editBtn.innerHTML = '<i class="fas fa-edit"></i> Editar';
            editBtn.addEventListener('click', () => editarFilme(filmeId));
            
            // Botão de excluir
            const deleteBtn = document.createElement('button');
            deleteBtn.className = 'admin-btn delete-btn';
            deleteBtn.innerHTML = '<i class="fas fa-trash"></i> Excluir';
            deleteBtn.addEventListener('click', () => confirmarExclusao(filmeId));
            
            adminControls.appendChild(editBtn);
            adminControls.appendChild(deleteBtn);
            
            // Adicionar ao card
            const movieInfo = card.querySelector('.movie-info');
            if (movieInfo) {
                movieInfo.appendChild(adminControls);
            }
        });
    }
  
    abas.forEach(aba => {
        aba.addEventListener('click', function() {
           
            abas.forEach(a => {
                a.className = 'tab';
            });
            
            
            this.className = 'tab-active';
            
           
            const nomeAba = this.textContent;
            
            
            atualizarConteudo(nomeAba);
        });
    });
    
   
    function atualizarConteudo(nomeAba) {
       
        const colunas = document.querySelectorAll('.tab-coluna');
        
       
        colunas.forEach((coluna) => {
            const aba = coluna.querySelector('.tab, .tab-active');
            const cardContainer = coluna.querySelector('.card-container');
            
            if (!cardContainer) return;
            
            const card = cardContainer.querySelector('.card');
            
           
            if (!card) return;
            
           
            if (aba.textContent === 'Favoritos') {
                
                if (nomeAba === 'Favoritos') {
                    card.classList.remove('card-vazio');
                    card.style.display = 'flex';
                    if (card.querySelector('.info')) {
                        card.querySelector('.info').style.display = 'block';
                    }
                    if (card.querySelector('img')) {
                        card.querySelector('img').style.display = 'block';
                    }
                } else {
                    card.classList.add('card-vazio');
                    card.style.display = 'none';
                }
            } 
            
            else if (aba.textContent === nomeAba) {
               
                card.classList.add('card-vazio');
                card.style.display = 'flex'; 
                if (card.querySelector('.info')) {
                    card.querySelector('.info').style.display = 'none';
                }
                if (card.querySelector('img')) {
                    card.querySelector('img').style.display = 'none';
                }
                
              
                console.log(`Carregando dados de ${nomeAba} do banco de dados...`);
            } else {
               
                card.classList.add('card-vazio');
                card.style.display = 'none';
            }
        });
    }
    
    // Verifica resultados quando a página carrega
    verificarResultados();
    
    // Configurar o modal de confirmação de exclusão
    setupDeleteModal();
    
    // Adicionar botões de administração aos filmes se o usuário for admin
    adicionarBotoesAdmin();
    
    // Adiciona event listener para o formulário de busca
    const searchForm = document.querySelector('.search-container form');
    if (searchForm) {
        searchForm.addEventListener('submit', function(e) {
            // Neste ponto você poderia implementar a busca real
            // Por enquanto, apenas vamos garantir que a verificação seja feita após a busca
            setTimeout(verificarResultados, 300);
        });
    }
   
    const abaAtiva = document.querySelector('.tab-active');
    if (abaAtiva) {
        atualizarConteudo(abaAtiva.textContent);
    }
    
    // Função para configurar o modal de confirmação de exclusão
    function setupDeleteModal() {
        const modal = document.getElementById('deleteConfirmModal');
        const cancelBtn = document.getElementById('btnCancelDelete');
        const confirmBtn = document.getElementById('btnConfirmDelete');
        
        cancelBtn.addEventListener('click', function() {
            modal.style.display = 'none';
            filmeIdParaExcluir = null;
        });
        
        confirmBtn.addEventListener('click', function() {
            if (filmeIdParaExcluir) {
                excluirFilme(filmeIdParaExcluir);
            }
            modal.style.display = 'none';
        });
    }
    
    // Confirmar exclusão de filme
    function confirmarExclusao(filmeId) {
        filmeIdParaExcluir = filmeId;
        const modal = document.getElementById('deleteConfirmModal');
        modal.style.display = 'flex';
    }
    
    // Redirecionar para a página de edição com o ID do filme
    function editarFilme(filmeId) {
        window.location.href = `/paginaCadastroFilmes/cadastroFilmes.html?modo=editar&id=${filmeId}`;
    }
    
    // Excluir filme via API
    function excluirFilme(filmeId) {
        fetch('/excluirFilme', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `idFilme=${filmeId}`
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // Remover o card do filme da página
                const filmeCard = document.querySelector(`.movie-card[data-filme-id="${filmeId}"]`);
                if (filmeCard) {
                    filmeCard.remove();
                    // Verificar se ainda existem resultados
                    verificarResultados();
                }
                alert("Filme excluído com sucesso!");
            } else {
                alert(`Erro ao excluir o filme: ${data.message}`);
            }
        })
        .catch(error => {
            console.error('Erro ao excluir filme:', error);
            alert('Ocorreu um erro ao excluir o filme. Tente novamente mais tarde.');
        });
    }
      // Modificar a função que cria os cards de filmes para incluir o data-filme-id
    function criarCardFilme(filme) {
        const card = document.createElement('div');
        card.className = 'movie-card';
        card.setAttribute('data-filme-id', filme.id);
        
        // HTML para a imagem
        let imagemHTML = `
            <div class="movie-image-container">
                <div class="placeholder-image">
        `;
        
        // Se tiver um banner, usar ele, senão usar ícone padrão
        if (filme.bannerBase64) {
            imagemHTML += `<img src="data:image/jpeg;base64,${filme.bannerBase64}" alt="${filme.nome}" />`;
        } else {
            imagemHTML += `<i class="fas fa-image"></i>`;
        }
        
        imagemHTML += `
                </div>
            </div>
        `;
        
        // HTML para as informações do filme
        let infoHTML = `
            <div class="movie-info">
                <h3 class="movie-title">${filme.nome}</h3>
                <p class="movie-description">${filme.sinopse}</p>
                <div class="movie-metadata">
                    <div class="movie-rating">
                        <img src="img/estrela.svg" alt="Estrela" class="estrela-img">
                        ${filme.nota || 'N/A'} | ${new Date().getFullYear()}
                    </div>
                    <div class="movie-year">
                        <span>${filme.ano || 'N/A'}</span>
                    </div>
                </div>
        `;
        
        // Adicionar botões de admin se o usuário for admin
        if (isAdmin()) {
            infoHTML += `
                <div class="admin-controls">
                    <button class="admin-btn edit-btn" onclick="editarFilme(${filme.id})">
                        <i class="fas fa-edit"></i> Editar
                    </button>
                    <button class="admin-btn delete-btn" onclick="confirmarExclusao(${filme.id})">
                        <i class="fas fa-trash"></i> Excluir
                    </button>
                </div>
            `;
        }
        
        infoHTML += '</div>'; // Fechando movie-info
        
        // Montar o card completo
        card.innerHTML = imagemHTML + infoHTML;
        
        return card;
    }
});
