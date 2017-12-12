package org.team401.offseason2017.subsystems

import com.ctre.phoenix.Drive.SensoredTank
import com.ctre.phoenix.Drive.Styles
import com.ctre.phoenix.Mechanical.SensoredGearbox
import com.ctre.phoenix.MotorControl.CAN.TalonSRX
import com.ctre.phoenix.MotorControl.SmartMotorController
import com.ctre.phoenix.Schedulers.SequentialScheduler
import com.ctre.phoenix.Sensors.PigeonImu
import edu.wpi.first.wpilibj.CameraServer
import edu.wpi.first.wpilibj.Solenoid
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
//import org.team401.offseason2017.DriveStick
//import org.team401.offseason2017.Wheel
import org.snakeskin.dsl.buildSubsystem
import org.snakeskin.event.Events
import org.snakeskin.subsystem.Subsystem
import org.team401.offseason2017.*

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

object ShifterStates {
    const val HIGH = "high"
    const val LOW = "low"
    //const val AUTO = "auto"
}
const val SHIFTER_MACHINE = "shifting"

object DrivetrainStates {
    const val OPEN_LOOP = "open_loop"
    const val AUTO_SEQUENCE = "auto"
    const val TEST = "test"
}
const val DRIVETRAIN_MACHINE = "drive"

val Drivetrain: Subsystem = buildSubsystem {

    val leftFront = TalonSRX(Constants.MotorControllers.DRIVE_LEFT_FRONT_CAN)
    val leftMidF = TalonSRX(Constants.MotorControllers.DRIVE_LEFT_MIDF_CAN)
    val leftMidR = TalonSRX(Constants.MotorControllers.DRIVE_LEFT_MIDR_CAN)
    val leftRear = TalonSRX(Constants.MotorControllers.DRIVE_LEFT_REAR_CAN)

    val rightFront = TalonSRX(Constants.MotorControllers.DRIVE_RIGHT_FRONT_CAN)
    val rightMidF = TalonSRX(Constants.MotorControllers.DRIVE_RIGHT_MIDF_CAN)
    val rightMidR = TalonSRX(Constants.MotorControllers.DRIVE_RIGHT_MIDR_CAN)
    val rightRear = TalonSRX(Constants.MotorControllers.DRIVE_RIGHT_REAR_CAN)

    val left = SensoredGearbox(4096f, leftFront, leftMidF, leftMidR, leftRear, SmartMotorController.FeedbackDevice.CtreMagEncoder_Relative)
    val right = SensoredGearbox(4096f, rightFront, rightMidF, rightMidR, rightRear, SmartMotorController.FeedbackDevice.CtreMagEncoder_Relative)
    val drive = SensoredTank(left, right, true, false, Constants.DrivetrainParameters.WHEEL_RADIUS, Constants.DrivetrainParameters.WHEEL_DIST)
    val imu = PigeonImu(leftRear)

    val shifter = Solenoid(Constants.Pneumatics.SHIFTER_SOLENOID)

    val scheduler = SequentialScheduler(10)

    setup {
        AutoSequences.setGearboxes(left, right)
        AutoSequences.setDrivetrain(drive)
        AutoSequences.setImu(imu)
        leftFront.enableBrakeMode(false)
        rightFront.enableBrakeMode(false)
        leftFront.reverseSensor(true)
        rightFront.reverseSensor(false)
        leftFront.setCurrentLimit(Constants.DrivetrainParameters.CURRENT_LIMIT)
        rightFront.setCurrentLimit(Constants.DrivetrainParameters.CURRENT_LIMIT)
        leftFront.setVoltageRampRate(Constants.DrivetrainParameters.RAMP_RATE)
        rightFront.setVoltageRampRate(Constants.DrivetrainParameters.RAMP_RATE)
    }

    val shiftMachine = stateMachine(SHIFTER_MACHINE) {
        state (ShifterStates.HIGH) {
            entry {
                shifter.set(true)
            }
        }

        state (ShifterStates.LOW) {
            entry {
                shifter.set(false)
            }
        }

        default {
            entry {
                shifter.set(false)
            }
        }
    }

    val driveMachine = stateMachine(DRIVETRAIN_MACHINE) {
        state (DrivetrainStates.OPEN_LOOP) {
            action {
                drive.set(Styles.Basic.PercentOutput, DriveStick.readAxis { PITCH }.toFloat(), Wheel.readAxis { WHEEL }.toFloat())
                SmartDashboard.putNumber("Left enc velocity", left.GetMaster().encVelocity.toDouble())
                SmartDashboard.putNumber("Right enc velocity", right.GetMaster().encVelocity.toDouble())
            }
        }

        state (DrivetrainStates.AUTO_SEQUENCE) {
            entry {
                scheduler.Stop()
                scheduler.RemoveAll()
                for (loopable in AutoSequences.ActiveAutoSequence) {
                    scheduler.Add(loopable)
                }
                scheduler.Start()
            }
            action(10) {
                scheduler.Process()
            }
            exit {
                scheduler.Stop()
            }
        }

    }

    fun deconstruct(){
        leftFront.changeControlMode(SmartMotorController.TalonControlMode.PercentVbus)
        leftMidF.changeControlMode(SmartMotorController.TalonControlMode.PercentVbus)
        leftMidR.changeControlMode(SmartMotorController.TalonControlMode.PercentVbus)
        leftRear.changeControlMode(SmartMotorController.TalonControlMode.PercentVbus)

        rightFront.changeControlMode(SmartMotorController.TalonControlMode.PercentVbus)
        rightMidF.changeControlMode(SmartMotorController.TalonControlMode.PercentVbus)
        rightMidR.changeControlMode(SmartMotorController.TalonControlMode.PercentVbus)
        rightRear.changeControlMode(SmartMotorController.TalonControlMode.PercentVbus)
    }
    fun reconstruct(){
        leftMidF.follow(leftFront)
        leftMidR.follow(leftFront)
        leftRear.follow(leftFront)

        rightMidF.follow(rightFront)
        rightMidR.follow(rightFront)
        rightRear.follow(rightFront)
    }

    test("Drivetrain test"){
        deconstruct()//sets all motors to VBUS

        leftFront.set(1.0)
        Thread.sleep(4000)
        val leftFrontVolt = leftFront.outputVoltage
        val leftFrontCurrent = leftFront.outputCurrent
        println("leftFront Voltage : " + leftFrontVolt)
        println("leftFront Current : " + leftFrontCurrent)
        SmartDashboard.putNumber("leftFront Voltage", leftFrontVolt)
        SmartDashboard.putNumber("leftFront Current", leftFrontCurrent)
        leftFront.set(0.0)

        Thread.sleep(1000)

        leftMidF.set(1.0)
        Thread.sleep(4000)
        val leftMidFVolt = leftMidF.outputVoltage
        val leftMidFCurrent = leftMidF.outputCurrent
        println("leftMidF Voltage : " + leftMidFVolt)
        println("leftMidF Current : " + leftMidFCurrent)
        SmartDashboard.putNumber("leftMidF Voltage", leftMidFVolt)
        SmartDashboard.putNumber("leftMidF Current", leftMidFCurrent)
        leftMidF.set(0.0)

        Thread.sleep(1000)

        leftMidR.set(1.0)
        Thread.sleep(4000)
        val leftMidRVolt = leftMidR.outputVoltage
        val leftMidRCurrent = leftMidR.outputCurrent
        println("leftMidR Voltage : " + leftMidRVolt)
        println("leftMidR Current : " + leftMidRCurrent)
        SmartDashboard.putNumber("leftMidR Voltage",leftMidRVolt)
        SmartDashboard.putNumber("leftMidR Current",leftMidRCurrent)
        leftMidR.set(0.0)

        Thread.sleep(1000)

        leftRear.set(1.0)
        Thread.sleep(4000)
        val leftRearVolt = leftRear.outputVoltage
        val leftRearCurrent = leftRear.outputCurrent
        println("leftRear Voltage : " + leftRearVolt)
        println("leftRear Current : " + leftRearCurrent)
        SmartDashboard.putNumber("leftRear Voltage",leftRearVolt)
        SmartDashboard.putNumber("leftRear Current",leftRearCurrent)
        leftRear.set(0.0)

        Thread.sleep(1000)

        rightFront.set(1.0)
        Thread.sleep(4000)
        val rightFrontVolt = rightFront.outputVoltage
        val rightFrontCurrent = rightFront.outputCurrent
        println("rightFront Voltage : " + rightFrontVolt)
        println("rightFront Current : " + rightFrontCurrent)
        SmartDashboard.putNumber("rightFront Voltage",rightFrontVolt)
        SmartDashboard.putNumber("rightFront Current",rightFrontCurrent)
        rightFront.set(0.0)

        Thread.sleep(1000)

        rightMidF.set(1.0)
        Thread.sleep(4000)
        val rightMidFVolt = rightMidF.outputVoltage
        val rightMidFCurrent = rightMidF.outputCurrent
        println("rightMidF Voltage : " + rightMidFVolt)
        println("rightMidF Current : " + rightMidFCurrent)
        SmartDashboard.putNumber("rightMidF Voltage",rightMidFVolt)
        SmartDashboard.putNumber("rightMidF Current",rightMidFCurrent)
        rightMidF.set(0.0)

        Thread.sleep(1000)

        rightMidR.set(1.0)
        Thread.sleep(4000)
        val rightMidRVolt = rightMidR.outputVoltage
        val rightMidRCurrent = rightMidR.outputCurrent
        println("rightMidR Voltage : " + rightMidRVolt)
        println("rightMidR Current : " + rightMidRCurrent)
        SmartDashboard.putNumber("rightMidR Voltage", rightMidRVolt)
        SmartDashboard.putNumber("rightMidR Current", rightMidRCurrent)
        rightMidR.set(0.0)

        Thread.sleep(1000)

        rightRear.set(1.0)
        Thread.sleep(4000)
        val rightRearVolt = rightRear.outputVoltage
        val rightRearCurrent = rightRear.outputCurrent
        println("rightRear Voltage : " + rightRearVolt)
        println("rightRear Current : " + rightRearCurrent)
        SmartDashboard.putNumber("rightRear Voltage",rightRearVolt)
        SmartDashboard.putNumber("rightRear Current",rightRearCurrent)
        rightRear.set(0.0)

        Thread.sleep(1000)

        //check for discrepencies

        val TOLERANCE = 5.0

        if(leftFrontVolt - leftMidFVolt - leftMidRVolt - leftRearVolt - rightFrontVolt - rightMidFVolt
                - rightMidRVolt - rightRearVolt <= (TOLERANCE*-8)) {
            true
        }
        if(leftFrontCurrent - leftMidFCurrent - leftMidRCurrent - leftRearCurrent - rightFrontCurrent
                - rightMidFCurrent - rightMidRCurrent - rightRearCurrent <= (TOLERANCE*-8)){
            true
        }
        false


        val TOLERENCE = 5.0

        reconstruct()//reassigns to follow masters

        driveMachine.setState("default")

        false
    }


    on (Events.AUTO_ENABLED) {
        shiftMachine.setState(ShifterStates.LOW)

    }

    on (Events.TELEOP_ENABLED) {
        shiftMachine.setState(ShifterStates.HIGH)
        driveMachine.setState(DrivetrainStates.OPEN_LOOP)
    }


}