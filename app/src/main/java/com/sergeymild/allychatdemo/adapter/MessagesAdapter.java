package com.sergeymild.allychatdemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.sergeymild.allychatdemo.R;
import com.sergeymild.allychatdemo.holder.MessageHolder;
import com.sergeymild.allychatdemo.utils.RecyclerViewShowAnimations;
import com.sergeymild.allychatdemo.view.IListItemView;
import com.sergeymild.chat.models.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sergeyMild on 09/12/15.
 */
public class MessagesAdapter extends RecyclerView.Adapter<MessageHolder> {
    private List<Message> messageList = new ArrayList<>();
    private RecyclerViewShowAnimations recyclerViewShowAnimations = new RecyclerViewShowAnimations(true);

    @Override
    public int getItemViewType(int position) {
        if (position > messageList.size()) return RecyclerView.NO_POSITION;
        if (messageList.get(position).isFromMe()) {
            return R.layout.chat_operator_from_me_message_list_item;
        } else {
            return R.layout.chat_operator_from_operator_message_list_item;
        }
    }

    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        IListItemView view = (IListItemView) LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageHolder holder, int position) {
        holder.view.populate(messageList.get(position));
        recyclerViewShowAnimations.verticalAnimation(holder, position);
    }


    public boolean setMessageList(List<Message> messageList) {
        if (this.messageList.isEmpty()) {
            this.messageList = messageList;
            return true;
        } else {
            this.messageList.addAll(0, messageList);
            return false;
        }
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public void insertAtEnd(Message message) {
        boolean contains = false;

        for (Message inList : messageList) {
            contains = inList.getLocalId() != null && message.getLocalId() != null && inList.getLocalId().equals(message.getLocalId());
        }
        if (!contains) {
            this.messageList.add(messageList.size(), message);
        }
    }

    public void updateMessage(Message message) {
        for (int i = 0; i < messageList.size(); i++) {
            Message inList = messageList.get(i);
            if (inList.getLocalId() != null && message.getLocalId() != null && inList.getLocalId().equals(message.getLocalId())) {
                messageList.set(i, message);
                notifyItemChanged(i);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
