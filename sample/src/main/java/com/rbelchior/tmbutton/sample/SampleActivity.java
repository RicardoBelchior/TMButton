package com.rbelchior.tmbutton.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.rbelchior.tmbutton.TMButton;

public class SampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        TMButton tmButton = (TMButton) findViewById(R.id.TMButton);
        tmButton.setChecked(true);
    }
}
