# Fase 0: Familiarización con el stack · Chatbot con RAG

Prácticas realizadas antes de construir la **Feature 2 (Chatbot con RAG en la página pública)** de la Fase 2 del proyecto **Sistema de Gestión para la Oficina del Agua**, curso de Desarrollo Web, Universidad Mariano Gálvez de Guatemala, Campus Jutiapa.

**Autora:** Mijeli Azucena Lucero Burgos

## Objetivo

Entender a fondo qué es RAG y familiarizarse con cada tecnología del stack antes de implementar el microservicio, aplicando los lineamientos del curso: JDK 17, inyección de dependencias por constructor, Singleton en Spring Boot, contract-first con OpenAPI y Docker Compose local.

## Estructura

| Carpeta | Módulo | Qué demuestra |
|---|---|---|
| `practica-ollama/` | 0.3 | Modelos locales en Ollama (`bge-m3` y `llama3.1:8b`) y cálculo de similitud coseno entre embeddings |
| `practica-chatbot/` | 0.2.1 y 0.2.2 | Docker Compose con PostgreSQL + pgvector, réplica de MariaDB con usuario de solo lectura y DynamoDB Local |
| `practica-spring/` | 0.2.3 a 0.2.6 | Spring Boot 4.1 con JDK 17, inyección por constructor, Singleton (scopes y `@Bean`), OpenAPI contract-first con Swagger UI y Spring AI 2.0 (embeddings, chat, vector store y un mini-RAG) |
| `practica-next/` | 0.2.7 | React y Next.js 16 con JavaScript: componentes, estado y un BFF que conecta el widget con el mini-RAG de Spring |

## Stack

- Java 17 (Temurin) · Spring Boot 4.1 · Spring AI 2.0 · Maven
- Ollama: `bge-m3` (embeddings, 1024 dimensiones) y `llama3.1:8b` (chat)
- PostgreSQL 17 + pgvector · MariaDB 11.4 · DynamoDB Local
- Next.js 16 (JavaScript) · Docker Compose
- IntelliJ IDEA 2026 o superior

## Cómo ejecutar

**Requisitos:** Docker Desktop, JDK 17, Node.js 20 o superior y Ollama con los modelos descargados:
```
ollama pull bge-m3
ollama pull llama3.1:8b
```

**1. Bases de datos**

El volcado de la réplica no se incluye en el repositorio. Para generarlo desde la base de la fase 1:
```
docker exec agua_mariadb sh -c 'exec mariadb-dump -uroot -p"$MARIADB_ROOT_PASSWORD" --databases agua_db --single-transaction --routines --triggers' > practica-chatbot/replica/01-agua_db.sql
```
Luego:
```
cd practica-chatbot
docker compose up -d
```

**2. Spring Boot** (puerto 8082)
```
cd practica-spring
./mvnw spring-boot:run
```
Swagger UI: http://localhost:8082/swagger-ui.html

**3. Next.js** (puerto 3000)
```
cd practica-next
echo "SPRING_URL=http://localhost:8082" > .env.local
npm install
npm run dev
```

## Aprendizajes principales

- Un LLM solo alucina datos que no conoce; RAG le entrega fragmentos relevantes recuperados por similitud de significado.
- El modelo de embeddings debe ser el mismo para indexar y para consultar, y la dimensión del vector es fija.
- El umbral de similitud depende del modelo y debe calibrarse con preguntas reales.
- En Spring, el contenedor crea los beans e inyecta sus dependencias por constructor; por defecto son singleton y deben ser sin estado.
- Con contract-first, el contrato OpenAPI es la fuente de verdad y el código generado obliga a cumplirlo al compilar.
- El BFF oculta el microservicio: el navegador solo conoce Next.js.