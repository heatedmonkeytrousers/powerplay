package org.firstinspires.ftc.teamcode;

import android.bluetooth.le.ScanCallback;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//To Do: Adjust ideal rgb for lower values and check if masking works on field
@Autonomous(name = "Robot Setup Camera Super Class", group = "Robot")
@Disabled
public class                                        CameraSetupOpMode extends LinearOpMode {

    protected OpenCvWebcam webcam = null;
    protected Scalar mu = new Scalar(0, 0, 0);
    protected Motion.PARKING_SPOT position = Motion.PARKING_SPOT.PARK_ONE;


    @Override
    public void runOpMode() throws InterruptedException {
        // Build the camera class
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        webcam.setPipeline(new CameraCalibration());

        //webcam.setMillisecondsPermissionTimeout(2500); // Timeout for obtaining permission is configurable. Set before opening.
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {

            @Override
            public void onOpened() {
                //Camera Starts Running
                //1920, 1080
                webcam.startStreaming(432, 240, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {
                //Run this Code if there is an Error
            }
        });
    }

    public class CameraCalibration extends OpenCvPipeline {

        //Sets up position var and output
        Mat output = new Mat();

        @Override
        public Mat processFrame(Mat input) {
             double redDist = 0;
             double greenDist = 0;
             double blueDist = 0;
             double greyDist = 0;

            //Crops the input but sets the output to the un-cropped input
            Mat input2 = input;
            //input = input.submat(new Rect(860, 440, 80, 120));
            input = input.submat(new Rect(50, 50, 100, 120));
            output = input2;

            //Gets the averages for each red, green, and blue in the input
            mu = Core.mean(input);

            //Scalars for our ideal values
            Scalar red = new Scalar(175, 48, 51);
            Scalar green = new Scalar(54, 133, 113);
            Scalar blue = new Scalar(8, 106, 171);
            Scalar grey = new Scalar(140, 140, 140);
            Mat mask = new Mat(output.rows() , output.cols(), output.type());

            for (int r = 0; r < output.rows(); r++) {
                for (int c = 0; c < output.cols(); c++) {
                    double[] v = output.get(r, c);
                    redDist = Math.sqrt(Math.pow((v[0] - red.val[0]), 2) + Math.pow((v[1] - red.val[1]), 2) + Math.pow((v[2] - red.val[2]), 2));
                    greenDist = Math.sqrt(Math.pow((v[0] - green.val[0]), 2) + Math.pow((v[1] - green.val[1]), 2) + Math.pow((v[2] - green.val[2]), 2));
                    blueDist = Math.sqrt(Math.pow((v[0] - blue.val[0]), 2) + Math.pow((v[1] - blue.val[1]), 2) + Math.pow((v[2] - blue.val[2]), 2));
                    greyDist = Math.sqrt(Math.pow((v[0] - grey.val[0]), 2) + Math.pow((v[1] - grey.val[1]), 2) + Math.pow((v[2] - grey.val[2]), 2));

                    if (greyDist < redDist && greyDist < greenDist && greyDist < blueDist) {
                        //mask
                        //double [] data = {255,255,255,255};
                       // mask.put(r, c, data);
                    } else {
                        double [] data = {255,255,255,255};
                        mask.put(r, c, data);
                    }
                }
            }

            Core.bitwise_and(mask, output, output);

            //Determines the distance the averages are from our ideal values and normalizes them
            redDist = Math.sqrt(Math.pow((mu.val[0] - red.val[0]), 2) + Math.pow((mu.val[1] - red.val[1]), 2) + Math.pow((mu.val[2] - red.val[2]), 2));
            greenDist = Math.sqrt(Math.pow((mu.val[0] - green.val[0]), 2) + Math.pow((mu.val[1] - green.val[1]), 2) + Math.pow((mu.val[2] - green.val[2]), 2));
            blueDist = Math.sqrt(Math.pow((mu.val[0] - blue.val[0]), 2) + Math.pow((mu.val[1] - blue.val[1]), 2) + Math.pow((mu.val[2] - blue.val[2]), 2));

            //Determines position based on which distance is the smallest
            if (redDist < greenDist && redDist < blueDist) {
                //Red
                position = Motion.PARKING_SPOT.PARK_ONE;
            } else if (greenDist < blueDist) {
                //Green
                position = Motion.PARKING_SPOT.PARK_TWO;
            } else {
                //Blue
                position = Motion.PARKING_SPOT.PARK_THREE;
            }

            //Displays a rectangle for lining up the webcam

            Imgproc.rectangle(
                    output,
                    new Point(
                            50,
                            50),
                    new Point(
                            150,
                            170),
                    new Scalar(0, 0, 0), 2);



            //webcam.closeCameraDevice();

            //  Debug
            //telemetry.addData("Parking Spot", position);
            telemetry.update();
            return output;
        }
    }
}