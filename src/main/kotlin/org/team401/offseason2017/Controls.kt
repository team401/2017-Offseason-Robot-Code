package org.team401.offseason2017

import org.team401.offseason2017.subsystems.*
import org.snakeskin.dsl.HumanControls
import org.snakeskin.dsl.machine
import org.snakeskin.dsl.send
import org.snakeskin.logic.Direction
import org.snakeskin.logic.scalers.SquareScaler

/*
 * 2017-Offseason-Robot-Code - Created on 9/26/17
 * Author: Cameron Earle
 * 
 * This code is licensed under the GNU GPL v3
 * You can find more info in the LICENSE file at project root
 */

/**
 * @author Cameron Earle
 * @version 9/26/17
 */

val Wheel = HumanControls.drivingForceGT(0) {
    scaleAxis(Axes.WHEEL, SquareScaler)
}

val DriveStick = HumanControls.attack3(1) {
    scaleAxis(Axes.PITCH, SquareScaler)
    invertAxis(Axes.PITCH)

    whenButton(Buttons.TRIGGER) {
        pressed {
            Drivetrain.machine(SHIFTER_MACHINE).setState(ShifterStates.LOW)
        }
        released {
            Drivetrain.machine(SHIFTER_MACHINE).setState(ShifterStates.HIGH)
        }
    }
}


val Gamepad = HumanControls.f310(2) {
    invertAxis(Axes.RIGHT_X)

    whenButton(Buttons.A) {
        pressed {
            Sequences.getGear()
        }
        released {
            Sequences.stowArm()
        }
    }

    whenButton(Buttons.B) {
        pressed {
            Sequences.score()
        }
    }

    whenButton(Buttons.X) {
        pressed {
            Sequences.stowArm()
        }
    }

    whenButton(Buttons.Y) {
        pressed {
            Sequences.prepareScore()
        }
    }


    whenButton(Buttons.RIGHT_STICK) {
        pressed {
            Sequences.startClimb()
        }
    }

    whenButton(Buttons.LEFT_STICK) {
        pressed {
            Sequences.stopClimb()
        }
    }

    whenButton(Buttons.BACK) {
        pressed {
            Sequences.manualClimb()
        }
        released {
            Sequences.stopClimb()
        }
    }

}