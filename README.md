# AllyChat API
версия 1.2.4

дата обновления 29/01/16

# Prerequisites
- Java 1.7 or greater
- Android 4.0 or greater

добавить в build.gradle файл главного модуля:
```
android {
    ...
    packagingOptions {
        exclude 'META-INF/services/javax.annotation.processing.Processor'
    }
}
```

# 1. Нaчало работы с sdk
Инициализация и олучение instance чата:
```
new AllyChat.Builder().setContext(getApplicationContext())
                    .setAlias(alias)
                    .setHost("alfa-dev.allychat.ru")
                    .setAppId("app")
                    .setGcmPushToken(token)
                    .setIsLoggingEnabled(true)
                    .setOnSuccessInitialize(new OnSuccessInitialize() {
                        @Override
                        public void onSuccess(@NonNull AllyChat chat) {
                            // since this moment, you either can use this chat reference or call AllyChat.getInstance()
                        }
                    }).setOnFailureInitialize(new OnFailureInitialize() {
                        @Override
                        public void onFailInitialize(ErrorState errorState) {
                            // handle error
                        }
                    }).build();
```
.setAlias() можно не вызывать или передавать в них пустую строку - в этом случае юзер будет анонимным.
То же c .setGcmPushToken(), в этом случае пуш-уведомления не приходят.


# 2. Методы API
Вызовы API доступны через публичные статические методы AllyChatApi:
```
    getRooms(ChatCallback<List<Room>> callback)
    createRoom(String userAlias, ChatCallback<Room> callback)
    getUnreadCount(String roomId, ChatCallback<Integer> callback)


    messagesByOffset(Room room, int limit, int offset, ChatCallback<List<Message>> callback)
    messagesBeforeMessage(Message message, Room room, int limit, ChatCallback<List<Message>> callback)
    messagesAfterMessage(Message message, Room room, int limit, ChatCallback<List<Message>> callback)


    readMessage(String messageLocalId)
    sendMessage(Message message, ChatCallback<Message> callback,
                                       @Nullable CountingTypedFile.ProgressListener fileUploadListener)
    resendMessage(Message message, ChatCallback<Message> callback)
    buildAndSend(Room room, String messageText, String filePath, CountingTypedFile.ProgressListener progressListener, @Nullable ChatCallback<Message> callback)
    buildMessage(Room room, String messageText, @Nullable String filePath)


    registerGcm(Context context, String token)
    unRegisterGcm(Context context)
```
При вызове методов `messagesBeforeMessage` и `messagesAfterMessage`, а так же все отправленные и полученные через sdk сообщения кешируются.
При повторном запросе того же участка переписки возвращаются сообщения из кеша.

За исключением методов отсылки сообщений, все api работают по одинаковой схеме: требуется передать в метод callback, параметризованный нужным типом.
Например, для получения списка комнат:

```
        AllyChatApi.getRooms(new SimpleChatCallback<List<Room>>() {
                    @Override
                    public void success(List<Room> result) {
                        super.success(result);
                        roomsListView.setRoomsList(result);
                    }
                    @Override
                    public void failure(ErrorState errorState) {
                        super.failure(errorState);
                        // this override can be omitted
                    }
        });
```
В случае возникновения ошибочной ситуации, будет вызван метод failure переданного callback.



# 3. Отправка и получение сообщений
Перед отправкой запросов через AllyChatApi и для получения новых входящих сообщений, необходимо подписаться на соответствующие обратные вызовы при помощи соответствующих статических методов класса AllyChatSdk:
```
    class ChatActivity extends AppCompatActivity implements NetworkStateListener, OnMessage {
        ...
        @Override
        public void onResume() {
            super.onResume();
            AllyChatApi.registerListeners(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            AllyChatApi.unRegisterListeners(this);
        }

        @Override
        public void onStop() {
            super.onStop();
            // call this methods then you need to finish working with chat. To use chat again, build new AllyChat instance
            AllyChatApi.clearSession(true);
            AllyChat.getInstance().close()
        }

        ...
    }
```

Пример отправки сообщения с изображением:
```
    AllyChatApi.buildAndSend(room, messageText, absoluteFilePath, progressListener, callback);
```
# 4 Уведомления
Уведомление о новом сообщении приходит в в общий MessageStatusCallback на который клиент подписывается посредством AllyChatApi.registerListeners(this);
```
@Override
public void onMessage(@NonNull Message message) {
    roomView.addMessageToList(message);
}
```

Уведомление о завершении операций, для которых нет callback, приходят в onMessageStatusChanged:
```
    @Override
    public void onMessageStatusChanged(@NonNull Message message) {
        roomView.updateMessageInList(message);
    }
```
