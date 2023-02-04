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
public class StandardSetupOpMode extends CameraSetupOpMode {

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


    @Override
    public void runOpMode() throws InterruptedException {
        // Call camera setup
        super.runOpMode();

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
        motion = new Motion(frontLeftDrive, frontRightDrive, rearLeftDrive, rearRightDrive, null, elevator, claw);
        elevator.setMotion(motion);
    }
}