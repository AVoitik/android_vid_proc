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
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
import org.opencv.android.Utils;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.video.BackgroundSubtractor;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static org.opencv.android.Utils.bitmapToMat;

public class baseballImager {

    private String TAG = "baseballImager";

    private Bitmap backgroundFrame, nextFrame, tempFrame;
    private String filePath;
    private ImageView imageView;
    private int counter;
    private String frameID;
    private Mat grayImage, diffImg, matOne;
    private int erosion_size = 4;
    private int oldX = 0;
    private int oldY = 0;
    private int objCnt = 0;

    private int dilation_size = 4;

    private final List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

    public baseballImager(FFmpegCommands ffobj, ImageView imageView){
        this.filePath = ffobj.outFilePath();
        Log.d(TAG, filePath);
        this.imageView = imageView;
        this.counter = 1;
        this.frameID = ffobj.getOutputID();
        Log.d(TAG, frameID);
        backgroundFrame = getNextFrame();
        matOne = bitmapToGrayMat(backgroundFrame);
    }


    public void findBaseball(){
        //Need to grab the next frame
        Log.d(TAG, "Grabbed Frame #: " + Integer.toString(counter));
        nextFrame = getNextFrame();
        while(nextFrame != null) {

            //Create empty mat to hold difference
            Mat diffImg = new Mat();

            //Convert frame to grayscale
            Mat matTwo = bitmapToGrayMat(nextFrame);

            //Perform difference to get "Intensity Image"
            //      stored in diffImg
            Core.absdiff(matOne, matTwo, diffImg);

            //So null values don't mess us up
            Mat biLatMat = diffImg.clone();

            //Bilateral filter to get the edges
            Imgproc.bilateralFilter(diffImg, biLatMat, 11, 30, 17);

            //Create empty mat to hold thresholding
            Mat thrMat = biLatMat.clone();

            //Do image thresholding
            Imgproc.threshold(biLatMat, thrMat, 30, 255, Imgproc.THRESH_BINARY);

            //Empty mat to hold erode and dialate
            Mat erDiMat = thrMat.clone();

            //Erode the thresholded image
            Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2 * erosion_size + 1, 2 * erosion_size + 1));
            Imgproc.erode(thrMat, erDiMat, erodeElement);

            //Dialate the thresholded image
            Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2 * dilation_size + 1, 2 * dilation_size + 1));
            Imgproc.dilate(erDiMat, erDiMat, dilateElement);

            Imgproc.findContours(erDiMat.clone(), contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            if(contours.size() == 0){
                Log.d(TAG, "NOTHING FOUND AT FRAME NUMBER: " + counter);
                counter++;
                nextFrame = getNextFrame();
            }else{
                Log.d(TAG, "BALL FOUND AT FRAME NUMBER: " + counter + " SIZE: " + contours.size());
                displayMat(erDiMat);
                nextFrame = null;

            }
        }
    }

    private Mat bitmapToGrayMat(Bitmap bmp){
        Mat tmp = new Mat(bmp.getHeight(), bmp.getWidth(), CvType.CV_8UC1, new Scalar(4));
        bitmapToMat(bmp, tmp);
        Imgproc.cvtColor(tmp, tmp, Imgproc.COLOR_BGR2GRAY);

        return tmp;
    }

    private Bitmap getNextFrame(){
        File f = new File(filePath + frameID + Integer.toString(counter) + ".bmp");
        if(!f.exists()){
            return null;
        }else{
            Bitmap bmp = BitmapFactory.decodeFile(filePath + frameID + Integer.toString(counter) + ".bmp");
            counter++;
            return bmp;
        }

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
        Bitmap tempFrame = Bitmap.createBitmap(backgroundFrame.getWidth(), backgroundFrame.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(frameMat, tempFrame);
        displayBitmap(tempFrame);
    }
}
