package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;

public class Elevator extends Thread {

    private static int LOW_POSITION = 3941;
    private static int MIDDLE_POSITION = 6647;
    private static int HIGH_POSITION = 9700;
    private DcMotor elevatorDrive;
    private int totalCounts;
    private Gamepad gamepad;

    private Motion motion;

    public Elevator(DcMotor elevatorDrive, Gamepad gamepad) {
        this.elevatorDrive = elevatorDrive;
        this.gamepad = gamepad;
    }
    public int getTotalCounts() {
        return totalCounts;
    }
    public boolean isUp() {
        return elevatorDrive.getCurrentPosition() > LOW_POSITION;
    }
    @Override
    public void run() {
        while (!isInterrupted()) {
            double elevatorPower = gamepad.left_trigger - gamepad.right_trigger;
            elevatorPower = Range.clip(elevatorPower, -1.0, 1.5);
            elevatorDrive.setPower(elevatorPower);
            totalCounts = elevatorDrive.getCurrentPosition();

            // The set buttons for the elevators highs
            if (gamepad.a) {
                elevatorDrive.setPower(-1);
                while (totalCounts > 0) {
                    totalCounts = elevatorDrive.getCurrentPosition();
                    if (gamepad.start) {
                        elevatorDrive.setPower(0);
                        break;
                    }
                }
                elevatorDrive.setPower(0.1);
                while (totalCounts < 0) {
                    totalCounts = elevatorDrive.getCurrentPosition();
                }

            } else if (gamepad.b) {
                elevatorDrive.setPower(1);
                while (totalCounts < LOW_POSITION) {
                    totalCounts = elevatorDrive.getCurrentPosition();
                    if (gamepad.start) {
                        elevatorDrive.setPower(0);
                        break;
                    }
                }
            } else if (gamepad.x) {
                elevatorDrive.setPower(1);
                while (totalCounts < MIDDLE_POSITION) {
                    totalCounts = elevatorDrive.getCurrentPosition();
                    if (gamepad.start) {
                        elevatorDrive.setPower(0);
                        break;
                    }
                }
            } else if (gamepad.y) {
                elevatorDrive.setPower(1);
                while (totalCounts < HIGH_POSITION) {

                    elevatorDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    elevatorDrive.setTargetPosition(HIGH_POSITION);
                    totalCounts = elevatorDrive.getCurrentPosition();

                    if (gamepad.start) {
                        elevatorDrive.setPower(0);
                        elevatorDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                        break;
                    }
                }
            } else {
                elevatorDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }
        }
    }
}
