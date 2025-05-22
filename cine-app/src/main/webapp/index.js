let filmesExibidos = new Set();

document.addEventListener('DOMContentLoaded', () => {
    filmesExibidos.clear();
    
    inicializarMenuLateral();
    carregarDestaqueSemana();
    
    Promise.resolve()
        .then(() => {
            return carregarFilmesMelhoresAvaliados();
        })
        .then(() => {
            return carregarFilmesPorGenero('Aventura', 'carrossel-aventura');
        })
        .then(() => {
            return carregarFilmesPorGenero('Comédia', 'carrossel-comedia');
        })
        .then(() => {
            return carregarFilmesPorGenero('Ação', 'carrossel-acao');
        })
        .catch(error => console.error('Erro ao carregar carrosséis:', error));
});

function carregarDestaqueSemana() {
    fetch('/cadastrarFilme')
        .then(response => {
            if (!response.ok) {
                throw new Error('Erro ao buscar filmes: ' + response.statusText);
            }
            return response.json();
        })
        .then(filmes => {
            if (!filmes || filmes.length === 0) return;
            const destaque = filmes.find(f => f.id === 8) || filmes[0];
            const destaqueContainer = document.getElementById('destaque-semanal');
            if (destaqueContainer) {
                destaqueContainer.innerHTML = '';
                destaqueContainer.appendChild(criarCardDestaque(destaque));
                filmesExibidos.add(destaque.id);
            }
        })
        .catch(error => {
            console.error('Erro ao carregar destaque da semana:', error);
        });
}

function inicializarMenuLateral() {
    preCarregarMenu();
}

function preCarregarMenu() {
    fetch('./menuLateral/menuLateral.html')
        .then(response => {
            if (!response.ok) {
                throw new Error('Não foi possível carregar o menu lateral');
            }
            return response.text();
        })
        .catch(error => {
            console.error('Erro ao carregar menu lateral:', error);
        });
}

function abrirMenuLateral() {
    const menu = document.getElementById('menuLateral');
    
    if (menu.classList.contains('active')) {
        return fecharMenuLateral();
    } else {
        menu.classList.add('active');
        fetch('./menuLateral/menuLateral.html')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Não foi possível carregar o menu lateral');
                }
                return response.text();
            })
            .then(data => {
                menu.innerHTML = data;
                const overlay = document.createElement('div');
                overlay.className = 'menu-overlay';
                overlay.style.position = 'fixed';
                overlay.style.top = '0';
                overlay.style.left = '0';
                overlay.style.width = '100%';
                overlay.style.height = '100%';
                overlay.style.background = 'rgba(0, 0, 0, 0.5)';
                overlay.style.zIndex = '999';
                overlay.addEventListener('click', fecharMenuLateral);
                menu.insertBefore(overlay, menu.firstChild);
                const botaoFechar = document.getElementById('btnFecharMenu');
                if (botaoFechar) {
                    botaoFechar.addEventListener('click', fecharMenuLateral);
                }
                configurarLinksMenu();
                const logoutBtn = document.getElementById('logout');
                if (logoutBtn) {
                    logoutBtn.addEventListener('click', (e) => {
                        e.preventDefault();
                        alert('Logout realizado com sucesso!');
                        fecharMenuLateral();
                    });
                }
            })
            .catch(error => {
                console.error('Erro ao carregar menu lateral:', error);
                menu.classList.remove('active');
            });
    }
}

function configurarLinksMenu() {
    const links = document.querySelectorAll('.menu-lateral a');
    links.forEach(link => {
        if (link.id !== 'logout') {
            link.addEventListener('click', () => {
            });
        }
    });
}

function fecharMenuLateral() {
    const menu = document.getElementById('menuLateral');
    const menuLateral = document.querySelector('.menu-lateral');
    const overlay = document.querySelector('.menu-overlay');
    if (menuLateral) {
        menuLateral.classList.add('closing');
    }
    if (overlay) {
        overlay.classList.add('closing');
    }
    setTimeout(() => {
        menu.classList.remove('active');
        menu.innerHTML = '';
    }, 300); 
}

function inicializarCarrosselMelhoresAvaliados() {
    const carrossel = document.querySelector('.carrosselMelhoresAvaliados');
    if (!carrossel) return;
    const track = carrossel.querySelector('.carrossel-track');
    const botaoEsquerda = carrossel.querySelector('.carrossel-seta-esquerda');
    const botaoDireita = carrossel.querySelector('.carrossel-seta-direita');
    const slides = Array.from(track.querySelectorAll('.cardMelhoresAvaliados'));
    const container = carrossel.querySelector('.carrossel-container');
    if (!track || !botaoEsquerda || !botaoDireita || slides.length === 0) return;
    const slideWidth = slides[0].getBoundingClientRect().width;
    const slideMargin = 20; 
    const slidesToShow = 2;
    const slidesToScroll = 1;
    let currentIndex = 0;
    if (slides.length <= slidesToShow) {
        botaoEsquerda.style.display = 'none';
        botaoDireita.style.display = 'none';
    }
    configureSlides();
    window.addEventListener('resize', () => {
        const newSlideWidth = slides[0].getBoundingClientRect().width;
        configureSlides(newSlideWidth);
    });
    botaoEsquerda.addEventListener('click', () => {
        moveSlide('anterior');
    });
    botaoDireita.addEventListener('click', () => {
        moveSlide('proximo');
    });
    function moveSlide(direcao) {
        if (direcao === 'proximo') {
            if (currentIndex < slides.length - slidesToShow) {
                currentIndex += slidesToScroll;
            } else {
                currentIndex = 0; 
            }
        } else {
            if (currentIndex > 0) {
                currentIndex -= slidesToScroll;
            } else {
                currentIndex = slides.length - slidesToShow; 
            }
        }
        configureSlides();
    }
    function configureSlides(customSlideWidth) {
        const currentSlideWidth = customSlideWidth || slideWidth;
        const offset = currentIndex * (currentSlideWidth + slideMargin);
        track.style.transform = `translateX(-${offset}px)`;
        slides.forEach((slide, index) => {
            slide.style.opacity = '1';
            slide.style.visibility = 'visible';
            if (index < currentIndex || index > currentIndex + slidesToShow) {
                slide.style.opacity = '0.5';
            }
        });
        if (currentIndex === 0) {
            botaoEsquerda.style.opacity = '0.5';
        } else {
            botaoEsquerda.style.opacity = '1';
        }
        if (currentIndex >= slides.length - slidesToShow) {
            botaoDireita.style.opacity = '0.5';
        } else {
            botaoDireita.style.opacity = '1';
        }
    }
}

function inicializarCarrosselCategorias() {
    const carrosseis = document.querySelectorAll('.carrossel-categoria');
    carrosseis.forEach(carrossel => {
        const track = carrossel.querySelector('.carrossel-track');
        const botaoEsquerda = carrossel.querySelector('.carrossel-seta-esquerda');
        const botaoDireita = carrossel.querySelector('.carrossel-seta-direita');
        const slides = Array.from(track.querySelectorAll('.card-categoria'));
        const container = carrossel.querySelector('.carrossel-container');
        if (!track || !botaoEsquerda || !botaoDireita || slides.length === 0) return;
        const slideWidth = slides[0].getBoundingClientRect().width;
        const slideMargin = 20; 
        const slidesToShow = 5;
        const slidesToScroll = 1;
        let currentIndex = 0;
        if (slides.length <= slidesToShow) {
            botaoEsquerda.style.display = 'none';
            botaoDireita.style.display = 'none';
        }
        configureSlides();
        window.addEventListener('resize', () => {
            const newSlideWidth = slides[0].getBoundingClientRect().width;
            configureSlides(newSlideWidth);
        });
        botaoEsquerda.addEventListener('click', () => {
            moveSlide('anterior');
        });
        botaoDireita.addEventListener('click', () => {
            moveSlide('proximo');
        });
        function moveSlide(direcao) {
            if (direcao === 'proximo') {
                if (currentIndex < slides.length - slidesToShow) {
                    currentIndex += slidesToScroll;
                } else {
                    currentIndex = 0;
                }
            } else {
                if (currentIndex > 0) {
                    currentIndex -= slidesToScroll;
                } else {
                    currentIndex = slides.length - slidesToShow;
                }
            }
            configureSlides();
        }
        function configureSlides(customSlideWidth) {
            const currentSlideWidth = customSlideWidth || slideWidth;
            const gapSize = 30;
            const offset = currentIndex * (currentSlideWidth + gapSize);
            track.style.transform = `translateX(-${offset}px)`;
            slides.forEach((slide, index) => {
                slide.style.opacity = '1';
                if (index < currentIndex || index >= currentIndex + slidesToShow) {
                    slide.style.opacity = '0.7';
                }
            });
            if (currentIndex === 0) {
                botaoEsquerda.style.opacity = '0.5';
            } else {
                botaoEsquerda.style.opacity = '1';
            }
            if (currentIndex >= slides.length - slidesToShow) {
                botaoDireita.style.opacity = '0.5';
            } else {
                botaoDireita.style.opacity = '1';
            }
        }
    });
}



// Card de destaque da semana
function criarCardDestaque(filme) {
    const card = document.createElement('div');
    card.className = 'card-destaque-semanal';

    const imgSrc = filme.bannerFixoUrl || './assets/images/paginaInical/CartazHomemdeFerro.png';
    
    const img = document.createElement('img');
    img.src = imgSrc;
    img.alt = filme.nome;
    img.onerror = function() {
        this.src = './assets/images/paginaInical/CartazHomemdeFerro.png';
    };
    card.appendChild(img);

    const content = document.createElement('div');
    content.className = 'destaque-conteudo';

    const label = document.createElement('span');
    label.className = 'destaque-label';
    label.textContent = 'DESTAQUE DA SEMANA';
    content.appendChild(label);

    const nome = document.createElement('h1');
    nome.className = 'destaque-titulo';
    nome.textContent = filme.nome;
    content.appendChild(nome);

    const sinopse = document.createElement('p');
    sinopse.className = 'destaque-sinopse';
    sinopse.textContent = filme.sinopse || 'Sem sinopse disponível.';
    content.appendChild(sinopse);
    
    const rating = document.createElement('div');
    rating.style.display = 'flex';
    rating.style.alignItems = 'center';
    rating.style.marginTop = '10px';
    
    const star = document.createElement('span');
    star.textContent = '★';
    star.style.color = '#FFD700';
    star.style.marginRight = '5px';
    
    const ratingText = document.createElement('span');
    ratingText.textContent = `${filme.nota || '9.0'} | ${filme.ano || '2020'}`;
    ratingText.style.fontSize = '0.9rem';
    
    rating.appendChild(star);
    rating.appendChild(ratingText);
    content.appendChild(rating);

    card.appendChild(content);
    return card;
}

// Card completo com imagem, nome e sinopse
function criarCardCompleto(filme) {
    const card = document.createElement('div');
    card.className = 'card-filme-completo';
    const imgSrc = filme.bannerUrl || './assets/images/paginaInical/CartazHomemdeFerro.png';
    const img = document.createElement('img');
    img.src = imgSrc;
    img.alt = filme.nome;
    img.onerror = function() {
        this.src = './assets/images/paginaInical/CartazHomemdeFerro.png';
    };
    const nome = document.createElement('h2');
    nome.textContent = filme.nome;
    const sinopse = document.createElement('p');
    sinopse.textContent = filme.sinopse || 'Sem sinopse disponível.';
    card.appendChild(img);
    card.appendChild(nome);
    card.appendChild(sinopse);
    return card;
}

function carregarFilmesMelhoresAvaliados() {
    return fetch('/cadastrarFilme')
        .then(response => {
            if (!response.ok) {
                throw new Error('Erro ao buscar filmes: ' + response.statusText);
            }
            return response.json();
        })
        .then(filmes => {
            const melhoresFilmes = filmes.filter(filme => !filmesExibidos.has(filme.id));
            
            const track = document.querySelector('.carrosselMelhoresAvaliados .carrossel-track');
            if (!track) {
                console.error("Elemento track não encontrado para melhores avaliados");
                return Promise.resolve();
            }
            
            track.innerHTML = '';
            
            const maxFilmes = Math.min(melhoresFilmes.length, 10);
            for (let i = 0; i < maxFilmes; i++) {
                const filme = melhoresFilmes[i];
                if (filme && filme.id) {
                    filmesExibidos.add(filme.id);
                    const card = criarCardMelhoresAvaliados(filme);
                    track.appendChild(card);
                }
            }
            
            if (melhoresFilmes.length === 0) {
                const card = criarCardMelhoresAvaliadosPlaceholder();
                track.appendChild(card);
            }
            
            return new Promise(resolve => {
                setTimeout(() => {
                    inicializarCarrosselMelhoresAvaliados();
                    resolve();
                }, 100);
            });
        })
        .catch(error => {
            console.error('Erro ao carregar filmes:', error);
            
            const track = document.querySelector('.carrosselMelhoresAvaliados .carrossel-track');
            if (track) {
                track.innerHTML = '';
                const card = criarCardMelhoresAvaliadosPlaceholder();
                track.appendChild(card);
                
                return new Promise(resolve => {
                    setTimeout(() => {
                        inicializarCarrosselMelhoresAvaliados();
                        resolve();
                    }, 100);
                });
            }
            return Promise.resolve();
        });
}

function carregarFilmesPorGenero(genero, carrosselId) {
    return fetch(`/cadastrarFilme?genero=${encodeURIComponent(genero)}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Erro ao buscar filmes: ' + response.statusText);
            }
            return response.json();
        })
        .then(filmes => {
            if (filmes.length === 0) {
                return fetch('/cadastrarFilme')
                    .then(response => {
                        if (!response.ok) {
                            throw new Error('Erro ao buscar todos os filmes: ' + response.statusText);
                        }
                        return response.json();
                    })
                    .then(todosFilmes => {
                        return todosFilmes;
                    });
            }
            return filmes;
        })
        .then(filmes => {
            if (!filmes || !Array.isArray(filmes)) {
                console.error(`Dados de filmes inválidos para ${genero}`);
                return Promise.resolve();
            }
            
            const filmesValidos = filmes.filter(filme => filme && filme.id);
            
            let filmesDisponiveis = filmesValidos.filter(filme => !filmesExibidos.has(filme.id));
            
            if (filmesDisponiveis.length < 5) {
                const filmesComplementares = filmesValidos.filter(filme => 
                    !filmesDisponiveis.some(f => f.id === filme.id)
                ).slice(0, 5 - filmesDisponiveis.length);
                
                filmesDisponiveis = [...filmesDisponiveis, ...filmesComplementares];
            }
            
            const filmesCarrossel = filmesDisponiveis.slice(0, 10);
            
            filmesCarrossel.forEach(filme => {
                if (filme && filme.id) {
                    filmesExibidos.add(filme.id);
                }
            });
            
            const track = document.querySelector(`#${carrosselId} .carrossel-track`);
            if (!track) {
                console.error(`Elemento track não encontrado para o carrossel ${carrosselId}`);
                return Promise.resolve();
            }
            
            track.innerHTML = '';
            
            filmesCarrossel.forEach(filme => {
                if (filme) {
                    const card = criarCardCategoria(filme);
                    track.appendChild(card);
                }
            });
            
            if (filmesCarrossel.length === 0) {
                for (let i = 0; i < 5; i++) {
                    const card = criarCardCategoriaPlaceholder(genero, i+1);
                    track.appendChild(card);
                }
            }
            
            return new Promise(resolve => {
                setTimeout(() => {
                    inicializarCarrosselCategorias();
                    resolve();
                }, 100);
            });
        })
        .catch(error => {
            console.error(`Erro ao carregar filmes de ${genero}:`, error);
            
            const track = document.querySelector(`#${carrosselId} .carrossel-track`);
            if (track) {
                track.innerHTML = '';
                for (let i = 0; i < 5; i++) {
                    const card = criarCardCategoriaPlaceholder(genero, i+1);
                    track.appendChild(card);
                }
                
                return new Promise(resolve => {
                    setTimeout(() => {
                        inicializarCarrosselCategorias();
                        resolve();
                    }, 100);
                });
            }
            return Promise.resolve();
        });
}

function criarCardMelhoresAvaliados(filme) {
    const card = document.createElement('div');
    card.className = 'cardMelhoresAvaliados';
    
    if (!filme) {
        console.error('Filme inválido passado para criarCardMelhoresAvaliados');
        return criarCardMelhoresAvaliadosPlaceholder();
    }
    
    const imgSrc = filme.bannerUrl || './assets/images/paginaInical/CartazHomemdeFerro.png';
    
    const nome = document.createElement('h2');
    nome.textContent = filme.nome || 'Filme sem título';
    
    const sinopse = document.createElement('p');
    sinopse.textContent = filme.sinopse || 'Sem sinopse disponível.';
    
    const genero = document.createElement('span');
    genero.textContent = filme.genero || 'Sem gênero';
    genero.className = 'filme-genero';
    
    const img = document.createElement('img');
    img.src = imgSrc;
    img.alt = `${filme.nome || 'Filme'} Poster`;
    img.onerror = function() {
        this.src = './assets/images/paginaInical/CartazHomemdeFerro.png';
    };
    
    const conteudo = document.createElement('div');
    conteudo.appendChild(nome);
    conteudo.appendChild(genero);
    conteudo.appendChild(sinopse);
    
    card.appendChild(img);
    card.appendChild(conteudo);
    
    if (filme.id) {
        card.setAttribute('data-filme-id', filme.id);
    }
    
    card.addEventListener('click', () => {
        if (filme.id) {
            alert(`Filme: ${filme.nome}\nGênero: ${filme.genero}\nSinopse: ${filme.sinopse}`);
        } else {
            alert(`Filme: ${filme.nome}\nGênero: ${filme.genero || 'N/A'}\nSinopse: ${filme.sinopse || 'N/A'}`);
        }
    });
    
    return card;
}

function criarCardMelhoresAvaliadosPlaceholder() {
    const card = document.createElement('div');
    card.className = 'cardMelhoresAvaliados';
    
    card.innerHTML = `
        <img src="./assets/images/paginaInical/CartazHomemdeFerro.png" alt="Placeholder Poster">
        <div>
            <h2>Filme Exemplo</h2>
            <p>Este é um filme de exemplo. Adicione filmes ao banco de dados para vê-los aqui.</p>
        </div>
    `;
    
    return card;
}

function criarCardCategoria(filme) {
    const card = document.createElement('div');
    card.className = 'card-categoria';
    
    if (!filme) {
        console.error('Filme inválido passado para criarCardCategoria');
        return criarCardCategoriaPlaceholder('Erro', '');
    }
    
    const imgSrc = filme.bannerUrl || './assets/images/paginaInical/CartazHomemdeFerro.png';
    
    const img = document.createElement('img');
    img.src = imgSrc;
    img.alt = filme.nome || 'Filme sem título';
    img.onerror = function() {
        this.src = './assets/images/paginaInical/CartazHomemdeFerro.png';
    };
    
    const titulo = document.createElement('h3');
    titulo.textContent = filme.nome || 'Filme sem título';
    
    if (filme.id) {
        card.setAttribute('data-filme-id', filme.id);
    }
    
    card.addEventListener('click', () => {
        if (filme.id && filme.nome) {
            alert(`Filme: ${filme.nome}`);
        }
    });
    
    card.appendChild(img);
    card.appendChild(titulo);
    
    return card;
}

function criarCardCategoriaPlaceholder(genero, numero) {
    const card = document.createElement('div');
    card.className = 'card-categoria';
    
    card.innerHTML = `
        <img src="./assets/images/paginaInical/CartazHomemdeFerro.png" alt="Filme ${genero} ${numero}">
        <h3>${genero} ${numero}</h3>
    `;
    
    return card;
}

// Função para carregar imagem de filme sob demanda (para uso futuro)
function carregarImagemFilme(filmeId, tipoImagem, elementoImg) {
    const url = `/cadastrarFilme?imagem=${tipoImagem}&id=${filmeId}`;
    
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 5000);
    
    fetch(url, { signal: controller.signal })
        .then(response => {
            clearTimeout(timeoutId);
            if (!response.ok) {
                throw new Error('Erro ao carregar imagem');
            }
            return response.blob();
        })
        .then(blob => {
            const imageUrl = URL.createObjectURL(blob);
            elementoImg.src = imageUrl;
            
            elementoImg.onload = () => URL.revokeObjectURL(imageUrl);
        })
        .catch(error => {
            console.error('Erro ao carregar imagem:', error);
            elementoImg.onerror();
        });
}
