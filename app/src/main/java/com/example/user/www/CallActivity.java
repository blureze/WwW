package com.example.user.www;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CallActivity extends AppCompatActivity {
    private Button call_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        call_btn = (Button) findViewById(R.id.call_button);
        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strInput = "0978298587";
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
                }
                else {
                    myIntentDial = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + strInput));
                }
                startActivity(myIntentDial);
            }
        });
    }

}
