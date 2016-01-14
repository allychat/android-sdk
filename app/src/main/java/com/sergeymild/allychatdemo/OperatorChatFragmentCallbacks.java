package com.sergeymild.allychatdemo;

import android.support.annotation.NonNull;

import com.sergeymild.chat.models.Message;

/**
 * Created by sergeyMild on 10/12/15.
 */
public interface OperatorChatFragmentCallbacks {
    void onBackClicked();
    void showMoreMessages(int offset);
    void sendMessage(@NonNull Message message);
    void resendMessage(@NonNull Message message);
    void choosePhoto();
}
