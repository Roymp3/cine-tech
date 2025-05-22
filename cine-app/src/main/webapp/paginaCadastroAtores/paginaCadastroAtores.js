const imageInput = document.getElementById('fotoAtor');
const divImagem = document.getElementById('inserirAtor');
const uploadIcon = document.getElementById('uploadIcon');
const removerImagem = document.getElementById('removerImagem');

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

const botaoSalvar = document.querySelector('.botao-salvar');

botaoSalvar.addEventListener('click', function() {    botaoSalvar.disabled = true;
    botaoSalvar.innerText = 'Enviando...';
    const nomeAtor = document.getElementById('nomeAtor').value.trim();
    const biografiaAtor = document.getElementById('biografiaAtor').value.trim();
    const nacionalidadeAtor = document.getElementById('nacionalidadeAtor').value;
    const dataNascimento = document.getElementById('dataNascimentoAtor').value;
    const premios = document.getElementById('premiosAtor').value.trim();
    const filmesFamosos = document.getElementById('filmesFamososAtor').value.trim();


    if (!nomeAtor) {
        alert('Por favor, preencha o nome do ator.');
        resetBotaoSalvar();
        return;
    }
    
    if (!nacionalidadeAtor) {
        alert('Por favor, selecione a nacionalidade do ator.');
        resetBotaoSalvar();
        return;
    }
    
    if (!imageInput.files[0]) {
        alert('Por favor, selecione uma imagem para o ator.');
        resetBotaoSalvar();
        return;
    }
      const arquivoTamanho = imageInput.files[0].size;
    if (arquivoTamanho > 5 * 1024 * 1024) {
        alert('A imagem selecionada é muito grande. Tamanho máximo: 5MB.');
        resetBotaoSalvar();
        return;
    }
    

    const confirmarEnvio = confirm('Você está prestes a cadastrar um novo ator. Deseja continuar?');
    if (!confirmarEnvio) {
        resetBotaoSalvar();
        return;
    }


    const formData = new FormData();
    formData.append('nome', nomeAtor);
    formData.append('biografia', biografiaAtor);
    formData.append('nacionalidade', nacionalidadeAtor);
    formData.append('dataNascimento', dataNascimento);
    formData.append('premios', premios);
    formData.append('filmesFamosos', filmesFamosos);
    formData.append('foto', imageInput.files[0]);
      console.log('Enviando dados:');
    console.log('Nome:', nomeAtor);
    console.log('Biografia:', biografiaAtor);
    console.log('Nacionalidade:', nacionalidadeAtor);
    console.log('Data de nascimento:', dataNascimento);
    console.log('Prêmios:', premios);
    console.log('Filmes famosos:', filmesFamosos);
    console.log('Arquivo de foto:', imageInput.files[0].name, imageInput.files[0].size + ' bytes');
    fetch('/cadastrarAtor', {
        method: 'POST',
        body: formData
    })
    .then(response => {
        console.log('Status da resposta:', response.status);
        console.log('Headers:', [...response.headers.entries()]);
          return response.clone().text().then(text => {
            console.log('Resposta bruta recebida:', text);
              if (!response.ok) {
                console.log('Resposta não-OK, status:', response.status);
                const contentType = response.headers.get('content-type');
                console.log('Tipo de conteúdo:', contentType);
                  if (contentType && contentType.includes('application/json')) {
                    try {
                        const jsonData = JSON.parse(text);
                        console.log('Dados JSON parseados:', jsonData);
                        throw new Error(jsonData.message || 'Erro ao cadastrar o ator');
                    } catch (jsonError) {
                        console.error('Erro ao parsear JSON:', jsonError);
                        throw new Error('Erro ao processar resposta do servidor');
                    }
                } else {
                    throw new Error(text || 'Erro ao cadastrar o ator');
                }
            }
            

            console.log('Resposta OK, verificando se é JSON');
            try {
                if (text && text.trim()) {
                    const jsonData = JSON.parse(text);
                    console.log('Dados JSON parseados com sucesso:', jsonData);
                    return jsonData;
                } else {
                    console.log('Resposta vazia, tratando como sucesso');
                    return { status: 'success', message: 'Ator cadastrado com sucesso' };
                }
            } catch (jsonError) {
                console.error('Erro ao parsear resposta como JSON:', jsonError);
                console.log('Tratando como sucesso sem dados JSON');
                return { status: 'success', message: 'Ator cadastrado com sucesso (sem detalhes)' };
            }
        });
    })
    .then(data => {
        console.log('Sucesso:', data);
        document.getElementById('modalSucesso').style.display = 'flex';
        limparFormulario();
    })
    .catch(error => {
        console.error('Erro:', error);
        document.getElementById('modalErro').style.display = 'flex';
        document.querySelector('#modalErro .modal-mensagem').textContent = 
            `Erro ao cadastrar o ator: ${error.message || 'Verifique sua conexão e tente novamente.'}`;
    })
    .finally(() => {
        resetBotaoSalvar();
    });
});


function resetBotaoSalvar() {
    botaoSalvar.disabled = false;
    botaoSalvar.innerText = 'Salvar';
}


function limparFormulario() {
    document.getElementById('nomeAtor').value = '';
    document.getElementById('biografiaAtor').value = '';
    document.getElementById('nacionalidadeAtor').value = '';
    document.getElementById('dataNascimentoAtor').value = '';
    document.getElementById('premiosAtor').value = '';
    document.getElementById('filmesFamososAtor').value = '';


    divImagem.style.backgroundImage = '';
    uploadIcon.style.opacity = '1';
    imageInput.value = '';
    removerImagem.style.display = 'none';
}

const botaoCancelar = document.querySelector('.botao-cancelar');
botaoCancelar.addEventListener('click', function() {
    if(confirm('Tem certeza que deseja cancelar? Todos os dados não salvos serão perdidos.')) {        limparFormulario();
    }
});

function fecharModalSucesso() {    document.getElementById('modalSucesso').style.display = 'none';
}

function fecharModalErro() {
    document.getElementById('modalErro').style.display = 'none';
}


document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        document.getElementById('modalSucesso').style.display = 'none';
        document.getElementById('modalErro').style.display = 'none';
    }
});
