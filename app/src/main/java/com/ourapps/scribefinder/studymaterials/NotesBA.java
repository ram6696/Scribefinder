package com.ourapps.scribefinder.studymaterials;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ourapps.scribefinder.R;
import com.ourapps.scribefinder.needy.NeedyMainPage;
import com.ourapps.scribefinder.studymaterials.ba_notes.BA1stSem;
import com.ourapps.scribefinder.studymaterials.ba_notes.BA2ndSem;
import com.ourapps.scribefinder.studymaterials.ba_notes.BA3rdSem;
import com.ourapps.scribefinder.studymaterials.ba_notes.BA4thSem;
import com.ourapps.scribefinder.studymaterials.ba_notes.BA5thSem;

public class NotesBA extends AppCompatActivity {


    String item[] = new String[]{"1st SEM", "2nd SEM","3rd SEM","4th SEM","5th SEM" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_b);

        ListView listView = findViewById(R.id.ListView1);
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

            }
        });
    }

    public void goBackToPreviousActivity(View view) {
        finish();
    }

    public static class StudyMaterial extends AppCompatActivity {

        String item[] = new String[]{"B.A"};

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_study_material);

            ListView COURSE = findViewById(R.id.ListView);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item);
            COURSE.setAdapter(adapter);
            COURSE.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent v = new Intent(StudyMaterial.this,NotesBA.class);
                    startActivity(v);
                }else if (position == 1) {
                    //B.Com....
    //                Intent v = new Intent(com.ourapps.scribefinder.StudyMaterials.NotesBA.StudyMaterial.this,NotesBA.class);
    //                startActivity(v);
                }
                }
            });
        }

        public void goBackToPreviousActivity(View view) {

            startActivity(new Intent(StudyMaterial.this, NeedyMainPage.class));
            finish();
        }

        @Override
        public void onBackPressed() {

            startActivity(new Intent(StudyMaterial.this, NeedyMainPage.class));
            finish();
        }
    }
}
