package com.sergeymild.allychat;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sergeymild.allychat.view.rooms.IRoomsListView;
import com.sergeymild.chat.AllyChat;
import com.sergeymild.chat.callbacks.SimpleChatCallback;
import com.sergeymild.chat.models.ErrorState;
import com.sergeymild.chat.models.Room;
import com.sergeymild.chat.services.http.AllyChatApi;
import com.sergeymild.event_bus.EventBus;

import java.util.Collections;
import java.util.List;

/**
 * Created by sergeyMild on 28/12/15.
 */
public class RoomsFragment extends Fragment implements RoomFragmentListener {
    public static LruCache<String, Integer> roomUnreadMessagesCountCache = new LruCache<>(30); //todo get rid of cache in favor of realm
    public static final String TAG = "RoomsFragment";
    private IRoomsListView roomsListView;

    interface ChatSelectListener {
        void onChatSelected(@NonNull Room room);
    }

    public static RoomsFragment newInstance() {
        return new RoomsFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getInstance().register(this, RoomFragmentListener.class);
        AllyChatApi.getRooms(new SimpleChatCallback<List<Room>>() {
            @Override
            public void success(List<Room> result) {
                super.success(result);
                roomsListView.clearList();
                roomsListView.setRoomsList(result);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getInstance().unRegister(this, RoomFragmentListener.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BaseActivity activity = (BaseActivity) getActivity();
        activity.hideBackButton();
        activity.setTitle("Комнаты");

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
    }

    @Override
    public void onStop() {
        super.onStop();
        if (roomUnreadMessagesCountCache!= null) roomUnreadMessagesCountCache.evictAll(); //todo npe
        roomUnreadMessagesCountCache = null;
    }

    @Override
    public void getUnreadCount(final String roomId) {
        AllyChatApi.getUnreadCount(roomId, new SimpleChatCallback<Integer>() {
            @Override
            public void success(Integer result) {
                super.success(result);
                RoomsFragment.roomUnreadMessagesCountCache.put(roomId, result);
            }
        });
    }

    @Override
    public void startChatActivity(@NonNull Room room) {
        ((ChatSelectListener) getActivity()).onChatSelected(room);
    }
}
