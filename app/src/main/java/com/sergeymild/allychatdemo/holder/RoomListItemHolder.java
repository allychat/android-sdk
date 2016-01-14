package com.sergeymild.allychatdemo.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sergeymild.allychatdemo.view.IListItemView;
import com.sergeymild.chat.models.Room;

/**
 * Created by sergeyMild on 28/12/15.
 */
public class RoomListItemHolder extends RecyclerView.ViewHolder {
    public IListItemView<Room> view;

    public RoomListItemHolder(IListItemView<Room> view) {
        super((View) view);
        this.view = view;
    }
}
