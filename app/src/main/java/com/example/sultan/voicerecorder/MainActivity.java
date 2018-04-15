package com.example.sultan.voicerecorder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
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
    private TextView timer; //recording time
    private boolean recording;
    private int min = 0, sec = 0, hour = 0;
    private Handler handler;
    private MediaRecorder recorder;
    private File recordedFile = new File(Environment.getExternalStorageDirectory(), "Voice Recorder"); //recorded files' path
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        defineVariables();
        recordHandler();
        showRecordings();
    }

    /*
     *  calls all initial methods
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
                if (!recording) {
                    recording = true;
                    runnable.run();
                    recordButton.setImageResource(R.drawable.pause_button);
                    try {
                        createRecorder();
                        recorder.prepare();
                        recorder.start();
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                    recorder.setOutputFile(filePath);
                } else {
                    recording = false;
                    recorder.stop();
                    recorder.reset();

                    handler.removeCallbacks(runnable);
                    recordButton.setImageResource(R.drawable.record_button);
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
                if (recording) {
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
     * start recorded_files.xml
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
            recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
            recorder.setAudioSamplingRate(1600);
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
        String checkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Voice Recorder";
        File checkFile = new File(checkPath);
        File file_list[] = checkFile.listFiles();
        int i = 1;

        if(file_list.length > 0) {
            i = file_list.length + 1;
        }

        filePath = checkPath + "/Voice note" + i + ".mp3";
    }

    /*
     * try to access directory, or create one if it does not exist
     */
    private void AccessFiles() {
        if (!recordedFile.exists()) {
            recordedFile.mkdir();
        }
    }

    private void defineVariables() {
        openDir = findViewById(R.id.savedFiles);
        timer = findViewById(R.id.Timer);
        handler = new Handler();
        recordButton = findViewById(R.id.recordButton);
        stopButton = findViewById(R.id.stopButton);
        recorder = new MediaRecorder();
    }

    /*
     * keeps track of time
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (recording) {
                handler.postDelayed(runnable, 1000);
                timer.setText(updateTimer());
                sec++;
            }
        }
    };
}
