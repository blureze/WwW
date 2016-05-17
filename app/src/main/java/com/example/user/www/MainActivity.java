package com.example.user.www;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button call_btn;
    private Button show_btn;

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

        show_btn = (Button) findViewById(R.id.showmapbutton);
        show_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent call_intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(call_intent);
            }
        });
    }

    
}