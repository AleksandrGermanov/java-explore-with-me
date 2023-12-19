# java-explore-with-me

## Задачи третьего этапа проектной работы _(реализация собственной функциональности)_

### Формат:

> ### Задача
>  - **подзадача** : _(опционально)_ комментарии

### Добавить ось изменений для работы с комментариями

- **создать таблицу в бд** : `name = comments`
- **создать модель**
- **создать ДТО и мапперы**
- **создать репозиторий**
- **создать сервис**

### Добавить связь с другими сущностями

- **создать коллекцию в `User`**
- **создать коллекцию в `Event`**
- **добавить флаг `permitComments` в `Event` и в БД**
-
- **добавить флаг `permitComments (default = true)` в `NewEventDto`**
- **добавить коллекцию `comments (СommentShortDto)` в `EventFullDto`**, сортировка `created_on_DESC`
- **добавить флаг `permitComments` в `EventFullDto`**
- **добавить поле `сomments (Long)` в `EventShortDto`**
- **поправить создание и маппинг измененных объектов**

### Декларировать возможность создания комментариев для пользователей

- **добавить в `PrivateEventController` маппинг `(CommentShortDto) POST /users/{userId}/events/{eventId}/comments`** : 
  принимает `NewCommentDto`, устанавливает `CommentState = POSTED`,
  `UserState[APP_USER, REQUESTER, INITIATOR]` получается автоматически в сервисе

### Декларировать возможность получения комментариев для пользователей, администраторов

- **создать `PrivateCommentController`**
- **Добавить маппинг `(List<CommentShortDto> - возвращаемое значение)
  GET /users/{userId}/comments?event={eventId}?commentState={commentState}&sort={sort}&from={from}&size={size}`** :
  получение всех или для конкретного `Event` комметариев пользователя, `sort = CREATED_ASC, (default)CREATED_DESC`
- **Добавить маппинг `(CommentFullDto) GET /users/{userId}/comments/{commentId}`** :
  получение конкретного комментария.
  <br><br>

- **добавить в `PrivateEventController` маппинг `(List<CommentShortDto>) 
  GET /users/{userId}/events/{eventId}/comments?userState={userState}
  &commentState={commentState}&sort={sort}&from={from}&size={size}`** :
  получение всех комментариев для события с возможностью фильтрации,
  <br><br>

- **создать `AdminCommentController`**
- **Добавить маппинг `(List<CommentFullDto>)
  GET /admin/comments/all?eventId={eventId}&userIds={[userIds]}&userState={userState}
  &commentState={commentState}&sort={sort}&from={from}&size={size}`** :
  получение комментариев c фильтрацией по событию, комментаторам, состоянию пользователя,
  состоянию комментария, с опциональными queryParams
  `sort = CREATED_ASC, (default)CREATED_DESC, COMMENTATOR_ID`.
- **Добавить маппинг `(CommentFullDto) GET /admin/comments/{commentId}`** :
  получение конкретного комментария.

### Декларировать возможность изменения комментариев для пользователей, администраторов

- **Добавить маппинг `(CommentShortDto) PATCH /users/{userId}/comments/{commentId}`** : `CommentState = UPDATED`,
  принимает `NewCommentDto`, проверка userId = commentatorId, комментарии со статусом `MODERATED` редактировать запрещено(код 409).
- **Добавить маппинг `(CommentShortDto) PATCH /users/{userId}/comments/{commenId}/remove?event={eventId}`** :
  `CommentState = DELETED_BY_USER`,  проверка userId = commentatorId, при маппинге `Comment` в DTO в поле `text` передавать `"removed"`
- **Добавить маппинг `(CommentShortDto) PATCH /users/{userId}/comments/{commenId}/restore?event={eventId}`** :
  только для комментариев с `CommentState = DELETED_BY_USER`,  проверка userId = commentatorId, новый `CommentState = UPDATED`
  <br><br>

- **Добавить маппинг `PATCH /admin/comments/{commentId}/moderate`** : `CommentState = MODERATED`, принимает `NewCommentDto`

### Декларировать возможность удаления комментариев для администраторов

- **Добавить маппинг `DELETE /admin/comments/{commentId}`**

### Декларировать возможность сортировки подбоки событий по наиболее обсуждаемым

- **Добавить `PublicSortType = MOST_DISCUSSED`**

### Реализовать заявленную функциональность

- **Имплементировать методы сервиса, репозиториев**
- **Обработать ошибки** :
> Имя ошибки (код) <- сценарий возниконовения.<br>
> - CommentatorMismatchException (409) <- когда id пользователя не совпадает с id комментатора.<br>
> - RequestParametersMisusageException (400) <- неправильное использоване параметров запроса 
  (сортировка COMMENTATOR_ID, когда производится поиск комментариев пользователя).<br>
> - CommentStateMismatchException (409) <- несовпадение статуса комментария для требуемой операции.<br>
> - CommentNotFoundException (404)<- запрашиваемый комментарий не найден в БД.<br>
> - CommentsAreNotAllowedException (409) <- комментарии не разрешены для размещения для данного события.

### Тестирование

- **Юнит-тестирование**
- **Постман тестирование**

### Деплой