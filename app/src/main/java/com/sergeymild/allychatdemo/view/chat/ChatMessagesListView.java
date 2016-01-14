package com.sergeymild.allychatdemo.view.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.sergeymild.allychatdemo.OperatorChatFragmentCallbacks;
import com.sergeymild.allychatdemo.R;
import com.sergeymild.allychatdemo.adapter.MessagesAdapter;
import com.sergeymild.allychatdemo.adapter.SectionedRecyclerViewAdapter;
import com.sergeymild.allychatdemo.view.EndlessRecyclerOnScrollListener;
import com.sergeymild.allychatdemo.view.SpaceItemDecorator;
import com.sergeymild.chat.models.Message;
import com.sergeymild.event_bus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sergeyMild on 09/12/15.
 */
public class ChatMessagesListView extends FrameLayout implements IChatMessagesListView {
    @Bind(R.id.list_view) RecyclerView listView;
    private MessagesAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private SectionedRecyclerViewAdapter sectionedAdapter;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private long lastSectionDate = 0;

    public ChatMessagesListView(Context context, AttributeSet attrs) {
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
        linearLayoutManager.setStackFromEnd(true);
        listView.setLayoutManager(linearLayoutManager);
        listView.addItemDecoration(new SpaceItemDecorator(getContext()));
        adapter = new MessagesAdapter();

        sectionedAdapter = new SectionedRecyclerViewAdapter(getContext(), R.layout.chat_section_list_item, R.id.section_text, adapter);

        //Apply this adapter to the RecyclerView
        listView.setAdapter(sectionedAdapter);


        EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(final int currentPage) {
                EventBus.getInstance().of(OperatorChatFragmentCallbacks.class)
                        .ifPresent(callbacks -> callbacks.showMoreMessages(currentPage));
            }
        };
        listView.addOnScrollListener(endlessRecyclerOnScrollListener);
    }


    public void setMessageList(@NonNull List<Message> messageList) {
        boolean isDataChanged = adapter.setMessageList(messageList);
        List<Message> messageList1 = adapter.getMessageList();
        List<SectionedRecyclerViewAdapter.Section> sections = new ArrayList<>();
        Set<String> date = new HashSet<>();
        for (int i = 0; i < messageList1.size(); i++) {
            String dateString = dateFormat.format(messageList1.get(i).getCreatedAtMillis());
            if (!date.contains(dateString)) {
                sections.add(new SectionedRecyclerViewAdapter.Section(i, dateString));
                if (lastSectionDate < messageList1.get(i).getCreatedAtMillis()) {
                    lastSectionDate = messageList1.get(i).getCreatedAtMillis();
                }
            }
            date.add(dateString);
        }
        sectionedAdapter.setSections(sections);
        if (isDataChanged) {
            sectionedAdapter.notifyDataSetChanged();
        } else {
            sectionedAdapter.notifyItemRangeInserted(0, messageList.size());
        }
    }

    public void addMessageToList(@NonNull Message message) {
        boolean isNewSectionInserted = false;
        Calendar messageCalendar = Calendar.getInstance();
        message.setCreatedAtMillis(messageCalendar.getTimeInMillis());
        Calendar lastSectionDateCalendar = Calendar.getInstance();

        messageCalendar.setTimeInMillis(message.getCreatedAtMillis());
        lastSectionDateCalendar.setTimeInMillis(lastSectionDate);

        adapter.insertAtEnd(message);
        if (messageCalendar.get(Calendar.DAY_OF_YEAR) > lastSectionDateCalendar.get(Calendar.DAY_OF_YEAR)) {
            lastSectionDate = messageCalendar.getTimeInMillis();
            List<SectionedRecyclerViewAdapter.Section> sectionsList = sectionedAdapter.getSectionsList();
            sectionsList.add(new SectionedRecyclerViewAdapter.Section(adapter.getItemCount() - 1, dateFormat.format(lastSectionDate)));
            sectionedAdapter.setSections(sectionsList);
            isNewSectionInserted = true;
        }

        int totalItemsCount = sectionedAdapter.getSectionsCount() + adapter.getItemCount();
        if (isNewSectionInserted) {
            sectionedAdapter.notifyItemRangeInserted(totalItemsCount - 2, totalItemsCount);
        } else {
            sectionedAdapter.notifyItemInserted(totalItemsCount);
        }
        linearLayoutManager.smoothScrollToPosition(listView, null, totalItemsCount);
    }

    public void updateMessageInList(@NonNull Message message) {
        adapter.updateMessage(message);
    }
}
