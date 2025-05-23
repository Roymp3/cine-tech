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
        exibirMensagemErro('Filme não encontrado. ID não especificado na URL.');
        return;
    }

    console.log("Carregando filme com ID: " + filmeId);
    document.querySelector('.filme-info .descricao .sinopse').textContent = 'Carregando informações do filme...';
    document.querySelector('.filme-info .descricao h1').textContent = 'Carregando...';
    
    // URL atualizada para buscar filme por ID
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
                exibirMensagemErro('Filme não encontrado nos dados retornados');
                return;
            }
            
            console.log('Dados do filme recebidos:', filme);
            preencherInfoFilme(filme);
        })
        .catch(err => {
            console.error('Erro ao buscar filme:', err);
            exibirMensagemErro('Erro ao carregar o filme: ' + err.message);
            
            // Alternativa caso a primeira tentativa falhe - tentar outra rota
            console.log('Tentando rota alternativa...');
            fetch('/filme?id=' + filmeId)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Erro HTTP ' + response.status);
                    }
                    return response.json();
                })
                .then(filme => {
                    if (!filme) {
                        throw new Error('Filme não encontrado');
                    }
                    console.log('Dados do filme recebidos pela rota alternativa:', filme);
                    preencherInfoFilme(filme);
                })
                .catch(secondErr => {
                    console.error('Erro na segunda tentativa:', secondErr);
                    // Mantém a mensagem de erro original
                });
        });
}

function preencherInfoFilme(filme) {
    // Preencher o título
    document.querySelector('.filme-info .descricao h1').textContent = filme.nome;
    
    // Preencher a sinopse
    document.querySelector('.filme-info .descricao .sinopse').textContent = filme.sinopse;
    
    // Preencher o gênero
    const generoElement = document.querySelector('.genero-elenco p:first-child');
    if (generoElement) {
        generoElement.innerHTML = `<strong>Gênero</strong><br>${filme.genero || 'Não especificado'}`;
    }
    
    // Iniciar carregamento dos atores
    buscarAtoresDoFilme(filme.id);
    
    // Atualizar a imagem do poster se disponível
    const imgElement = document.querySelector('.filme-info .poster');
    if (imgElement) {
        if (filme.bannerEncoded) {
            imgElement.src = `data:image/jpeg;base64,${filme.bannerEncoded}`;
            imgElement.alt = `Poster de ${filme.nome}`;
        } else if (filme.bannerUrl) {
            imgElement.src = filme.bannerUrl;
            imgElement.alt = `Poster de ${filme.nome}`;
            // fallback para url absoluta caso necessário
            imgElement.onerror = function() {
                imgElement.src = `/cadastrarFilme?imagem=banner&id=${filme.id}`;
            };
        } else {
            // mantém imagem padrão
            imgElement.alt = `Poster de ${filme.nome}`;
        }
    }
    
    // Atualizar o título da página
    document.title = `${filme.nome} - CineTech`;
}

function buscarAtoresDoFilme(filmeId) {
    if (!filmeId) {
        console.error('ID do filme não especificado');
        return;
    }
    
    console.log('Buscando atores para o filme ID:', filmeId);

    // Selecionar o elemento correto para o elenco (o segundo <p> após o <hr>)
    const elencoElement = document.querySelector('.genero-elenco p:last-child');
    if (!elencoElement) {
        console.error('Elemento de elenco não encontrado');
        return;
    }

    elencoElement.innerHTML = '<strong>Elenco</strong><br>';
    fetch('/atoresPorFilme?idFilme=' + filmeId)
        .then(response => {
            if (!response.ok) {
                throw new Error('Erro HTTP ' + response.status);
            }
            return response.json();
        })        .then(atores => {
            console.log("Atores recebidos:", atores);
            if (!atores || !Array.isArray(atores) || atores.length === 0) {
                elencoElement.innerHTML = '<strong>Elenco</strong><br>Não disponível';
                console.log('Nenhum ator encontrado para o filme');
                return;
            }

            let elencoHTML = '<strong>Elenco</strong><br>';
            
            if (atores.length > 0) {
                // Verificar propriedades disponíveis
                console.log('Primeiro ator:', atores[0]);
                // Usar o campo 'nome' ou 'nmAtor' dependendo de qual estiver disponível
                const nomesAtores = atores.map(ator => {
                    if (ator.nome) {
                        return ator.nome;
                    } else if (ator.nmAtor) {
                        return ator.nmAtor; 
                    } else {
                        console.warn('Ator sem nome:', ator);
                        return 'Ator Desconhecido';
                    }
                }).join(', ');
                elencoHTML += nomesAtores;
            }
            
            elencoElement.innerHTML = elencoHTML;
            console.log('Atores carregados com sucesso!');
        })
        .catch(err => {
            console.error('Erro ao buscar atores:', err);
            elencoElement.innerHTML = '<strong>Elenco</strong><br>Não disponível';
            
            // Tentar uma rota alternativa
            console.log('Tentando rota alternativa para buscar atores...');
            fetch('/buscarAtores?idFilme=' + filmeId)
                .then(response => response.ok ? response.json() : Promise.reject('Erro na resposta'))
                .then(atores => {
                    if (atores && atores.length > 0) {
                        let elencoHTML = '<strong>Elenco</strong><br>';
                        const nomesAtores = atores.map(ator => ator.nome || ator.nmAtor || 'Desconhecido').join(', ');
                        elencoHTML += nomesAtores;
                        elencoElement.innerHTML = elencoHTML;
                        console.log('Atores carregados pela rota alternativa!');
                    }
                })
                .catch(err2 => console.error('Erro na segunda tentativa:', err2));
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
