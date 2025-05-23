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


    const formData = new FormData();
    formData.append('nome', nomeCliente);
    formData.append('usuario', usuarioCliente);
    formData.append('email', emailCliente);
    formData.append('telefone', telefoneCliente);
    formData.append('senha', senhaCliente);
      console.log('Enviando dados:');
    console.log('Nome:', nomeCliente);
    console.log('Usuario:', emailCliente);
    console.log('Telefone:', telefoneCliente);;
    fetch('/create-user', {
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
                        throw new Error(jsonData.message || 'Erro ao cadastrar o Usuario');
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
                    return { status: 'success', message: 'Usuario cadastrado com sucesso' };
                }
            } catch (jsonError) {
                console.error('Erro ao parsear resposta como JSON:', jsonError);
                console.log('Tratando como sucesso sem dados JSON');
                return { status: 'success', message: 'Usuario cadastrado com sucesso (sem detalhes)' };
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




function fecharModalSucesso() {    document.getElementById('modalSucesso').style.display = 'none';
}

function fecharModalErro() {
    document.getElementById('modalErro').style.display = 'none';
}