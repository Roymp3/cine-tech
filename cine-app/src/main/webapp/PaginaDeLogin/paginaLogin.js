  const form = document.querySelector('form');

    form.addEventListener('submit', function() {
        const email = document.getElementById('email').value;
        sessionStorage.setItem('emailLogado', email);
    });