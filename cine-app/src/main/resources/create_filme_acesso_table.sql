-- Criação de tabela para controle de acessos a filmes
CREATE TABLE IF NOT EXISTS tb_filme_acesso (
    id_acesso INT AUTO_INCREMENT PRIMARY KEY,
    id_filme INT NOT NULL,
    data_acesso TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_usuario VARCHAR(50),
    FOREIGN KEY (id_filme) REFERENCES tb_filme(id_filme)
);

-- Índice para melhorar as consultas de filmes mais acessados
CREATE INDEX IF NOT EXISTS idx_filme_acesso ON tb_filme_acesso(id_filme);
