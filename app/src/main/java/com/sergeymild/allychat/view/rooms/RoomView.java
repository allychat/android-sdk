package com.sergeymild.allychat.view.rooms;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sergeymild.allychat.R;
import com.sergeymild.allychat.view.IListItemView;
import com.sergeymild.chat.models.Message;
import com.sergeymild.chat.models.Room;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sergeyMild on 28/12/15.
 */
public class RoomView extends FrameLayout implements IListItemView<Room> {
    @Bind(R.id.message_count)           TextView messageCount;
    @Bind(R.id.avatar)                  ImageView avatar;
    @Bind(R.id.user_name)               TextView userName;
    @Bind(R.id.message)                 TextView message;
    @Bind(R.id.date)                    TextView date;
    @Bind(R.id.message_count_container) View messageCountContainer;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("E", Locale.getDefault());

    public RoomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (isInEditMode()) return;

        ButterKnife.bind(this, this);
    }

    @Override
    public void populate(final Room room) {
        Message lastMessage = room.getLastMessage();
        messageCountContainer.setVisibility(GONE);

//        final Integer integer = RoomsFragment.roomUnreadMessagesCountCache.get(room.getId());
//        if (integer == null) {
//            EventBus.getInstance().of(RoomFragmentListener.class)
//                    .ifPresent(new Consumer<RoomFragmentListener>() {
//                        @Override
//                        public void accept(RoomFragmentListener listener) {
//                            listener.getUnreadCount(room.getId());
//                        }
//                    });
//        } else if (integer > 0) {
//            messageCountContainer.setVisibility(VISIBLE);
//            messageCount.setText(String.valueOf(integer));
//        }

        if (lastMessage != null) {
            if (!isNull(lastMessage.getUser().getName())) {
                userName.setText(lastMessage.getUser().getName());
            } else {
                userName.setText(lastMessage.getUser().getAlias());
            }

            if (!isNull(lastMessage.getUser().getAvatarUrl())) {
                avatar.setVisibility(VISIBLE);
                Picasso.with(getContext())
                        .load(lastMessage.getUser().getAvatarUrl())
                        .placeholder(R.drawable.avatar)
                        .into(avatar, new Callback() {
                            @Override
                            public void onSuccess() {}

                            @Override
                            public void onError() {
                                avatar.setImageResource(R.drawable.avatar);
                            }
                        });
            } else {
                avatar.setVisibility(GONE);
            }

            if (!isNull(lastMessage.getMessage())){
                message.setText(lastMessage.getMessage());
            } else if (!isNull(lastMessage.getFile())) {
                message.setText("[image]");
            }

            if (!isNull(lastMessage.getCreatedAtMillis())) {
                date.setVisibility(VISIBLE);
                date.setText(dateFormat.format(lastMessage.getCreatedAtMillis()));
            } else {
                date.setVisibility(GONE);
            }

            if (!lastMessage.isRead()) {
                messageCountContainer.setVisibility(VISIBLE);
            } else {
                messageCountContainer.setVisibility(GONE);
            }

        } else {
            avatar.setImageResource(R.drawable.avatar);
            userName.setText("Default");
            message.setText("no message yet");
        }
    }

    private boolean isNull(Object o) {
        return null == o || "null".equals(o) || "".equals(o);
    }
}
