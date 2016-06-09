package com.sergeymild.allychat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.sergeymild.chat.AllyChat;
import com.sergeymild.chat.models.Room;

/**
 * Created by sergeyMild on 28/12/15.
 */
public class RoomsActivity extends BaseActivity implements RoomsFragment.ChatSelectListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        getFragmentManager().beginTransaction()
                .add(R.id.activity_fragment_place, RoomsFragment.newInstance())
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (AllyChat.getInstance() != null) AllyChat.getInstance().close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AllyChat.getInstance() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    public void onChatSelected(@NonNull Room room) {
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.activity_fragment_place, ChatFragment.newInstance(room))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().findFragmentById(R.id.activity_fragment_place) instanceof RoomsFragment)
            super.onBackPressed();
        else
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(R.id.activity_fragment_place, RoomsFragment.newInstance(), RoomsFragment.TAG)
                    .commit();
    }
}
