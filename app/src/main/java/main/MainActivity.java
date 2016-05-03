package main;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.user.www.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView lvPhone;
    private Button call_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        call_btn = (Button) findViewById(R.id.phone_call_button);
        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent call_intent = new Intent(MainActivity.this, CallActivity.class);
                startActivity(call_intent);
            }
        });

        lvPhone = (ListView) findViewById(R.id.listPhone);

        List<PhoneBook> listPhoneBook = new ArrayList<PhoneBook>();
        listPhoneBook.add(new PhoneBook(
                BitmapFactory.decodeResource(getResources(), R.mipmap.pic_1),
                "Pete Houston", "010-9817-6331"));
        listPhoneBook.add(new PhoneBook(
                BitmapFactory.decodeResource(getResources(), R.mipmap.pic_2),
                "Lina Cheng", "046-7764-1142"));
        listPhoneBook.add(new PhoneBook(
                BitmapFactory.decodeResource(getResources(), R.mipmap.pic_3),
                "Jenny Nguyen", "0913-223-498"));
        PhoneBookAdapter adapter = new PhoneBookAdapter(this, listPhoneBook);
        lvPhone.setAdapter(adapter);
    }
}

