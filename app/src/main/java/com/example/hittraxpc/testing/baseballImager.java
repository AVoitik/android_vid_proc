//Alex Voitik
//
//baseballImager.java
//
//This class deals with all the methods that are needed
//      to process our sequence of images in openCV.
//
//Much of this code is taken from Peter Tarca's
//      ball_trk_no_color.py project

package com.example.hittraxpc.testing;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import org.opencv.android.Utils;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;

import static org.opencv.android.Utils.bitmapToMat;

public class baseballImager {

    private String TAG = "baseballImager";

    private Bitmap backgroundFrame, nextFrame;
    private String filePath;
    private ImageView imageView;
    private int counter;
    private String frameID;
    private Mat grayImage, diffImg;

    public baseballImager(FFmpegCommands ffobj, ImageView imageView){
        this.filePath = ffobj.outFilePath();
        Log.d(TAG, filePath);
        this.imageView = imageView;
        this.counter = 1;
        this.frameID = ffobj.getOutputID();
        Log.d(TAG, frameID);
        backgroundFrame = getNextFrame();

    }


    public void findBaseball(){
        //Need to grab the first frame
        Log.d(TAG, "Grabbed Frame #: " + Integer.toString(counter));
        nextFrame = getNextFrame();
        //Do the differencing
        Mat diffImg = new Mat();
        Mat matTwo = bitmapToGrayMat(nextFrame);
        Mat matOne = bitmapToGrayMat(backgroundFrame);

        Core.absdiff(matOne, matTwo, diffImg);
        displayMat(diffImg);
    }

    private Mat bitmapToGrayMat(Bitmap bmp){
        Mat tmp = new Mat(bmp.getHeight(), bmp.getWidth(), CvType.CV_8UC1, new Scalar(4));
        bitmapToMat(bmp, tmp);
        Imgproc.cvtColor(tmp, tmp, Imgproc.COLOR_BGR2GRAY);

        return tmp;
    }

    private Bitmap getNextFrame(){
        Bitmap bmp = BitmapFactory.decodeFile(filePath + frameID + Integer.toString(counter) + ".bmp");
        counter++;

        return bmp;
    }

    private Bitmap getPreviousFrame(){
        Bitmap bmp = BitmapFactory.decodeFile(filePath + frameID + Integer.toString(counter - 1) + ".bmp");
        counter--;
        return bmp;
    }

    private void displayBitmap(Bitmap frame){
        imageView.setImageBitmap(frame);
    }

    private void displayMat(Mat frameMat){
        Utils.matToBitmap(frameMat, backgroundFrame);
        displayBitmap(backgroundFrame);
    }
}
