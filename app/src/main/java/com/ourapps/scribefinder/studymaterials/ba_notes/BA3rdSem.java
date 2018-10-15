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

public class BA3rdSem extends AppCompatActivity {

     String item[] = {"English","History"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ba3rd_sem);

        ListView listView = findViewById(R.id.ListView9);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item);
        listView.setAdapter(adapter);
        final Intent[] intent = new Intent[1];
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F3rd%20Sem%2FEnglish%2FENGLISH.doc?alt=media&token=6ebb4cd8-4148-4c7a-bbe4-d32f0967959b"));
                        startActivity(intent[0]);
                        break;

                    case 1:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F3rd%20Sem%2FHistory%2FHistory.docx?alt=media&token=b7da8bcc-a99b-48f3-865f-2186354cf24a"));
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
