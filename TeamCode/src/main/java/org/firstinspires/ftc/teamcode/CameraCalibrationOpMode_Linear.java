package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.checkerframework.checker.units.qual.C;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;
import org.openftc.easyopencv.OpenCvWebcam;

@TeleOp(name = "Camera Calibration", group = "Linear Opmode")
public class CameraCalibrationOpMode_Linear extends StandardSetupOpMode {
    OpenCvWebcam webcam;

    @Override
    public void runOpMode() {
        //Set Up Webcam and Pipeline
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);

        webcam.setPipeline(new CameraCalibration());

        webcam.setMillisecondsPermissionTimeout(2500); // Timeout for obtaining permission is configurable. Set before opening.
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {

            @Override
            public void onOpened() {
                //Camera Starts Running
                webcam.startStreaming(1920, 1080, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {
                //Run this Code if there is an Error
            }
        });
        telemetry.addLine("Waiting for start");
        telemetry.addData("Parking spot", position);
        telemetry.addData("Red Dist", (int) redDist);
        telemetry.addData("Green Dist", (int) greenDist);
        telemetry.addData("Blue Dist", (int) blueDist);
        telemetry.addData("Mean", "%d %d %d", (int) mu.val[0], (int) mu.val[1], (int) mu.val[2]);
        telemetry.update();
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            sleep(50);
        }

    }


}

