package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

public class Claw extends  Thread {
    static final double INCREMENT   = 0.001;
    static final double MAX_POS     =  0.25;
    static final double MIN_POS     =  0.1;
    private Servo clawServo;
    private Gamepad gamepad;


    public Claw(Servo clawServo, Gamepad gamepad) {
        this.clawServo = clawServo;
        this.gamepad = gamepad;
    }
    @Override
    public void run() {
        while(!isInterrupted()) {
            double position = clawServo.getPosition();
            if (gamepad.dpad_left) {
                position += INCREMENT ;
            } else if (gamepad.dpad_right) {
                position -= INCREMENT ;
            }
            if (gamepad.left_bumper) {
                position = MAX_POS;
            }
            if(gamepad.right_bumper) {
                position = MIN_POS;
            }
            if (position <= MIN_POS ) {
                position = MIN_POS;
            }
            if (position >= MAX_POS ) {
                position = MAX_POS;
            }
            clawServo.setPosition(position);
        }
    }
}
