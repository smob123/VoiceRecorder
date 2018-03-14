package com.example.sultan.voicerecorder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    ImageView recordButton, stopButton;
    private TextView timer;
    private boolean recording;
    private int min = 0, sec = 0;
    private Handler handler;
    private MediaRecorder recorder;
    private String fileName = Environment.getExternalStorageDirectory().getAbsolutePath();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        defineVariables();
        record();
    }

    private void getPermissions() {
        String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 0);
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        createRecorder();
    }

    private void createRecorder() {
        recorder = new MediaRecorder();
        try {
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
            fileName += "/test.mp3";
            recorder.setOutputFile(fileName);
            recorder.prepare();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void record() {
        recording = false;

        getPermissions();
        recordButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(!recording) {
                    recording = true;
                    runnable.run();
                    recordButton.setImageResource(R.drawable.pause_button);
                    recorder.start();
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(recording) {
                    recording = false;
                    handler.removeCallbacks(runnable);
                    sec = min = 0;
                    timer.setText(updateTimer());
                    recorder.stop();
                    recordButton.setImageResource(R.drawable.record_button);
                }
            }
        });
    }

    private String updateTimer() {
        if(sec > 59) {
            min++;
            sec = 0;
        }

        if(min > 59) {
            min = sec = 0;
        }

        return String.format("%02d", min) + ":" + String.format("%02d", sec);
    }

    private void defineVariables() {
        timer = findViewById(R.id.Timer);
        handler = new Handler();
        recordButton = findViewById(R.id.recordButton);
        stopButton = findViewById(R.id.stopButton);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
                handler.postDelayed(runnable, 1000);
                timer.setText(updateTimer());
                sec++;
        }
    };
}