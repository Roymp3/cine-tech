 function deletarLinha(botao) {
        const linha = botao.parentNode.parentNode;
        linha.remove();
    }
    function deletarLinha(botao) {
        const linha = botao.closest("tr");
        linha.remove();
    }

    function editarLinha(botao) {
        const linha = botao.closest("tr");
        const tds = linha.querySelectorAll("td");

      
        if (botao.textContent === "Alterar") {
            for (let i = 0; i < tds.length - 1; i++) { 
                const textoAtual = tds[i].textContent;
                tds[i].innerHTML = `<input type="text" value="${textoAtual}">`;
            }
            botao.textContent = "Salvar";
        } else {
            
            for (let i = 0; i < tds.length - 1; i++) {
                const input = tds[i].querySelector("input");
                if (input) {
                    tds[i].textContent = input.value;
                }
            }
            botao.textContent = "Alterar";
        }
    }
    function editarImagem(botao) {
    const linha = botao.closest('tr');
    const inputFile = linha.querySelector('input[type="file"]');
    inputFile.click(); 
}

function editarImagem(botao) {
  const linha = botao.closest('tr');
  const inputFile = linha.querySelector('input[type="file"]');
  inputFile.click(); 
}