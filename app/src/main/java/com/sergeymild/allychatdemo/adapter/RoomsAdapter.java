package com.sergeymild.allychatdemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.sergeymild.allychatdemo.R;
import com.sergeymild.allychatdemo.holder.RoomListItemHolder;
import com.sergeymild.allychatdemo.utils.RecyclerViewShowAnimations;
import com.sergeymild.allychatdemo.view.IListItemView;
import com.sergeymild.chat.models.Room;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sergeyMild on 28/12/15.
 */
public class RoomsAdapter extends RecyclerView.Adapter<RoomListItemHolder> {
    private List<Room> roomList = new ArrayList<>();
    private RecyclerViewShowAnimations recyclerViewShowAnimations = new RecyclerViewShowAnimations(true);

    @Override
    public int getItemViewType(int position) {
        if (position > roomList.size()) return RecyclerView.NO_POSITION;
        return R.layout.list_item_room;
    }

    @Override
    public RoomListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        IListItemView view = (IListItemView) LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new RoomListItemHolder(view);
    }

    @Override
    public void onBindViewHolder(RoomListItemHolder holder, int position) {
        holder.view.populate(roomList.get(position));
        recyclerViewShowAnimations.verticalAnimation(holder, position);
    }


    public void setRoomList(List<Room> roomList) {
        if (this.roomList.isEmpty()) {
            this.roomList = roomList;
            notifyDataSetChanged();
        } else {
            this.roomList.addAll(0, roomList);
            notifyItemRangeInserted(0, roomList.size());
        }
    }

    public Room getItem(int position) {
        return roomList.get(position);
    }

    public Room getRoom(String roomId) {
        for (Room room : roomList) {
            if (room.getId() != null && room.getId().equals(roomId)) {
                return room;
            }
        }
        return null;
    }

    public void updateRoom(Room room) {
        for (int i = 0; i < roomList.size(); i++) {
            Room inList = roomList.get(i);
            if (inList.getId() != null && room.getId() != null && inList.getId().equals(room.getId())) {
                roomList.set(i, room);
                notifyItemChanged(i);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }
}