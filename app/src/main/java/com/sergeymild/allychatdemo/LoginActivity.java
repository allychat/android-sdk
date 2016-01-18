package com.sergeymild.allychatdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.sergeymild.allychatdemo.gcm.AllyChattPreferences;
import com.sergeymild.allychatdemo.gcm.RegistrationIntentService;
import com.sergeymild.chat.AllyChat;
import com.sergeymild.chat.models.SdkSettings;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sergeyMild on 27/12/15.
 */
public class LoginActivity extends BaseActivity {
    @Bind(R.id.login)                   protected EditText loginEditText;
    @Bind(R.id.host)                    protected EditText hostEditText;
    @Bind(R.id.login_button)            protected Button loginButton;
    @Bind(R.id.login_text_input_layout) protected TextInputLayout loginTextInputLayout;
    @Bind(R.id.progressBar)             protected ProgressBar progressBar;

    private BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            boolean sentToken = sharedPreferences.getBoolean(AllyChattPreferences.SENT_TOKEN_TO_SERVER, false);
            if (sentToken) {
                //success
                SdkSettings.getInstance().setIsInProcessInitializing(false);
                startActivity(new Intent(LoginActivity.this, RoomsActivity.class));
                finish();
            } else {
                //error
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        hostEditText.setText("https://alfa-dev.allychat.ru");
        loginEditText.setText("AGC1O4");
    }

    @OnClick(R.id.login_button)
    protected void onLoginClicked() {
        progressBar.setVisibility(View.VISIBLE);
        loginButton.setText(R.string.login_button_loading);
        loginEditText.setEnabled(false);
        loginButton.setEnabled(false);
        hostEditText.setEnabled(false);
        String loginName = loginEditText.getText().toString();
        boolean hasError = loginName.length() <= 0;
        String error = hasError ? getString(R.string.login_error) : null;
        loginTextInputLayout.setErrorEnabled(hasError);
        loginTextInputLayout.setError(error);

        if (hasError) {
            loginButton.setEnabled(true);
            loginEditText.setEnabled(true);
            hostEditText.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            loginButton.setText(R.string.login_button);
            return;
        }

        if (!SdkSettings.getInstance().isInProcessInitializing()) {
            SdkSettings.getInstance().setIsInProcessInitializing(true);
            AllyChat.Builder builder = new AllyChat.Builder().setContext(getApplicationContext());

            builder.setAlias(loginEditText.getText().toString());
            builder.setHost(getHost())
                    .setAppId("sense")
                    .setIsLoggingEnabled(true);

            builder.setOnSuccessInitialize(chat -> {

                Intent intent = new Intent(getApplicationContext(), RegistrationIntentService.class);
                startService(intent);

            }).setOnFailureInitialize(errorState -> {
                progressBar.setVisibility(View.GONE);
                loginButton.setEnabled(true);
                loginEditText.setEnabled(true);
                hostEditText.setEnabled(true);
                loginButton.setText(R.string.login_button);
                SdkSettings.getInstance().setIsInProcessInitializing(false);
            }).build();
        }

    }

    private String getHost() {
        String host = hostEditText.getText().toString();
        return !TextUtils.isEmpty(host) ? host :  "https://alfa-dev.allychat.ru";
    }


    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(AllyChattPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
}
