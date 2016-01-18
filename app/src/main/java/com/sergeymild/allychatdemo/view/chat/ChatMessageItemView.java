package com.sergeymild.allychatdemo.view.chat;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.sergeymild.allychatdemo.OperatorChatFragmentCallbacks;
import com.sergeymild.allychatdemo.R;
import com.sergeymild.allychatdemo.view.IListItemView;
import com.sergeymild.chat.models.Message;
import com.sergeymild.chat.models.MessageStatus;
import com.sergeymild.chat.services.http.ChatUtils;
import com.sergeymild.event_bus.EventBus;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sergeyMild on 09/12/15.
 */
public class ChatMessageItemView extends LinearLayout implements IListItemView<Message> {
    @Bind(R.id.message_text)                    protected TextView messageTextView;
    @Bind(R.id.message_image)                   protected RoundedImageView messageFileImage;
    @Nullable @Bind(R.id.not_sended_message)    protected TextView notSendedMessageTextView;
    @Nullable @Bind(R.id.image_container)       protected FrameLayout imageContainer;
    @Nullable @Bind(R.id.upload_image_progress) protected ProgressBar uploadImageProgress;
    private Message entity;

    private Callback imageLoadCallback =  new Callback() {
        @Override
        public void onSuccess() {
            if (uploadImageProgress != null) uploadImageProgress.setVisibility(GONE);
        }

        @Override
        public void onError() {
            if (uploadImageProgress != null) uploadImageProgress.setVisibility(GONE);
        }
    };

    public ChatMessageItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this, this);


        setOnClickListener(v -> {
            if (entity.getStatus() == MessageStatus.STATUS_FAILED) {
                EventBus.getInstance().of(OperatorChatFragmentCallbacks.class)
                        .ifPresent(callbacks -> callbacks.resendMessage(entity));
            }
        });
    }

    @Override
    public void populate(Message entity) {
        this.entity = entity;
        Log.d("messageStatus", entity.getNameStatus());
        Log.d("messageStatus", "------------------------");

        if (TextUtils.isEmpty(entity.getMessage())) {
            messageTextView.setVisibility(GONE);
        } else {
            messageTextView.setVisibility(VISIBLE);
            messageTextView.setText(entity.getMessage());
        }

        if (notSendedMessageTextView != null) {

            if (entity.getStatus() == MessageStatus.STATUS_FAILED) {
                notSendedMessageTextView.setVisibility(VISIBLE);
                notSendedMessageTextView.setText(getResources().getString(R.string.transfer_message_failed_message));
            } else {
                notSendedMessageTextView.setVisibility(GONE);
            }
        }


        if (imageContainer != null && uploadImageProgress != null && messageFileImage != null) {

            if (TextUtils.isEmpty(entity.getFile())) {
                imageContainer.setVisibility(GONE);
            } else {
                imageContainer.setVisibility(VISIBLE);
                if (entity.getFile().startsWith("https")) {
                    uploadImageProgress.setVisibility(VISIBLE);
                    Picasso.with(getContext()).load(entity.getFile())
                            .resize(300, 300)
                            .placeholder(R.drawable.ic_image_placeholder)
                            .into(messageFileImage, imageLoadCallback);

                } else {
                    uploadImageProgress.setVisibility(VISIBLE);
                    Picasso.with(getContext()).load(new File(entity.getFile()))
                            .resize(300, 300)
                            .placeholder(R.drawable.ic_image_placeholder)
                            .into(messageFileImage, imageLoadCallback);
                }
                if (entity.getStatus() == MessageStatus.STATUS_SENDING) {
                    Log.d("message", "image not empty status sending " + entity.getMessage());
                    uploadImageProgress.setVisibility(VISIBLE);
                }

                if (entity.getStatus() == MessageStatus.STATUS_SEND) {
                    Log.d("message", "image not empty status send " + entity.getMessage());
                    uploadImageProgress.setVisibility(GONE);
                }

                if (messageTextView.getVisibility() == VISIBLE) {
                    MarginLayoutParams layoutParams = (MarginLayoutParams) messageTextView.getLayoutParams();
                    layoutParams.bottomMargin = getResources().getDimensionPixelSize(R.dimen.margin_8);
                    messageTextView.setLayoutParams(layoutParams);
                }
            }
        }


        if (!entity.isRead()) {
            ChatUtils.readMessage(entity);
        }
    }
}
