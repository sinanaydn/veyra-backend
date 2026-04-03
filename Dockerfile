FROM eclipse-temurin:25-jdk

WORKDIR /app

# Maven ile build edilmiş jar dosyasını image içine kopyalıyoruz
COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
