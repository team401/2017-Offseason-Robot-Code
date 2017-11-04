package org.team401.offseason2017

import com.ctre.phoenix.Drive.SensoredTank
import com.ctre.phoenix.Drive.Styles
import com.ctre.phoenix.ILoopable
import com.ctre.phoenix.Motion.*
import com.ctre.phoenix.Sensors.PigeonImu

object AutoSequences {
    class LoopableTask(private val task: () -> Unit): ILoopable {
        override fun OnStart() {
            task()
        }

        override fun IsDone() = true
        override fun OnLoop() {}
        override fun OnStop() {}
    }

    private lateinit var tankDrive: SensoredTank
    private lateinit var imu: PigeonImu

    val straightParam = ServoParameters()
    val distanceParam = ServoParameters()
    val rotationParam = ServoParameters()

    init {

    }

    val ActiveAutoSequence = arrayListOf<ILoopable>()
    private fun add(loopable: ILoopable) = ActiveAutoSequence.add(loopable)

    fun zero() {
        imu.SetYaw(0.0)
        tankDrive.SetPosition(0.0f)
        ActiveAutoSequence.clear()
    }

    fun setDrivetrain(drivetrain: SensoredTank) {
        tankDrive = drivetrain
    }

    fun setImu(imu: PigeonImu) {
        this.imu = imu
    }

    fun scoreCenterGear() {
        //DRIVE TO AIRSHIP
        add(ServoStraightDistanceWithImu(imu, tankDrive, Styles.Smart.PercentOutput, straightParam, distanceParam,
                0.0f, 12.0f))
        //SCORE GEAR
        add(LoopableTask { Sequences.score() })
    }

    fun scoreRightGear() {

    }

    fun scoreLeftGear() {

    }

}

