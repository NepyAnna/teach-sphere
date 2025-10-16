FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw package -DskipTests -B
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN apt-get update && apt-get install -y postgresql-client && rm -rf /var/lib/apt/lists/*
COPY --from=build /app/target/*.jar app.jar
#ENTRYPOINT ["java", "-jar", "app.jar"]
#COPY wait-for-redis.sh /wait-for-redis.sh
#ENTRYPOINT ["/wait-for-redis.sh"]
#CMD ["java", "-jar", "app.jar"]
COPY wait-for-postgres.sh /wait-for-postgres.sh
ENTRYPOINT ["/wait-for-postgres.sh"]
CMD ["java", "-jar", "app.jar"]
#COPY wait-for-services.sh /wait-for-services.sh
#RUN chmod +x /wait-for-services.sh
#ENTRYPOINT ["/wait-for-services.sh"]
#CMD ["java", "-jar", "app.jar"]