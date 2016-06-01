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
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.skyfishjy.library.RippleBackground;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private Button receiver_btn;
    private Receiver mReceiver;
    private Button call_btn, show_btn;
    private ImageButton contact_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        mReceiver = new Receiver(this);

        receiver_btn = (Button) findViewById(R.id.receiver_button);
        receiver_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mReceiver.setStatus("receiver");
                //new Receive();
                Intent call_intent = new Intent(MainActivity.this, SendActivity.class);
                call_intent.putExtra("phone_number", "0911624707");
                startActivity(call_intent);
            }
        });

        show_btn = (Button) findViewById(R.id.showmapbutton);
        show_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent call_intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(call_intent);
            }
        });
        final RippleBackground rippleBackground = (RippleBackground)findViewById(R.id.content);
        rippleBackground.startRippleAnimation();

        contact_btn = (ImageButton) findViewById(R.id.contact_button);
        contact_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rippleBackground.stopRippleAnimation();
                Intent call_intent = new Intent(MainActivity.this, ContactActivity.class);
//                call_intent.putExtra("receiver", mReceiver);
                startActivity(call_intent);
                MainActivity.this.finish();
            }
        });
    }

    public class Receive {
        private final int ringing = 1;
        private final int dialing = 2;

        private Timer incoming_timer, outgoing_timer;
        private TimerTask timerTask;
        private int counter = 0;

        public String phone_number;

        public Receive() {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            //final Chronometer myChronometer = (Chronometer)findViewById(R.id.chronometer);
            PhoneStateListener callStateListener = new PhoneStateListener() {
                int lastState = TelephonyManager.CALL_STATE_IDLE;
                public void onCallStateChanged(int state, String incomingNumber)
                {
                    // TODO React to incoming call.
                    if(state==TelephonyManager.CALL_STATE_RINGING)
                    {
                        // Toast.makeText(getApplicationContext(), "Phone Is Ringing", Toast.LENGTH_LONG).show();
                        startTimer(ringing, incomingNumber);
                        lastState = TelephonyManager.CALL_STATE_RINGING;
                    }

                    if(state==TelephonyManager.CALL_STATE_IDLE)
                    {
//                        Toast.makeText(getApplicationContext(), phone_number, Toast.LENGTH_LONG).show();
                        if(lastState == TelephonyManager.CALL_STATE_RINGING) {
                            stopTimer(ringing);
                        }
                    }
                }
            };
            tm.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }

        public void startTimer(int state, String incomingNumber) {
            counter = 0;
            phone_number = incomingNumber;
            Log.d("number", phone_number);
            //set a new Timer
            if(state == ringing) {
                incoming_timer = new Timer();
                //initialize the TimerTask's job
                initializeTimerTask(state);
                //schedule the timer, after the first 0ms the TimerTask will run every 1sec
                incoming_timer.schedule(timerTask, 0, 1000);
            }
        }

        public void initializeTimerTask(int state) {
            if(state == ringing) {
                timerTask = new TimerTask() {
                    public void run() {
                        counter++;
                        Log.d("incoming", String.valueOf(counter));
                    }
                };
            }
        }

        public void stopTimer(int state) {
            //stop the timer, if it's not already null
            if(state == ringing) {
                if (incoming_timer != null) {
                    Log.d("incoming", String.valueOf(counter));     // 2 seconds delay
                    incoming_timer.cancel();
                    incoming_timer = null;

//                    Log.d("number1", phone_number);
//                    Toast.makeText(getApplicationContext(), "phone_number", Toast.LENGTH_LONG).show();
                    // get GPS and jump to the SendActivity to send the response
                    Intent sending = new Intent(MainActivity.this, SendActivity.class);
                    Log.d("number1", phone_number);
                    sending.putExtra("phone_number", phone_number);
                    startActivity(sending);
                }
            }
        }
    }
}

