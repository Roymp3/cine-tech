document.addEventListener('DOMContentLoaded', function() {
    
    const abas = document.querySelectorAll('.tab, .tab-active');
    
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
});
