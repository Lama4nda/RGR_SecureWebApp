# 1. Використовуємо образ з Java 8 (оскільки в pom.xml java 1.8)
FROM eclipse-temurin:8-jdk-alpine

# 2. Створюємо змінну для назви файлу (опціонально, для зручності)
ARG JAR_FILE=target/*.jar

# 3. Копіюємо зібраний .jar файл всередину образа під назвою app.jar
COPY ${JAR_FILE} app.jar

# 4. Команда, яка виконається при запуску контейнера
ENTRYPOINT ["java","-jar","/app.jar"]