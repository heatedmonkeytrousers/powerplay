package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.Claw.MAX_POS;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.opencv.core.Mat;

public class Elevator extends Thread {

    public static int GROUND_POSITION = 0;
    public static int LOW_POSITION = 4041;
    public static int MIDDLE_POSITION = 6747;
    public static int HIGH_POSITION = 9350;

    private DcMotor elevatorDrive;
    private int totalCounts;
    private Gamepad gamepad;

    private Claw claw;

    public enum ELEVATOR_HEIGHT
    {
        GROUND,
        LOW,
        MEDIUM,
        HIGH
    }

    public Elevator(DcMotor elevatorDrive, Gamepad gamepad, Claw claw) {
        this.elevatorDrive = elevatorDrive;
        this.gamepad = gamepad;
        this.claw = claw;
    }
    public int getTotalCounts() {
        return totalCounts;
    }
    public boolean isUp() {
        return elevatorDrive.getCurrentPosition() > LOW_POSITION/2;
    }

    private void setPosition(double power, int position) {
        power = Range.clip(power, -1.0, 1.5);
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
            default:
                return;
        }
    }
    public void drop () {
        setPosition(-1, elevatorDrive.getCurrentPosition() - 500);
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
                setPosition(-1, GROUND_POSITION);

            } else if (gamepad.b) {

                setPosition(1, LOW_POSITION);

            } else if (gamepad.x) {

                setPosition(1, MIDDLE_POSITION);

            } else if (gamepad.y) {
                    setPosition(1, HIGH_POSITION);

            } else if (gamepad.back) {
                drop();
            }

        }
    }
}

