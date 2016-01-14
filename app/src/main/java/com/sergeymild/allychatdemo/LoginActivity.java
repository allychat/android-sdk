package com.sergeymild.allychatdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Button;
import android.widget.EditText;

import com.sergeymild.allychatdemo.gcm.AllyChattPreferences;
import com.sergeymild.allychatdemo.gcm.RegistrationIntentService;
import com.sergeymild.chat.AllyChat;
import com.sergeymild.chat.callbacks.OnFailureInitialize;
import com.sergeymild.chat.callbacks.OnSuccessInitialize;
import com.sergeymild.chat.models.ErrorState;
import com.sergeymild.chat.models.SdkSettings;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sergeyMild on 27/12/15.
 */
public class LoginActivity extends BaseActivity {
    @Bind(R.id.login)                   EditText loginEditText;
    @Bind(R.id.login_button)            Button loginButton;
    @Bind(R.id.login_text_input_layout) TextInputLayout loginTextInputLayout;

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

        loginEditText.setText("AGC1O4");
    }

    @OnClick(R.id.login_button)
    protected void onLoginClicked() {
        loginButton.setEnabled(false);
        String loginName = loginEditText.getText().toString();
        boolean hasError = loginName.length() <= 0;
        String error = hasError ? getString(R.string.login_error) : null;
        loginTextInputLayout.setErrorEnabled(hasError);
        loginTextInputLayout.setError(error);

        if (hasError) {
            loginButton.setEnabled(true);
            return;
        }

        if (!SdkSettings.getInstance().isInProcessInitializing()) {
            SdkSettings.getInstance().setIsInProcessInitializing(true);
            AllyChat.Builder builder = new AllyChat.Builder().setContext(getApplicationContext());

            builder.setAlias(loginEditText.getText().toString());
            builder.setHost("my-dev.allychat.ru")
                    .setAppId("app")
                    .setIsLoggingEnabled(true);

            builder.setOnSuccessInitialize(chat -> {

                Intent intent = new Intent(getApplicationContext(), RegistrationIntentService.class);
                startService(intent);

            }).setOnFailureInitialize(errorState -> {
                loginButton.setEnabled(true);
                SdkSettings.getInstance().setIsInProcessInitializing(false);
            }).build();
        }

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
