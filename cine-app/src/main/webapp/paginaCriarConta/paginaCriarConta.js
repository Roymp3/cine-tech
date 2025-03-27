document.getElementById("telefone").oninput = function() {
    this.value = formatarTelefone(this.value);
}

function formatarTelefone(numero) {
    numero = numero.replace(/\D/g, "");

    if (numero.length >= 11) {
        return `(${numero.slice(0, 2)}) ${numero.slice(2, 7)}-${numero.slice(7, 11)}`;
    }

    return numero;
}