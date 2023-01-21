package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

@TeleOp(name = "Camera Calibration", group = "Linear Opmode")
public class CameraCalibrationOpMode_Linear extends StandardSetupOpMode {
    OpenCvWebcam webcam;

    @Override
    public void runOpMode() {

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

