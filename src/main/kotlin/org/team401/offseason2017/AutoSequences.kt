package org.team401.offseason2017

import com.ctre.phoenix.Drive.SensoredTank
import com.ctre.phoenix.Drive.Styles
import com.ctre.phoenix.ILoopable
import com.ctre.phoenix.Motion.*
import com.ctre.phoenix.Sensors.PigeonImu

object AutoSequences {
    private lateinit var tankDrive: SensoredTank
    private lateinit var imu: PigeonImu

    fun setDrivetrain(drivetrain: SensoredTank) {
        tankDrive = drivetrain
    }

    fun setImu(imu: PigeonImu) {
        this.imu = imu
    }

    val ActiveAutoSequence = arrayListOf<ILoopable>()

    fun firstMoveForward(){
        val driveParams = ServoParameters()//pigeon
        val distParams = ServoParameters()//for distance (encoders?)

        val moveForward = ServoStraightDistanceWithImu(imu, tankDrive, Styles.Smart.PercentOutput, driveParams, distParams, 0f,10f)

        ActiveAutoSequence.add(moveForward)
    }
    fun turn(heading: Float){
        //turning
        val params = ServoParameters()

        val turn = ServoZeroTurnWithImu(imu, tankDrive, Styles.Basic.PercentOutput, heading, params)

        ActiveAutoSequence.add(turn)
    }
    fun secondMove(heading: Float){
        val driveParams = ServoParameters()//pigeon
        val distParams = ServoParameters()//for distance (encoders?)

        val moveForward = ServoStraightDistanceWithImu(imu, tankDrive, Styles.Smart.PercentOutput, driveParams, distParams, heading,3f)

        ActiveAutoSequence.add(moveForward)
    }
}

