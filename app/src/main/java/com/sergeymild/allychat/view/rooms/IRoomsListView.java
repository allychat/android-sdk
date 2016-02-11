package com.sergeymild.allychat.view.rooms;

import android.support.annotation.NonNull;

import com.sergeymild.chat.models.Room;

import java.util.List;

/**
 * Created by sergeyMild on 28/12/15.
 */
public interface IRoomsListView {
    void setRoomsList(@NonNull List<Room> roomsList);
    void updateRoom(@NonNull String roomId, @NonNull Integer messageCount);

    void clearList();
}
