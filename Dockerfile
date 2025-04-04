# Dockerfile
FROM openjdk:21-jdk-slim AS builder

# Define o Timezone
ENV TZ=UTC

WORKDIR /walletapp

COPY pom.xml .
COPY src ./src

# Instala as dependências e compila o projeto
RUN apt-get update && apt-get install -y maven && mvn clean package -DskipTests

# Imagem final para rodar a aplicação
FROM openjdk:21-jdk-slim

WORKDIR /walletapp

# Copia o JAR gerado na etapa de build
COPY --from=builder /walletapp/target/*.jar app.jar

EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
