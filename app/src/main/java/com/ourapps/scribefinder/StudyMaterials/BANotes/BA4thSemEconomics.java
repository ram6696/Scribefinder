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

public class BA4thSemEconomics extends AppCompatActivity {

    String item[] = {"Chapter1","Chapter 2","Chapter 3","Chapter 4","Chapter 6"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ba4th_sem_economics);

        ListView listView = findViewById(R.id.ListView11);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item);
        listView.setAdapter(adapter);
        final Intent[] intent = new Intent[1];
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F4th%20Sem%2FEconomics%2Fchapter%201%20Introduction.docx?alt=media&token=e6c6263d-d987-46ac-b630-7fcdd39bc03b"));
                        startActivity(intent[0]);
                    case 1:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F4th%20Sem%2FEconomics%2Fchapter%202%20revenue.docx?alt=media&token=f8917867-89a8-4623-8210-85afbec14c17"));
                        startActivity(intent[0]);
                        break;

                    case 2:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F4th%20Sem%2FEconomics%2Fchapter%203%20expenditure.docx?alt=media&token=3360fc33-054c-47ab-800c-c6ba88946867"));
                        startActivity(intent[0]);
                        break;

                    case 3:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F4th%20Sem%2FEconomics%2Fchapter%204%20public%20dept.docx?alt=media&token=5c99fa59-9f49-4f43-b6dc-28aec86435e5"));
                        startActivity(intent[0]);
                        break;

                    case 4:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F4th%20Sem%2FEconomics%2Fchapter%206.docx?alt=media&token=87a79837-6a15-4024-ac95-b1d078dacb12s"));
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

