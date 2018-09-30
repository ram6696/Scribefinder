package com.ourapps.scribefinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class NotesBA extends AppCompatActivity {


    String item[] = new String[]{"1st SEM", "2nd SEM","3rd SEM","4th SEM","5th SEM","6th SEM" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_b);

        ListView listView = (ListView) findViewById(R.id.ListView1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    startActivity(new Intent(NotesBA.this, BA1stSem.class));
                }
                if(position == 1) {
                    startActivity(new Intent(NotesBA.this, BA2ndSem.class ));
                }
                if(position == 2) {
                    startActivity(new Intent(NotesBA.this, BA3rdSem.class ));
                }
                if(position == 3) {
                    startActivity(new Intent(NotesBA.this, BA4thSem.class ));
                }
                if(position == 4) {
                    startActivity(new Intent(NotesBA.this, BA5thSem.class ));
                }
                if(position == 5) {
                    startActivity(new Intent(NotesBA.this, BA6thSem.class ));
                }
            }
        });
    }

    public void goBackToPreviousActivity(View view) {
        finish();
    }
}
