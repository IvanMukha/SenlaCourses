# Муха Иван Иванович

## Требования
- Установленная Java версии - 21
- Установленный Docker версии - 4.28.0
- Установленный Apache Maven версии - 3.9.6

## Шаги по запуску
## Запуск проекта "Project Manager"
1. Склонировать проект : https://github.com/IvanMukha/SenlaCourses.git
2. В корневой директории выполнить команду: mvn clean package
3. В корневой директории выполнить команду: docker-compose up --build
После успешного запуска, приложение будет доступно по адресу: http://localhost:8080
4. Для остановки приложения выполнить команду docker-compose down