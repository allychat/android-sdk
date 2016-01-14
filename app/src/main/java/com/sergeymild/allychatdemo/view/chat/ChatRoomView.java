package com.sergeymild.allychatdemo.view.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.sergeymild.allychatdemo.R;
import com.sergeymild.allychatdemo.view.EnterMessageView;
import com.sergeymild.chat.models.Message;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sergeyMild on 09/12/15.
 */
public class ChatRoomView extends FrameLayout implements IChatRoomView {
    @Bind(R.id.messages_list_view) IChatMessagesListView messagesListView;
    @Bind(R.id.enter_message_view) EnterMessageView enterMessagesView;

    public ChatRoomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (isInEditMode()) return;

        ButterKnife.bind(this, this);
    }

    @Override
    public void setMessageList(@NonNull List<Message> messageList) {
        messagesListView.setMessageList(messageList);
    }

    @Override
    public void addMessageToList(@NonNull Message message) {
        messagesListView.addMessageToList(message);
    }

    @Override
    public void updateMessageInList(@NonNull Message message) {
        messagesListView.updateMessageInList(message);
    }

    @Override
    public void onInternetConnectionChanged(boolean isConnected) {
        enterMessagesView.onInternetConnectionChanged(isConnected);
    }
}
