package com.sergeymild.allychatdemo;

import android.os.Bundle;

public class MainActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        showBack();

        getFragmentManager().beginTransaction()
                .replace(R.id.activity_fragment_place, ChatFragment.newInstance(getIntent()), ChatFragment.TAG)
                .commit();
    }
}
