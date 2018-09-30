package com.ourapps.scribefinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class StudyMaterial extends AppCompatActivity {

    String item[] = new String[]{"B.A", "B.Com"};

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
                Intent v = new Intent(com.ourapps.scribefinder.StudyMaterial.this,NotesBA.class);
                startActivity(v);
            }else if (position == 1) {
                //B.Com....
//                Intent v = new Intent(com.ourapps.scribefinder.StudyMaterial.this,NotesBA.class);
//                startActivity(v);
            }
            }
        });
    }

    public void goBackToPreviousActivity(View view) {
        finish();
        //startActivity(new Intent(StudyMaterial.this, NeedyMainPage.class));
    }

    @Override
    public void onBackPressed() {
        finish();
        //startActivity(new Intent(StudyMaterial.this, NeedyMainPage.class));
    }
}
