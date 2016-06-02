package com.example.user.www;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

public class SendActivity extends AppCompatActivity {
    private TextView send_tv;
    protected boolean isCall = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        Log.d("aaa", "SendActivity");
        // get phone number
        Intent intent = this.getIntent();
        String phone_number = intent.getStringExtra("phone_number");    // get phone number

        // get GPS -> ArrayList
        GPSLocation gpsLocation = new GPSLocation(this);
        LatLng myLocation = gpsLocation.userLocation;
        String lat = String.valueOf(myLocation.latitude);
        String lng = String.valueOf(myLocation.longitude);

        // ready to call
        Call myCall = new Call(phone_number);

        // send longitude
        for(int i = 2; i < 6; i++) {
            if(lng.charAt(i) == '.')    continue;
            int time = Integer.valueOf(lng.charAt(i))-48;
            Log.d("qqqq", String.valueOf(time));

            while(myCall.getIsCall());  // it is currently in a call

            Log.d("qqqq", "call");
            if(i == 2) {    // delay 1 sec
                if(time == 0)
                    myCall.dial(18);
                else
                    myCall.dial(time + 8);
            }
            else {
                if(time == 0)
                    myCall.dial(17);
                else
                    myCall.dial(time + 7);
            }

            myCall.startTimer();
            isCall = true;
        }

        // send latitude
        for(int i = 1; i < 5; i++) {
            if(lat.charAt(i) == '.')    continue;
            int time = Integer.valueOf(lat.charAt(i))-48;
            Log.d("qqqq", String.valueOf(time));

            while(myCall.getIsCall());  // it is currently in a call

            Log.d("qqqq", "call");

            if(time == 0)
                myCall.dial(17);
            else
                myCall.dial(time + 7);
            myCall.startTimer();
            isCall = true;
        }
    }

    public class Call {
        private Timer outgoing_timer;
        private TimerTask timerTask;
        private int counter;
        private String phone_number;
        private int calling_time;

        public Call(String number) {
            this.phone_number = number;
        }

        public void dial(int time) {
            Intent myIntentDial;
            calling_time = time;
            // ACTION_CALL -> call the number directly without the dialer
            // ACTION_DIAL -> ask users to select an application for calling
            if (ActivityCompat.checkSelfPermission(SendActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                myIntentDial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone_number));
                return;
            } else {
                myIntentDial = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone_number));
            }
            startActivity(myIntentDial);
        }

        public void startTimer() {
            counter = calling_time;
            //set a new Timer

            outgoing_timer = new Timer();
            //initialize the TimerTask's job
            initializeTimerTask();
            //schedule the timer, after the first 0ms the TimerTask will run every 1sec
            outgoing_timer.schedule(timerTask, 0, 1000);

        }

        public void initializeTimerTask() {
            timerTask = new TimerTask() {
                public void run() {
                    counter--;
                    Log.d("timer", String.valueOf(counter));
                    if(counter == 0) {     // 6 seconds delay
                        Log.d("outgoing", "hang up the phone");
                        stopTimer();
                        // hang up the phone
                        hangup();
                    }
                }
            };
        }

        public void stopTimer() {
            //stop the timer, if it's not already null
            if (outgoing_timer != null) {
                Log.d("outgoing2", String.valueOf(counter));     // 2 seconds delay
                outgoing_timer.cancel();
                timerTask.cancel();
                outgoing_timer = null;
                timerTask = null;
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

                Thread.sleep(3000);
                isCall = false;

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("error", "FATAL ERROR: could not connect to telephony subsystem");
                Log.d("error", "Exception object: " + e);
            }
        }

        public boolean getIsCall() {
            return isCall;
        }
    }
}
