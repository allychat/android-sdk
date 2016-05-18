package com.sergeymild.allychat;

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
import android.widget.Toast;

import com.sergeymild.allychat.gcm.AllyChattPreferences;
import com.sergeymild.allychat.gcm.RegistrationIntentService;
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
    @Bind(R.id.hostname)                EditText hostNameEditText;
    @Bind(R.id.appid)                   EditText appIdEditText;
    @Bind(R.id.login_button)            Button loginButton;
    @Bind(R.id.login_text_input_layout) TextInputLayout loginTextInputLayout;

    private BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(LoginActivity.this, "Registered gcm token, can login now", Toast.LENGTH_SHORT).show();
            loginButton.setEnabled(true);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        loginEditText.setText("AGC1O4");
        loginButton.setEnabled(false);
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


        String gcmToken = getSharedPreferences("prefs", Context.MODE_PRIVATE).getString("token", null);
        initializeChat(loginEditText.getText().toString(), hostNameEditText.getText().toString(),
                appIdEditText.getText().toString(), gcmToken);
    }


    /**
     * attempts to initialize AllyChat singleton; passing result of attempt to one of two callbacks.
     * @param alias actual user alias or a blank string in case of anonymous user
     * @param token token received after subscription to GCM push using AllyChat's SENDER_ID
     */
    private void initializeChat(String alias, String hostName, String appId, String token) {
        new AllyChat.Builder().setContext(getApplicationContext())
                .setAlias(alias)
                .setHost(hostName)
                .setAppId(appId)
                .setGcmPushToken(token)
                .setIsLoggingEnabled(true)
                .setOnSuccessInitialize(new OnSuccessInitialize() {
                    @Override
                    public void onSuccess(@NonNull AllyChat chat) {
                        startActivity(new Intent(LoginActivity.this, RoomsActivity.class));
                        finish();
                    }
                }).setOnFailureInitialize(new OnFailureInitialize() {
                    @Override
                    public void onFailInitialize(ErrorState errorState) {
                        loginButton.setEnabled(true);
                    }
                }).build();
    }


    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(AllyChattPreferences.REGISTRATION_COMPLETE));
        // start registering gcm token
        Intent intent = new Intent(getApplicationContext(), RegistrationIntentService.class);
        startService(intent);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
}
