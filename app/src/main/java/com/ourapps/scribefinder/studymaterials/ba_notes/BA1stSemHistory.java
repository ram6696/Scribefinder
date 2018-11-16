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

public class BA1stSemHistory extends AppCompatActivity {

    String item[] ={"Unit 1","Unit 2","Unit 3","Unit 4","Unit 5"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ba1st_sem_history);

        ListView listView = findViewById(R.id.ListView6);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item);
        listView.setAdapter(adapter);
        final Intent[] intent = new Intent[1];
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F1st%20Sem%2FHistory%2FUnitI.doc?alt=media&token=5f22347a-d151-40f4-82b8-35745e800a1f"));
                        startActivity(intent[0]);
                        break;
                    case 1:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F1st%20Sem%2FHistory%2FUnitII.doc?alt=media&token=6e66b1d9-5ee7-4dcf-b696-aab2e227837a"));
                        startActivity(intent[0]);
                        break;
                    case 2:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F1st%20Sem%2FHistory%2FUnitIII.doc?alt=media&token=ca055306-6ecb-4bac-9c98-4810c142ccba"));
                        startActivity(intent[0]);
                        break;
                    case 3:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F1st%20Sem%2FHistory%2FUnitIV.doc?alt=media&token=6b32d58a-f33a-4971-895f-8c9c2d9c65cd"));
                        break;
                    case 4:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F1st%20Sem%2FHistory%2FUnitV.doc?alt=media&token=8eaf02bf-ee59-459c-8563-544b805411b1"));
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


