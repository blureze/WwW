package com.example.user.www;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/***
 * After the caller send the request to get another person's location, jumps to this Activity and wait for the response.
 * The Activity will display the ringing time of each incoming phone call.
 */

public class WaitActivity extends AppCompatActivity {
    private TextView response_tv;
    private Receiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);

        response_tv = (TextView) findViewById(R.id.text_response);

//        mReceiver = getIntent().getParcelableExtra("receiver");
        new Receive();
    }

    private class Receive {
        private Timer incoming_timer;
        private TimerTask timerTask;
        private int counter = 0;
        private String response;
        private ArrayList<Integer> location;

        public Receive() {
            response = new String();
            location = new ArrayList<Integer>();
            /*count the calling time*/
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            //final Chronometer myChronometer = (Chronometer)findViewById(R.id.chronometer);
            PhoneStateListener callStateListener = new PhoneStateListener() {
                int lastState = TelephonyManager.CALL_STATE_IDLE;

                public void onCallStateChanged(int state, String incomingNumber) {
                    if (state == TelephonyManager.CALL_STATE_RINGING) {
                        Toast.makeText(getApplicationContext(), "Phone Is Ringing", Toast.LENGTH_LONG).show();
                        startTimer();
                        lastState = TelephonyManager.CALL_STATE_RINGING;
                    }
                    if (state == TelephonyManager.CALL_STATE_IDLE) {
                        //Toast.makeText(getApplicationContext(),"phone is neither ringing nor in a call", Toast.LENGTH_LONG).show();
                        if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                            stopTimer();
                        }
                    }
                }
            };
            tm.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }

        public void startTimer() {
            counter = 0;
            //set a new Timer
            incoming_timer = new Timer();
            //initialize the TimerTask's job
            initializeTimerTask();
            //schedule the timer, after the first 0ms the TimerTask will run every 1sec
            incoming_timer.schedule(timerTask, 0, 1000);
        }

        public void initializeTimerTask() {
            timerTask = new TimerTask() {
                public void run() {
                    counter++;
                    Log.d("incoming", String.valueOf(counter));
                }
            };
        }

        public void stopTimer() {
            //stop the timer, if it's not already null
            if (incoming_timer != null) {
                Log.d("incoming", String.valueOf(counter));     // 2 seconds delay
                response = response + String.valueOf(counter-2) + '\n';
                if(counter -2 == 10)
                    location.add(0);
                else
                    location.add(counter - 2);

                incoming_timer.cancel();
                incoming_timer = null;

                /* Add messages */
                response_tv.setText(response);
                if(location.size() == 6) {
                    Intent intent = new Intent(WaitActivity.this, MapActivity.class);
                    String lat = "2" + String.valueOf(location.get(3)) + "." + String.valueOf(location.get(4)) + String.valueOf(location.get(5));
                    String lng = "12" + String.valueOf(location.get(0)) + "." + String.valueOf(location.get(1)) + String.valueOf(location.get(2));
                    intent.putExtra("lat", lat);
                    intent.putExtra("lng", lng);
                    startActivity(intent);
                }
            }
        }
    }
}


