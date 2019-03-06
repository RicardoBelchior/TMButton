package com.rbelchior.tmbutton.sample;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ListView;

import com.rbelchior.tmbutton.TMButton;

public class SampleActivity extends AppCompatActivity {

    static final boolean SHOW_LIST = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        TMButton tmButton1 = findViewById(R.id.button_1);
        tmButton1.setColorChecked(Color.MAGENTA);
        tmButton1.setColorUnchecked(Color.MAGENTA);
        tmButton1.setChecked(false, false, true);

        TMButton tmButton3 = findViewById(R.id.button_3);
        tmButton3.setChecked(true);

        if (SHOW_LIST) {
            ListView listView = findViewById(R.id.list_view);
            FakeList.show(listView);
        }
    }

}
