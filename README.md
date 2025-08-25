# Task Manager API

## Como rodar localmente

```bash
# compilar
mvn clean package

# executar
java -jar target/task-manager-api-0.0.1-SNAPSHOT.jar
```

Ou utilizando Docker:

```bash
# build da imagem
docker build -t task-manager-api .

# executar
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:oracle:thin:@//host:1521/ORCL \
  -e SPRING_DATASOURCE_USERNAME=usuario \
  -e SPRING_DATASOURCE_PASSWORD=senha \
  task-manager-api
```

## Variáveis de ambiente (Oracle)
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

## Endpoints
- `POST /api/tasks`
- `GET /api/tasks/{id}`
- `GET /api/tasks` (filtros: `status`)
- `PUT /api/tasks/{id}`
- `DELETE /api/tasks/{id}`

## Paginação e ordenação
Os endpoints de listagem aceitam os parâmetros padrão do Spring Data:
- `page` (0 a n)
- `size`
- `sort=campo,asc|desc`

## Segurança
Não há autenticação ou autorização configuradas; todos os endpoints são públicos.

## Configurações de performance e segurança
- CORS liberado para `http://localhost:4200`, permitindo que o frontend local acesse a API.
- Rate limit em memória (Bucket4j) para requisições `POST`, `PUT` e `DELETE`, reduzindo abusos e consumo excessivo de recursos.

## Swagger
A documentação interativa está disponível em `http://localhost:8080/swagger-ui.html`.

