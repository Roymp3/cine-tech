const imageInput = document.getElementById('bannerFilme');
const divImagem = document.getElementById('inserirFilme');
const uploadIcon = document.getElementById('uploadIcon');
const removerImagem = document.getElementById('removerImagem');

const imageInputFixo = document.getElementById('bannerFilmeFixo');
const divImagemFixo = document.getElementById('inserirFilmeFixo');
const uploadIconFixo = document.getElementById('uploadIconFixo');
const removerImagemFixo = document.getElementById('removerImagemFixo');


imageInput.addEventListener('change', function(event) {
    const file = event.target.files[0];

    console.log(file);
    if(!file) {
        return;
    }

    const reader = new FileReader();

    reader.onload = function(e) {
        divImagem.style.backgroundImage = `url(${e.target.result})`;
        uploadIcon.style.opacity = '0';
        removerImagem.style.display = 'block';
    }

    reader.readAsDataURL(file);
});

removerImagem.addEventListener('click', function() {
    divImagem.style.backgroundImage = '';
    uploadIcon.style.opacity = '1';
    imageInput.value = '';
    removerImagem.style.display = 'none';
});

imageInputFixo.addEventListener('change', function(event) {
    const file = event.target.files[0];

    console.log(file);
    if(!file) {
        return;
    }

    const reader = new FileReader();

    reader.onload = function(e) {

        const img = new Image();
        img.onload = function() {
            divImagemFixo.style.backgroundImage = `url(${e.target.result})`;
            uploadIconFixo.style.opacity = '0';
            removerImagemFixo.style.display = 'block';
        };
        img.src = e.target.result;
    }

    reader.readAsDataURL(file);
});

removerImagemFixo.addEventListener('click', function() {
    divImagemFixo.style.backgroundImage = '';
    uploadIconFixo.style.opacity = '1';
    imageInputFixo.value = '';
    removerImagemFixo.style.display = 'none';
});

const botaoSalvar = document.querySelector('.botao-salvar');

botaoSalvar.addEventListener('click', function() {

    const nomeFilme = document.getElementById('nomeFilme') ? document.getElementById('nomeFilme').value : '';
    const sinopseFilme = document.getElementById('sinopseFilme') ? document.getElementById('sinopseFilme').value : '';
    const generoFilme = document.getElementById('generoFilme') ? document.getElementById('generoFilme').value : '';
    const destaqueSemanaInput = document.getElementById('destaqueSemana');
    const destaqueSemana = destaqueSemanaInput ? destaqueSemanaInput.checked : false;

    console.log(destaqueSemana)

    if (!nomeFilme || !generoFilme) {
        alert('Por favor, preencha todos os campos obrigatórios.');
        return;
    }    // Verificar as imagens apenas se não estivermos no modo de edição ou se novas imagens foram fornecidas
    if (!modoEdicao && !imageInput.files[0]) {
        alert('Por favor, selecione uma imagem para o cartaz do filme.');
        return;
    }
    if (!modoEdicao && !imageInputFixo.files[0]) {
        alert('Por favor, selecione uma imagem para o banner do filme.');
        return;
    }
    
    if (atoresSelecionados.length === 0) {
        alert('Por favor, adicione pelo menos um ator ao filme.');
        return;
    }
      // Mensagem de confirmação baseada no modo
    const mensagemConfirmacao = modoEdicao ? 
        'Você está prestes a atualizar este filme. Deseja continuar?' : 
        'Você está prestes a cadastrar um novo filme. Deseja continuar?';
    
    const confirmarEnvio = confirm(mensagemConfirmacao);
    if (!confirmarEnvio) {
        return;
    }const formData = new FormData();
    formData.append('nome', nomeFilme);
    formData.append('sinopse', sinopseFilme);
    formData.append('genero', generoFilme);
    formData.append('destaqueSemana', destaqueSemana);
    formData.append('banner', imageInput.files[0]);
    formData.append('bannerFixo', imageInputFixo.files[0]);
    
    // Processamos os atores, separando os existentes dos novos
    const atoresExistentes = atoresSelecionados.filter(ator => !ator.novo).map(a => a.id);
    const atoresNovos = atoresSelecionados.filter(ator => ator.novo).map(a => a.nome);
      // Adicionar os atores ao formulário
    formData.append('atoresExistentesIds', JSON.stringify(atoresExistentes));
    formData.append('atoresNovos', JSON.stringify(atoresNovos));

    // Se estiver no modo de edição, adicionar o ID do filme
    if (modoEdicao && filmeIdEmEdicao) {
        formData.append('idFilme', filmeIdEmEdicao);
    }

    // URL baseada no modo (edição ou cadastro)
    const url = modoEdicao ? '/atualizarFilme' : '/cadastrarFilme';
    
    fetch(url, {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (response.ok) {
            document.getElementById('modalSucesso').style.display = 'flex';
            
            if (!modoEdicao) {
                limparFormulario();
            }
            return response.json();
        } else {
            throw new Error('Erro ao ' + (modoEdicao ? 'atualizar' : 'cadastrar') + ' o filme');
        }
    })


});

function limparFormulario() {
    document.getElementById('nomeFilme').value = '';
    document.getElementById('sinopseFilme').value = '';
    document.getElementById('generoFilme').value = '';
    document.getElementById('duracaoFilme').value = '';
    document.getElementById('classificacaoFilme').value = '';
    document.getElementById('dataEstreiaFilme').value = '';
    document.getElementById('destaqueSemana').checked = false;

    divImagem.style.backgroundImage = '';
    uploadIcon.style.opacity = '1';
    imageInput.value = '';
    removerImagem.style.display = 'none';
    
    divImagemFixo.style.backgroundImage = '';
    uploadIconFixo.style.opacity = '1';
    imageInputFixo.value = '';
    removerImagemFixo.style.display = 'none';
    
    // Limpar atores selecionados
    atoresSelecionados = [];
    atualizarAtoresSelecionados();
}

const botaoCancelar = document.querySelector('.botao-cancelar');
botaoCancelar.addEventListener('click', function() {
    if(confirm('Tem certeza que deseja cancelar? Todos os dados não salvos serão perdidos.')) {
        limparFormulario();
    }
});

function fecharModalSucesso() {
    document.getElementById('modalSucesso').style.display = 'none';
}
function fecharModalErro() {
    document.getElementById('modalErro').style.display = 'none';
}

// Sistema de seleção de atores
// Array para armazenar os atores selecionados
let atoresSelecionados = [];
let proximoIdTemporario = -1; // IDs negativos para atores temporários
let searchTimeout; // Para implementar debounce na busca

// Configuração do sistema de atores quando o documento estiver carregado
document.addEventListener('DOMContentLoaded', function() {
    // Inicializar os elementos após o DOM estar carregado
    const atorInput = document.getElementById('atorInput');
    const adicionarAtorBtn = document.getElementById('adicionarAtor');
    const atoresSelecionadosDiv = document.getElementById('atoresSelecionados');
    
    // Criar um elemento para exibir os resultados de pesquisa em tempo real
    const resultadosPesquisaDiv = document.createElement('div');
    resultadosPesquisaDiv.className = 'resultados-pesquisa';
    resultadosPesquisaDiv.style.display = 'none';
    atorInput.parentElement.appendChild(resultadosPesquisaDiv);

    // Ajuste para garantir que o form de atores tenha altura adequada
    const updateAtoresFormHeight = () => {
        const atoresForm = document.querySelector('.atores-form');
        const atoresSelecionadosHeight = atoresSelecionadosDiv.scrollHeight;
        
        // Ajusta a altura mínima conforme necessário
        if (atoresSelecionadosHeight > 0 && atoresSelecionados.length > 0) {
            const newHeight = Math.max(50, atoresSelecionadosHeight + 70); // Base height + altura das tags
            atoresForm.style.minHeight = newHeight + 'px';
        } else {
            atoresForm.style.minHeight = '50px';
        }
    };
    
    // Função para buscar atores conforme digita
    const buscarAtoresEmTempoReal = (termo) => {
        if (!termo || termo.length < 1) {
            resultadosPesquisaDiv.style.display = 'none';
            return;
        }
        
        // Fazer a requisição para o backend para buscar os atores
        // O backend agora usa LOWER() para busca case-insensitive
        fetch(`/buscarAtores?termo=${encodeURIComponent(termo)}`)
            .then(res => res.json())
            .then(data => {
                // Filtrar atores que já estão selecionados (case-insensitive)
                const atoresFiltrados = data.filter(ator => 
                    !atoresSelecionados.some(a => a.id === ator.id)
                );
                
                // Mostrar os resultados em tempo real
                if (atoresFiltrados.length > 0) {
                    resultadosPesquisaDiv.innerHTML = '';
                    resultadosPesquisaDiv.style.display = 'block';
                    
                    atoresFiltrados.slice(0, 10).forEach(ator => {
                        const itemResult = document.createElement('div');
                        itemResult.className = 'resultado-item';
                        itemResult.textContent = ator.nome;
                        itemResult.addEventListener('click', () => {
                            selecionarAtor(ator);
                            atorInput.value = '';
                            resultadosPesquisaDiv.style.display = 'none';
                        });
                        resultadosPesquisaDiv.appendChild(itemResult);
                    });
                } else {
                    resultadosPesquisaDiv.style.display = 'none';
                }
            })
            .catch(err => {
                console.error('Erro ao buscar atores:', err);
                resultadosPesquisaDiv.style.display = 'none';
            });
    };
    
    // Evento para busca em tempo real com pequeno debounce para evitar muitas requisições
    atorInput.addEventListener('input', function() {
        const termo = this.value.trim();
        
        // Limpar timeout anterior para implementar debounce
        clearTimeout(searchTimeout);
        
        // Definir novo timeout (200ms é um bom valor para buscas em tempo real)
        searchTimeout = setTimeout(() => {
            buscarAtoresEmTempoReal(termo);
        }, 200);
    });
    
    // Ocultar resultados quando clicar fora
    document.addEventListener('click', function(e) {
        if (!resultadosPesquisaDiv.contains(e.target) && e.target !== atorInput) {
            resultadosPesquisaDiv.style.display = 'none';
        }
    });

    // Função para selecionar um ator e adicioná-lo à lista
    window.selecionarAtor = function(ator) {
        if (atoresSelecionados.some(a => a.id === ator.id)) {
            return;
        }
        
        atoresSelecionados.push(ator);
        atualizarAtoresSelecionados();
    };

    // Função para remover um ator da lista de selecionados
    window.removerAtor = function(atorId) {
        atoresSelecionados = atoresSelecionados.filter(ator => ator.id !== atorId);
        atualizarAtoresSelecionados();
    };

    // Função para atualizar a exibição dos atores selecionados
    window.atualizarAtoresSelecionados = function() {
        atoresSelecionadosDiv.innerHTML = '';
        
        atoresSelecionados.forEach(ator => {
            const atorTag = document.createElement('div');
            atorTag.classList.add('ator-tag');
            
            const atorNome = document.createElement('span');
            atorNome.classList.add('ator-tag-nome');
            atorNome.textContent = ator.nome;
            
            const removerBtn = document.createElement('span');
            removerBtn.classList.add('ator-tag-remover');
            removerBtn.innerHTML = '×';
            removerBtn.addEventListener('click', () => removerAtor(ator.id));
            
            atorTag.appendChild(atorNome);
            atorTag.appendChild(removerBtn);
            atoresSelecionadosDiv.appendChild(atorTag);
        });
        
        // Atualizar a altura do form de atores
        updateAtoresFormHeight();
    };

    // Evento para adicionar um ator personalizado (que não está na lista)
    adicionarAtorBtn.addEventListener('click', () => {
        const nomeAtor = atorInput.value.trim();
        if (!nomeAtor) return;
        
        // Verificar se já existe um ator com esse nome na lista de selecionados (case-insensitive)
        if (atoresSelecionados.some(a => a.nome.toLowerCase() === nomeAtor.toLowerCase())) {
            atorInput.value = '';
            return;
        }
        
        // Verificar primeiro se o ator já existe no backend (agora usando busca case-insensitive)
        fetch(`/buscarAtorPorNome?nome=${encodeURIComponent(nomeAtor)}`)
            .then(res => res.json())
            .then(ator => {
                if (ator && ator.id) {
                    // O ator existe no backend
                    selecionarAtor(ator);
                } else {
                    // Criar um ator temporário
                    const novoAtor = {
                        id: proximoIdTemporario--,
                        nome: nomeAtor,
                        novo: true // Marcar como novo para salvar no backend depois
                    };
                    selecionarAtor(novoAtor);
                }
                atorInput.value = '';
                resultadosPesquisaDiv.style.display = 'none';
            })
            .catch(err => {
                console.error('Erro ao verificar ator:', err);
                // Em caso de erro, criar um ator temporário
                const novoAtor = {
                    id: proximoIdTemporario--,
                    nome: nomeAtor,
                    novo: true
                };
                selecionarAtor(novoAtor);
                atorInput.value = '';
                resultadosPesquisaDiv.style.display = 'none';
            });
    });

    // Permitir que o usuário pressione Enter para adicionar um ator
    atorInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            // Verificar se há resultados visíveis
            if (resultadosPesquisaDiv.style.display === 'block' && resultadosPesquisaDiv.children.length > 0) {
                // Selecionar o primeiro resultado
                const firstResult = resultadosPesquisaDiv.children[0];
                firstResult.click();
            } else {
                // Adicionar manualmente
                adicionarAtorBtn.click();
            }
        }
    });
    
    // Navegação pelos resultados com teclado
    atorInput.addEventListener('keydown', function(e) {
        if (resultadosPesquisaDiv.style.display === 'block') {
            const results = resultadosPesquisaDiv.querySelectorAll('.resultado-item');
            let activeIndex = Array.from(results).findIndex(item => item.classList.contains('active'));
            
            switch(e.key) {
                case 'ArrowDown':
                    e.preventDefault();
                    if (activeIndex < results.length - 1) {
                        if (activeIndex >= 0) results[activeIndex].classList.remove('active');
                        results[activeIndex + 1].classList.add('active');
                    } else if (activeIndex === -1 && results.length > 0) {
                        results[0].classList.add('active');
                    }
                    break;
                case 'ArrowUp':
                    e.preventDefault();
                    if (activeIndex > 0) {
                        results[activeIndex].classList.remove('active');
                        results[activeIndex - 1].classList.add('active');
                    }
                    break;
            }
        }
    });
    
    // Ajustar foco para melhor experiência do usuário
    atorInput.addEventListener('focus', function() {
        const atoresForm = document.querySelector('.atores-form');
        atoresForm.classList.add('focused');
        
        // Mostrar resultados novamente se houver texto no campo
        if (this.value.trim().length > 0) {
            buscarAtoresEmTempoReal(this.value.trim());
        }
    });
    
    atorInput.addEventListener('blur', function() {
        setTimeout(() => {
            const atoresForm = document.querySelector('.atores-form');
            atoresForm.classList.remove('focused');
        }, 200);
    });
});

// Variável para controlar o modo de edição
let modoEdicao = false;
let filmeIdEmEdicao = null;

// Função para verificar se é modo de edição e carregar dados
function verificarModoEdicao() {
    const urlParams = new URLSearchParams(window.location.search);
    const modo = urlParams.get('modo');
    const id = urlParams.get('id');
    
    if (modo === 'editar' && id) {
        modoEdicao = true;
        filmeIdEmEdicao = id;
        carregarDadosFilme(id);
        
        // Atualizar título da página e do botão
        document.querySelector('.main-title').textContent = 'Edição de Filme';
        document.querySelector('#btnEnviar').textContent = 'Atualizar Filme';
    }
}

// Carregar dados do filme para edição
function carregarDadosFilme(id) {
    fetch(`/buscarFilmePorId?idFilme=${id}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Erro ao buscar dados do filme');
            }
            return response.json();
        })
        .then(data => {
            if (data && data.filme) {
                preencherFormulario(data.filme, data.atores);
            }
        })
        .catch(error => {
            console.error('Erro ao carregar dados do filme:', error);
            alert('Não foi possível carregar os dados do filme para edição.');
        });
}

// Preencher o formulário com os dados do filme
function preencherFormulario(filme, atores) {
    document.getElementById('nomeFilme').value = filme.nome || '';
    document.getElementById('sinopseFilme').value = filme.sinopse || '';
    document.getElementById('generoFilme').value = filme.genero || '';
    document.getElementById('destaqueSemana').checked = filme.destaqueSemana || false;
    
    // Preencher imagens se disponíveis
    if (filme.bannerBase64) {
        divImagem.style.backgroundImage = `url(data:image/jpeg;base64,${filme.bannerBase64})`;
        uploadIcon.style.opacity = '0';
        removerImagem.style.display = 'block';
    }
    
    if (filme.bannerFixoBase64) {
        divImagemFixo.style.backgroundImage = `url(data:image/jpeg;base64,${filme.bannerFixoBase64})`;
        uploadIconFixo.style.opacity = '0';
        removerImagemFixo.style.display = 'block';
    }
    
    // Limpar atores selecionados e adicionar os do filme
    atoresSelecionados = [];
    if (atores && atores.length > 0) {
        atores.forEach(ator => {
            atoresSelecionados.push({
                id: ator.idAtor,
                nome: ator.nmAtor,
                novo: false
            });
        });
    }
    atualizarAtoresSelecionados();
}

// Executar ao carregar a página
document.addEventListener('DOMContentLoaded', function() {
    verificarModoEdicao();
});
