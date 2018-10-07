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

public class BA1stSemEnglsih extends AppCompatActivity {

    String item[] = new String[]{"Chapter 1", "Chapter 2", "Chapter 3", "Chapter 4", "Chapter 5","Chapter 6","Chapter 7","Chapter 8"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ba1st_sem_englsih);

        ListView listView = findViewById(R.id.ListView4);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item);
        listView.setAdapter(adapter);
        final Intent[] intent = new Intent[1];
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F1st%20Sem%2FEnglish%2FChapter%201.doc?alt=media&token=5c594534-a210-4c07-b95d-0a1ce3826077"));
                        startActivity(intent[0]);
                        break;
                    case 1:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F1st%20Sem%2FEnglish%2FChapter%202.doc?alt=media&token=8750ea80-d1a9-4430-a676-d7f2f4433c41 "));
                        startActivity(intent[0]);
                        break;
                    case 2:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F1st%20Sem%2FEnglish%2FChapter%203.doc?alt=media&token=e2fa6dda-ad3b-4bb7-8d57-1ce4ceaa6a8d "));
                        startActivity(intent[0]);
                        break;
                    case 3:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse(" https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F1st%20Sem%2FEnglish%2FChapter%204.doc?alt=media&token=71348dd4-3fad-413d-8194-5168183b882c"));
                        break;
                    case 4:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse(" https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F1st%20Sem%2FEnglish%2FChapter%205.doc?alt=media&token=fa05c210-2725-4ca0-93d5-22a8affc9dcd"));
                        startActivity(intent[0]);
                        break;
                    case 5:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse(" https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F1st%20Sem%2FEnglish%2FChapter%205.doc?alt=media&token=fa05c210-2725-4ca0-93d5-22a8affc9dcd"));
                        startActivity(intent[0]);
                        break;
                    case 6:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F1st%20Sem%2FEnglish%2FChapter%207.doc?alt=media&token=5ffb4583-d09d-4a1c-bb8d-a8cf0d8960f1 "));
                        startActivity(intent[0]);
                        break;
                    case 7:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F1st%20Sem%2FEnglish%2FChapter%208.doc?alt=media&token=e15e7074-ded6-438c-8cea-06dc95c01f70 "));
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

