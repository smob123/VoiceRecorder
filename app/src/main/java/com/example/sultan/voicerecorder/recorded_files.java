package com.example.sultan.voicerecorder;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class recorded_files extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private File filePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Voice Recorder");
    private String[] displayedFiles;
    private MediaPlayer player;
    private boolean playing = false;

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
                displayedFiles[i] = files[i].toString().substring(files[i].toString().lastIndexOf("/") + 1);
            }

            displayList();
        } catch (Exception e) {
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
        File getSelectedFile = new File(filePath.getAbsolutePath() + "/" + fileName.getText());
        view.setBackgroundColor(Color.parseColor("#bdd5fc"));

        playSoundFile(getSelectedFile, view);
    }

    private void playSoundFile(File file, final View currentView) {
        if (!playing) {
            player = new MediaPlayer();
            try {
                player.setDataSource(file.getAbsolutePath());
                player.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }

            player.start();
            Toast.makeText(this, "Click again to stop", Toast.LENGTH_SHORT).show();
            playing = true;

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    player.stop();
                    player.release();
                    playing = false;
                    currentView.setBackgroundColor(Color.WHITE);
                }
            });
        } else {
            player.stop();
            player.release();
            playing = false;
            currentView.setBackgroundColor(Color.WHITE);
        }
    }
}
