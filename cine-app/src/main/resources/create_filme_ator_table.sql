CREATE TABLE IF NOT EXISTS tb_filme_ator (
    id_filme_ator INT AUTO_INCREMENT PRIMARY KEY,
    id_filme INT NOT NULL,
    id_ator INT NOT NULL,
    FOREIGN KEY (id_filme) REFERENCES tb_filme(id_filme),
    FOREIGN KEY (id_ator) REFERENCES tb_ator(id_ator),
    UNIQUE (id_filme, id_ator)
);
