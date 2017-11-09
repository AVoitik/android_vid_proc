//Alex Voitik
//
//FFmpegUtilities.java
//
//This file contains all of the methods needed to complete
//      ffmpeg execution on files in android
//
//
package com.example.hittraxpc.testing;

import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;


public class FFmpegUtilities {

    protected String TAG = "FFmpeg Utilities";

    private FFmpeg utilFFmpeg;
    private Context context;

    //Constructor
    //
    //Initialize context and ffmpeg object

    FFmpegUtilities(FFmpeg ffmpeg, Context context){
        utilFFmpeg = ffmpeg;
        this.context = context;
    }

    //****************************************************************************
    //This method initializes the ffmpeg binaries and controls the advance of the
    //application
    //
    //Pre: Button resource from main layout
    //
    //Post: Button enabled on success, else disabled. This is for the
    //      sake of trying to deter users from using old hardware
    //      from using this application.
    //*****************************************************************************
    protected void initializeFFmpegLibrary(final Button beginButton) {

        try {
            utilFFmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart(){}

                @Override
                public void onFailure() {beginButton.setEnabled(false);}

                @Override
                public void onSuccess() {beginButton.setEnabled(true);}

                @Override
                public void onFinish() {}

            });
        } catch (FFmpegNotSupportedException e) {
            Log.d(TAG, "FFmpeg Not Supported");
            beginButton.setEnabled(false);
        }
    }

    //*****************************************************************************
    //This method will execute a ffmpeg console command.
    //
    //Pre: Needs a initialized and valid ffobj in order to run
    //
    //Post: Toast output to tell what the outcome of the execution was
    //      and a boolean return type
    //*****************************************************************************
    protected boolean ffmpegCommand(final FFmpegCommands ffobj, final Button btn, final ImageView iv) {
        //String selectCommand = "select=between'(n," + Integer.toString(VIDSTART_IN) + "," + Integer.toString(VIDEND_IN) + ")'";

        //Check if the command object file exists or not
        if(ffobj.isValid() == false){
            return false;
        }
        try {
            utilFFmpeg.execute(ffobj.getCommand(), new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {Toast.makeText(context, "Started...", Toast.LENGTH_SHORT).show();}

                @Override
                public void onProgress(String message) {}

                @Override
                public void onFailure(String message) {

                    Toast.makeText(context, "Failed. Check Logcat for details", Toast.LENGTH_LONG).show();
                    Log.d(TAG, message);
                    btn.setEnabled(false);
                }

                @Override
                public void onSuccess(String message) {
                    Toast.makeText(context, "Succeeded", Toast.LENGTH_LONG).show();
                    btn.setEnabled(true);
                }

                @Override
                public void onFinish() {
                    baseballImager bimg = new baseballImager(ffobj, iv);
                    bimg.findBaseball();
                }
            });

        } catch (FFmpegCommandAlreadyRunningException e) {
            Toast.makeText(context, "FFmpeg is already running", Toast.LENGTH_LONG).show();
        }

        return true;
    }
}
