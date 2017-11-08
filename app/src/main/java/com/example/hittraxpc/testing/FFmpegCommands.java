package com.example.hittraxpc.testing;

import android.util.Log;

import java.io.File;

//TODO: Comment this file

public class FFmpegCommands {

    private String TAG = "FFMPEG Command Class";
    FFmpegType type;
    private String fileName;
    private String filePath;
    private String outFileName;
    private String cmd;
    private int verified = 0;
    private String[] fullCommand;
    private String saveFileID;

    public FFmpegCommands(FFmpegType type, String fileName, String filePath){
        this.type = type;
        this.fileName = fileName;
        this.filePath = filePath;
        verified = verifyPath(fileName, filePath);
        initialize();
    }

    //TODO: Add the rest of the ffmpeg commands to here
    private void initialize(){
        switch(type){
            case GET_EVERY_FIFTH_FRAME:
                outFileName = "everyFifth%d.bmp";
                saveFileID = "everyFifth";
                cmd = "select='not(mod(n,5))'";
                fullCommand = new String[]{"-i", filePath(), "-vsync", "0", "-preset", "ultrafast", "-vf", cmd, outFilePath() + outFileName};
                break;
            default:
                outFileName = "";
                cmd = "";
                break;
        }
    }

    private int verifyPath(String fileName, String filePath){
        File file = new File(filePath + fileName);
        if(file.exists()){
            return 1;
        }else{
            Log.d(TAG, "File Path Invalid: " + file.getPath());
            return 0;
        }
    }

    public boolean isValid(){
        if(verified == 0){
            return false;
        }else{
            return true;
        }
    }

    public String getOutputID(){return saveFileID;}

    public String filePath(){
        return filePath + fileName;
    }

    public String outFilePath(){
        return filePath + "Source/";
    }

    public String getOutputFileName(){
        return outFileName;
    }

    public String getType(){
        return type.toString();
    }

    public String getInputFileName(){
        return fileName;
    }

    public void setInputFileName(String fileName){

        this.fileName = fileName;

        //Check to see if valid path and file
        verified = verifyPath(fileName, filePath);
    }

    public String getInputFilePath(){
        return filePath;
    }

    public void setInputFilePath(String filePath){

        //Check to see if valid path and file
        this.filePath = filePath;
        verified = verifyPath(fileName, filePath);
    }

    public String[] getCommand(){ return fullCommand; }


}
