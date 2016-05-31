package com.example.user.www;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.app.Activity;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/***
 *  This Activity is mainly for the caller.
 * After the user(caller) select a person from the phonebook, the system will automatically make a phone call.
 *  The phone call will automatically hang up after a period of time, and then jumps to WaitActivity to wait for response.
 */

public class ContactActivity extends Activity {

    private ListView lvPhone;
    private Dialog progressDialog;
    List<HashMap<String, String>> contactList;
    HashMap<String, String> contactItem;
    ContactAdapter adapter;
    public int[] pic = {R.mipmap.pic_1, R.mipmap.pic_2, R.mipmap.pic_3,
            R.mipmap.pic_4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        lvPhone = (ListView) findViewById(R.id.listPhone);
        fetchContacts();
        new RemoteDataTask().execute();
    }

    public void fetchContacts() {

        String phoneNumber = null;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;


        ContentResolver contentResolver = getContentResolver();

        Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);

        contactList = new ArrayList<>();

        // Loop for every contact in the phone
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

                if (hasPhoneNumber > 0) {

                    // Query and loop for every phone number of the contact
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);

                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                    }
                    phoneCursor.close();

                    contactItem = new HashMap<>();
                    contactItem.put("name", name);
                    contactItem.put("phone", phoneNumber);
                    contactList.add(contactItem);
                }
            }
        }
    }

    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        // Override this method to do custom remote calls
        protected Void doInBackground(Void... params) {
            // Gets the current list of material_in in sorted order
            try {
                fetchContacts();
            } catch (Exception e) {
                Log.e("error", "Can't fetch contact");
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            ContactActivity.this.progressDialog = ProgressDialog.show(
                    ContactActivity.this, "", "Loading...Contact", true);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result) {
            // Put the list of todos into the list view
            adapter = new ContactAdapter(ContactActivity.this);
            lvPhone.setAdapter(adapter);

            ContactActivity.this.progressDialog.dismiss();
        }

    }

    public final class MyView {
        public ImageView contact_img;
        public TextView contact_name;
        public String contact_number;
        public ImageButton contact_btn;
    }

    private class ContactAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public ContactAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return contactList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            MyView myviews = null;
            myviews = new MyView();
            convertView = inflater.inflate(R.layout.list_phonebook, null);
            myviews.contact_img = (ImageView) convertView
                    .findViewById(R.id.person_image);
            myviews.contact_name = (TextView) convertView
                    .findViewById(R.id.person_name);
            myviews.contact_btn = (ImageButton) convertView
                    .findViewById(R.id.person_btn);

            Random r = new Random();
            myviews.contact_img.setImageBitmap(BitmapFactory.decodeResource(getResources(), pic[r.nextInt(4)]));
            myviews.contact_name.setText(contactList.get(position).get("name"));
            myviews.contact_number = contactList.get(position).get("phone");
            myviews.contact_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("my phone", contactList.get(position).get("phone"));
                    Call call = new Call(contactList.get(position).get("phone"));
//                    Call call = new Call("0911624707");
                    call.dial();
                }
            });

            return convertView;
        }
    }

    private class Call {
        private final int ringing = 1;
        private final int dialing = 2;

        //        private Button call_btn;
        private Timer incoming_timer, outgoing_timer;
        private TimerTask timerTask;
        private int counter = 0;
        private String phone_number;

        public Call(String phone_number) {
            this.phone_number = phone_number;
        }

        public void dial() {
            Intent myIntentDial;
            // ACTION_CALL -> call the number directly without the dialer
            // ACTION_DIAL -> ask users to select an application for calling
            if (ActivityCompat.checkSelfPermission(ContactActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                myIntentDial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone_number));
                return;
            } else {
                myIntentDial = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone_number));
            }
            startActivity(myIntentDial);

            /*after dialing, count the calling time*/
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
//                        Toast.makeText(getApplicationContext(), "Phone Is Ringing", Toast.LENGTH_LONG).show();
                        startTimer(ringing);
                        lastState = TelephonyManager.CALL_STATE_RINGING;
                    }
                    if(state==TelephonyManager.CALL_STATE_OFFHOOK)
                    {
                        //Toast.makeText(getApplicationContext(),"Phone is Currently in A call", Toast.LENGTH_LONG).show();
                        startTimer(dialing);
                        lastState = TelephonyManager.CALL_STATE_OFFHOOK;
                    }
                    if(state==TelephonyManager.CALL_STATE_IDLE)
                    {
                        //Toast.makeText(getApplicationContext(),"phone is neither ringing nor in a call", Toast.LENGTH_LONG).show();
                        if(lastState == TelephonyManager.CALL_STATE_OFFHOOK) {
                            stopTimer(dialing);
                        }
                        else if(lastState == TelephonyManager.CALL_STATE_RINGING) {
                            stopTimer(ringing);
                        }
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

                            // jump to WaitActivity
                            Intent waiting = new Intent(ContactActivity.this, WaitActivity.class);
                            startActivity(waiting);
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
