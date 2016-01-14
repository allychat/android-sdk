package com.sergeymild.allychatdemo.view.rooms;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.sergeymild.allychatdemo.R;
import com.sergeymild.allychatdemo.RoomFragmentListener;
import com.sergeymild.allychatdemo.adapter.RoomsAdapter;
import com.sergeymild.allychatdemo.utils.RecyclerTouchListener;
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

        listView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), (view, position) ->
                EventBus.getInstance().of(RoomFragmentListener.class)
                        .ifPresent(listener -> listener.startChatActivity(adapter.getItem(position)))));
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
}
