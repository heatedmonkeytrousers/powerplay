package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

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

@TeleOp (name="Camera Calibration", group="Linear Opmode")
public class CameraCalibrationOpMode_Linear extends LinearOpMode {
    OpenCvWebcam webcam;

    @Override
    public void runOpMode() {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);

        webcam.setPipeline(new CameraCalibration());

        webcam.setMillisecondsPermissionTimeout(2500); // Timeout for obtaining permission is configurable. Set before opening.
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {

            @Override
            public void onOpened() {
                webcam.startStreaming(1920, 1080, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {

            }
        });
        telemetry.addLine("Waiting for start");
        telemetry.update();

        waitForStart();
        while (opModeIsActive()) {
            sleep(100);

        }

    }

    public class CameraCalibration extends OpenCvPipeline {

        private int position;

        Mat output = new Mat();

        @Override
        public Mat processFrame(Mat input) {

            input = input.submat(new Rect(860, 440, 80, 120));
            output = input;

            Scalar mu = Core.mean(input);

            Scalar red = new Scalar(186, 37, 48);
            Scalar green = new Scalar(48, 141, 106);
            Scalar blue = new Scalar(1, 123, 187);

            double redDist = Math.sqrt(Math.pow((mu.val[0]-red.val[0]),2) + Math.pow((mu.val[1]-red.val[1]),2) + Math.pow((mu.val[2]-red.val[2]),2));
            double greenDist = Math.sqrt(Math.pow((mu.val[0]-green.val[0]),2) + Math.pow((mu.val[1]-green.val[1]),2) + Math.pow((mu.val[2]-green.val[2]),2));
            double blueDist = Math.sqrt(Math.pow((mu.val[0]-blue.val[0]),2) + Math.pow((mu.val[1]-blue.val[1]),2) + Math.pow((mu.val[2]-blue.val[2]),2));

            if (redDist < greenDist && redDist < blueDist) {
                //red
                position = 1;
            } else if (greenDist < blueDist) {
                //green
                position = 2;
            } else {
                //blue
                position = 3;
            }

                Imgproc.rectangle(
                        output,
                        new Point(
                                860,
                                440),
                        new Point(
                                940,
                                590),
                        new Scalar(0, 0, 0), 5);

                telemetry.addData("Parking spot", position);
                telemetry.addData("Mean", "%d %d %d", (int)mu.val[0], (int)mu.val[1], (int)mu.val[2]);
                telemetry.update();
                sleep(100);

                return output;
            }
        }
    }

