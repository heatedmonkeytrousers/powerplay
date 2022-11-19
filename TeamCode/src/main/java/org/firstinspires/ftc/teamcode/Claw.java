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
            //org.firstinspires.ftc.teamcode.Claw Code
            if (gamepad.dpad_left) {
                double position = clawServo.getPosition();
                position += INCREMENT ;
                clawServo.setPosition(position);
                if (position <= MIN_POS ) {
                    position = MIN_POS;
                }
                if (position >= MAX_POS ) {
                    position = MAX_POS;
                }
            } else if (gamepad.dpad_right) {
                double position = clawServo.getPosition();
                position -= INCREMENT ;
                clawServo.setPosition(position);
                if (position <= MIN_POS ) {
                    position = MIN_POS;
                }
                if (position >= MAX_POS ) {
                    position = MAX_POS;
                }
            }
        }
    }
}
