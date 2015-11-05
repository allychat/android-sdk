# AllyChat API
версия 1.1.3

дата обновления 04/11/2015

##Prerequisites
- Java 1.7 or greater
- Android 4.0 or greater

##Java-style Quickstart

Версия 1.1 и выше
# 0. Важные отличия новой версии:

- Работа с данными происходит через возвращаемые списки требуемых данных.
- Приложению не нужно объявлять контент провайдер и т.д у себя в манифесте и производить собственные запросы к данным (хотя, это и возможно, через контент провайдер библиотеки)


# 1. Нaчало работы с sdk
Для работы с SDK необходимо выполнить его инициализацию одним из статических методов sdkInitialize(...)
Например:
```
AllyChatSdk.sdkInitialize(getApplicationContext(), "Apa91bezz5bnw1-MQciijcNOi1w_uy6GQ2wmlnMbLloFlcV9h1NgPDocVObOfmhsfMPqlyghYgykjOmVBLlDPscDfhMyR8zmnuTjppDtTaujYEByodc5zK6EXvw3jioW6cHqzCkTTzi1", "alfa-dev.allychat.ru", "my_alias", "mobile");
```

При указании device token произойдет автоматическая подписка на Push сообщения. Дополнительных действий для подписки производить не нужно.

# 2. Методы API

    public static void sendMessage(Message message)

    public static void resendMessage(String messageLocalId)
    
    public static void registerForGCM(String deviceToken, AllyChatCallback<Integer> requestCallback)
    
    public static void getRooms(final AllyChatCallback<List<Room>> requestCallback)
    
    public static void getMessages(final String roomId, final int limit, final String lastReadMessageId, final boolean showNew, final AllyChatCallback<List<Message>> requestCallback)
    public static void getLastMessages(final String roomId, final int limit, final AllyChatCallback<List<Message>> requestCallback)
    public static void getFirstMessages(final String roomId, final int limit, final AllyChatCallback<List<Message>> requestCallback)
    public static void getUser(String userId, String roomId, final AllyChatCallback<User> requestCallback)
    public static void readMessage(final String messageLocalId)

Данные методы объявлены и определены в классе AllyChatApi, через который и осуществляется взаимодействие
    
    За исключением методов отсылки сообщений, все api работают по одинаковой схеме: требуется передать в метод callback параметризованный нужным типом.
    Например, для получения списка комнат:  
```
        AllyChatApi.getRooms(new AllyChatCallback<List<Room>>() {
            @Override
            public void onCompleted(List<Room> rooms) {
                Toast.makeText(RoomsActivity.this, "Got list of rooms", Toast.LENGTH_LONG).show();
                Log.i(TAG, "Got list of rooms");
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(AllyChatError error) {
            }
        });
    }
```
    В случае возниктовения ошибочной ситуации, будет вызван метод onError переданного callback.

# 3. Отправка и получение сообщений
Для отправки сообщений и получения входящих, необходимо подписаться на соответствующие обратные вызовы при помощи соответствующих статических методов класса AllyChatSdk:
```
    public static void registerMessagesStatusListener(MessageStatusCallback listener) {
        messagesStatusListeners.add(listener);
    }

    public static void unregisterMessagesStatusListener(MessageStatusCallback listener) {
        messagesStatusListeners.remove(listener);
    }

    public static void registerIncomingMessagesListener(IncomingMessageListener listener) {
        incomingMessagesListeners.add(listener);
    }

    public static void unregisterIncomingMessagesListener(IncomingMessageListener listener) {
        incomingMessagesListeners.remove(listener);
    }
```

Важно, не забывать производить отписку от обратных вызовов при уничножении объектов, содержащих strong референсы на объекты слушателей.

Пример отправки сообщения:
        Message message = new Message();
        message.setRoomID(roomId);
        message.setMessage("bla bla bla");
        message.setFileAttachmentURL(imageCaptureUri); // если требуется отсылка картинки, то передавать uri файла этой картинки
        AllyChatApi.sendMessage(message);
        
# 4 Уведомление о прочитанном сообщении
Метод public static void readMessage(final String messageLocalId)

Уведомление о завершении операции приходит в общий MessageStatusCallback на который клиент подписывается посредством 
public static void registerMessagesStatusListener(MessageStatusCallback listener)
