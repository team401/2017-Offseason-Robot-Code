package org.team401.offseason2017

import org.team401.offseason2017.subsystems.*
import org.snakeskin.component.LightLink
import org.snakeskin.dsl.machine

object Sequences {
    val arm = GearHolder.machine(GEAR_ARM_MACHINE)
    val intake = GearHolder.machine(GEAR_INTAKE_MACHINE)
    val climber = Climber.machine(CLIMBER_MACHINE)

    fun getGear() {
        arm.setState(GearArmStates.DOWN)
        intake.setState(GearIntakeStates.COLLECT)
        LightBar.signal(LightLink.Color.BLUE)
    }

    fun stowArm() {
        if (GEAR_PRESENT) {
            intake.setState(GearIntakeStates.RETAIN)
        } else {
            intake.setState(GearIntakeStates.OFF)
        }
        arm.setState(GearArmStates.STOW)
        println("Arm Stowed")
    }

    fun prepareScore() {
        arm.setState(GearArmStates.UP)
    }

    fun score() {
        arm.setState(GearArmStates.DOWN_SCORE)
        intake.setState(GearIntakeStates.EJECT)
    }

    fun startClimb() {
        climber.setState(ClimberStates.CLIMB)
        LightBar.breathe(LightLink.Color.ORANGE)
    }

    fun stopClimb() {
        climber.setState(ClimberStates.OFF)
    }

    fun manualClimb() {
        climber.setState(ClimberStates.MANUAL_CLIMB)
    }
}