package com.example.user.www;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.user.www.SendActivity;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by user on 2016/5/31.
 */
public class Receiver implements Parcelable {
    private final int ringing = 1;
    private final int dialing = 2;

    private Timer incoming_timer, outgoing_timer;
    private TimerTask timerTask;
    private int counter = 0;
    private Context mcontext;
    private String status;
    private String phone_number;

    public Receiver(Context context) {
        this.mcontext = context;
        this.status = "caller";

        TelephonyManager tm = (TelephonyManager) mcontext.getSystemService(Context.TELEPHONY_SERVICE);
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

                if(state==TelephonyManager.CALL_STATE_IDLE)
                {
                    //Toast.makeText(getApplicationContext(),"phone is neither ringing nor in a call", Toast.LENGTH_LONG).show();
                    if(lastState == TelephonyManager.CALL_STATE_RINGING) {
                        stopTimer(ringing);
                    }
                }
            }
        };
        tm.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    protected Receiver(Parcel in) {
        counter = in.readInt();
        status = in.readString();
        phone_number = in.readString();
    }

    public static final Creator<Receiver> CREATOR = new Creator<Receiver>() {
        @Override
        public Receiver createFromParcel(Parcel in) {
            return new Receiver(in);
        }

        @Override
        public Receiver[] newArray(int size) {
            return new Receiver[size];
        }
    };

    public void setStatus(String status) {
        this.status = status;
        Log.d("status",status);
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

                Log.d("status","send " + status);
                // get GPS and jump to the SendActivity to send the response
                if(status == "receiver") {
                    Intent sending = new Intent(mcontext, SendActivity.class);
                    sending.putExtra("phone_number", phone_number);
                    mcontext.startActivity(sending);
                }
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ringing);
        dest.writeInt(dialing);
        dest.writeInt(counter);
        dest.writeString(status);
        dest.writeString(phone_number);
    }
}
