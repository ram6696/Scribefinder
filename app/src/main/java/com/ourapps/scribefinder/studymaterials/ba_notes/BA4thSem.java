package com.ourapps.scribefinder.studymaterials.ba_notes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ourapps.scribefinder.R;

public class BA4thSem extends AppCompatActivity {

    String item[] = {"Economics","History","Personal Develpoment"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ba4th_sem);

        ListView listView = findViewById(R.id.ListView10);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item);
        listView.setAdapter(adapter);
        final Intent[] intent = new Intent[1];
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        intent[0] = new Intent(BA4thSem.this,BA4thSemEconomics.class);
                         startActivity(intent[0]);
                        break;

                    case 1:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F4th%20Sem%2FHistory%2Fhistory_4th_sem.docx?alt=media&token=844fcc0a-b7cf-461d-85e7-14fa8ad68fdc"));
                        startActivity(intent[0]);
                        break;

                    case 2:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F4th%20Sem%2FPD%2Fpersonal_devolepment_4th_sem.docx?alt=media&token=9bf8309d-4814-4ec3-8d59-28edbdc1f001"));
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
