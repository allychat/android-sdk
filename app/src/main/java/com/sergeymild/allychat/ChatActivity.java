package com.sergeymild.allychat;

import android.os.Bundle;

public class ChatActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        showBackButton();

//        getFragmentManager().beginTransaction()
//                .replace(R.id.activity_fragment_place, ChatFragment.newInstance(getIntent()), ChatFragment.TAG)
//                .commit();
    }
}
