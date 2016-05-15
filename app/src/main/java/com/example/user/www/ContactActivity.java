package com.example.user.www;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

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

                    /*Intent intent = new Intent();
                    intent.setClass(TVoteActivity.this, GoVoteActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("objectId",
                            voteList.get(position).get("objectId"));
                    // 將Bundle物件assign給intent
                    intent.putExtras(bundle);
                    startActivity(intent);*/
                }
            });

            return convertView;
        }
    }
}
