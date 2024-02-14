
# Blogs-SOLID

## Introdução
Este projeto é um sistema básico para um blog, incluindo adição e exclusão de postagens, comentários, logins, entre outros recursos. Ele utiliza uma combinação de tecnologias no backend e no frontend para oferecer uma experiência completa de blogging.

## Índice
- [Instalação](#instalação)
- [Configuração Utilizando Docker](#Configuração-Utilizando-Docker-(recomendado))
- [Configuração Manual](#Configuração-Manual)
  - [Backend](#backend)
  - [Frontend](#frontend)
- [Dependências](#dependências)
- [Documentação](#documentação)
- [Exemplos](#exemplos)
- [Resolução de Problemas](#resolução-de-problemas)
- [Contribuidores](#contribuidores)
- [Versões](#versões)
- [Licença](#licença)
- [Melhorias Futuras](#melhorias-futuras)

## Instalação
Para instalar este projeto, você precisará das seguintes ferramentas:
- Docker (recomendado para uma configuração simplificada)
- Node.js v20.10.0
- npm v10.2.5
- Angular CLI 17.1.0
- Java JDK 17
- Maven (se não estiver usando Docker)
- PostgreSQL (se não estiver usando Docker)

## Configuração Utilizando Docker (Recomendado)

- O uso do Docker simplifica a configuração do ambiente, gerenciando o backend, frontend e o banco de dados em contêineres

1. Clone o repositório:
2. git clone https://github.com/pro-pedropaulo/blog-spring-angular.git
3. cd blog-spring-angular/api/blog
4. docker-compose up --build

- Isso irá construir e iniciar os contêineres para o backend, frontend e PostgreSQL, configurando automaticamente o banco de dados.

## Configuração Manual

### Backend
1. Clone o repositório:
2. git clone https://github.com/pro-pedropaulo/blog-spring-angular.git
3. cd blog-spring-angular/api/blog
4. Crie um banco de dados PostgreSQL chamado `blog`.
5. Atualize o arquivo `src/main/resources/application.properties` com as credenciais do seu banco de dados.
6. Na pasta raiz do projeto backend, execute:
mvn spring-boot:run


O backend estará rodando em `http://localhost:8080/`

### Frontend
1. Navegue até a pasta do frontend:
cd ../web/blog
2. Instale as dependências:
``npm install
``
3. Execute o frontend:
``ng serve
``

Acesse `http://localhost:4200` no navegador para ver a aplicação em funcionamento,

## Dependências
- **Frontend:** Angular 17.1.0, Tailwind CSS, Angular Material, Font Awesome, Quill (editor de texto rico)
- **Backend:** Spring Boot 3.2.2, Spring Data JPA, Spring Security, PostgreSQL, Cloudinary (para armazenamento de mídia)

## Documentação
A documentação da API está disponível em:  http://localhost:8080/swagger-ui/index.html

## Exemplos

- Video  de apresentação do Projeto:  https://youtu.be/4Y3IlOUKH74

## Resolução de Problemas
- Problema conhecido: No sistema de like/dislike, quando um usuário dá um like, ele não consegue retirar a reação. Isso será corrigido na próxima atualização.

## Contribuidores
- Pedro Paulo

## Versões
### versão 1.1 - Atualização realizada 13/02/24 22:06
- adicionado testes unitarioes para controllers e services
- adicionado testes de integração para repositories
- adicionado possibilidade de executar projeto via docker
- adicionada excessoes para tratar erros
- adicionada algumas explicações de operações cruciais no swagger

## Licença
Este projeto é disponibilizado sob uma licença livre.

## Melhorias Futuras
- Aprimoramento da autenticação com JWT.
- Aprimorar Testes Unitários
- Padronização de codigo com ESLINT
- Criação de usuário com verificação por e-mail.
- Edição de usuário.
- Deploy da aplicação.
- Migração do armazenamento de mídia do Cloudinary para AWS.
- Criação de ambientes para produção e teste
- Mostrar usuarios que reagiram a publicação
- Outras melhorias conforme necessário.
