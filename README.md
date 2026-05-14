#  Fera Metalúrgica - Sistema Web

## 📌 Sobre o Projeto
O projeto **Fera Metalúrgica** é uma aplicação web desenvolvida para otimizar o atendimento e a gestão interna de uma empresa especializada em soluções metálicas sob medida e móveis industriais. 

O sistema foi desenhado com foco na **Experiência do Usuário (UX)**, permitindo que clientes conheçam o portfólio da empresa e solicitem orçamentos detalhados de forma intuitiva, enquanto oferece aos administradores um painel de controle para gerenciar atividades e demandas.

* **Página Institucional (Home):** Apresentação da marca, benefícios e galeria de projetos com efeito Modal/Lightbox interativo.
* **Solicitação de Orçamentos:** Formulário detalhado para clientes especificarem material, medidas e anexarem referências.
* **Autenticação (Interface):** Tela de login moderna com design em cards.
* **Dashboard do Administrador:** Painel de controle responsivo com métricas de orçamentos e uma lista de atividades atualizada em tempo real.
* **Gerenciamento de Usuários:** Cadastro e listagem de usuários administrativos.
* **Persistência com PostgreSQL:** Dados armazenados em banco relacional utilizando Spring Data JPA.

## 🛠️ Tecnologias Utilizadas    
Este projeto foi construído utilizando a arquitetura **MVC (Model-View-Controller)** com as seguintes tecnologias:

**Back-End:**
* [Java](https://www.java.com/) (Linguagem principal)
* [Spring Boot](https://spring.io/projects/spring-boot) (Framework base)
* [Spring Security](https://spring.io/projects/spring-security) (Autenticação e Autorização)
* [Spring Data JPA](https://spring.io/projects/spring-data-jpa) (Persistência de dados)
* [PostgreSQL](https://www.postgresql.org/) (Banco de Dados)
* [Thymeleaf](https://www.thymeleaf.org/) (Template Engine para renderização dinâmica)
* [Hibernate ORM](https://hibernate.org/orm/) (Framework ORM)

**Front-End:**
* HTML5 & CSS3 (Estilização pura e responsiva, sem uso de templates prontos)
* JavaScript (Vanilla, para manipulação de Modais e UI)

**DevOps / Ferramentas**
* [Docker](https://www.docker.com/) (Containerização)
* [Apache Maven](https://maven.apache.org/) (Gestão de Dependências)
* [GitHub](https://github.com/) (Gestão de Versionamento)

## ⚙️ Como Executar o Projeto

**Pré Requisitos:**
* Java 21+
* Maven
* Docker

**Subindo o banco PostgreSQL com Docker:**
```bash
docker run -d \
  --name metalurgica-postgres \
  -e POSTGRES_USER=fera \
  -e POSTGRES_PASSWORD=fera123 \
  -e POSTGRES_DB=metalurgicadb \
  -p 5433:5432 \
  --restart always \
  postgres:16
```

**▶️ Executando a Aplicação:**
```bash
mvn spring-boot:run
```

A aplicação estará disponível em:
http://localhost:8080/

**🔐 Credenciais padrão:**
* Email: admin@fera.com
* Senha: 1234

