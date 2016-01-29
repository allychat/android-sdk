# AllyChat API
версия 1.2.4

дата обновления 29/01/16

# Prerequisites
- Java 1.7 or greater
- Android 4.0 or greater

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
Вызовы API доступны через статические методы AllyChatApi:
```
    public static void sendMessage(Message message)
    public static void buildAndSend(String roomId, String messageText, String filePath, CountingTypedFile.ProgressListener progressListener)
    public static void resendMessage(String messageLocalId)
    public static void registerForGCM(String deviceToken, AllyChatCallback<Integer> requestCallback)
    public static void getRooms(final AllyChatCallback<List<Room>> requestCallback)
    public static void getMessages(final String roomId, final int limit, final String lastReadMessageId, final boolean showNew, final AllyChatCallback<List<Message>> requestCallback)
    public static void getLastMessages(final String roomId, final int limit, final AllyChatCallback<List<Message>> requestCallback)
    public static void getFirstMessages(final String roomId, final int limit, final AllyChatCallback<List<Message>> requestCallback)
    public static void getUser(String userId, String roomId, final AllyChatCallback<User> requestCallback)
    public static void readMessage(final String messageLocalId)
```

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
    class ChatActivity extends AppCompatActivity implements NetworkStateListener, OperatorChatFragmentCallbacks, OnMessage {
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
            AllyChatApi.clearSession(true);
            AllyChat.getInstance().close()
        }

        ...
    }
```

Пример отправки сообщения с изображением:
```
    AllyChatApi.buildAndSend(roomId, messageText, absoluteFilePath);
```
# 4 Уведомление о прочитанном сообщении
Метод, позволяющий отмечать сообщение как прочитанное:
```
    public static void readMessage(final String messageLocalId)
```
Уведомление о завершении операции приходит в общий MessageStatusCallback на который клиент подписывается посредством AllyChatApi.registerListeners(this); :
```
    @Override
    public void onMessageStatusChanged(@NonNull Message message) {
        roomView.updateMessageInList(message);
    }
```
