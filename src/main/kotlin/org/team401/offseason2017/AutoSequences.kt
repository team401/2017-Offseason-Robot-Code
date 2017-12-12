package org.team401.offseason2017

import com.ctre.phoenix.Drive.SensoredTank
import com.ctre.phoenix.Drive.Styles
import com.ctre.phoenix.ILoopable
import com.ctre.phoenix.Mechanical.SensoredGearbox
import com.ctre.phoenix.Motion.*
import com.ctre.phoenix.MotorControl.ControlMode
import com.ctre.phoenix.MotorControl.SmartMotorController
import com.ctre.phoenix.Sensors.PigeonImu
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.snakeskin.auto.Auto
import org.snakeskin.component.LightLink
import org.snakeskin.dsl.autoStep

object AutoSequences {
    class LoopableTask(private val task: () -> Unit): ILoopable {
        override fun OnStart() {
            task()
        }

        override fun IsDone() = true
        override fun OnLoop() {}
        override fun OnStop() {}
    }

    class LoopableTurn(val yaw: Double, val tolerance: Double): ILoopable {
        var done = false
        val imuMultiplier = .05
        val feedForward = .3

        var currentYaw = 0.0
        var leftDrive = 0.0
        var rightDrive = 0.0

        override fun OnStart() {
            zero()
            leftGearbox.SetControlMode(ControlMode.SmartControlMode.kPercentVbus)
            rightGearbox.SetControlMode(ControlMode.SmartControlMode.kPercentVbus)
        }

        override fun IsDone() = done

        override fun OnStop() {}

        override fun OnLoop() {
            currentYaw = imu.GetYawPitchRoll()[0]

            //abs(actual - desired) <= tolerance
            // -60 - 60 = -120 <? 4
            // 60 - -60 = 120 <? 4
            if (done || (Math.abs(currentYaw - yaw) <= tolerance) || Math.abs(currentYaw) + Math.abs(tolerance) >= Math.abs(yaw)) {
                leftGearbox.Set(0.0f)
                rightGearbox.Set(0.0f)
                done = true
            } else {
                leftDrive = -(((yaw - currentYaw) * imuMultiplier) + feedForward)
                rightDrive = ((yaw - currentYaw) * imuMultiplier) + feedForward
                leftGearbox.Set(leftDrive.toFloat())
                rightGearbox.Set(rightDrive.toFloat())
            }
        }

    }

    class LoopableDrive(val distance: Double, val tolerance: Double): ILoopable {

        var done = false
        val driveMultiplier = .6
        val imuMultiplier = .01
        val feedForward = .2

        var currentDistance = 0.0
        var currentYaw = 0.0
        var leftDrive = 0.0
        var rightDrive = 0.0

        override fun OnStart() {
            zero()
            leftGearbox.SetControlMode(ControlMode.SmartControlMode.kPercentVbus)
            rightGearbox.SetControlMode(ControlMode.SmartControlMode.kPercentVbus)
        }

        override fun IsDone() = done

        override fun OnStop() {
            leftGearbox.GetMaster().enableBrakeMode(false)
            rightGearbox.GetMaster().enableBrakeMode(false)
        }

        override fun OnLoop() {
            currentDistance = tankDrive.GetDistance().toDouble()
            currentYaw = imu.GetYawPitchRoll()[0]

            println("Left front enc velocity: " + leftGearbox.GetMaster().encVelocity)
            println("Right front enc velocity: " + rightGearbox.GetMaster().encVelocity)

            SmartDashboard.putNumber("Left front enc velocity", leftGearbox.GetMaster().encVelocity.toDouble())
            SmartDashboard.putNumber("Right front enc velocity", rightGearbox.GetMaster().encVelocity.toDouble())

            if (done || (Math.abs(currentDistance - distance) <= tolerance)) {
                leftGearbox.GetMaster().enableBrakeMode(true)
                rightGearbox.GetMaster().enableBrakeMode(true)
                leftGearbox.Set(0.0f)
                rightGearbox.Set(0.0f)
                done = true
            } else {
                leftDrive = ((1 - currentDistance / distance) * driveMultiplier) + feedForward + (currentYaw * imuMultiplier)
                rightDrive = ((1 - currentDistance / distance) * driveMultiplier) + feedForward - (currentYaw * imuMultiplier)
                leftGearbox.Set(leftDrive.toFloat())
                rightGearbox.Set(rightDrive.toFloat())
            }
        }
    }

    private lateinit var tankDrive: SensoredTank
    private lateinit var leftGearbox: SensoredGearbox
    private lateinit var rightGearbox: SensoredGearbox
    private lateinit var imu: PigeonImu

    val straightParam = ServoParameters()
    val distanceParam = ServoParameters()
    val rotationParam = ServoParameters()

    init {
        straightParam.P = 0.01f
        distanceParam.P = 0.5f

        straightParam.allowedError = .1f
        distanceParam.allowedError = .1f
    }

    val ActiveAutoSequence = arrayListOf<ILoopable>()
    private fun add(loopable: ILoopable) = ActiveAutoSequence.add(loopable)

    fun zero() {
        imu.SetYaw(0.0)
        tankDrive.SetPosition(0.0f)
    }

    fun clearSequences() {
        ActiveAutoSequence.clear()
    }

    fun setDrivetrain(drivetrain: SensoredTank) {
        tankDrive = drivetrain
    }

    fun setImu(imu: PigeonImu) {
        this.imu = imu
    }

    fun setGearboxes(left: SensoredGearbox, right: SensoredGearbox) {
        leftGearbox = left
        rightGearbox = right
    }

    fun scoreCenterGear() {

    }

    fun scoreRightGear() {
        add(LoopableDrive((9.0*12) + 5.0, 2.0))
        add(LoopableTask {LightBar.signal(LightLink.Color.VIOLET)})
        add(LoopableTurn(33.7, 2.0))
        add(LoopableTask {LightBar.signal(LightLink.Color.VIOLET)})
        add(LoopableDrive(27.0, 2.0))
        add(LoopableTask {LightBar.signal(LightLink.Color.VIOLET)})
        add(LoopableTask({ Sequences.score() }))
        add(LoopableTask {LightBar.signal(LightLink.Color.VIOLET)})
        add(LoopableTask({ Timer.delay(1.0) }))
        add(LoopableTask {LightBar.signal(LightLink.Color.VIOLET)})
        add(LoopableDrive(-27.0, 2.0))
        add(LoopableTask {LightBar.signal(LightLink.Color.VIOLET)})

    }

    fun scoreLeftGear() {
        /*
        add(LoopableDrive((9.0*12) + 5.0, 8.0)) //TO TURN
        add(LoopableTask {LightBar.signal(LightLink.Color.VIOLET)})
        add(LoopableTurn(-33.7, 6.0)) //TURN
        add(LoopableTask {LightBar.signal(LightLink.Color.VIOLET)})
        add(LoopableDrive(27.0, 4.0)) //TO AIRSHIP
        add(LoopableTask {LightBar.signal(LightLink.Color.VIOLET)})
        add(LoopableTask({ Sequences.score() })) //DELIVER
        add(LoopableTask {LightBar.signal(LightLink.Color.VIOLET)})
        add(LoopableTask({ Timer.delay(1.0) })) //WAIT
        add(LoopableTask {LightBar.signal(LightLink.Color.VIOLET)})
        add(LoopableDrive(-27.0, 8.0)) //REVERSE
        add(LoopableTask {LightBar.signal(LightLink.Color.VIOLET)})
        */

        add(LoopableDrive(12.0*12, 2.0))
    }

    val servoGo = autoStep {
        entry{
            val driveParams = ServoParameters()
            val turnParams = ServoParameters()
            //heading, distance
            ServoStraightDistanceWithImu(imu, tankDrive ,Styles.Smart.PercentOutput, driveParams, turnParams, 0f, 10f)
        }

    }

    //val AutoTest = Auto("Test of framework", LoopableDrive((5.0 *12), 10.0))
    //val ServoTest = Auto("CTRE Servo Test", servoGo)

}

