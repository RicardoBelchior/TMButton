package com.rbelchior.tmbutton.sample;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.TooltipCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.rbelchior.tmbutton.TMButton;

import java.util.ArrayList;
import java.util.List;

public class FakeList {

    public static void show(@NonNull ListView listView) {
        listView.setVisibility(View.VISIBLE);
        ArrayAdapter<Item> adapter = new Adapter(listView.getContext(), create());
        listView.setAdapter(adapter);
    }


    private static class Adapter extends ArrayAdapter<Item> {
        Adapter(Context context, List<Item> users) {
            super(context, 0, users);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            }
            TMButton tmButton = convertView.findViewById(R.id.tm_button);
            TextView text = convertView.findViewById(R.id.text);

            TooltipCompat.setTooltipText(tmButton, "TM BUTTON");
            TooltipCompat.setTooltipText(text, "TEXT");

            return convertView;
        }
    }

    static class Item {
    }

    private static List<Item> create() {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            items.add(new Item());
        }
        return items;
    }
}
