# android-sdk
Quickstart

Prerequisites
- Java 1.7 or greater
- Android 4.0 or greater

Java-style Quickstart

class com.allychat.library.Allychat

1. Создать экземпляр класса, передав
    Allychat allychat = new Allychat(Context context, String host, String alias, String appId);


2. Выставить listener'a
    allychat.setLsitener(new AllychatListener() {
        public void onMessageReceived(Allychat allychat, Message message) {
            // Пришло сообщение
            // метод вызывается не из главного потока
        }

        public String onRequestAuthToken(Allychat allychat) throws Exception {
            // Allychat запрашивает новый auth token
            // метод вызывается не из главного потока
        }
    });


3. При необходимости сразу начать прием сообщений вызвать метод
    allychat.attemptConnection();
    Если этот метод не будет вызван Allychat законнектится к серверу при первой попытке вызова одного из Api методов


4. Методы Api
    public List<Room> getRooms() throws AllychatException; // web api
    public List<User> getUsers(final String... ids) throws AllychatException; // web api
    public URI uploadFile(final File file, final ProgressCallback progressCallback) throws AllychatException; // web api
    public void updateAvatar(final String uri) throws AllychatException; // web api
    public String getUserAlias(final String userID) throws AllychatException; // web api
    public Room createRoom(final String opponentUserID) throws AllychatException; // web api
    public User searchUser(final String alias) throws AllychatException; // web api
    public boolean readMessage(final String messageID) throws AllychatException; // web api
    public List<Message> getLastMessages(final String roomID, final int limit) throws AllychatException; // web api
    public List<Message> getFirstMessages(final String roomID, final int limit) throws AllychatException; // web api
    public List<Message> getHistory(final String roomID, final int limit, final String lastMessageID, final boolean showNew); // web api
    public void sendMessage(Message message) throws AllychatException; // socket api

   Все методы блокирующие, не рекомендуется вызывать их в главном потоке. У методов web api можно проверить код ошибки, пришедший с сервера.
   Пример как это сделать ниже.

    try {
        allychat.getRooms();
    } catch (AllychatException e) {
        if (e.getCause() instanceof ApiException) {
            ApiException apiEx = (ApiException) e.getCause();
            if (apiEx.getError() == ApiException.Error.SERVER_ERROR) {
                if (apiEx.getSubcode() == SUBCODE_EXPIRED_TOKEN || apiEx.getSubcode() == SUBCODE_EXPIRED_AUTH_TOKEN) {
                    // сбросить токен
                }

            }
        }
    }
    Полный список ошибок см. ApiException, ApiException.ERROR


4. При завершении работы необходимо вызвать
    allychat.destroy();
   После этого класс перейдет в состояние destroyed, socket connection будет закрыт, а все вызовы Api будут бросать AllychatException.



Android-style Quickstart


Для удобства Android-разработчиков был реализован ContentProvider и Service для удобной интеграции в проект.

1. Создайте свой сервис, наследующий com.allychat.library.service.AllychatService
    public class MyCustomChatService extends AllychatService {


        @Override
        public String onRequestAuthToken(Allychat allychat) {
            return this.alias;
        }

        @Override
        protected void handleCommand(int command, Bundle extras) {
            super.handleCommand(command, extras);

            // implement your custom command, loading callbacks here
        }

        @Override
        protected void onCommandException(int command, Allychat.AllychatException e) {
            super.onCommandException(command, e);
            // handle error in your way
        }
    }


2. Обновите AndroidManifest.xml.
    - Удостоверьтесь, что <uses-permission android:name="android.permission.INTERNET" /> присутствует;
    - Добавьте свой сервис внутри тэга <application></application>
        <service android:name=".MyCustomChatService" />
    - Добавьте ContentProvider внутри тэга <application></application>
        <provider
            android:authorities="com.allychat.demo.content"
            android:name="com.allychat.library.content.AllychatContentProvider" />
      ВАЖНО: Authority у ContentProvider'a должен быть разным в разных приложениях, например можно сделать его зависящим от пакета
      приложения - ru.alfabank.mobile.android.allychat.content.


3. Запустите/Перезапустите чат с помощью helper метода сервиса restartChat
    MyCustomChatService.restartChat(Context context,
                                    Class<? extends AllychatService> serviceClass,
                                    String contentProviderAuthority, // authority тот же, что в манифесте
                                    String host,                     // host чат-сервера (my.allychat.ru)
                                    String appId,                    // app id
                                    String alias);                   // user alias


4. Доступ к сообщениям/румам осуществляется стандартными методами content provider - через Cursor.
   В классах пакета com.allychat.library.model описаны все поля и Uri (Message.FIELD_MESSAGE_ID, Message.getContentUri() и т.д.).
   Примеры:

   - Все румы
    Uri uri = Room.getContentUri("com.allychat.demo.content"); // content:// + authority from manifest + /rooms
    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
   - Сообщения рума
    cursor = getContentResolver().query(Message.getContentUri("com.allychat.demo.content"), null, Message.FIELD_ROOM_ID + " = ?", new String[] {roomId}, Message.FIELD_SENT_DATE);


5. Для загрузки новых данных через сервис можно использовать либо Intent'ы, либо методы-хелперы, например
    MyCustomChatService.loadLastMessages(this, MyCustomChatService.class, roomId, 10);
    MyCustomChatService.refreshRooms(this, MyCustomChatService.class);
    MyCustomChatService.sendTextMessage(RoomActivity.this, MyCustomChatService.class, roomId, editMessage.getText().toString());
    MyCustomChatService.sendImageMessage(RoomActivity.this, MyCustomChatService.class, roomId, editMessage.getText().toString(), attachedImage);
    MyCustomChatService.loadHistory( ... );
    MyCustomChatService.loadFirstMessages( ... );
    MyCustomChatService.retrySend( ... );
    MyCustomChatService.loadUser( ... );
    MyCustomChatService.readMessage( ... );

6. Статус сообщения
   Для остлеживания статуса сообщения у класса Message есть поле status, оно может принимать значения Message.STATUS_SYNCED,
   Message.STATUS_SYNCING, Message.STATUS_FAILED. Для повторной отсылки сообщения в случае ошибки используйте метод AllychatService.retrySend

