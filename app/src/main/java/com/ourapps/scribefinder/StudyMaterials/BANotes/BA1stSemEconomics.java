package com.ourapps.scribefinder.StudyMaterials;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ourapps.scribefinder.R;

public class BA1stSemEconomics extends AppCompatActivity {

    String item[] = new String[]{"Chapter 1", "Chapter 2", "Chapter 3", "Chapter 4", "Chapter 5"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ba1st_sem_economics);

        ListView listView = findViewById(R.id.ListView3);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item);
        listView.setAdapter(adapter);
        final Intent[] intent = new Intent[1];
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://drive.google.com/uc?export=download&id=1VNQwCWTgOSqN0qS8MY5ZoeEtnh73AJoy"));
                        startActivity(intent[0]);
                        break;
                    case 1:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F1st%20Sem%2FEconomics%2Fchapter%202.docx?alt=media&token=31863888-03fe-42dc-8e20-562b8adb5365"));
                        startActivity(intent[0]);
                        break;
                    case 2:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F1st%20Sem%2FEconomics%2Fchapter%203.docx?alt=media&token=24b7b3aa-ef12-4d0e-a05e-b103abd923a5"));
                        startActivity(intent[0]);
                        break;
                    case 3:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F1st%20Sem%2FEconomics%2Fchapter%204.docx?alt=media&token=ee375fe4-8335-4216-92ac-b846aa45e84d"));
                        startActivity(intent[0]);
                        break;
                    case 4:
                        intent[0] = new Intent(Intent.ACTION_VIEW);
                        intent[0].setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F1st%20Sem%2FEconomics%2Fchapter%205.docx?alt=media&token=c130f5c4-a6b0-41eb-92b4-e134fa518fc1"));
                        startActivity(intent[0]);
                        break;
                }
                /*if (position == 0) {
                    downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/scribesearchapp.appspot.com/o/BA%20NOTES%2F1st%20Sem%2FEconomics%2Fchapter%201.docx?alt=media&token=e0f81bb4-5cd4-4d07-88e3-a925d5348ff1");
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    Long reference = downloadManager.enqueue(request);
                }*/
            }
        });
    }

    public void goBackToPreviousActivity(View view) {
        finish();
    }
}
