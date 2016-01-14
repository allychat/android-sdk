package com.sergeymild.allychatdemo;

import android.support.annotation.NonNull;

import com.sergeymild.chat.models.Room;

/**
 * Created by sergeyMild on 28/12/15.
 */
public interface RoomFragmentListener {
    void getUnreadCount(@NonNull String roomId);
    void startChatActivity(@NonNull Room room);
}
