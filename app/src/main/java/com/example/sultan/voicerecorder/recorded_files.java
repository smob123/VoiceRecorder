package com.example.sultan.voicerecorder;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;

public class recorded_files extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private File filePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Voice Recorder");
    private String[] displayedFiles;

    private ListView list;
    private TextView listIsEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recorded_files);

        list = findViewById(R.id.list);
        listIsEmpty = findViewById(R.id.txtView);
        getFiles();
    }

    public void getFiles() {
        File files[] = filePath.listFiles();
        try {
            displayedFiles = new String[files.length];

            for (int i = 0; i < displayedFiles.length; i++) {
                displayedFiles[i] = files[i].toString().substring(files[i].toString().lastIndexOf("/")+1);
            }

            displayList();
        } catch(Exception e){
            list.setVisibility(View.INVISIBLE);
            listIsEmpty.setVisibility(View.VISIBLE);
        }
    }

    public void displayList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(list.getContext(), android.R.layout.simple_list_item_1, displayedFiles);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
    }

    /*
     *   get selected file from storage and play it.
     */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView fileName = (TextView) view;
        File getSelectedFile = new File(filePath.getAbsolutePath() + fileName.getText());


    }



    private void playSoundFile() {

    }
}
