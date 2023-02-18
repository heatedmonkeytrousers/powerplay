package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.Claw.MAX_POS;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.opencv.core.Mat;

public class Elevator extends Thread {
        //117 RPM Values
    /*

        public static int GROUND_POSITION = 0;
        public static int LOW_POSITION = 4141;
        public static int MIDDLE_POSITION = 6847;
        public static int HIGH_POSITION = 9450;

        public static int DROP_AMOUNT = 800;

        public static int CONE_5_POS = 1529;
        public static int CONE_4_POS = 1145;
        public static int CONE_3_POS = 748;
        public static int CONE_2_POS = 400;
        public static int CONE_1_POS = GROUND_POSITION;
     */

    // 435rpm - 13.7:1
    // 312rpm - 19.2:1
        public static int GROUND_POSITION = 0;
        public static int LOW_POSITION = (int) ((4141/50.9) * 19.2);
        public static int MIDDLE_POSITION = (int) ((6847/50.9) * 19.2);
        public static int HIGH_POSITION = (int) ((9650/50.9) * 19.2);

        public static int DROP_AMOUNT = (int) ((800/50.9) * 19.2);

        public static int CONE_5_POS = (int) ((1529/50.9) * 19.2);
        public static int CONE_4_POS = (int) ((1145/50.9) * 19.2);
        public static int CONE_3_POS = (int) ((748/50.9) * 19.2);
        public static int CONE_2_POS = (int) ((400/50.9) * 19.2);
        public static int CONE_1_POS = GROUND_POSITION;

        public static int MANUAL_DROP = (int) ((200/50.9) * 19.2);

        public static int CONE_WIGGLE = (int)(CONE_2_POS * 0.1);

        public static double ELEVATOR_SPEED = 1;
        public static double MIN_ELEVATOR_SPEED = -1;
        public static double MAX_ELEVATOR_SPEED = 1;

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
                    setPosition(power, elevatorDrive.getCurrentPosition() - DROP_AMOUNT);
                    try {
                        Thread.sleep(DROP_AMOUNT);
                    } catch (InterruptedException e) {

                    }
                    claw.openClaw();
                    break;
                case ADJUST_DOWN:
                    setPosition(power, elevatorDrive.getCurrentPosition()-MANUAL_DROP);
                    break;
                case ADJUST_UP:
                    setPosition(power,elevatorDrive.getCurrentPosition()+MANUAL_DROP);
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
            setPosition(ELEVATOR_SPEED, elevatorDrive.getCurrentPosition() - DROP_AMOUNT);
            totalCounts = elevatorDrive.getCurrentPosition();
            try {
                Thread.sleep(DROP_AMOUNT);
            } catch (InterruptedException e) {

            }
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                double elevatorPower = gamepad.left_trigger - gamepad.right_trigger;

                totalCounts = elevatorDrive.getCurrentPosition();

                // The set buttons for the elevators highs
                if (gamepad.a) {

                    setPosition(ELEVATOR_SPEED, GROUND_POSITION);

                } else if (gamepad.b) {

                    setPosition(ELEVATOR_SPEED, LOW_POSITION);

                } else if (gamepad.x) {

                    setPosition(ELEVATOR_SPEED, MIDDLE_POSITION);

                } else if (gamepad.y) {
                        setPosition(ELEVATOR_SPEED, HIGH_POSITION);

                } else if (gamepad.dpad_down) {
                    int pos = elevatorDrive.getCurrentPosition()-MANUAL_DROP;
                    if (pos < GROUND_POSITION) pos = GROUND_POSITION;
                    setPosition(ELEVATOR_SPEED, pos);

                } /*else if (gamepad.right_trigger!=0 && gamepad.left_trigger==0) {
                    int pos = elevatorDrive.getCurrentPosition()-MANUAL_DROP;
                    //if (pos < GROUND_POSITION) pos = GROUND_POSITION;
                    setPosition(ELEVATOR_SPEED, pos);
                }
                */
                else if (gamepad.right_trigger !=0) {
                    int pos = elevatorDrive.getCurrentPosition()+100;
                    if (pos > HIGH_POSITION+200) pos = HIGH_POSITION+200;
                    setPosition(ELEVATOR_SPEED, pos);
                }
                else if (gamepad.dpad_up) {
                    int pos = elevatorDrive.getCurrentPosition()+MANUAL_DROP;
                    if(pos>HIGH_POSITION + MANUAL_DROP) pos = HIGH_POSITION + MANUAL_DROP;
                    setPosition(ELEVATOR_SPEED, pos);

                } else if (gamepad.left_trigger !=0) {
                    int pos = elevatorDrive.getCurrentPosition()-100;
                    if (pos < GROUND_POSITION) pos = GROUND_POSITION;
                    setPosition(ELEVATOR_SPEED, pos);
                }
                else if (gamepad.back) {
                    elevatorDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                } else if (gamepad.dpad_left) {
                    int pos = elevatorDrive.getCurrentPosition();
                    if (pos > CONE_5_POS + CONE_WIGGLE){
                        setPosition(ELEVATOR_SPEED, ELEVATOR_HEIGHT.CONE_5);
                    } else if (pos > CONE_4_POS + CONE_WIGGLE){
                        setPosition(ELEVATOR_SPEED, ELEVATOR_HEIGHT.CONE_4);
                    } else if (pos > CONE_3_POS + CONE_WIGGLE){
                        setPosition(ELEVATOR_SPEED, ELEVATOR_HEIGHT.CONE_3);
                    } else if (pos > CONE_2_POS + CONE_WIGGLE){
                        setPosition(ELEVATOR_SPEED, ELEVATOR_HEIGHT.CONE_2);
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

                }
            }
        }
}


