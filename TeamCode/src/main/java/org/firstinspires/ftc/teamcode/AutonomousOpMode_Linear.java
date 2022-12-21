package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="Robot: Auto Drive To Line", group="Robot")

public class AutonomousOpMode_Linear extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor frontLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor rearLeftDrive = null;
    private DcMotor rearRightDrive = null;
    private DcMotor elevatorDrive = null;
    private Servo clawServo = null;

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

        elevatorDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        elevatorDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Wait for the game to start (driver presses PLAY)
        elevatorDrive.setTargetPosition(0);

        Claw claw =new Claw(clawServo, null);
        Elevator elevator = new Elevator(elevatorDrive,null, claw);
        Motion motion = new Motion (frontLeftDrive, frontRightDrive, rearLeftDrive, rearRightDrive, null , elevator);
        elevator.setMotion(motion);

        waitForStart();
        runtime.reset();


        //Autonomous time!!!!!
        //copy code from ftc computer
        claw.closeClaw();
        elevator.setPosition(-1, Elevator.ELEVATOR_HEIGHT.LOW);
        motion.translate (Motion.Direction.FORWARD, 2.7, 0.6);
        elevator.setPosition(-1, Elevator.ELEVATOR_HEIGHT.HIGH);
        motion.translate(Motion.Direction.BACKWARD, 0.45, 0.5);
        motion.rotation (Motion.Direction.LEFT,45,0.5);
        motion.translate (Motion.Direction.FORWARD, .35,0.5);
        elevator.drop();
        motion.translate (Motion.Direction.BACKWARD, .35,0.5);
        elevator.setPosition(-1, Elevator.ELEVATOR_HEIGHT.CONE_5);
        motion.rotation(Motion.Direction.RIGHT, 135,0.5);
        claw.openClaw();
        motion.translate (Motion.Direction.FORWARD,1.05,0.5);
        sleep(300);
        claw.closeClaw();
        sleep(400);
        elevator.setPosition(-1, Elevator.ELEVATOR_HEIGHT.LOW);
        sleep(400);
        motion.translate(Motion.Direction.BACKWARD, 1,0.5);
        motion.rotation(Motion.Direction.RIGHT, 45,0.5);
        motion.translate (Motion.Direction.FORWARD, .35,0.5);
        elevator.drop();
        motion.translate (Motion.Direction.BACKWARD, .35,0.5);
        motion.rotation(Motion.Direction.RIGHT, 45,0.5);
        motion.translate (Motion.Direction.FORWARD, .5,0.5);
        elevator.setPosition(-1, Elevator.ELEVATOR_HEIGHT.GROUND);

    }

}
