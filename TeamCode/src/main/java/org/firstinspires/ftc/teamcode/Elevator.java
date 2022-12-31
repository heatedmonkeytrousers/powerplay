package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.Claw.MAX_POS;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.opencv.core.Mat;

public class Elevator extends Thread {

    public static int GROUND_POSITION = 0;
    public static int LOW_POSITION = 4141;
    public static int MIDDLE_POSITION = 6847;
    public static int HIGH_POSITION = 9450;

    public static int CONE_5_POS = 1529;
    public static int CONE_4_POS = 1145;
    public static int CONE_3_POS = 748;
    public static int CONE_2_POS = 400;
    public static int CONE_1_POS = GROUND_POSITION;
    public static int CONE_WIGGLE = (int)(CONE_2_POS * 0.1);

    public static double ELEVATOR_SPEED = 1.5;
    public static double MIN_ELEVATOR_SPEED = -2.0;
    public static double MAX_ELEVATOR_SPEED = 2.0;

    private DcMotor elevatorDrive;
    private int totalCounts;
    private Gamepad gamepad;
    private Motion motion;

    private Claw claw;

    public enum ELEVATOR_HEIGHT
    {
        GROUND,
        LOW,
        MEDIUM,
        HIGH,
        DROP,
        ADJUST_DOWN,
        ADJUST_UP,
        CONE_5,
        CONE_4,
        CONE_3,
        CONE_2,
        CONE_1
    }

    public Elevator(DcMotor elevatorDrive, Gamepad gamepad, Claw claw) {
        this.elevatorDrive = elevatorDrive;
        this.gamepad = gamepad;
        this.claw = claw;
        this.motion = null;
    }

    public void setMotion (Motion motion) {
        this.motion = motion;
    }

    public int getTotalCounts() {
        return totalCounts;
    }
    public boolean isUp() {
        return elevatorDrive.getCurrentPosition() > LOW_POSITION/2;
    }

    private void setPosition(double power, int position) {
        power = Range.clip(power, MIN_ELEVATOR_SPEED, MAX_ELEVATOR_SPEED);
        elevatorDrive.setTargetPosition(position);
        elevatorDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        elevatorDrive.setPower(power);

    }

    public void setPosition(double power, ELEVATOR_HEIGHT height) {
        switch(height) {
            case GROUND:
                setPosition(power, GROUND_POSITION);
                break;
            case LOW:
                setPosition(power, LOW_POSITION);
                break;
            case MEDIUM:
                setPosition(power, MIDDLE_POSITION);
                break;
            case HIGH:
                setPosition(power, HIGH_POSITION);
                break;
            case DROP:
                setPosition(power, elevatorDrive.getCurrentPosition() - 500);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {

                }
                claw.openClaw();
                break;
            case ADJUST_DOWN:
                setPosition(power, elevatorDrive.getCurrentPosition()-250);
                break;
            case ADJUST_UP:
                setPosition(power,elevatorDrive.getCurrentPosition()+250);
                break;
            case CONE_5:
                setPosition(power, CONE_5_POS);
                break;
            case CONE_4:
                setPosition(power,CONE_4_POS);
                break;
            case CONE_3:
                setPosition(power, CONE_3_POS);
                break;
            case CONE_2:
                setPosition(power, CONE_2_POS);
                break;
            case CONE_1:
                setPosition(power, CONE_1_POS);
                break;
            default:
                return;
        }
    }
    public void drop () {
        setPosition(-ELEVATOR_SPEED, elevatorDrive.getCurrentPosition() - 500);
        totalCounts = elevatorDrive.getCurrentPosition();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {

        }
        claw.openClaw();

    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            double elevatorPower = gamepad.left_trigger - gamepad.right_trigger;
            //elevatorPower = Range.clip(elevatorPower, -1.0, 1.5);
            //elevatorDrive.setPower(elevatorPower);
            totalCounts = elevatorDrive.getCurrentPosition();

            // The set buttons for the elevators highs
            if (gamepad.a) {
                setPosition(-ELEVATOR_SPEED, GROUND_POSITION);

            } else if (gamepad.b) {

                setPosition(ELEVATOR_SPEED, LOW_POSITION);

            } else if (gamepad.x) {

                setPosition(ELEVATOR_SPEED, MIDDLE_POSITION);

            } else if (gamepad.y) {
                    setPosition(ELEVATOR_SPEED, HIGH_POSITION);

            } else if (gamepad.dpad_down) {
                int pos = elevatorDrive.getCurrentPosition()-100;
                if(pos<GROUND_POSITION) pos = GROUND_POSITION;
                setPosition(-ELEVATOR_SPEED, pos);

            } else if (gamepad.dpad_up) {
                int pos = elevatorDrive.getCurrentPosition()+100;
                if(pos>HIGH_POSITION) pos = HIGH_POSITION;
                setPosition(ELEVATOR_SPEED, pos);

            } else if (gamepad.back) {
                drop();
            } else if (gamepad.dpad_left) {
                int pos = elevatorDrive.getCurrentPosition();
                if (pos > CONE_5_POS + CONE_WIGGLE){
                    setPosition(-ELEVATOR_SPEED, ELEVATOR_HEIGHT.CONE_5);
                } else if (pos > CONE_4_POS + CONE_WIGGLE){
                    setPosition(-ELEVATOR_SPEED, ELEVATOR_HEIGHT.CONE_4);
                } else if (pos > CONE_3_POS + CONE_WIGGLE){
                    setPosition(-ELEVATOR_SPEED, ELEVATOR_HEIGHT.CONE_3);
                } else if (pos > CONE_2_POS + CONE_WIGGLE){
                    setPosition(-ELEVATOR_SPEED, ELEVATOR_HEIGHT.CONE_2);
                }
                else{
                    setPosition(-ELEVATOR_SPEED, ELEVATOR_HEIGHT.CONE_1);
                }
            } else if (gamepad.dpad_right) {
                int pos = elevatorDrive.getCurrentPosition();
                if(pos < CONE_1_POS){
                    setPosition(ELEVATOR_SPEED, ELEVATOR_HEIGHT.CONE_1);
                } else if (pos < CONE_2_POS - CONE_WIGGLE){
                    setPosition(ELEVATOR_SPEED, ELEVATOR_HEIGHT.CONE_2);
                } else if (pos < CONE_3_POS - CONE_WIGGLE){
                    setPosition(ELEVATOR_SPEED, ELEVATOR_HEIGHT.CONE_3);
                } else if (pos < CONE_4_POS - CONE_WIGGLE){
                    setPosition(ELEVATOR_SPEED, ELEVATOR_HEIGHT.CONE_4);
                } else if (pos < CONE_5_POS - CONE_WIGGLE){
                    setPosition(ELEVATOR_SPEED, ELEVATOR_HEIGHT.CONE_5);
                }
                else{
                    setPosition(-ELEVATOR_SPEED, ELEVATOR_HEIGHT.CONE_1);
                }
            }
        }
    }
}


