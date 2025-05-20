document.addEventListener('DOMContentLoaded', function() {
    
    const abas = document.querySelectorAll('.tab, .tab-active');
    
  
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
    
   
    const abaAtiva = document.querySelector('.tab-active');
    if (abaAtiva) {
        atualizarConteudo(abaAtiva.textContent);
    }
});