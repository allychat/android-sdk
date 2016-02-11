package com.sergeymild.allychat.view.chat;

import android.support.annotation.NonNull;

import com.sergeymild.chat.models.Message;

import java.util.List;

/**
 * Created by sergeyMild on 09/12/15.
 */
public interface IChatRoomView {
    void setMessageList(@NonNull List<Message> messageList);
    void addMessageToList(@NonNull Message message);
    void updateMessageInList(@NonNull Message message);
    void onInternetConnectionChanged(boolean isConnected);

}
