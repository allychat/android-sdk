package com.sergeymild.allychat.view.rooms;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sergeymild.allychat.RoomFragmentListener;
import com.sergeymild.chat.callbacks.SimpleChatCallback;
import com.sergeymild.chat.models.Room;
import com.sergeymild.chat.services.http.AllyChatApi;
import com.sergeymild.event_bus.Consumer;
import com.sergeymild.event_bus.EventBus;

public class NewChatFloatingActionButton extends FloatingActionButton {
    public NewChatFloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        final MaterialDialog.InputCallback aliasInputDialogSubmitCallback = new MaterialDialog.InputCallback() {
            @Override
            public void onInput(MaterialDialog dialog, CharSequence input) {
                final MaterialDialog progressDialog = new MaterialDialog.Builder(getContext())
                        .progress(true, 0).content("Processing").show();
                AllyChatApi.createRoom(input.toString(), new SimpleChatCallback<Room>() {
                    @Override
                    public void success(final Room result) {
                        super.success(result);
                        progressDialog.dismiss();
                        EventBus.getInstance().of(RoomFragmentListener.class)
                                .ifPresent(new Consumer<RoomFragmentListener>() {
                                    @Override
                                    public void accept(RoomFragmentListener listener) {
                                        listener.startChatActivity(result);
                                    }
                                });
                    }
                });
            }
        };


        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getContext())
                        .title("New conversation")
                        .positiveText("Start")
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input("recipient's alias", "", aliasInputDialogSubmitCallback)
                        .show();
            }
        });
    }
}
