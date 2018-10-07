package com.ourapps.scribefinder.StudyMaterials.BANotes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ourapps.scribefinder.R;

public class BA2ndSem extends AppCompatActivity {

    String item[] ={"Economics","History","Indian Constitution"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ba2nd_sem);

        ListView listView = findViewById(R.id.ListView8);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item);
        listView.setAdapter(adapter);
        final Intent[] intent = new Intent[1];
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F2nd%20Sem%2FEconomics%2FB.A%202nd%20sem%20Managerial%20Economics.docx?alt=media&token=af6335aa-83ec-4d50-9889-8fbe65df05e8"));
                        startActivity(intent[0]);
                        break;
                    case 1:
                        intent[0] = new Intent(BA2ndSem.this,BA2ndSemHistory.class);

                        startActivity(intent[0]);
                        break;
                    case 2:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F2nd%20Sem%2FI.C%2FCONSTITUTION%20OF%20INDIA.docx?alt=media&token=673c8938-fd64-4402-8e80-b6068eca4e1a"));
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


