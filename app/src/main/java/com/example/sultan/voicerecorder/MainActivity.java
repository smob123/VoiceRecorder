package com.example.sultan.voicerecorder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private ImageView recordButton, stopButton;
    private ImageButton openDir; //displays recorded files
    private TextView timer; //handles recording time
    private boolean recording, paused = false;
    private int min = 0, sec = 0, hour = 0;
    private Handler handler; //responsible for keeping track of recording time
    private MediaRecorder recorder; //responsible for recording audio
    private File recordedFile = new File(Environment.getExternalStorageDirectory(), "Voice Recorder"); //recorded files' path
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //button to view saved saved audio records
        openDir = findViewById(R.id.savedFiles);
        //the actual timer on the screen
        timer = findViewById(R.id.Timer);
        handler = new Handler();
        //record, and stop recording buttons
        recordButton = findViewById(R.id.recordButton);
        stopButton = findViewById(R.id.stopButton);
        //initialize the MediaRecoder
        recorder = new MediaRecorder();

        //set the recording events
        recordHandler();
        showRecordings();
    }

    /*
     *  gets permissions, and sets button onClick events
     */
    private void recordHandler() {
        recording = false;

        getPermissions();
        PlayButtonHandlner();
        StopButtonHandler();
    }

    /*
     * get mic and storage permissions
     */
    private void getPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 0);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    /*
     * handle starting recording
     */
    private void PlayButtonHandlner() {
        recordButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //if the recording is not fully stopped, and not paused
                if (!recording && !paused) {
                    recording = true;
                    runnable.run(); //start the timer
                    recordButton.setImageResource(R.drawable.pause_button); //change the record button's image to a pause icon image
                    try {
                        //reset the recorder, and start recording
                        createRecorder();
                        recorder.prepare();
                        recorder.start();
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                    //set the output file
                    recorder.setOutputFile(filePath);
                    //if currently recording
                } else if(recording && !paused) {
                    recording = false;
                    paused = true;
                    recorder.pause(); //pause recording

                    handler.removeCallbacks(runnable); //stop the timer
                    recordButton.setImageResource(R.drawable.record_button); //reset the image
                }
                else {
                    //otherwise, resume recording
                    recorder.resume();
                    recording = true;
                    paused = false;
                }
        }
    });
}

    /*
     * handle stopping recording
     */
    private void StopButtonHandler() {
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //if currently recording
                if (recording) {
                    //reset the recorder, and the timer
                    recording = false;
                    handler.removeCallbacks(runnable);
                    sec = min = 0;
                    timer.setText(updateTimer());
                    recorder.stop();
                    recorder.reset();
                    recordButton.setImageResource(R.drawable.record_button);
                }
            }
        });
    }

    /*
     * go to the recorded files screen
     */
    public void showRecordings() {
        openDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, recorded_files.class));
            }
        });
    }

    /*
     * update displayed timer
     */
    private String updateTimer() {
        if (sec > 59) {
            min++;
            sec = 0;
        }

        if (min > 59) {
            min = sec = 0;
            hour++;
            return String.format(Locale.US, "%02d", hour) + ":" + String.format(Locale.US, "%02d", min) +
                    ":" + String.format(Locale.US, "%02d", sec);
        }

        return String.format(Locale.US, "%02d", min) + ":" + String.format(Locale.US, "%02d", sec);
    }

    /*
     * handle setting up mediaRecorder object
     */
    private void createRecorder() {
        try {
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            AccessFiles();
            FileNaming();
            recorder.setOutputFile(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * handle file names to be stored
     */
    private void FileNaming() {
        //the directory where the recorded files are stored
        String checkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Voice Recorder";
        File checkFile = new File(checkPath);
        //get the files in the directory
        File file_list[] = checkFile.listFiles();
        int i = 1; //the number of the new recorded file

        //check if there is more than one file
        if(file_list.length > 0) {
            i = file_list.length + 1;
        }

        //set the path, and name of the new file
        filePath = checkPath + "/Voice note" + i + ".3gp";
    }

    /*
     * try to access directory, or create one if it does not exist
     */
    private void AccessFiles() {
        if (!recordedFile.exists()) {
            recordedFile.mkdir();
        }
    }

    /*
     * keeps track of time
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (recording) {
                //update the timer once every second
                handler.postDelayed(runnable, 1000);
                timer.setText(updateTimer());
                sec++;
            }
        }
    };
}
