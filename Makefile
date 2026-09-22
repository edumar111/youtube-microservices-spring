# Atajos del curso (ep. 14). Requiere Docker y JDK 25 (para build/test).
COMPOSE_ALL = docker compose -f docker-compose.all.yml

.PHONY: build test up down logs ps token

## Compila y corre los tests (Testcontainers, requiere Docker)
build:
	./mvnw clean verify

test:
	./mvnw test

## Levanta TODO el sistema (microservicios + Kong + Kafka + observabilidad)
up:
	$(COMPOSE_ALL) up --build -d

down:
	$(COMPOSE_ALL) down -v

logs:
	$(COMPOSE_ALL) logs -f

ps:
	$(COMPOSE_ALL) ps

## Obtiene un token de servicio (client_credentials) del Authorization Server
token:
	@curl -s -u store-client:store-secret \
		-d grant_type=client_credentials -d scope=product.read \
		http://localhost:9000/oauth2/token
