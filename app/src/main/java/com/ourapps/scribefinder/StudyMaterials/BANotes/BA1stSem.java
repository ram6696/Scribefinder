package com.ourapps.scribefinder.StudyMaterials.BANotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ourapps.scribefinder.R;

public class BA1stSem extends AppCompatActivity {

    String item[] = new String[]{"Economics", "English", "Environment and Public Health", "History" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ba1st_sem);

        ListView listView = findViewById(R.id.ListView2);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    startActivity(new Intent(BA1stSem.this, BA1stSemEconomics.class));
                }
            }
        });
    }

    public void goBackToPreviousActivity(View view) {
        finish();
    }
}
