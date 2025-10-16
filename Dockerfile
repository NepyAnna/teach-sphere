FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw package -DskipTests -B

FROM eclipse-temurin:21-jre
USER root
RUN apt-get update && apt-get install -y postgresql-client && rm -rf /var/lib/apt/lists/*
RUN PGPASSWORD=jZpu4D1INu1n6hxLjgQcndehiMdx1AV5 psql -h dpg-d3o1tjre5dus73ac3iig-a.frankfurt-postgres.render.com -U teach_sphere teach_sphere
RUN pg_isready -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" -d "$PGDATABASE"
COPY --from=build /app/target/*.jar app.jar
#ENTRYPOINT ["java", "-jar", "app.jar"]
COPY wait-for-postgres.sh /wait-for-postgres.sh
ENTRYPOINT ["/wait-for-postgres.sh"]
CMD ["java", "-jar", "app.jar"]