function getQueryParam(param) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(param);
}

function exibirMensagemErro(mensagem) {
    document.querySelector('.filme-info .descricao h1').textContent = 'Erro';
    document.querySelector('.filme-info .descricao .sinopse').textContent = mensagem;
    document.querySelector('.filme-info .descricao .genero-elenco').style.display = 'none';
}

function carregarFilme() {
    const filmeId = getQueryParam('id');
    if (!filmeId) {
        console.error('ID do filme não especificado na URL');
        exibirMensagemErro('Filme não encontrado');
        return;
    }

    console.log("Carregando filme com ID: " + filmeId);
    document.querySelector('.filme-info .descricao .sinopse').textContent = 'Carregando informações do filme...';
    document.querySelector('.filme-info .descricao h1').textContent = 'Carregando...';
    
    fetch('/cadastrarFilme?id=' + filmeId)
        .then(response => {
            if (!response.ok) {
                throw new Error('Erro HTTP ' + response.status);
            }
            return response.json();
        })
        .then(filmes => {
            let filme = null;
            if (Array.isArray(filmes)) {
                filme = filmes.find(f => String(f.id) === String(filmeId));
            } else if (filmes && String(filmes.id) === String(filmeId)) {
                filme = filmes;
            }
            
            if (!filme) {
                console.error('Filme não encontrado nos dados retornados');
                exibirMensagemErro('Filme não encontrado');
                return;
            }
            
            console.log('Dados do filme recebidos:', filme);
            preencherInfoFilme(filme);
        })
        .catch(err => {
            console.error('Erro ao buscar filme:', err);
            exibirMensagemErro('Erro ao carregar o filme: ' + err.message);
        });
}

function preencherInfoFilme(filme) {
    const poster = document.querySelector('.filme-info .poster');
    if (poster && filme.bannerUrl) {
        let url = filme.bannerUrl;
        if (!/^https?:\/\//.test(url) && !url.startsWith('/')) {
            url = '/' + url;
        }
        poster.src = url;
        poster.alt = 'Pôster de ' + (filme.nome || 'filme');
    }
    
    const titulo = document.querySelector('.filme-info .descricao h1');
    if (titulo) {
        titulo.textContent = filme.nome || '';
    }
    
    const sinopse = document.querySelector('.filme-info .descricao .sinopse');
    if (sinopse) {
        sinopse.textContent = filme.sinopse || 'Nenhuma sinopse disponível.';
    }
    
    const genero = document.querySelector('.filme-info .descricao .genero-elenco p');
    if (genero) {
        genero.innerHTML = '<strong>Gênero</strong><br>' + (filme.genero || 'Não classificado');
    }
    
    buscarAtoresDoFilme(filme.id);
    if (filme.destaqueSemana) {
        let label = document.createElement('span');
        label.textContent = 'DESTAQUE DA SEMANA';
        label.style.background = '#FFD700';
        label.style.color = '#222';
        label.style.fontWeight = 'bold';
        label.style.padding = '4px 10px';
        label.style.borderRadius = '8px';
        label.style.marginLeft = '10px';
        if (titulo) titulo.appendChild(label);
    }
    
    document.querySelector('.comentarios-container').style.display = 'block';
}

function buscarAtoresDoFilme(filmeId) {
    if (!filmeId) {
        console.error('ID do filme não especificado');
        return;
    }

    const elencoElement = document.querySelector('.filme-info .descricao .genero-elenco p:nth-child(3)');
    if (!elencoElement) return;

    elencoElement.innerHTML = '<strong>Elenco</strong><br>';
    fetch('/atoresPorFilme?idFilme=' + filmeId)
        .then(response => {
            if (!response.ok) {
                throw new Error('Erro HTTP ' + response.status);
            }
            return response.json();
        })
        .then(atores => {
            console.log("Atores recebidos:", atores);
            if (!atores || !Array.isArray(atores) || atores.length === 0) {
                elencoElement.innerHTML = '<strong>Elenco</strong><br>Não disponível';
                return;
            }

            let elencoHTML = '<strong>Elenco</strong><br>';
            
            if (atores.length > 0) {
                const nomesAtores = atores.map(ator => ator.nome).join(', ');
                elencoHTML += nomesAtores;
            }
            
            elencoElement.innerHTML = elencoHTML;
            console.log('Atores carregados:', atores);
        })
        .catch(err => {
            console.error('Erro ao buscar atores:', err);
            elencoElement.innerHTML = '<strong>Elenco</strong><br>Não disponível';
        });
}

function setupLikeDislikeButtons() {
    const likeButtons = document.querySelectorAll('.btnLike');
    const dislikeButtons = document.querySelectorAll('.btnDislike');
    
    likeButtons.forEach(button => {
        button.addEventListener('click', function() {
            const count = this.nextElementSibling;
            let value = parseInt(count.textContent || '0');
            count.textContent = value + 1;
        });
    });
    
    dislikeButtons.forEach(button => {
        button.addEventListener('click', function() {
            const count = this.nextElementSibling;
            let value = parseInt(count.textContent || '0');
            count.textContent = value + 1;
        });
    });
}

document.addEventListener('DOMContentLoaded', () => {
    carregarFilme();
    setupLikeDislikeButtons();
});
