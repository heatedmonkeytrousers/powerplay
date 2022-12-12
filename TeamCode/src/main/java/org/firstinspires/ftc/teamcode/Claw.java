package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

public class Claw extends  Thread {
    static final double INCREMENT   = 0.001;
    static public double START_MAX_POS = 0.26;
    static final double MAX_POS     =  0.24;
    static final double MIN_POS     =  0.1;
    private Servo clawServo;
    private Gamepad gamepad;


    public Claw(Servo clawServo, Gamepad gamepad) {
        this.clawServo = clawServo;
        this.gamepad = gamepad;
    }

    public void closeClaw () {
        clawServo.setPosition(MIN_POS);
    }

    public void openClaw () {
        clawServo.setPosition(MAX_POS);
    }

    @Override
    public void run() {
        while(!isInterrupted()) {
            double position = clawServo.getPosition();

            if (gamepad.left_bumper) {
                openClaw();
            }
            else if(gamepad.right_bumper) {
                closeClaw();
            }
        }
    }
}
