package com.sergeymild.allychat;

import android.support.annotation.NonNull;

import com.sergeymild.chat.models.Message;

/**
 * Created by sergeyMild on 10/12/15.
 */
public interface OperatorChatFragmentCallbacks {
    void onBackClicked();
    void showMoreMessages(Message lastMessage, boolean after);
    void sendMessage(@NonNull String messageText);
    void resendMessage(@NonNull Message message);
    void choosePhoto();
}
