package com.sergeymild.allychatdemo.view.rooms;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sergeymild.allychatdemo.R;
import com.sergeymild.allychatdemo.utils.PrintDateUtils;
import com.sergeymild.allychatdemo.view.IListItemView;
import com.sergeymild.chat.models.Message;
import com.sergeymild.chat.models.Room;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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
                        .resize(80, 80)
                        .centerCrop()
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
                avatar.setImageResource(R.drawable.avatar);
            }

            if (!isNull(lastMessage.getMessage())){
                message.setText(lastMessage.getMessage());
            } else if (!isNull(lastMessage.getFile())) {
                message.setText(R.string.last_message_image);
            }

            if (!isNull(lastMessage.getCreatedAtMillis())) {
                String dateString = PrintDateUtils.getFormattedDateForRoomLastMessage(lastMessage.getCreatedAtMillis());
                date.setText(dateString);
                date.setVisibility(VISIBLE);
            } else {
                date.setVisibility(GONE);
            }

        } else {
            avatar.setImageResource(R.drawable.avatar);
            userName.setText(R.string.no_user);
            message.setText(R.string.empty_chat);
        }
    }

    private boolean isNull(Object o) {
        return null == o || "null".equals(o) || "".equals(o);
    }
}
