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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private Button receiver_btn, contact_btn, show_btn;
    private Receiver mReceiver;

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
                new Receive();
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
        contact_btn = (Button) findViewById(R.id.contact_button);
        contact_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent call_intent = new Intent(MainActivity.this, ContactActivity.class);
//                call_intent.putExtra("receiver", mReceiver);
                startActivity(call_intent);
                MainActivity.this.finish();
            }
        });
    }

    private class Receive {
        private final int ringing = 1;
        private final int dialing = 2;

        private Timer incoming_timer, outgoing_timer;
        private TimerTask timerTask;
        private int counter = 0;
        private String phone_number;

        public Receive() {

            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            //final Chronometer myChronometer = (Chronometer)findViewById(R.id.chronometer);
            PhoneStateListener callStateListener = new PhoneStateListener() {
                int lastState = TelephonyManager.CALL_STATE_IDLE;
                public void onCallStateChanged(int state, String incomingNumber)
                {
                    // TODO React to incoming call.
                    phone_number = incomingNumber;
                    if(state==TelephonyManager.CALL_STATE_RINGING)
                    {
                        // Toast.makeText(getApplicationContext(), "Phone Is Ringing", Toast.LENGTH_LONG).show();
                        startTimer(ringing);
                        lastState = TelephonyManager.CALL_STATE_RINGING;
                    }
//                    if(state==TelephonyManager.CALL_STATE_OFFHOOK)
//                    {
//                        //Toast.makeText(getApplicationContext(),"Phone is Currently in A call", Toast.LENGTH_LONG).show();
//                        startTimer(dialing);
//                        lastState = TelephonyManager.CALL_STATE_OFFHOOK;
//                    }
                    if(state==TelephonyManager.CALL_STATE_IDLE)
                    {
                        //Toast.makeText(getApplicationContext(),"phone is neither ringing nor in a call", Toast.LENGTH_LONG).show();
                        if(lastState == TelephonyManager.CALL_STATE_RINGING) {
                            stopTimer(ringing);
                        }
//                        else if(lastState == TelephonyManager.CALL_STATE_OFFHOOK) {
//                            stopTimer(dialing);
//                        }
                    }
                }
            };
            tm.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }

        public void startTimer(int state) {
            counter = 0;
            //set a new Timer
            if(state == ringing) {
                incoming_timer = new Timer();
                //initialize the TimerTask's job
                initializeTimerTask(state);
                //schedule the timer, after the first 0ms the TimerTask will run every 1sec
                incoming_timer.schedule(timerTask, 0, 1000);
            }
            else if (state == dialing) {
                outgoing_timer = new Timer();
                //initialize the TimerTask's job
                initializeTimerTask(state);
                //schedule the timer, after the first 0ms the TimerTask will run every 1sec
                outgoing_timer.schedule(timerTask, 0, 1000);
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
            else if(state == dialing) {
                timerTask = new TimerTask() {
                    public void run() {
                        counter++;
                        Log.d("outgoing", String.valueOf(counter));
                        if(counter == 7) {     // 6 seconds delay
                            Log.d("outgoing", "hang up the phone");
                            // hang up the phone
                            hangup();
                        }
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

                    // get GPS and jump to the SendActivity to send the response
                    Intent sending = new Intent(MainActivity.this, SendActivity.class);
                    sending.putExtra("phone_number", phone_number);
                    startActivity(sending);
//                    onDestroy();
                }
            }
            else if(state == dialing) {
                if (outgoing_timer != null) {
                    //Log.d("outgoing", String.valueOf(counter));     // 2 seconds delay
                    outgoing_timer.cancel();
                    outgoing_timer = null;
                }
            }
        }

        public void hangup(){
            try {
                String serviceManagerName = "android.os.ServiceManager";
                String serviceManagerNativeName = "android.os.ServiceManagerNative";
                String telephonyName = "com.android.internal.telephony.ITelephony";
                Class<?> telephonyClass;
                Class<?> telephonyStubClass;
                Class<?> serviceManagerClass;
                Class<?> serviceManagerNativeClass;
                Method telephonyEndCall;
                Object telephonyObject;
                Object serviceManagerObject;
                telephonyClass = Class.forName(telephonyName);
                telephonyStubClass = telephonyClass.getClasses()[0];
                serviceManagerClass = Class.forName(serviceManagerName);
                serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
                Method getService = // getDefaults[29];
                        serviceManagerClass.getMethod("getService", String.class);
                Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
                Binder tmpBinder = new Binder();
                tmpBinder.attachInterface(null, "fake");
                serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
                IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
                Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
                telephonyObject = serviceMethod.invoke(null, retbinder);
                telephonyEndCall = telephonyClass.getMethod("endCall");
                telephonyEndCall.invoke(telephonyObject);

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("error", "FATAL ERROR: could not connect to telephony subsystem");
                Log.d("error", "Exception object: " + e);
            }
        }
    }
}

