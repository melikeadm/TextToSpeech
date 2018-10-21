package com.project.wecycle;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity implements
        TextToSpeech.OnInitListener{

    private TextToSpeech tts;
    private BluetoothAdapter mBTAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        tts = new TextToSpeech(this, this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);



        Thread background = new Thread() {
            public void run() {

                try {
                    // Thread will sleep for 5 seconds
                    sleep(6*1000);

                    // After 5 seconds redirect to another intent
                    mBTAdapter = BluetoothAdapter.getDefaultAdapter();

                    if(mBTAdapter.isEnabled()){
                        Intent i = new Intent(getBaseContext(), Homescreen.class);
                        startActivity(i);
                    }
                    else
                    {
                        Intent i = new Intent(getBaseContext(), OpenBTActivity.class);
                        startActivity(i);
                    }
                    //Remove activity
                    finish();

                } catch (Exception e) {

                }
            }
        };

        // start thread
        background.start();


    }



    @Override
    public void onInit(int status) {
        // TODO Auto-generated method stub

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.getDefault());

            // tts.setPitch(5); // set pitch level

            // tts.setSpeechRate(2); // set speech speed rate

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language is not supported");
            } else {
             //   btnSpeak.setEnabled(true);
                speakOut();
            }

        } else {
            Log.e("TTS", "Initilization Failed");
        }

    }


    private void speakOut() {

        tts.speak("Visaykıl uygulamasına hoşgeldiniz.", TextToSpeech.QUEUE_FLUSH, null);
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