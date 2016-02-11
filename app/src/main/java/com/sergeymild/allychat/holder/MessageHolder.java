package com.sergeymild.allychat.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sergeymild.allychat.view.IListItemView;
import com.sergeymild.chat.models.Message;

/**
 * Created by sergeyMild on 09/12/15.
 */
public class MessageHolder extends RecyclerView.ViewHolder {
    public IListItemView<Message> view;

    public MessageHolder(IListItemView<Message> view) {
        super((View) view);
        this.view = view;
    }
}
