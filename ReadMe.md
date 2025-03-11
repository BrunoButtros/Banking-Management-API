# Banking Management API

A **Banking Management API** é uma aplicação para gestão financeira voltada para clientes bancários. A API oferece funcionalidades para gerenciamento de usuários, transações financeiras, geração de relatórios e integração com serviços externos para consulta de saldo e conversão de moedas.

## Índice

- [Arquitetura](#arquitetura)
- [Funcionalidades](#funcionalidades)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Instalação e Configuração](#instalação-e-configuração)
  - [Execução Local](#execução-local)
  - [Execução com Docker](#execução-com-docker)
- [Documentação da API](#documentação-da-api)
- [Testes](#testes)
- [Desenvolvimento](#desenvolvimento)
- [Licença](#licença)

---

## Arquitetura

A aplicação segue uma arquitetura em camadas:

- **Controllers:** Responsáveis por receber e tratar as requisições HTTP.
- **Services:** Implementam a lógica de negócio e as validações necessárias.
- **Repositories:** Gerenciam o acesso aos dados usando Spring Data JPA.
- **Segurança:** Implementada com Spring Security e JWT para proteger os endpoints.
- **Documentação:** Gerada automaticamente com Springdoc OpenAPI e disponibilizada via Swagger UI.

---

## Funcionalidades

- **Autenticação:** Login de usuários utilizando JWT.
- **Gestão de Usuários:** Criação, atualização, consulta (por ID ou e-mail) e exclusão de usuários.
- **Gestão de Transações:** Operações CRUD para transações financeiras, com suporte a filtros por tipo, intervalo de datas e paginação.
- **Relatórios:** Geração de relatórios em PDF e Excel para resumo de transações.
- **Integração com Serviços Externos:** Consulta de saldo (via Mock API) e conversão de moedas.

---

## Tecnologias Utilizadas

- **Linguagem:** Java 17
- **Framework:** Spring Boot 3.4.1
- **Segurança:** Spring Security, JWT
- **Persistência:** Spring Data JPA com MySQL (produção) e H2 (testes)
- **Documentação:** Springdoc OpenAPI / Swagger UI
- **Containers:** Docker & Docker Compose
- **Outras:** Apache POI, iTextPDF

---

## Instalação e Configuração

### Execução Local

1. **Pré-requisitos**  
   - Java 17 e Maven instalados.
   - MySQL rodando na máquina local com as configurações definidas em `application.properties`.

2. **Configuração**  
   O arquivo `src/main/resources/application.properties` já contém as configurações necessárias. Exemplo:
   ```properties
   # Configurações do servidor
   server.port=8080

   # Configurações do banco de dados (MySQL)
   spring.datasource.url=${BANKING_MANAGEMENT_JDBC_URL:jdbc:mysql://localhost:3306/banking_management?createDatabaseIfNotExist=true}
   spring.datasource.username=root
   spring.datasource.password=root
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

   # Configuração do JPA
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=false
   spring.jpa.properties.hibernate.format_sql=true

   logging.level.org.hibernate=warn

   # Endpoints externos
   mock.api.balance.url=${MOCK_API_BALANCE_URL:https://run.mocky.io/v3/586637c9-c8c9-414b-959c-6028efdd0c47}
   exchange.api.key=${EXCHANGE_API_KEY:https://api.exchangerate.host/convert}

   logging.level.dev.bruno.banking.service=DEBUG
   logging.level.root=INFO

   # Configurações de segurança
   jwt.secret=5f4e1d7b87e3df44e3e72f6ecf87611b8c5bc477a50cf914a786a980d09bdb7c
   jwt.expiration.ms=86400000


3. **Rodando a Aplicação:**

    No terminal, na raiz do projeto, execute:
        mvn spring-boot:run

4. **Acessando a API:**

    URL base: http://localhost:8080
    Documentação interativa (Swagger UI): http://localhost:8080/swagger-ui/index.html

### Execução com Docker

1. **Pré-requisitos:**
    Docker e Docker Compose instalados.

2. **Dockerfile:**

    Crie um arquivo Dockerfile na raiz do projeto com o seguinte conteúdo:

        dockerfile
            FROM eclipse-temurin:17-jdk-alpine

            WORKDIR /app

            COPY target/banking-management-api-0.0.1-SNAPSHOT.jar app.jar

            EXPOSE 8080

            ENTRYPOINT ["java", "-jar", "app.jar"]


3. **docker-compose.yml** 

    Crie o arquivo docker-compose.yml na raiz do projeto:

        version: '3.8'

        services:
         db:
         image: mysql:latest
         container_name: banking-db
            restart: always
            environment:
            MYSQL_ROOT_PASSWORD: root
            MYSQL_DATABASE: banking_management
            ports:
            - "3307:3306"
            volumes:
            - mysql_data:/var/lib/mysql

        app:
            build: .
            container_name: banking-app
            restart: always
            depends_on:
              - db
            environment:
              BANKING_MANAGEMENT_JDBC_URL: jdbc:mysql://db:3306/banking_management?createDatabaseIfNotExist=true
              SPRING_DATASOURCE_USERNAME: root
              SPRING_DATASOURCE_PASSWORD: root
              MOCK_API_BALANCE_URL: ${MOCK_API_BALANCE_URL:https://run.mocky.io/v3/586637c9-c8c9-414b-959c-6028efdd0c47}
              EXCHANGE_API_KEY: ${EXCHANGE_API_KEY:https://api.exchangerate.host/convert}
            ports:
              - "8080:8080"

        volumes:
            mysql_data:

4. **Build e Execução:**

    Execute no terminal:

    docker-compose up -d --build

5. **Acessando a Aplicação via Docker:**

    - URL base: [http://localhost:8080](http://localhost:8080)
    - Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)


## Documentação da API

    A documentação interativa (Swagger UI) é gerada via Springdoc OpenAPI.  
    Acesse:  
        [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
## Testes

    O projeto conta com testes unitários e de integração para as principais camadas (controllers, services e repositórios).
    Para executar os testes, use o comando:

    mvn test

1. **Clonar o Repositório:**

    git clone https://github.com/BrunoButtros/Banking-Management-API
    cd banking-management-api


2. **Abrir na IDE:**  
    Pode usar IntelliJ, Eclipse ou VS Code com suporte a Java/Maven.

3. **Variáveis de Ambiente:**  
    Se quiser customizar as variáveis (por exemplo, MOCK_API_BALANCE_URL, EXCHANGE_API_KEY), defina-as no arquivo application.properties ou no docker-compose.yml.

4. **Rodar Localmente:**

    mvn spring-boot:run

**Rodar com Docker:**

    docker-compose up -d --build


5. **Executar os Testes:**

    mvn test

## Swagger (Documentação Interativa)
    A documentação dos endpoints é gerada automaticamente e disponibilizada via Swagger UI.
    Acesse http://localhost:8080/swagger-ui/index.html para visualizar e testar os endpoints.

## Licença
    
        MIT License

    Copyright (c) [2025] [Bruno Coelho Buttros]

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.


