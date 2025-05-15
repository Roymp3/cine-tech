


function abrirMenuLateral() {
    const menu = document.getElementById('menuLateral');

    if (menu.innerHTML.trim() !== ''){
        return menu.innerHTML = '';

    }
    else{
        document.getElementById('ocultar').style.display = 'none';
        fetch('/menuLateral/menuLateral.html')
            .then(response => response.text())
            .then(data => {
                menu.innerHTML = data;

                const botaoFechar = document.getElementById('btnFecharMenu');
                if (botaoFechar) {
                    botaoFechar.addEventListener('click', fecharMenuLateral);
                }
            });
    }

}

function fecharMenuLateral() {
    const menu = document.getElementById('menuLateral');
    menu.innerHTML = '';
    document.getElementById('ocultar').style.display = 'flex'

}
