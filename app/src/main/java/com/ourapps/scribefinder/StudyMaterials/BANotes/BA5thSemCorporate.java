package com.ourapps.scribefinder.StudyMaterials.BANotes;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ourapps.scribefinder.R;

public class BA5thSemCorporate extends AppCompatActivity {

    String item[] = {"Module 1","Module 2","Module 3","Module 4","Module 5"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ba5th_sem_corporate);

        ListView listView = findViewById(R.id.ListView13);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item);
        listView.setAdapter(adapter);
        final Intent[] intent = new Intent[1];
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F5th%20Sem%2FCoporate%20Economics%2FModule%201..docx?alt=media&token=ca3a953d-ba57-4dfe-b8e8-282ce40247a9"));
                        startActivity(intent[0]);
                    case 1:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F5th%20Sem%2FCoporate%20Economics%2FModule%202.docx?alt=media&token=9e0e8e6f-5bd4-4829-ae9a-54ee64882a64"));
                        startActivity(intent[0]);
                        break;

                    case 2:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F5th%20Sem%2FCoporate%20Economics%2FModule%203.docx?alt=media&token=a16814c8-31a7-4413-853b-67ea0dfa88d5"));
                        startActivity(intent[0]);
                        break;

                    case 3:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F5th%20Sem%2FCoporate%20Economics%2FModule%204.docx?alt=media&token=bd31f3a4-4445-489f-a080-03a9051db2b4"));
                        startActivity(intent[0]);
                        break;

                    case 4:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F5th%20Sem%2FCoporate%20Economics%2Fmodule%205.docx?alt=media&token=2f7ae059-e5c7-4ea8-b09c-dc8910cfbc9a"));
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

