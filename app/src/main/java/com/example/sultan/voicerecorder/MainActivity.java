package com.example.sultan.voicerecorder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    ImageView recordButton, stopButton;
    private TextView timer;
    private boolean recording;
    private int min = 0, sec = 0, hour = 0;
    private Handler handler;
    private MediaRecorder recorder;
    private ArrayList<File> fileList = new ArrayList<File>();
    File recordedFile = new File(Environment.getExternalStorageDirectory(), "Voice Recorder");
    private String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        defineVariables();
        recordHandler();
    }

    private void recordHandler() {
        recording = false;

        getPermissions();
        PlayButtonHandlner();
        StopButtonHandler();
    }

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

    private void createRecorder() {
        try {
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
            AccessFiles();
            FileNaming();
            recorder.setOutputFile(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void FileNaming() {
        String checkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Voice Recorder";
        File checkFile = new File(checkPath);
        File file_list[] = checkFile.listFiles();
        int i = 1;

        if(file_list.length > 0) {
            i = file_list.length + 1;
        }

        filePath = checkPath + "/Voice note" + i + ".mp3";
        System.out.println(filePath);
    }

    private void AccessFiles() {
        if (!recordedFile.exists()) {
            recordedFile.mkdir();
        }
    }

    private void defineVariables() {
        timer = findViewById(R.id.Timer);
        handler = new Handler();
        recordButton = findViewById(R.id.recordButton);
        stopButton = findViewById(R.id.stopButton);
        recorder = new MediaRecorder();
    }

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
