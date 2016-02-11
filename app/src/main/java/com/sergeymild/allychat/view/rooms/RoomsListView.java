package com.sergeymild.allychat.view.rooms;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.sergeymild.allychat.R;
import com.sergeymild.allychat.RoomFragmentListener;
import com.sergeymild.allychat.adapter.RoomsAdapter;
import com.sergeymild.allychat.utils.RecyclerTouchListener;
import com.sergeymild.chat.models.Room;
import com.sergeymild.event_bus.Consumer;
import com.sergeymild.event_bus.EventBus;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sergeyMild on 28/12/15.
 */
public class RoomsListView extends FrameLayout implements IRoomsListView {
    @Bind(R.id.list_view) RecyclerView listView;
    @Bind(R.id.new_room_fab) NewChatFloatingActionButton newChatButton;
    private RoomsAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    public RoomsListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (isInEditMode()) return;

        ButterKnife.bind(this, this);
        initViews();
    }

    protected void initViews() {
        linearLayoutManager = new LinearLayoutManager(getContext());
        listView.setLayoutManager(linearLayoutManager);

        listView.setAdapter((adapter = new RoomsAdapter()));

        listView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                EventBus.getInstance().of(RoomFragmentListener.class)
                        .ifPresent(new Consumer<RoomFragmentListener>() {
                            @Override
                            public void accept(RoomFragmentListener listener) {
                                listener.startChatActivity(adapter.getItem(position));
                            }
                        });
            }
        }));
    }

    @Override
    public void setRoomsList(@NonNull List<Room> roomsList) {
        adapter.setRoomList(roomsList);
    }

    @Override
    public void updateRoom(@NonNull String roomId, @NonNull Integer messageCount) {
        Room room = adapter.getRoom(roomId);
        if (room != null) {
            room.setUnreadMessageCount(messageCount);
            adapter.updateRoom(room);
        }
    }

    @Override
    public void clearList() {
        adapter.clearList();
    }
}
