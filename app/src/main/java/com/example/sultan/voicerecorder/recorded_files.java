package com.example.sultan.voicerecorder;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class recorded_files extends AppCompatActivity implements AdapterView.OnItemClickListener {

    //the directory where the recording files are stored
    private File filePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Voice Recorder");
    private String[] displayedFiles; //list of files that are shown on the screen
    private MediaPlayer player; //responsible for playing the recordings
    private boolean playing = false; //check if a recording is playing

    private ListView list; //the list of files
    private TextView listIsEmpty; //only shows if the list is empty

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recorded_files);

        //initialize the UI variables
        list = findViewById(R.id.list);
        listIsEmpty = findViewById(R.id.txtView);
        //grab the files from the directory
        getFiles();
    }

    /*
     * get the recorded files from the directory
     */
    public void getFiles() {
        //files in the directory
        File files[] = filePath.listFiles();
        try {
            displayedFiles = new String[files.length];

            //get the names of the files from the absolute path of each file
            for (int i = 0; i < displayedFiles.length; i++) {
                displayedFiles[i] = files[i].toString().substring(files[i].toString().lastIndexOf("/") + 1);
            }

            //show the file names on the screen
            displayList();
        } catch (Exception e) {
            list.setVisibility(View.INVISIBLE);
            listIsEmpty.setVisibility(View.VISIBLE);
        }
    }

    /*
     * displays the audio files' names on the screen
     */
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

    /*
     * plays the selected audio file
     */
    private void playSoundFile(File file, final View currentView) {
        //if no audio is currently playing
        if (!playing) {
            //initialize the MediaPlayer, and start playing the audio
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

            //when the played recording is finished
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    //stop, and release the MediaPlayer object
                    player.stop();
                    player.release();
                    playing = false;
                    currentView.setBackgroundColor(Color.WHITE);
                }
            });
        } else {
            //otherwise stop, and release the MediaPlayer
            player.stop();
            player.release();
            playing = false;
            currentView.setBackgroundColor(Color.WHITE);
        }
    }
}
