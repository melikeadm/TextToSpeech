/*
 * Released under MIT License http://opensource.org/licenses/MIT
 * Copyright (c) 2013 Plasty Grove
 * Refer to file LICENSE or URL above for full text
 */

package com.project.wecycle;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements
        TextToSpeech.OnInitListener {

    private static final String TAG = "BlueTest5-MainActivity";
    private int mMaxChars = 50000;//Default
    private UUID mDeviceUUID;
    private BluetoothSocket mBTSocket;
    private ReadInput mReadThread = null;
    private TextToSpeech tts;


    private boolean mIsUserInitiatedDisconnect = false;

    // All controls here
    private TextView mTxtReceive;
    private EditText mEditSend;
    private Button mBtnDisconnect;
    private Button mBtnSend;
    private Button mBtnClear;
    private Button mBtnClearInput;
    private ScrollView scrollView;
    private CheckBox chkScroll;
    private CheckBox chkReceiveText;

    private boolean mIsBluetoothConnected = false;

    private BluetoothDevice mDevice;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityHelper.initialize(this);
        tts = new TextToSpeech(this, this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        mDevice = b.getParcelable(Homescreen.DEVICE_EXTRA);
        mDeviceUUID = UUID.fromString(b.getString(Homescreen.DEVICE_UUID));
        mMaxChars = b.getInt(Homescreen.BUFFER_SIZE);

        Log.d(TAG, "Ready");

        mBtnDisconnect = (Button) findViewById(R.id.btnDisconnect);
        mBtnDisconnect.setVisibility(View.GONE);
        mBtnSend = (Button) findViewById(R.id.btnSend);
        mBtnSend.setVisibility(View.GONE);
        mBtnClear = (Button) findViewById(R.id.btnClear);
        mBtnClear.setVisibility(View.GONE);
        mTxtReceive = (TextView) findViewById(R.id.txtReceive);
        mTxtReceive.setVisibility(View.GONE);
        mEditSend = (EditText) findViewById(R.id.editSend);
        mEditSend.setVisibility(View.GONE);
        scrollView = (ScrollView) findViewById(R.id.viewScroll);
        chkScroll = (CheckBox) findViewById(R.id.chkScroll);
        chkScroll.setVisibility(View.GONE);
        chkReceiveText = (CheckBox) findViewById(R.id.chkReceiveText);
        chkReceiveText.setVisibility(View.GONE);
        mBtnClearInput = (Button) findViewById(R.id.btnClearInput);
        mBtnClearInput.setVisibility(View.GONE);

        mTxtReceive.setMovementMethod(new ScrollingMovementMethod());

        mBtnDisconnect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mIsUserInitiatedDisconnect = true;
                new DisConnectBT().execute();
            }
        });

        mBtnSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                try {
                    mBTSocket.getOutputStream().write(mEditSend.getText().toString().getBytes());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        mBtnClear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mEditSend.setText("");
            }
        });

        mBtnClearInput.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mTxtReceive.setText("");
            }
        });

    }

    @Override
    public void onInit(int status) {
        // TODO Auto-generated method stub

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language is not supported");
            } else {
                //   btnSpeak.setEnabled(true);
                //speakOut();
            }

        } else {
            Log.e("TTS", "Initilization Failed");
        }

    }


    private void solaDon() {
        tts.speak("Sola dön", TextToSpeech.QUEUE_FLUSH, null);
    }
    private void sagaDon() {
        tts.speak("Sağa dön", TextToSpeech.QUEUE_FLUSH, null);
    }
    private void speakOut3() {
        tts.speak("Cihaza erişilemiyor", TextToSpeech.QUEUE_FLUSH, null);
    }
    public void basarili() {
        tts.speak("Bisiklet sürmeye hazırsın", TextToSpeech.QUEUE_FLUSH, null);


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

    private class ReadInput implements Runnable {
        private boolean bStop = false;
        private Thread t;
        public ReadInput() {
            t = new Thread(this, "Input Thread");
            t.start();
            basarili();
        }
        public boolean isRunning() {
            return t.isAlive();
        }


        @Override
        public void run() {
            InputStream inputStream;
            try {
                inputStream = mBTSocket.getInputStream();
                while (!bStop) {
                    byte[] buffer = new byte[10];
                    if (inputStream.available() > 0) {
                        inputStream.read(buffer);
                        int i = 0;
                        for (i = 0; i < buffer.length && buffer[i] != 0; i++) {
                        }
                        final String strInput = new String(buffer, 0, i);
                        if (chkReceiveText.isChecked()) {
                            mTxtReceive.post(new Runnable() {
                                @Override
                                public void run() {
                                    //		String[] degerler =	strInput.split(",");
                                    String solLazer = strInput.substring(0, 1);
                                    String sagLazer = strInput.substring(2, 3);
                                    String mesafeSensor = strInput.substring(4, (strInput.length() - 1));
                                    Integer mesafeBilgisi = Integer.valueOf(mesafeSensor);


                                    if (mesafeBilgisi < 200 && solLazer.equals("1") && sagLazer.equals("1")) {
                                        Random rnd = new Random();
                                        int sayi = rnd.nextInt(2);
                                        if (sayi == 1) {
                                            solaDon();
                                            //Toast.makeText(getApplicationContext(),"sayı 1",Toast.LENGTH_LONG).show();
                                        } else if (sayi == 0) {
                                            sagaDon();
                                            //Toast.makeText(getApplicationContext(),"sayı 0",Toast.LENGTH_LONG).show();
                                        } else {
                                            //	Toast.makeText(getApplicationContext(),"Sola Don sayı farklı",Toast.LENGTH_LONG).show();
                                        }
                                    } else if (mesafeBilgisi <= 200) {
                                        if (solLazer.equals("0") && sagLazer.equals("1")) {
                                            sagaDon();
                                        } else if (sagLazer.equals("0") && solLazer.equals("1")) {
                                            solaDon();
                                        }
                                    }









								/*	for (String deger:degerler)
									{
									//	if( (Integer.valueOf(deger.substring(1,3)))>80)

										//if(deger.substring(1,3).equals("200")) {


										if( deger.substring(0,1).equals("1")  )
										{
											speakOut(); //Sola dön Sesi
											//Toast.makeText(getApplicationContext(),"1le başlıyor",Toast.LENGTH_LONG).show();
										}
										else if(deger.substring(0,1).equals("0") )
										{
											sagaDon(); //Engel Var
										//	Toast.makeText(getApplicationContext(),"2 ile başlıyor",Toast.LENGTH_LONG).show();

										}

										else
										{
										//	Toast.makeText(getApplicationContext(),"Engel Yok",Toast.LENGTH_LONG).show();


										} //}
									} */
                                    mTxtReceive.append(strInput + "\n");
                                    int txtLength = mTxtReceive.getEditableText().length();
                                    if (txtLength > mMaxChars) {
                                        mTxtReceive.getEditableText().delete(0, txtLength - mMaxChars);
                                    }
                                    if (chkScroll.isChecked()) { // Scroll only if this is checked
                                        scrollView.post(new Runnable() { // Snippet from http://stackoverflow.com/a/4612082/1287554
                                            @Override
                                            public void run() {
                                                scrollView.fullScroll(View.FOCUS_DOWN);
                                            }
                                        });
                                    }
                                }
                            });
                        }

                    }
                    Thread.sleep(500);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        public void stop() {
            bStop = true;
        }

    }

    private class DisConnectBT extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (mReadThread != null) {
                mReadThread.stop();
                while (mReadThread.isRunning())
                    ; // Wait until it stops
                mReadThread = null;

            }

            try {
                mBTSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mIsBluetoothConnected = false;
            if (mIsUserInitiatedDisconnect) {
                finish();
            }
        }

    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        if (mBTSocket != null && mIsBluetoothConnected) {
            new DisConnectBT().execute();
        }
        Log.d(TAG, "Paused");
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mBTSocket == null || !mIsBluetoothConnected) {
            new ConnectBT().execute();
        }
        Log.d(TAG, "Resumed");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Stopped");
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean mConnectSuccessful = true;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MainActivity.this, "Lütfen Bekleyin", "Bağlanıyor");// http://stackoverflow.com/a/11130220/1287554
        }

        @Override
        protected Void doInBackground(Void... devices) {

            try {
                if (mBTSocket == null || !mIsBluetoothConnected) {
                    mBTSocket = mDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mBTSocket.connect();
                }
            } catch (IOException e) {
                // Unable to connect to device
                e.printStackTrace();
                mConnectSuccessful = false;
            }
            return null;
        }

        public void basarili2() {

            tts.speak("Bisiklet sürmeye hazırsın", TextToSpeech.QUEUE_FLUSH, null);


        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


            if (!mConnectSuccessful) {
                speakOut3();
                //Toast.makeText(getApplicationContext(), "Could not connect to device. Is it a Serial device? Also check if the UUID is correct in the settings", Toast.LENGTH_LONG).show();
                finish();
            } else {
                //speakOut3();
                msg("WeCycle ile iletişim kuruldu!");
                //tts.speak("Bisiklet sürmeye hazırsın", TextToSpeech.QUEUE_FLUSH, null);
                mIsBluetoothConnected = true;
                mReadThread = new ReadInput(); // Kick off input reader

            }

            progressDialog.dismiss();
        }


    }

}
