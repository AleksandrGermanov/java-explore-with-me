# java-explore-with-me

## Сервис для объявления и посещения мероприятий. Дипломный проект курса "Java-разработчик" на платформе Яндекс-Практикум.

---

## Цель

Разработка многомодульного web-приложения в рамках подготовленной спецификации OpenAPI 3.0/Swagger,
разработка собственной функциональности с использованием SpringBoot, SpringData (JPA), J-unit,
Mockito, Postgresql, SLF4J (Logback), Git(github), Postman, Docker Compose.

---

>[!IMPORTANT]
> Проект реализован на Java SE 11
>
> Для запуска пректа нужно:
>
> для запуска приложения необходимо:<br>
>  1. склонировать репозиторий
>  2. проверить и при необходимости освободить 8080, 9090, 5432, 5555 порты
>  3. убедиться, что `Docker daemon` запущен
>  4. в терминале с поддержкой Maven, docker-compose и JDK 11 или выше выполнить
     (из папки с клонированным репозиторием)<br>
     `mvn install`<br>
     `docker-compose up -d`<br>
    Для пользователей Windows можно воспользоваться скриптом [deploy.cmd](/deploy.cmd).
> 5. [Вот здесь](https://github.com/yandex-praktikum/java-explore-with-me/tree/main_svc/postman) есть 
    postman-коллекции для проверки функциональности, которая есть в ТЗ. Их можно запустить.
> 6. После того, как будет запущена хоть одна коллекция из п.5, произойдет популяция БД и [можно будет проверить](/postman)
    реализацию эндпоинтов третьего этапа (реализация собственной функциональности).

---

### Функциональность

 - Соответствие заявленным спецификациям [для сервиса статистики](/ewm-stats-service-spec.json), 
[для основного сервиса](/ewm-main-service-spec.json).
 - Реализована возможность оставления комментариев к событиям.

### 1-й этап: Сервис статистики

> [!NOTE]
>#### Формат:
> > - **Задача**
> > - - **подзадача** : _(опционально)_ комментарии

#### Разработка сервиса статистики по предоставленной [спецификации](/ewm-stats-service-spec.json) с использованием [Swagger Editor](https://editor.swagger.io)

- **создание многомодульной структуры проекта**
- **настройка `pom.xml` для родительского и дочерних модулей**
- **разработка собственно [сервера статистики](/statistics/server)**
- **обособление общих DTO в отдельный [модуль](/statistics/common-dto-lib)**
- **написание [клиента](/statistics/client) с целью последующей интеграции в основной сервис**
- **unit-тестирование**
- **postman-тестирование с использованием подготовленной коллекции**
- **деплой с помощью docker-compose**

> [!TIP]
> Highlights!
> - В классе EndpointHitClientImpl в
    методе [retrieveStatsViewList](https://github.com/AleksandrGermanov/java-explore-with-me/blob/b70d38969fe2dfb4fc308655a9d18e1233ef0169/statistics/client/src/main/java/ru/practicum/endpointhitclient/EndpointHitClientImpl.java#L63)
    реализовано добавление опционального query-параметра(List<String> uris) и возврашение параметризованного типа
    с помощью RestTemplate
> - В классе [EndpointHitDto](/statistics/common-dto-lib/src/main/java/ru/practicum/commondtolib/EndpointHitDto.java)
    настроена работа ObjectMapper'а : дата сериализуется с помощью кастомного маппера,
    сериализируются только ненулевые поля.
> - В [тестовых классах модуля общих DTO](/statistics/common-dto-lib/src/test/java/ru/practicum/commondtolib)
    получилось использовать тестовые билиотеки, поставляемые в спринговском стартере,
    при помощи пустого конфигурационного класса (в модуле нет класса с main-методом, контекст автоматически не
    создается),
    а в самих тестах использованы методы библиотеки assertj.
> - В классе
    [StatsViewRepositoryImpl](/statistics/server/src/main/java/ru/practicum/statisticsserver/endpointhit/repository/StatsViewRepositoryImpl.java)
    представлен пример парсинга объекта типа Tuple в объект StatsView, который не является JPA-сущностью.
    Метод получения объекта альтернативен JPQL `select new...`.

---

### 2-й этап: Сервис статистики

#### Разработка основного сервиса по предоставленной [спецификации](/ewm-main-service-spec.json) с использованием [Swagger Editor](https://editor.swagger.io)

- **разработка собственно [основного сервиса](/ewm-app)**
- **интеграция [клиента](/statistics/client) сервиса статистики, с помощью
  [конфигурационного класса](/ewm-app/src/main/java/ru/practicum/ewmapp/EndpointHitClientInjector.java)**
- **unit-тестирование**
- **postman-тестирование с использованием подготовленной коллекции**
- **деплой с помощью docker-compose**

> [!TIP]
> Highlights!
> - В классе [Event](/ewm-app/src/main/java/ru/practicum/ewmapp/event/model/Event.java)
    представлен пример сложного ORM: `@Embedded, @Where, @OneToMany, @ManyToOne, @Enumerated, @Transient и др.`
> - В пакете [compilation](/ewm-app/src/main/java/ru/practicum/ewmapp/compilation)
    есть пример ORM c реализацией связи many-to-many через переходную таблицу - сущность с комплексным
    первичным ключом 
    (см. [CompilationEventRelation.java](/ewm-app/src/main/java/ru/practicum/ewmapp/compilation/model/CompilationEventRelation.java)).
> - В пакете [util](/ewm-app/src/main/java/ru/practicum/ewmapp/util)
    есть пример [StringToEnumConverterFactory](/ewm-app/src/main/java/ru/practicum/ewmapp/util/StringToEnumConverterFactory.java).
    Код взят [отсюда](https://www.baeldung.com/spring-type-conversions "https://www.baeldung.com/spring-type-conversions") с небольшим, но важным исправлением,
    которое делает  сгенерированные конвертеры нечувствительными к регистру.

---

### 3-й этап : реализация собственной функциональности

> [!NOTE]
> Задачи 3го этапа расписаны более подробно, т.к. это по сути дорожная карта, по которой создавался 3й этап.

#### Реализация возможности комментировать события

- **Добавить ось изменений для работы с комментариями**
- - **создать таблицу в бд** : `name = comments`
- - **создать модель**
- - **создать ДТО и мапперы**
- - **создать репозиторий**
- - **создать сервис**
    <br>
    <br>
- **Добавить связь с другими сущностями**
- - **создать коллекцию в `User`**
- - **создать коллекцию в `Event`**
- - **добавить флаг `permitComments` в `Event` и в БД**
- - **добавить флаг `permitComments (default = true)` в `NewEventDto`**
- - **добавить коллекцию `comments (СommentShortDto)` в `EventFullDto`**, сортировка `created_on_DESC`
- - **добавить флаг `permitComments` в `EventFullDto`**
- - **добавить поле `сomments (Long)` в `EventShortDto`**
- - **поправить создание и маппинг измененных объектов**
    <br>
    <br>
- **Декларировать возможность создания комментариев для пользователей**

- - **добавить в `PrivateEventController` маппинг `(CommentShortDto) POST /users/{userId}/events/{eventId}/comments`** :
  принимает `NewCommentDto`, устанавливает `CommentState = POSTED`,
  `UserState[APP_USER, REQUESTER, INITIATOR]` получается автоматически в сервисе
  <br>
  <br>
- **Декларировать возможность получения комментариев для пользователей, администраторов**

- - **создать `PrivateCommentController`**
- - **Добавить маппинг `(List<CommentShortDto> - возвращаемое значение)
  GET /users/{userId}/comments?event={eventId}?commentState={commentState}&sort={sort}&from={from}&size={size}`** :
  получение всех или для конкретного `Event` комметариев пользователя, `sort = CREATED_ASC, (default)CREATED_DESC`
- - **Добавить маппинг `(CommentFullDto) GET /users/{userId}/comments/{commentId}`** :
  получение конкретного комментария.
  <br>
  <br>

- - **добавить в `PrivateEventController` маппинг `(List<CommentShortDto>)
  GET /users/{userId}/events/{eventId}/comments?userState={userState}
  &commentState={commentState}&sort={sort}&from={from}&size={size}`** :
  получение всех комментариев для события с возможностью фильтрации,
  <br><br>

- - **создать `AdminCommentController`**
- - **Добавить маппинг `(List<CommentFullDto>)
  GET /admin/comments/all?eventId={eventId}&userIds={[userIds]}&userState={userState}
  &commentState={commentState}&sort={sort}&from={from}&size={size}`** :
  получение комментариев c фильтрацией по событию, комментаторам, состоянию пользователя,
  состоянию комментария, с опциональными queryParams
  `sort = CREATED_ASC, (default)CREATED_DESC, COMMENTATOR_ID`.
- - **Добавить маппинг `(CommentFullDto) GET /admin/comments/{commentId}`** :
  получение конкретного комментария.
  <br>
  <br>
- **Декларировать возможность изменения комментариев для пользователей, администраторов**
- - **Добавить маппинг `(CommentShortDto) PATCH /users/{userId}/comments/{commentId}`** : `CommentState = UPDATED`,
  принимает `NewCommentDto`, проверка userId = commentatorId, комментарии со статусом `MODERATED` редактировать
  запрещено(код 409).
- - **Добавить маппинг `(CommentShortDto) PATCH /users/{userId}/comments/{commenId}/remove?event={eventId}`** :
  `CommentState = DELETED_BY_USER`, проверка userId = commentatorId, при маппинге `Comment` в DTO в поле `text`
  передавать `"removed"`
- - **Добавить маппинг `(CommentShortDto) PATCH /users/{userId}/comments/{commenId}/restore?event={eventId}`** :
  только для комментариев с `CommentState = DELETED_BY_USER`, проверка userId = commentatorId,
  новый `CommentState = UPDATED`
- - **Добавить маппинг `PATCH /admin/comments/{commentId}/moderate`** : `CommentState = MODERATED`,
  принимает `NewCommentDto`
  <br>
  <br>
- **Декларировать возможность удаления комментариев для администраторов**
- - **Добавить маппинг `DELETE /admin/comments/{commentId}`**
  <br>
  <br>
- **Декларировать возможность сортировки подбоки событий по наиболее обсуждаемым**
- - **Добавить `PublicSortType = MOST_DISCUSSED`**
  <br>
  <br>
- **Реализовать заявленную функциональность**
- - **Имплементировать методы сервиса, репозиториев**
- - **Обработать ошибки** :

> Имя ошибки (код) <- сценарий возниконовения.<br>
> - CommentatorMismatchException (409) <- когда id пользователя не совпадает с id комментатора.<br>
> - RequestParametersMisusageException (400) <- неправильное использоване параметров запроса
    (сортировка COMMENTATOR_ID, когда производится поиск комментариев пользователя).<br>
> - CommentStateMismatchException (409) <- несовпадение статуса комментария для требуемой операции.<br>
> - CommentNotFoundException (404)<- запрашиваемый комментарий не найден в БД.<br>
> - CommentsAreNotAllowedException (409) <- комментарии не разрешены для размещения для данного события.

- **Тестирование**
- - **Юнит-тестирование**
- - **Постман тестирование**
    <br>
    <br>
- **Деплой**

> [!TIP]
> Highlights!
> - В пакете [util](/ewm-app/src/main/java/ru/practicum/ewmapp/util)
    есть класс [CustomRepository.java](/ewm-app/src/main/java/ru/practicum/ewmapp/util/CustomRepository.java),
    который выделился в результате рефакторинга написанных репозиториев для сущностей Event и Comment.
    Были применены концепции параметризованных типов, функциональных интерфейсов, вложенных классов.
    Для сущности Event можно посмотреть репозиторий [до рефакторинга](https://github.com/AleksandrGermanov/java-explore-with-me/blob/6eb749305e1c5fc4a9310addea8c5895e7996ca1/ewm-app/src/main/java/ru/practicum/ewmapp/event/repository/CustomEventRepositoryImpl.java#L1)
    и [текущую версию](/ewm-app/src/main/java/ru/practicum/ewmapp/event/repository/CustomEventRepositoryImpl.java)
