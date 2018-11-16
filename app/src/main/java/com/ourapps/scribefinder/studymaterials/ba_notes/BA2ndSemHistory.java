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

public class BA2ndSemHistory extends AppCompatActivity {
   String item[] ={"Chapter 1","Chapter 2","Chapter 5","Chapter 6"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ba2nd_sem_history);

        ListView listView = findViewById(R.id.ListView7);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item);
        listView.setAdapter(adapter);
        final Intent[] intent = new Intent[1];
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F2nd%20Sem%2FHistory%2FChapter%201.%20Introduction.docx?alt=media&token=5995689b-43b2-4fb5-813e-de493072d201"));
                        startActivity(intent[0]);
                        break;
                    case 1:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F2nd%20Sem%2FHistory%2FChapter%202.%20The%20Sultanate%20of%20Delhi%20.docx?alt=media&token=fb84dbd6-63b3-4e9b-8750-1022a2d6ca91"));
                        startActivity(intent[0]);
                        break;
                    case 2:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F2nd%20Sem%2FHistory%2FChapter%205.%20The%20Struggle%20between%20the%20Afghans%20and%20Mughals%20for%20Supremacy.docx?alt=media&token=ef85fa00-0e90-4808-b9e3-e64c9eb4b1d0"));
                        startActivity(intent[0]);
                        break;
                    case 3:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F2nd%20Sem%2FHistory%2FChapter%206.%20Maratha%20Swarajya%20(Self%20%E2%80%93%20Dominion%20State).docx?alt=media&token=dbb51bda-5ea8-45a4-bded-47e17f24ed46"));
                        break;


                }

            }
        });
    }
    public void goBackToPreviousActivity(View view) {
        finish();
    }
}

