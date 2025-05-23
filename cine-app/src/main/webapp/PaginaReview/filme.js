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
                carregarComentarios(filmeId);
            verificarStatusLogin();
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
                    carregarComentarios(filmeId);
                    verificarStatusLogin();
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

// Variáveis globais para armazenar informações do usuário logado
let usuarioLogado = false;
let nomeUsuario = '';
let emailUsuario = '';

// Verifica se o usuário está logado
function verificarStatusLogin() {
    console.log('Verificando status de login...');
    fetch('/comentarios?acao=verificarUsuario')
        .then(response => {
            console.log('Resposta da verificação de login:', response.status);
            if (!response.ok) {
                throw new Error(`Erro ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log('Dados do status de login:', data);
            usuarioLogado = data.logado;
            
            if (usuarioLogado) {
                nomeUsuario = data.nome;
                emailUsuario = data.email;
                console.log('Usuário logado:', nomeUsuario, emailUsuario);
                
                // Atualiza a seção de comentário novo para mostrar o nome do usuário
                const nomeElement = document.querySelector('.comentario-novo .nome');
                if (nomeElement) {
                    nomeElement.textContent = nomeUsuario;
                }
                
                // Mostra o formulário de comentário
                document.querySelector('.comentario-novo').style.display = 'block';
            } else {
                console.log('Usuário não está logado');
                
                // Atualiza a seção de comentário novo para mostrar mensagem de login
                const comentarioNovo = document.querySelector('.comentario-novo');
                if (comentarioNovo) {
                    comentarioNovo.innerHTML = `
                        <h2>Deixe seu comentário</h2>
                        <div class="comentario-card">
                            <p>Para deixar um comentário, você precisa estar <a href="/PaginaDeLogin/PaginaDeLogin.html">logado</a>.</p>
                        </div>
                    `;
                }
            }
        })
        .catch(error => {
            console.error('Erro ao verificar status de login:', error);
            // Em caso de erro, assume que o usuário não está logado
            usuarioLogado = false;
            document.querySelector('.comentario-novo').style.display = 'none';
        });
}

// Função para carregar os comentários do filme
function carregarComentarios(filmeId) {
    console.log('Carregando comentários para o filme ID:', filmeId);
    
    fetch(`/comentarios?acao=listar&idFilme=${filmeId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Erro ao carregar comentários');
            }
            return response.json();
        })
        .then(comentarios => {
            console.log('Comentários recebidos:', comentarios);
            exibirComentarios(comentarios);
        })
        .catch(error => {
            console.error('Erro ao buscar comentários:', error);
            const reviewsSection = document.querySelector('.reviews');
            if (reviewsSection) {
                reviewsSection.innerHTML = `
                    <h2>Reviews</h2>
                    <div class="comentario-card">
                        <p>Não foi possível carregar os comentários. Tente novamente mais tarde.</p>
                    </div>
                `;
            }
        });
}

// Função para exibir comentários na interface
function exibirComentarios(comentarios) {
    const reviewsSection = document.querySelector('.reviews');
    if (!reviewsSection) return;
    
    if (!comentarios || comentarios.length === 0) {
        reviewsSection.innerHTML = `
            <h2>Reviews</h2>
            <div class="comentario-card">
                <p>Este filme ainda não possui comentários. Seja o primeiro a comentar!</p>
            </div>
        `;
        return;
    }
    
    let html = '<h2>Reviews</h2>';
    
    comentarios.forEach(comentario => {
        const data = new Date(comentario.dataComentario).toLocaleDateString('pt-BR');
        const stars = renderizarEstrelas(comentario.avaliacao);
        
        // Verificar se o comentário é do usuário atual para mostrar opção de excluir
        const isOwner = usuarioLogado && emailUsuario === comentario.emailUsuario;
        const menuOptions = isOwner ? 
            `<div class="menu-options" onclick="excluirComentario(${comentario.id})">⋯</div>` : 
            '<span class="menu-icon">⋯</span>';
        
        html += `
            <div class="comentario-card" id="comentario-${comentario.id}">
                <div class="usuario-info">
                    <img src="../assets/images/imgfilme/user.svg" class="avatar">
                    <span class="nome">${comentario.nomeUsuario}</span>
                    <span class="estrelas">${stars}</span>
                    <span class="data">${data}</span>
                    ${menuOptions}
                </div>
                <p class="texto">${comentario.texto}</p>
                <div class="acoes">
                    <img src="../assets/images/imgfilme/Like.svg" class="com-margem btnLike" 
                         onclick="reagirComentario(${comentario.id}, true)" alt="Like">
                    <span class="likeCount">${comentario.likes || 0}</span>
                    <img src="../assets/images/imgfilme/dislike.svg" class="com-margem btnDislike" 
                         onclick="reagirComentario(${comentario.id}, false)" alt="Dislike">
                    <span class="dislikeCount">${comentario.dislikes || 0}</span>
                </div>
            </div>
        `;
    });
    
    reviewsSection.innerHTML = html;
}

// Renderiza as estrelas baseadas na avaliação (1-5)
function renderizarEstrelas(avaliacao) {
    // Limita a avaliação entre 1 e 5
    const rating = Math.min(5, Math.max(1, avaliacao || 0));
    
    // Retorna string com estrelas preenchidas baseadas na avaliação
    let stars = '';
    for (let i = 1; i <= 5; i++) {
        stars += i <= rating ? '★' : '☆';
    }
    return stars;
}

// Adicionar um novo comentário
function adicionarComentario() {
    console.log('Iniciando adicionarComentario()...');
    if (!usuarioLogado) {
        alert('Você precisa estar logado para comentar!');
        window.location.href = '/PaginaDeLogin/PaginaDeLogin.html';
        return;
    }
    
    const filmeId = getQueryParam('id');
    if (!filmeId) {
        console.error('ID do filme não especificado');
        return;
    }
    console.log('ID do filme:', filmeId);
    
    const comentarioInput = document.querySelector('.comentario-input input');
    const texto = comentarioInput.value.trim();
    
    if (!texto) {
        alert('Por favor, escreva um comentário antes de enviar.');
        return;
    }
    
    // Recuperar a avaliação das estrelas (implementar seleção de estrelas)
    // Por enquanto usaremos um valor fixo para teste
    const avaliacao = document.querySelector('.estrela-selecionada') ? 
                     document.querySelectorAll('.estrela-selecionada').length : 5;
    
    console.log('Dados do comentário: texto=', texto, 'avaliacao=', avaliacao);
    
    const comentario = {
        idFilme: filmeId,
        texto: texto,
        avaliacao: avaliacao
    };
    
    console.log('Enviando request para adicionar comentário:', JSON.stringify(comentario));
    
    fetch('/comentarios?acao=adicionar', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(comentario)
    })
    .then(response => {
        console.log('Resposta recebida:', response.status);
        if (!response.ok) {
            return response.text().then(text => {
                console.error('Erro detalhado:', text);
                throw new Error(`Erro ${response.status}: ${text}`);
            });
        }
        return response.json();
    })
    .then(data => {
        console.log('Comentário adicionado com sucesso:', data);
        comentarioInput.value = ''; // Limpar o campo de comentário
        carregarComentarios(filmeId); // Recarregar os comentários
    })
    .catch(error => {
        console.error('Erro completo:', error);
        alert(`Erro ao adicionar comentário: ${error.message}. Tente novamente.`);
    });
}

// Reagir a um comentário (like/dislike)
function reagirComentario(comentarioId, isLike) {
    if (!usuarioLogado) {
        alert('Você precisa estar logado para reagir a um comentário!');
        return;
    }
    
    fetch('/comentarios?acao=reagir', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            idComentario: comentarioId,
            like: isLike
        })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Erro ao reagir ao comentário');
        }
        return response.json();
    })
    .then(data => {
        console.log('Reação registrada:', data);
        
        // Atualizar contadores na interface
        const comentarioElement = document.getElementById(`comentario-${comentarioId}`);
        if (comentarioElement) {
            const likeCount = comentarioElement.querySelector('.likeCount');
            const dislikeCount = comentarioElement.querySelector('.dislikeCount');
            
            if (isLike) {
                likeCount.textContent = parseInt(likeCount.textContent || '0') + 1;
            } else {
                dislikeCount.textContent = parseInt(dislikeCount.textContent || '0') + 1;
            }
        }
    })
    .catch(error => {
        console.error('Erro:', error);
        alert('Erro ao registrar reação. Tente novamente.');
    });
}

// Excluir comentário
function excluirComentario(comentarioId) {
    if (!usuarioLogado) {
        alert('Você precisa estar logado para excluir um comentário!');
        return;
    }
    
    if (!confirm('Tem certeza que deseja excluir este comentário?')) {
        return;
    }
    
    fetch('/comentarios?acao=excluir', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            idComentario: comentarioId
        })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Erro ao excluir comentário');
        }
        return response.json();
    })
    .then(data => {
        console.log('Comentário excluído:', data);
        
        // Remover comentário da interface
        const comentarioElement = document.getElementById(`comentario-${comentarioId}`);
        if (comentarioElement) {
            comentarioElement.remove();
        }
        
        // Se não houver mais comentários, mostrar mensagem
        const comentarios = document.querySelectorAll('.comentario-card');
        if (comentarios.length <= 1) { // O formulário de novo comentário também é um comentario-card
            const reviewsSection = document.querySelector('.reviews');
            if (reviewsSection) {
                reviewsSection.innerHTML = `
                    <h2>Reviews</h2>
                    <div class="comentario-card">
                        <p>Este filme ainda não possui comentários. Seja o primeiro a comentar!</p>
                    </div>
                `;
            }
        }
    })
    .catch(error => {
        console.error('Erro:', error);
        alert('Erro ao excluir comentário. Tente novamente.');
    });
}

// Função para configurar sistema de rating com estrelas
function setupStarRating() {
    const estrelas = document.querySelectorAll('.comentario-novo .estrelas');
    
    if (estrelas.length === 0) return;
    
    // Substituir o texto das estrelas por elementos interativos
    estrelas.forEach(element => {
        element.innerHTML = `
            <span class="estrela" data-valor="1">☆</span>
            <span class="estrela" data-valor="2">☆</span>
            <span class="estrela" data-valor="3">☆</span>
            <span class="estrela" data-valor="4">☆</span>
            <span class="estrela" data-valor="5">☆</span>
        `;
        
        // Adicionar eventos de clique para cada estrela
        const estrelasIndividuais = element.querySelectorAll('.estrela');
        estrelasIndividuais.forEach(estrela => {
            estrela.addEventListener('click', function() {
                const valor = parseInt(this.getAttribute('data-valor'));
                
                // Resetar todas as estrelas
                estrelasIndividuais.forEach(e => {
                    e.textContent = '☆';
                    e.classList.remove('estrela-selecionada');
                });
                
                // Preencher até a estrela clicada
                for (let i = 0; i < estrelasIndividuais.length; i++) {
                    const e = estrelasIndividuais[i];
                    if (i < valor) {
                        e.textContent = '★';
                        e.classList.add('estrela-selecionada');
                    }
                }
            });
        });
    });
}

// Configurar eventos para o botão de enviar comentário
function setupBotaoEnviarComentario() {
    const btnEnviar = document.querySelector('.comentario-input img');
    
    if (btnEnviar) {
        btnEnviar.addEventListener('click', adicionarComentario);
    }
    
    // Também permitir envio ao pressionar Enter no input
    const comentarioInput = document.querySelector('.comentario-input input');
    if (comentarioInput) {
        comentarioInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                adicionarComentario();
            }
        });
    }
}

document.addEventListener('DOMContentLoaded', () => {
    carregarFilme();
    setupStarRating();
    setupBotaoEnviarComentario();
});
