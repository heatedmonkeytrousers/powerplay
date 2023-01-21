package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

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

@Autonomous(name = "Robot Setup Super Class", group = "Robot")
@Disabled
public class StandardSetupOpMode extends LinearOpMode {

    // Declare Robot Setup members.
    protected ElapsedTime runtime = new ElapsedTime();
    protected DcMotor frontLeftDrive = null;
    protected DcMotor frontRightDrive = null;
    protected DcMotor rearLeftDrive = null;
    protected DcMotor rearRightDrive = null;
    protected DcMotor elevatorDrive = null;
    protected Servo clawServo = null;

    protected Claw claw = null;
    protected Elevator elevator = null;
    protected Motion motion = null;

    protected OpenCvWebcam webcam = null;
    protected Scalar mu = null;
    protected double redDist = 0;
    protected double greenDist = 0;
    protected double blueDist = 0;
    protected Motion.PARKING_SPOT position = Motion.PARKING_SPOT.PARK_ONE;


    @Override
    public void runOpMode() throws InterruptedException {

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        frontLeftDrive = hardwareMap.get(DcMotor.class, "frontLeftDrive"); //ch3
        frontRightDrive = hardwareMap.get(DcMotor.class, "frontRightDrive"); //ch2
        rearLeftDrive = hardwareMap.get(DcMotor.class, "rearLeftDrive"); //ch1
        rearRightDrive = hardwareMap.get(DcMotor.class, "rearRightDrive"); //ch0
        elevatorDrive = hardwareMap.get(DcMotor.class, "elevatorDrive"); //ch3
        clawServo = hardwareMap.get(Servo.class, "claw"); //eh0

        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // Pushing the left stick forward MUST make robot go forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips
        frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
        rearLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        rearRightDrive.setDirection(DcMotor.Direction.REVERSE);
        elevatorDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        clawServo.setDirection(Servo.Direction.FORWARD);

        // Init elevator drive modes
        elevatorDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        elevatorDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Set elevator to position 0 (the elevator has to be on the ground!)
        elevatorDrive.setTargetPosition(0);

        // Build the Claw class
        claw = new Claw(clawServo, null);

        // Build the elevator class
        elevator = new Elevator(elevatorDrive, null, claw);

        // Build the Motion class and give it a motion object
        motion = new Motion(frontLeftDrive, frontRightDrive, rearLeftDrive, rearRightDrive, null, elevator);
        elevator.setMotion(motion);

        // Build the camera class
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

        // Wait for the user to start the autonomous operation
        waitForStart();

        // Reset the 30 second runtime timer
        runtime.reset();
    }

    public Motion.PARKING_SPOT getParkingSpot() {
        return position;
    }

    public class CameraCalibration extends OpenCvPipeline {

        //Sets up position var and output
        Mat output = new Mat();

        @Override
        public Mat processFrame(Mat input) {

            //Crops the input but sets the output to the uncropped input
            Mat input2 = input;
            input = input.submat(new Rect(860, 440, 80, 120));
            output = input2;

            //Gets the averages for each red, green, and blue in the input
            mu = Core.mean(input);

            //Scalars for our ideal values
            Scalar red = new Scalar(186, 37, 48);
            Scalar green = new Scalar(48, 141, 106);
            Scalar blue = new Scalar(1, 123, 187);

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

            //Allows the user to see the cropped input
            if (gamepad1.y) {
                output = input;
            }

            //Displays a rectangle for lining up the webcam
            Imgproc.rectangle(
                    output,
                    new Point(
                            860,
                            440),
                    new Point(
                            940,
                            590),
                    new Scalar(0, 0, 0), 5);


            return output;
        }
    }
};
