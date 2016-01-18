package com.sergeymild.allychatdemo;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sergeymild.allychatdemo.view.chat.IChatRoomView;
import com.sergeymild.chat.callbacks.NetworkStateListener;
import com.sergeymild.chat.callbacks.SimpleChatCallback;
import com.sergeymild.chat.event_publisher.OnMessage;
import com.sergeymild.chat.models.Message;
import com.sergeymild.chat.services.http.ChatUtils;
import com.sergeymild.chat.utils.Checks;
import com.sergeymild.event_bus.EventBus;
import com.sergeymild.filepicker.ChooseDialog;
import com.sergeymild.filepicker.GalleryConstants;
import com.sergeymild.filepicker.models.PhotoEntry;

import java.io.File;
import java.util.List;

/**
 * Created by sergeyMild on 08/12/15.
 */
public class ChatFragment extends Fragment implements NetworkStateListener, OperatorChatFragmentCallbacks, OnMessage {
    public static final String TAG = "ChatFragment";
    private ChatActivity chatActivity;
    private int MESSAGE_LOADING_COUNT = 50;

    private IChatRoomView roomView;
    public static final int CAMERA_PHOTO = 0x10;
    private String roomId;

    public static ChatFragment newInstance(Intent intent) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isSupport", intent.getBooleanExtra("isSupport", false));
        bundle.putString("roomId", intent.getStringExtra("roomId"));
        ChatFragment chatFragment = new ChatFragment();
        chatFragment.setArguments(bundle);
        return chatFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        chatActivity = (ChatActivity) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chat_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        roomView = (IChatRoomView) view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        roomId = getArguments().getString("roomId");
        boolean isSupport = getArguments().getBoolean("isSupport");

        ChatUtils.getMessages(roomId, true, MESSAGE_LOADING_COUNT, new SimpleChatCallback<List<Message>>() {
            @Override
            public void success(List<Message> result) {
                super.success(result);
                roomView.setMessageList(result);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getInstance().register(this, OperatorChatFragmentCallbacks.class);
        ChatUtils.registerListeners(this);
        roomView.onInternetConnectionChanged(Checks.checkOnline());
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getInstance().unRegister(this, OperatorChatFragmentCallbacks.class);
        ChatUtils.unRegisterListeners(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ChatUtils.clearSession(true);
    }

    @Override
    public void onNetworkStateChanged(boolean isConnected) {
        roomView.onInternetConnectionChanged(isConnected);
    }

    @Override
    public void onBackClicked() {
        getActivity().onBackPressed();
    }

    @Override
    public void showMoreMessages(int offset) {
        ChatUtils.getMoreMessages(roomId, true, MESSAGE_LOADING_COUNT, new SimpleChatCallback<List<Message>>() {
            @Override
            public void success(List<Message> result) {
                super.success(result);
                roomView.setMessageList(result);
            }
        });
    }

    @Override
    public void sendMessage(@NonNull Message message) {
        ChatUtils.sendMessage(message);
    }

    @Override
    public void resendMessage(@NonNull Message message) {
        ChatUtils.resendMessage(message);
    }

    @Override
    public void choosePhoto() {
        new ChooseDialog(getActivity())
                .setFragmentForResult(this)
                .setOnPathGenerated(path -> chatActivity.absolutePath = path)
                .show();
    }

    @Override
    public void onMessage(@NonNull Message message) {
        roomView.addMessageToList(message);
    }

    @Override
    public void onMessageStatusChanged(@NonNull Message message) {
        roomView.updateMessageInList(message);
    }

    @Override
    public void onMessageEventChanged(@NonNull Message message) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == GalleryConstants.FAIL || resultCode == Activity.RESULT_CANCELED) return;

        String path = null;
        switch (requestCode) {
            case GalleryConstants.CAMERA_REQUEST_CODE:
                path = chatActivity.absolutePath;
                break;
            case GalleryConstants.GALLERY_REQUEST_CODE:
                PhotoEntry photoEntry = (PhotoEntry) data.getExtras().getSerializable(GalleryConstants.PHOTO_ENTRY);
                path = photoEntry.path;
                break;
            case GalleryConstants.FILES_REQUEST_CODE:
                String file = data.getExtras().getString(GalleryConstants.FILE_ENTRY);
                path = file;
        }

        if (!TextUtils.isEmpty(path) && new File(path).exists()) {
            Message message = ChatUtils.buildMessage("support", "", path);
            roomView.addMessageToList(message);
            ChatUtils.sendMessage(message);
        }
    }
}
