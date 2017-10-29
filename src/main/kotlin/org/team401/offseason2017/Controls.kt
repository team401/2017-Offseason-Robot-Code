package org.team401.offseason2017

import org.team401.offseason2017.subsystems.*
import org.team401.snakeskin.dsl.HumanControls
import org.team401.snakeskin.dsl.machine
import org.team401.snakeskin.dsl.send
import org.team401.snakeskin.logic.Direction

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

val Wheel = HumanControls.drivingForceGT(0)

val DriveStick = HumanControls.attack3(1) {
    whenButton(Buttons.TRIGGER) {
        pressed {
            Drivetrain.machine(SHIFTER_MACHINE).setState(ShifterStates.LOW)
        }
        released {
            Drivetrain.machine(SHIFTER_MACHINE).setState(ShifterStates.AUTO)
        }
    }
}

val MashStick = HumanControls.extreme3d(2) {
    /*
    whenHatChanged(Hats.STICK_HAT) {
        when (it) {
            Direction.NORTH -> Climber.machine(CLIMBER_MACHINE).setState(ClimberStates.CLIMB)
            Direction.SOUTH -> Climber.machine(CLIMBER_MACHINE).setState(ClimberStates.OFF)
        }
    }

    whenButton(Buttons.BASE_BOTTOM_RIGHT) {
        pressed {
            Climber.machine(CLIMBER_MACHINE).setState(ClimberStates.MANUAL_CLIMB)
        }
        released {
            Climber.machine(CLIMBER_MACHINE).setState(ClimberStates.OFF)
        }
    }
    */

    whenHatChanged(Hats.STICK_HAT) {
        val arm = GearHolder.machine(GEAR_ARM_MACHINE)
        when (it) {
            Direction.WEST -> arm.setState(GearArmStates.STOW)
            Direction.SOUTH -> arm.setState(GearArmStates.DOWN)
            Direction.EAST -> arm.setState(GearArmStates.DOWN_SCORE)
            Direction.NORTH -> arm.setState(GearArmStates.UP)
        }
    }
}