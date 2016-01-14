package com.sergeymild.allychatdemo.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.sergeymild.allychatdemo.OperatorChatFragmentCallbacks;
import com.sergeymild.allychatdemo.R;
import com.sergeymild.allychatdemo.utils.DrawableUtils;
import com.sergeymild.chat.services.http.ChatUtils;
import com.sergeymild.event_bus.Consumer;
import com.sergeymild.event_bus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sergeyMild on 09/12/15.
 */
public class EnterMessageView extends FrameLayout {
    @Bind(R.id.choose_attachment)   protected ImageButton chooseAttachmentButton;
    @Bind(R.id.message_edit_text)   protected EditText messageEditText;
    @Bind(R.id.send_message)        protected ImageButton sendMessageButton;

    public EnterMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (isInEditMode()) {return;}
        ButterKnife.bind(this, this);
        onInternetConnectionChanged(true);
    }

    public void setClickListeners() {
        chooseAttachmentButton.setOnClickListener(v -> EventBus.getInstance().of(OperatorChatFragmentCallbacks.class)
                .ifPresent(OperatorChatFragmentCallbacks::choosePhoto));

        sendMessageButton.setOnClickListener(v -> {
            if (messageEditText.getText().length() == 0) return;
            EventBus.getInstance().of(OperatorChatFragmentCallbacks.class)
                    .ifPresent(callbacks -> callbacks.sendMessage(ChatUtils.buildMessage(messageEditText.getText().toString())));
            messageEditText.setText("");
        });
    }

    public void onInternetConnectionChanged(boolean isConnected) {
        if (isConnected) {
            Drawable plusDrawable = DrawableUtils.getTintedDrawable(getContext(), R.drawable.ic_plus, R.color.green1);
            chooseAttachmentButton.setImageDrawable(plusDrawable);
            Drawable sendDrawable = DrawableUtils.getTintedDrawable(getContext(), R.drawable.ic_send_message, R.color.green1);
            sendMessageButton.setImageDrawable(sendDrawable);
            messageEditText.setEnabled(true);
            messageEditText.setHint(R.string.chat_placeholder);
            setClickListeners();
        } else {
            Drawable plusDrawable = DrawableUtils.getTintedDrawable(getContext(), R.drawable.ic_plus, R.color.gray);
            chooseAttachmentButton.setImageDrawable(plusDrawable);
            Drawable sendDrawable = DrawableUtils.getTintedDrawable(getContext(), R.drawable.ic_send_message, R.color.gray);
            sendMessageButton.setImageDrawable(sendDrawable);
            messageEditText.setEnabled(false);
            messageEditText.setHint(R.string.chat_offline);
            chooseAttachmentButton.setOnClickListener(null);
            sendMessageButton.setOnClickListener(null);
        }
    }
}
