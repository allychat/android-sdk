package com.sergeymild.allychatdemo;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sergeymild.allychatdemo.view.rooms.IRoomsListView;
import com.sergeymild.chat.callbacks.SimpleChatCallback;
import com.sergeymild.chat.models.Room;
import com.sergeymild.chat.services.http.ChatUtils;
import com.sergeymild.event_bus.EventBus;

import java.util.List;

/**
 * Created by sergeyMild on 28/12/15.
 */
public class RoomsFragment extends Fragment implements RoomFragmentListener {
    public static LruCache<String, Integer> roomUnreadMessagesCountCache = new LruCache<>(30);
    public static final String TAG = "RoomsFragment";
    private IRoomsListView roomsListView;

    public static RoomsFragment newInstance() {
        return new RoomsFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getInstance().register(this, RoomFragmentListener.class);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getInstance().unRegister(this, RoomFragmentListener.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rooms_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        roomsListView = (IRoomsListView) view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        ChatUtils.getRooms(new SimpleChatCallback<List<Room>>() {
            @Override
            public void success(List<Room> result) {
                super.success(result);
                roomsListView.setRoomsList(result);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (roomUnreadMessagesCountCache != null) {
            roomUnreadMessagesCountCache.evictAll();
            roomUnreadMessagesCountCache = null;
        }
    }

    @Override
    public void getUnreadCount(@NonNull final String roomId) {
        ChatUtils.getUnreadCount(roomId, new SimpleChatCallback<Integer>() {
            @Override
            public void success(Integer result) {
                super.success(result);
                RoomsFragment.roomUnreadMessagesCountCache.put(roomId, result);
            }
        });
    }

    @Override
    public void startChatActivity(@NonNull Room room) {
        startActivity(new Intent(getActivity(), MainActivity.class)
                .putExtra("isSupport", room.isSupport())
                .putExtra("roomId", room.getId()));
    }
}
