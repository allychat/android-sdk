package com.sergeymild.allychatdemo;

import android.support.annotation.LayoutRes;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/**
 * Created by sergeyMild on 28/12/15.
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected Toolbar toolbar;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setUpToolbar();
        ViewCompat.setElevation(toolbar, getResources().getDimensionPixelSize(R.dimen.elevation));
    }

    protected void setUpToolbar() {
        setSupportActionBar(toolbar);
    }

    protected void showBack() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
