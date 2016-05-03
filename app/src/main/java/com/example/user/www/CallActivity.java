package com.example.user.www;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class CallActivity extends AppCompatActivity {
    private Button call_btn;
    private Timer timer;
    private TimerTask timerTask;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        call_btn = (Button) findViewById(R.id.call_button);
        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strInput = "0911624707";
                Intent myIntentDial;
                // ACTION_CALL -> call the number directly without the dialer
                // ACTION_DIAL -> ask users to select an application for calling
                if (ActivityCompat.checkSelfPermission(CallActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    myIntentDial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + strInput));
                    return;
                } else {
                    myIntentDial = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + strInput));
                }
                startActivity(myIntentDial);
            }
        });

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //final Chronometer myChronometer = (Chronometer)findViewById(R.id.chronometer);
        PhoneStateListener callStateListener = new PhoneStateListener() {
            int lastState = TelephonyManager.CALL_STATE_IDLE;
            public void onCallStateChanged(int state, String incomingNumber)
            {
                // TODO React to incoming call.
                String number=incomingNumber;
                if(state==TelephonyManager.CALL_STATE_RINGING)
                {
                    Toast.makeText(getApplicationContext(), "Phone Is Ringing", Toast.LENGTH_LONG).show();
                    startTimer();
                    lastState = TelephonyManager.CALL_STATE_RINGING;
                }
                if(state==TelephonyManager.CALL_STATE_OFFHOOK)
                {
                    Toast.makeText(getApplicationContext(),"Phone is Currently in A call", Toast.LENGTH_LONG).show();
                    //startTimer();

                    //myChronometer.start();
                    lastState = TelephonyManager.CALL_STATE_OFFHOOK;
                }
                if(state==TelephonyManager.CALL_STATE_IDLE)
                {
                    Toast.makeText(getApplicationContext(),"phone is neither ringing nor in a call", Toast.LENGTH_LONG).show();
                    if(lastState == TelephonyManager.CALL_STATE_RINGING) {
                        stopTimer();
                        //myChronometer.stop();
                    }
                }
            }
        };
        tm.listen(callStateListener,PhoneStateListener.LISTEN_CALL_STATE);
    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        counter = 0;
        //initialize the TimerTask's job
        initializeTimerTask();
        //schedule the timer, after the first 0ms the TimerTask will run every 1sec
        timer.schedule(timerTask, 0, 1000);
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                counter++;
                Log.d("test", String.valueOf(counter));
            }
        };
    }

    public void stopTimer() {
        //stop the timer, if it's not already null
        if (timer != null) {
            Log.d("test", String.valueOf(counter));
            timer.cancel();
            timer = null;
        }
    }
}
