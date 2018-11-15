package com.ourapps.scribefinder.studymaterials.ba_notes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ourapps.scribefinder.R;

public class BA5thSem extends AppCompatActivity {

    String item[] = {"Corporate Economics","Rural Development"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ba5th_sem);

        ListView listView = findViewById(R.id.ListView12);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item);
        listView.setAdapter(adapter);
        final Intent[] intent = new Intent[1];
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        intent[0] = new Intent(BA5thSem.this,BA5thSemCorporate.class);
                         startActivity(intent[0]);
                        break;

                    case 1:
                        intent[0] = new Intent(BA5thSem.this,BA5thSemRural.class);
                         startActivity(intent[0]);
                        break;

                }

            }
        });
    }

    public void goBackToPreviousActivity(View view) {
        finish();
    }
}
