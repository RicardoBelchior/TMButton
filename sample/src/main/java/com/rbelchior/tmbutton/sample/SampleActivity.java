package com.rbelchior.tmbutton.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.rbelchior.tmbutton.TMButton;

public class SampleActivity extends AppCompatActivity {

    static final boolean SHOW_LIST = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        TMButton tmButton = findViewById(R.id.TMButton);
        tmButton.setChecked(true);

        if (SHOW_LIST) {
            ListView listView = findViewById(R.id.list_view);
            FakeList.show(listView);
        }
    }

}
