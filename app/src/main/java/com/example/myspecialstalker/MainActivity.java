package com.example.myspecialstalker;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_NO = 1;
    public static final int GRANTED = PackageManager.PERMISSION_GRANTED;

    public static final String NOT_READY_MSG = "Please Fill All Fields";
    public static final String READY_MSG = "Ready to Send SMS!";
    public static final String SP_PHONE_NO = "phoneNo";
    public static final String SP_MESSAGE = "message";
    public static final String PER_READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    public static final String PER_OUTGOING_CALLS = Manifest.permission.PROCESS_OUTGOING_CALLS;
    public static final String PER_SEND_SMS = Manifest.permission.SEND_SMS;
    public static final String PRE_DEFINED_MESSAGE = "I'm going to call this number: ";

    public final String[] PERMISSIONS_TO_REQUEST = {PER_READ_PHONE_STATE, PER_OUTGOING_CALLS,
            PER_SEND_SMS};

    public static String localPhoneNo;
    public static String localMessage;

    public static boolean phoneFlag = false;
    public static boolean messageFlag = true;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    EditText phoneNo, message;
    TextView title, phoneNoMissingData, messageMissingData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (checkPermission(PER_READ_PHONE_STATE) || checkPermission(PER_OUTGOING_CALLS) ||
                checkPermission(PER_SEND_SMS))
            requestPermissions();

        else
        {
            setContentView(R.layout.activity_main);
            launchActivity();
        }
    }

    public void launchActivity()
    {
        phoneNo = (EditText) findViewById(R.id.phone_number);
        message = (EditText) findViewById(R.id.text_message);
        title = (TextView) findViewById(R.id.instructions);

        messageMissingData = (TextView)findViewById(R.id.textView_message);
        phoneNoMissingData = (TextView) findViewById(R.id.textView_phoneNo);

        messageMissingData.setVisibility(View.INVISIBLE);
        phoneNoMissingData.setVisibility(View.INVISIBLE);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();

        localPhoneNo = sp.getString(SP_PHONE_NO, "");
        localMessage = sp.getString(SP_MESSAGE, "");

        phoneFlag = !localPhoneNo.equals("");
        messageFlag = !localMessage.equals("");
        if(!messageFlag)
        {
            message.setText(PRE_DEFINED_MESSAGE);
            localMessage = PRE_DEFINED_MESSAGE;
            messageFlag = true;
        }
        else
        {
            message.setText(localMessage);
        }
        phoneNo.setText(localPhoneNo);


        if (phoneFlag && messageFlag)
        {
            title.setText(READY_MSG);
        }
        else
        {
            title.setText(NOT_READY_MSG);
        }
        phoneNo.addTextChangedListener(phoneFieldWatcher());

        message.addTextChangedListener(messageFieldWatcher());
    }

    public TextWatcher phoneFieldWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                return;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() > 0)
                {
                    phoneFlag = true;
                    phoneNoMissingData.setVisibility(View.INVISIBLE);
                    if (isReady())
                    {
                        title.setText(READY_MSG);
                    }
                    else
                    {
                        title.setText(NOT_READY_MSG);
                    }
                }
                else
                {
                    title.setText(NOT_READY_MSG);
                    phoneFlag = false;
                    phoneNoMissingData.setVisibility(View.VISIBLE);
                }
                localPhoneNo = s.toString();
                editor.putString(SP_PHONE_NO, s.toString());
                editor.apply();
            }
            @Override
            public void afterTextChanged(Editable s) {
                return;
            }
        };
    }

    public TextWatcher messageFieldWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                return;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String msg = message.getText().toString();
                if (msg.equals("")) {
                    messageFlag = false;
                    title.setText(NOT_READY_MSG);
                    messageMissingData.setVisibility(View.VISIBLE);
                } else {
                    messageFlag = true;
                    messageMissingData.setVisibility(View.INVISIBLE);
                    if (isReady()) {
                        title.setText(READY_MSG);
                    } else {
                        title.setText(NOT_READY_MSG);
                    }
                }
                localMessage = s.toString();
                editor.putString(SP_MESSAGE, s.toString());
                editor.apply();
            }

            @Override
            public void afterTextChanged(Editable s) {
                return;
            }
        };
    }

    public boolean isReady()
    {
        return messageFlag && phoneFlag;
    }



    public boolean checkPermission(String permission)
    {
        return ContextCompat.checkSelfPermission(MainActivity.this, permission) != GRANTED;
    }

    public void requestPermissions()
    {
        ActivityCompat.requestPermissions(this, PERMISSIONS_TO_REQUEST, REQUEST_NO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_NO) {
            if (verifyingPermissions(grantResults)) {
                setContentView(R.layout.activity_main);
                launchActivity();
            }
            else
                requestPermissions();
        }
    }

    public boolean verifyingPermissions(int[] grantResults)
    {
        return grantResults.length == 3 && grantResults[0] == GRANTED && grantResults[1] == GRANTED
                && grantResults[2] == GRANTED;
    }

}
