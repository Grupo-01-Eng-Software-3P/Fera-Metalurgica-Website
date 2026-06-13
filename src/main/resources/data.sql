-- Garante a criação do usuário administrador sem duplicar dados
MERGE INTO usuario AS target
    USING (SELECT 'admin@fera.com' AS email) AS source
    ON target.email = source.email
    WHEN NOT MATCHED THEN
        INSERT (nome, cargo, email, senha)
            VALUES ('Lucas', 'Gerente', 'admin@fera.com', '$2a$10$Qj57Z.91bFv7G/o.K.Xbueq91l02xK.Qk3bE0yX2YxYxYxYxYxYxY');
