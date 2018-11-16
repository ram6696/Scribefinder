package com.ourapps.scribefinder.studymaterials.ba_notes;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ourapps.scribefinder.R;

public class BA1stSemEPH extends AppCompatActivity {

    String item1[] ={"MCQ Questions","Chapter 1","Chapter 2","Chapter 3","Chapter 4"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ba1st_sem_eph);
        ListView listView = findViewById(R.id.ListView5);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item1);
        listView.setAdapter(adapter);
        final Intent[] intent = new Intent[1];
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F1st%20Sem%2FEnvironment%20and%20Public%20health%2FMCQ%20questions.docx?alt=media&token=b0d8fa6b-1a8c-4c2f-83bf-5984b83af504"));
                        startActivity(intent[0]);
                        break;
                    case 1:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F1st%20Sem%2FEnvironment%20and%20Public%20health%2FUNIT%201..docx?alt=media&token=e6b95bf8-76ba-490d-b6bb-3e071472bafa"));
                        startActivity(intent[0]);
                        break;
                    case 2:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F1st%20Sem%2FEnvironment%20and%20Public%20health%2FUNIT%202..docx?alt=media&token=65157d45-91fd-4c2c-afb3-a5769565aa01"));
                        startActivity(intent[0]);
                        break;
                    case 3:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F1st%20Sem%2FEnvironment%20and%20Public%20health%2FUNIT%203..docx?alt=media&token=dff851da-7198-46e6-8815-f2c5ae9aea73"));
                        break;
                    case 4:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F1st%20Sem%2FEnvironment%20and%20Public%20health%2FUNIT%204..docx?alt=media&token=1753c5f7-1aef-44db-9e56-8d992cd98793"));
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


