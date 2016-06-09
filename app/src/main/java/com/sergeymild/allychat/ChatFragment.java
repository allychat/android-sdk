package com.sergeymild.allychat;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;
import com.google.gson.reflect.TypeToken;
import com.sergeymild.allychat.view.chat.IChatRoomView;
import com.sergeymild.chat.AllyChat;
import com.sergeymild.chat.callbacks.ChatCallback;
import com.sergeymild.chat.callbacks.NetworkStateListener;
import com.sergeymild.chat.callbacks.SimpleChatCallback;
import com.sergeymild.chat.event_publisher.OnMessage;
import com.sergeymild.chat.models.ErrorState;
import com.sergeymild.chat.models.Message;
import com.sergeymild.chat.models.Room;
import com.sergeymild.chat.services.http.AllyChatApi;
import com.sergeymild.chat.services.http.AllyChatApiDeprecated;
import com.sergeymild.chat.services.http.AllyChatRestAdapterBuilder;
import com.sergeymild.chat.utils.Checks;
import com.sergeymild.event_bus.EventBus;

import java.io.File;
import java.util.Collections;
import java.util.List;

import sergeymild.com.library.ChooseDialog;
import sergeymild.com.library.GalleryActivity;
import sergeymild.com.library.models.PhotoEntry;

/**
 * Created by sergeyMild on 08/12/15.
 */
public class ChatFragment extends Fragment implements NetworkStateListener, OperatorChatFragmentCallbacks, OnMessage {
    public static final String TAG = "ChatFragment";
    private String absolutePath;
    private int MESSAGE_LOADING_COUNT = 50;

    private IChatRoomView roomView;
    public static final int CAMERA_PHOTO = 0x10;
    private Room room;


    public static ChatFragment newInstance(Room room) {
        Bundle bundle = new Bundle();
        bundle.putString("room", AllyChatRestAdapterBuilder.buildGson().toJson(room));

        ChatFragment chatFragment = new ChatFragment();
        chatFragment.setArguments(bundle);
        return chatFragment;
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BaseActivity activity = (BaseActivity) getActivity();
        activity.showBackButton();
        activity.setTitle("Чат");

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
        room = AllyChatRestAdapterBuilder.buildGson().fromJson(
                getArguments().getString("room"), new TypeToken<Room>(){}.getType());

        if (room.getLastMessage() != null)
            AllyChatApi.messagesBeforeMessage(room.getLastMessage(), room, MESSAGE_LOADING_COUNT, new SimpleChatCallback<List<Message>>() {
                @Override
                public void success(List<Message> result) {
                    super.success(result);
                    result.add(room.getLastMessage());
                    roomView.setMessageList(result);
                }
            });
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getInstance().register(this, OperatorChatFragmentCallbacks.class);

        roomView.onInternetConnectionChanged(Checks.checkOnline());
        AllyChatApi.registerListeners(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getInstance().unRegister(this, OperatorChatFragmentCallbacks.class);
        AllyChatApi.unRegisterListeners(this);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onNetworkStateChanged(boolean isConnected) {
        roomView.onInternetConnectionChanged(isConnected);
        if (isConnected) {
            // reload messages after reconnect
            AllyChatApi.getRooms(new SimpleChatCallback<List<Room>>() {
                @Override
                public void success(List<Room> result) {
                    room = Stream.of(result)
                            .filter(new Predicate<Room>() {
                                @Override
                                public boolean test(Room value) {
                                    return value.getId().equals(room.getId());
                                }
                            })
                            .findFirst().get();
                    AllyChatApi.messagesBeforeMessage(room.getLastMessage(), room, MESSAGE_LOADING_COUNT, new SimpleChatCallback<List<Message>>() {
                        @Override
                        public void success(List<Message> result) {
                            super.success(result);
                            roomView.setMessageList(Collections.<Message>emptyList());
                            result.add(room.getLastMessage());
                            roomView.setMessageList(result);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onBackClicked() {
        getActivity().onBackPressed();
    }

    @Override
    public void showMoreMessages(Message lastMessage, boolean after) {
        Log.e("CachedMessages", "onShowMore");
        AllyChatApi.messagesBeforeMessage(lastMessage, room, MESSAGE_LOADING_COUNT, new SimpleChatCallback<List<Message>>() {
            @Override
            public void success(List<Message> result) { //todo callsuper?
                roomView.setMessageList(result);
            }
        });
    }

    @Override
    public void sendMessage(@NonNull String messageText) {
        AllyChatApi.sendMessage(
                AllyChatApi.buildMessage(room, messageText, null),
                addMessageToListOnSuccess, null);
    }

    @Override
    public void resendMessage(@NonNull Message message) {
        AllyChatApi.resendMessage(message, addMessageToListOnSuccess);
    }

    @Override
    public void choosePhoto() {
        new ChooseDialog(getActivity())
                .setChooseDialogListener(new ChooseDialog.ChooseDialogListener() {
                    @Override
                    public void onCamera(Intent takePictureIntent, String absolutePath) {
                        ChatFragment.this.absolutePath = absolutePath;
                        startActivityForResult(takePictureIntent, CAMERA_PHOTO);
                    }

                    @Override
                    public void onGallery() {
                        startActivityForResult(new Intent(getActivity(), GalleryActivity.class), GalleryActivity.REQUEST_CODE);
                    }
                })
                .show();
    }

    @Override
    public void onMessage(@NonNull Message message) {
        if (message.getRoom().equals(room.getId()))
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
        if (requestCode == CAMERA_PHOTO && new File(absolutePath).exists()) {
            AllyChatApi.buildAndSend(room, "", absolutePath, null, addMessageToListOnSuccess);
            return;
        }

        if (requestCode != GalleryActivity.REQUEST_CODE) return;
        if (resultCode == GalleryActivity.SUCCESS) {
            PhotoEntry photoEntry = (PhotoEntry) data.getExtras().getSerializable(GalleryActivity.PHOTO_ENTRY);
            if (photoEntry != null && !TextUtils.isEmpty(photoEntry.path) && new File(photoEntry.path).exists()) {
                Message message = AllyChatApi.buildMessage(room, "", photoEntry.path);
                roomView.addMessageToList(message);
                AllyChatApi.sendMessage(message, addMessageToListOnSuccess, null);
            }
        }
    }


    private SimpleChatCallback<Message> addMessageToListOnSuccess = new SimpleChatCallback<Message>() {
        @Override
        public void success(Message result) {
            super.success(result);
            roomView.addMessageToList(result);
        }
    };
}
