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

public class BA5thSemRural extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ba5th_sem_rural);

        String item[] ={"Chapter 1","Chapter 2","Chapter 3","Chapter 4","Chapter 5"};
        ListView listView = findViewById(R.id.ListView14);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item);
        listView.setAdapter(adapter);
        final Intent[] intent = new Intent[1];
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F5th%20Sem%2FRural%20Economics%2FChapter-1.txt?alt=media&token=bfa98892-10ed-4e34-ac4c-5e8ebd99a0f1"));
                        startActivity(intent[0]);
                    case 1:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F5th%20Sem%2FRural%20Economics%2FChapter-2.txt?alt=media&token=f0f837cd-ab36-44ab-8adf-8c46ef0ff1a8"));
                        startActivity(intent[0]);
                        break;

                    case 2:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F5th%20Sem%2FRural%20Economics%2FChapter-3.txt?alt=media&token=3f35555f-5c48-4b70-a35d-ac35226b0924"));
                        startActivity(intent[0]);
                        break;

                    case 3:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F5th%20Sem%2FRural%20Economics%2FChapter-4.txt?alt=media&token=f55d52b8-ab3a-43bf-bfea-f768595b5c7d"));
                        startActivity(intent[0]);
                        break;

                    case 4:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F5th%20Sem%2FRural%20Economics%2FChapter-5.txt?alt=media&token=8448a5c7-2a5d-48ad-99c6-32d386047818"));
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

