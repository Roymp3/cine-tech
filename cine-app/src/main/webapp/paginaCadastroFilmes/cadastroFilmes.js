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
    const nomeFilme = document.getElementById('nomeFilme').value;
    const sinopseFilme = document.getElementById('sinopseFilme').value;
    const generoFilme = document.getElementById('generoFilme').value;
    const emCartaz = document.getElementById('emCartaz').checked;

    if (!nomeFilme || !generoFilme) {
        alert('Por favor, preencha todos os campos obrigatórios.');
        return;
    }    if (!imageInput.files[0]) {
        alert('Por favor, selecione uma imagem para o cartaz do filme.');
        return;
    }
    
    if (!imageInputFixo.files[0]) {
        alert('Por favor, selecione uma imagem para o banner do filme.');
        return;
    }
    
    const confirmarEnvio = confirm('Você está prestes a cadastrar um novo filme. Deseja continuar?');
    if (!confirmarEnvio) {
        return;
    }

    const formData = new FormData();
    formData.append('nome', nomeFilme);
    formData.append('sinopse', sinopseFilme);
    formData.append('genero', generoFilme);
    formData.append('emCartaz', emCartaz);
    formData.append('banner', imageInput.files[0]);
    formData.append('bannerFixo', imageInputFixo.files[0]);

    fetch('http://localhost:8080/cadastrarFilme', {
        method: 'POST',
        body: formData
    })
        .then(data => {
            if(data.ok){
                document.getElementById('modalSucesso').style.display = 'flex';
                limparFormulario();
            }
        })
        .then(response => {
            console.log(response);
            if (!response.ok) {
                throw new Error('Erro ao cadastrar o filme');
            }
            return response.json();
        })


});

function limparFormulario() {
    document.getElementById('nomeFilme').value = '';
    document.getElementById('sinopseFilme').value = '';
    document.getElementById('generoFilme').value = '';
    document.getElementById('duracaoFilme').value = '';
    document.getElementById('classificacaoFilme').value = '';
    document.getElementById('dataEstreiaFilme').value = '';
    document.getElementById('emCartaz').checked = false;

    divImagem.style.backgroundImage = '';
    uploadIcon.style.opacity = '1';
    imageInput.value = '';
    removerImagem.style.display = 'none';
    
    divImagemFixo.style.backgroundImage = '';
    uploadIconFixo.style.opacity = '1';
    imageInputFixo.value = '';
    removerImagemFixo.style.display = 'none';
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
