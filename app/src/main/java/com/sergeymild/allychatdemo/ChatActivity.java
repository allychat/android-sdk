package com.sergeymild.allychatdemo;

import android.os.Bundle;
import android.util.Log;

public class ChatActivity extends BaseActivity {
    private final String PATH = "path";
    protected String absolutePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        showBack();

        getFragmentManager().beginTransaction()
                .replace(R.id.activity_fragment_place, ChatFragment.newInstance(getIntent()), ChatFragment.TAG)
                .commit();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("logger", "onSaveInstanceState");
        outState.putString(PATH, absolutePath);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("logger", "onRestoreInstanceState");
        absolutePath = savedInstanceState.getString(PATH);
    }
}
