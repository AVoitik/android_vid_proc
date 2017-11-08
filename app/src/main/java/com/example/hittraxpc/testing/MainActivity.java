package com.example.hittraxpc.testing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TimingLogger;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    protected Button beginButton, scanButton;
    protected ImageView iv;

    FFmpeg ffmpeg;
    FFmpegUtilities util;
    private String TAG = "MainActivity";
    protected int i = 1;
    baseballImager bimg;
    FFmpegCommands myCommand;

    //TODO: Programatically set these
    protected String VIDPATH_IN = Environment.getExternalStorageDirectory() + File.separator + "Video/";
    protected String VIDNAME_IN = "V_20171106_143000.mp4";

    //Load the openCV library
    static{System.loadLibrary("opencv_java3");}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        beginButton = (Button) findViewById(R.id.begin_Button);
        scanButton = (Button) findViewById(R.id.scan_Button);
        iv = (ImageView)findViewById(R.id.image_View);

        scanButton.setEnabled(false);

        //Need to load the ffmpeg binaries
        ffmpeg = FFmpeg.getInstance(getBaseContext());

        myCommand = new FFmpegCommands(FFmpegType.GET_EVERY_FIFTH_FRAME, VIDNAME_IN, VIDPATH_IN);
        bimg = new baseballImager(myCommand, iv);

        util = new FFmpegUtilities(ffmpeg, this);
        util.initializeFFmpegLibrary(beginButton);


        //For now: this button only pulls every 5th frame
        beginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //execute ffmpeg command;
                if(!util.ffmpegCommand(myCommand, scanButton)){
                    Toast.makeText(getBaseContext(), "Could not pull frames. File does not exist", Toast.LENGTH_LONG).show();
                }
            }
        });

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bimg.findBaseball();
            }
        });
    }
}