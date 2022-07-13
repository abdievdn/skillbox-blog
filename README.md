# Дипломный проект «Блоговый движок»
## Курс "Java-разработчик с нуля"
### Образовательная платформа Skillbox
#
|||
| ------ | ------ |
| Версия JDK | 11 |
| Версия Spring Boot | [plugins/github/README.md][PlGh] |
| База данных | [plugins/googledrive/README.md][PlGd] |
| Сборщик проекта | [plugins/onedrive/README.md][PlOd] |

##### Установка и запуск проекта:
1. Клонировать проекта на локальный диск: git clone https://github.com/abdievdn/skillbox-blog
2. Прописать ссылку на бд в файле application.yml вместо существующей, установить логин и пароль для входа в бд; 
spring: 
  datasource:
    url: _jdbc:mysql://rc1b-33d4tkgdgxxtd3gs.mdb.yandexcloud.net/blog_
    username: _abdiev_
    password: _skillbox_
3. Ввести данные для подключения к мэйл сервису по протоколу smpt;
spring: 
  mail:
    host: _smtp.yandex.ru_
    port: _465_
    username: _adzmit_
    password: _uhsdpwviyxeuhaug_
4. Поменять при необходимости данные в ветке blog файла application.yml;
blog:
  info:
    title: _DevPub_
    subtitle: _Рассказы разработчиков_
    phone: _+375 29 708****_
    email: _adzmit@yandex.ru_
    copyright: _Дмитрий Абдиев_
    copyrightFrom: _2022_
5. Произвести сборку проекта: mvn install (вводить в папке проекта)
6. Запустить проект: mvn spring-boot:start (вводить в папке проекта)

Остановка приложения: mvn spring-boot:stop (вводить в папке проекта)

##### Папка demo
В папке находится дамп базы данных Dump20220628.sql, который можно импортировать в свою бд, для демонстрации. Папку blogfiles скопировать в корень локального диска (содержит файлы изображений).

##### Ссылка на проект
http://51.250.17.207:8080 (доступно ограниченное время, Yandex Cloud)