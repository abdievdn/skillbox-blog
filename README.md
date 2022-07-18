# Дипломный проект «Блоговый движок»
## Курс "Java-разработчик с нуля"
### Образовательная платформа Skillbox
#
|||
| ------ | ------ |
| Версия JDK | 11 |
| Версия Spring Boot | 2.6.4 |
| База данных | MySQL 8+ |
| Сборщик проекта | maven |

#### Установка и запуск проекта:
1. Клонировать проекта на локальный диск: git clone https://github.com/abdievdn/skillbox-blog.git
2. Прописать ссылку на бд в файле application.yml вместо существующей, установить логин и пароль для входа в бд; 
spring: 
&ensp; datasource:  
&emsp; url: _jdbc:mysql://rc1b-33d4tkgdgxxtd3gs.mdb.yandexcloud.net/blog_  
&emsp; username: _abdiev_  
&emsp; password: _skillbox_  
3. Ввести данные для подключения к мэйл сервису по протоколу smpt;  
spring:  
&ensp; mail:  
&emsp; host: _smtp.yandex.ru_  
&emsp; port: _465_  
&emsp; username: _adzmit_  
&emsp; password: _uhsdpwviyxeuhaug_  
ввести адрес электронной почты, с которой будет приходить ссылка восстановления пароля;  
blog:  
&ensp; info:  
&emsp; email:
4. Поменять при необходимости данные в ветке blog файла application.yml;
blog:  
&ensp; info:  
&emsp; title: _DevPub_  
&emsp; subtitle: _Рассказы разработчиков_  
&emsp; phone: _+375 29 708****_  
&emsp; email: _adzmit@yandex.ru_  
&emsp; copyright: _Дмитрий Абдиев_  
&emsp; copyrightFrom: _2022_  
5. Произвести сборку проекта: mvn install (вводить в папке проекта)
6. Запустить проект: mvn spring-boot:start (вводить в папке проекта)

Остановка приложения: mvn spring-boot:stop (вводить в папке проекта)

#### Папка demo
В папке находится дамп базы данных Dump20220628.sql, который можно импортировать в свою бд, для демонстрации. Папку blogfiles скопировать в корень локального диска (содержит файлы изображений).

#### Ссылка для презентации проекта
http://51.250.17.207:8080 (доступно ограниченное время, Yandex Cloud)
