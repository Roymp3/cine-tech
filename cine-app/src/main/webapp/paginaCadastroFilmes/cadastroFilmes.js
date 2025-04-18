const imageInput = document.getElementById('bannerFilme');
const divImagem = document.getElementById('inserirFilme');
const uploadIcon = document.getElementById('uploadIcon');


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
    }

    reader.readAsDataURL(file);


});