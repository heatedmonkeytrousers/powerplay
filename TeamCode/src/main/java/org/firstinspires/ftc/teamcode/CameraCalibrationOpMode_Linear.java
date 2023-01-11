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
                webcam.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT);
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
        //Above 200, below 100, below 100
        private double redThreshR = 117;
        private double redThreshG = 100;
        private double redThreshB = 100;

        //Below 100, above 150, below 150
        private double greenThreshR = 193;
        private double greenThreshG = 71;
        private double greenThreshB = 108;

        //Below 100, below 50, above 150
        private double blueThreshR = 100;
        private double blueThreshG = 110;
        private double blueThreshB = 150;

        private int red;
        private int blue;
        private int green;

        private int position;

        Mat output = new Mat();

        @Override
        public Mat processFrame(Mat input) {

            Mat redOutput = new Mat();
            Mat blueOutput = new Mat();
            Mat greenOutput = new Mat();

            Mat redTemp = new Mat();
            Mat blueTemp = new Mat();
            Mat greenTemp = new Mat();


            Mat input2 = input;
            input = input.submat(new Rect(220, 192, 100, 100));


            Core.extractChannel(input, redOutput, 0);
            Core.extractChannel(input, greenOutput, 1);
            Core.extractChannel(input, blueOutput, 2);

            //Red Channel
            Imgproc.threshold(redOutput, redTemp, redThreshR, 255, Imgproc.THRESH_BINARY);
            Imgproc.threshold(blueOutput, blueTemp, redThreshB, 255, Imgproc.THRESH_BINARY_INV);
            Imgproc.threshold(greenOutput, greenTemp, redThreshG, 255, Imgproc.THRESH_BINARY_INV);

            Core.bitwise_and(redTemp, blueTemp, redOutput);
            Core.bitwise_and(redOutput, greenTemp, redOutput);

            //Core.multiply(redOutput, new Scalar(128), redOutput);

            //Green Channel
            Imgproc.threshold(redOutput, redTemp, greenThreshR, 255, Imgproc.THRESH_BINARY_INV);
            Imgproc.threshold(blueOutput, blueTemp, greenThreshB, 255, Imgproc.THRESH_BINARY_INV);
            Imgproc.threshold(greenOutput, greenTemp, greenThreshG, 255, Imgproc.THRESH_BINARY);

            Core.bitwise_and(redTemp, blueTemp, greenOutput);
            Core.bitwise_and(greenOutput, greenTemp, greenOutput);

            //Core.multiply(greenOutput, new Scalar(128), greenOutput);

            //Blue Channel
            Imgproc.threshold(redOutput, redTemp, blueThreshR, 255, Imgproc.THRESH_BINARY_INV);
            Imgproc.threshold(blueOutput, blueTemp, blueThreshB, 255, Imgproc.THRESH_BINARY);
            Imgproc.threshold(greenOutput, greenTemp, blueThreshG, 255, Imgproc.THRESH_BINARY_INV);

            Core.bitwise_and(redTemp, blueTemp, blueOutput);
            Core.bitwise_and(blueOutput, greenTemp, blueOutput);

            //Core.multiply(blueOutput, new Scalar(128), blueOutput);


            if (gamepad1.dpad_up) {
                blueThreshR += 1;
            } else if (gamepad1.dpad_down) {
                blueThreshR -= 1;
            }
            if (gamepad1.dpad_right) {
                blueThreshG += 1;
            } else if (gamepad1.dpad_left) {
                blueThreshG -= 1;
            }
            if (gamepad1.right_bumper) {
                blueThreshB += 1;
            } else if (gamepad1.left_bumper) {
                blueThreshB -= 1;
            }

                /*
            } else if (gamepad1.right_trigger == 1) {
                highBlack += 1;
            } else if (gamepad1.left_trigger == 1) {
                highBlack -= 1;
            }
             */

                if (gamepad1.b) {
                    output = redOutput;
                    telemetry.addData("Current Output", "Red");
                } else if (gamepad1.x) {
                    output = blueOutput;
                    telemetry.addData("Current Output", "Blue");
                } else if (gamepad1.a) {
                    output = greenOutput;
                    telemetry.addData("Current Output", "Green");
                } else {
                    output = input2;
                    telemetry.addData("Current Output", "Regular");
                }

                //Mean
                //Red 200, 70, 85
                //Blue 40, 150, 190
                //Green 70, 150, 80

            /*
            Scalar lowGreenScalar = new Scalar(0,140,0);
            Scalar highGreenScalar = new Scalar(100,255,100);

            Scalar lowRedScalar = new Scalar(180, 0, 0);
            Scalar highRedScalar = new Scalar(255, 100, 100);

            Scalar lowBlueScalar = new Scalar(0, 130, 170);
            Scalar highBlueScalar = new Scalar(100, 170, 255);

            Core.inRange(input, lowGreenScalar, highGreenScalar, greenOutput);
            Core.inRange(input, lowRedScalar, highRedScalar, redOutput);
            Core.inRange(input, lowBlueScalar, highBlueScalar, blueOutput);
             */

                red = Core.countNonZero(redOutput);
                blue = Core.countNonZero(blueOutput);
                green = Core.countNonZero(greenOutput);

                Scalar mu = Core.mean(input);

                if (red > blue && red > green) {
                    position = 1;

                } else if (blue > green) {
                    position = 2;

                } else {
                    position = 3;
                }

                Imgproc.rectangle(
                        output,
                        new Point(
                                220,
                                192),
                        new Point(
                                320,
                                292),
                        new Scalar(0, 0, 0), 1);

                telemetry.addData("Parking spot", position);
                telemetry.addData("Red Pixels", red);
                telemetry.addData("Blue Pixels", blue);
                telemetry.addData("Green Pixels", green);
                telemetry.addData("GreenThreshR", blueThreshR);
                telemetry.addData("GreenThreshG", blueThreshG);
                telemetry.addData("GreenThreshB", blueThreshB);
                telemetry.addData("Mean", mu);
                telemetry.addLine("Dpad up/down: Change Red Thresh");
                telemetry.addLine("Dpad left/right: Change Blue Thresh");
                telemetry.addLine("Right/Left Bumper: Change Green Thresh");
                telemetry.update();
                sleep(100);

                return output;
            }
        }
    }

