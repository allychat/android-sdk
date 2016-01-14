package com.sergeymild.allychatdemo;

import android.os.Bundle;

/**
 * Created by sergeyMild on 28/12/15.
 */
public class RoomsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        showBack();

        getFragmentManager().beginTransaction()
                .replace(R.id.activity_fragment_place, RoomsFragment.newInstance(), RoomsFragment.TAG)
                .commit();

    }
}
