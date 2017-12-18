package com.example.sinichi.komiku;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.EditText;
/**
 * Created by yogie on 11/01/2016.
 */
public class Profile extends Activity {
    @Override
    protected void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
        setContentView(R.layout.profile);

        TextView a = (TextView) findViewById(R.id.TVProfileName);

        String UserName = getIntent().getStringExtra("username");

        a.setText(UserName);
    }
}
