SecureWebApp - Менеджер Відпочинку

Це RESTful веб-застосунок для управління курортами, розроблений в рамках розрахунково-графічної роботи. 
Проект реалізований на Java з використанням Spring Boot та розмежування прав доступу. Проект має простий веб інтерфейс і можливість авторизації користувачів.

Налаштування та Запуск
1. База Даних (Docker)
Запустіть контейнер з PostgreSQL (якщо він ще не запущений):
docker run --name postgresql_db -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5433:5432 -d postgres

2. Змінні середовища
Для запуску додатку необхідно задати змінну середовища для логіну та пароля БД:
DB_USERNAME: Логін до бази даних (postgres).
DB_PASSWORD: Пароль до бази даних (postgres).

4. Запуск
Запустіть клас com.example.secure.SecureWebAppApplication у IDE або через командний рядок:
mvn spring-boot:run -Dspring-boot.run.arguments=--DB_PASSWORD=postgres

Доступ (Логін/Пароль)
При першому запуску автоматично створюються наступні користувачі:
Роль: Admin
Логін: admin
Пароль: admin
Права: Має повний доступ (Create, Read, Update, Delete).

Роль: User
Логін: user
Пароль: user
Права: Може переглядати дані (Read-only).

Розроблено студенткою Брегида Катерина КІ-231
