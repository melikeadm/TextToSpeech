package com.project.wecycle;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class OpenBTActivity extends AppCompatActivity implements
        TextToSpeech.OnInitListener {
    private TextToSpeech tts;

    Button btnOpenBT;
    private BluetoothAdapter mBTAdapter;
    private static final int BT_ENABLE_REQUEST = 10;                               // This is the code we use for BT Enable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_bt);
        tts = new TextToSpeech(this, this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        btnOpenBT = (Button) findViewById(R.id.btnOpenBT);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();


        if (!mBTAdapter.isEnabled()) {

            mBTAdapter = BluetoothAdapter.getDefaultAdapter();

            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, BT_ENABLE_REQUEST);
        }
        speakOut();
        btnOpenBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Homescreen.class);
                startActivity(intent);
            }
        });
        speakOut2();
    }
    @Override// TODO Auto-generated method stub
    //   btnSpeak.setEnabled(true);
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.getDefault());
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Dil desteklenmiyor");
            } else {
                speakOut();
            }
        } else {
            Log.e("TTS", "Initilization Failed");
        }
    }
    private void speakOut() {
        tts.speak("Cihazınızın Buluutut özelliğini açmak için Evete tıklayın! "
                , TextToSpeech.QUEUE_FLUSH, null);
    }
    private void speakOut2() {
        tts.speak("Bağlanmak için tıklayın! ", TextToSpeech.QUEUE_FLUSH,
                null);
    }


    @Override
    public void onDestroy() {
        // Don't forget to shutdown!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

}


