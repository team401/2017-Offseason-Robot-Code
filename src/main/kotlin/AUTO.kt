import com.ctre.phoenix.Drive.SensoredTank
import com.ctre.phoenix.Drive.Styles
import com.ctre.phoenix.Mechanical.SensoredGearbox
import com.ctre.phoenix.Motion.ServoGoStraightWithImu
import com.ctre.phoenix.Motion.ServoParameters
import com.ctre.phoenix.Motion.ServoZeroTurnWithImu
import com.ctre.phoenix.MotorControl.CAN.TalonSRX
import com.ctre.phoenix.MotorControl.SmartMotorController
import com.ctre.phoenix.Schedulers.SequentialScheduler
import com.ctre.phoenix.Sensors.PigeonImu
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import org.team401.offseason2017.Constants

/**
 * @author Eli Jelesko
 * @version 11/1/17
 */
enum class Choices {
    CENTER_TO_AIRSHIP, RIGHT_TO_AIRSHIP, LEFT_TO_AIRSHIP
}
//sets up drivetrain
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

val tank = SensoredTank(left, right, false, true, Constants.DrivetrainParameters.WHEEL_RADIUS, Constants.DrivetrainParameters.WHEEL_DIST)

val imu = PigeonImu(leftFront)//Perhaps not really where the Pigeon is

val schedule = SequentialScheduler(20)

val drive = ServoParameters()


//sendable chooser for selecting auto routine
val sendable = SendableChooser<Enum<Choices>>()

fun auto(){
    //adds options to the sendable chooser
    sendable.addObject("Center to Airship", Choices.CENTER_TO_AIRSHIP)//do I need?
    sendable.addObject("Right to Airship", Choices.RIGHT_TO_AIRSHIP)
    sendable.addObject("Left to Airship", Choices.LEFT_TO_AIRSHIP)
    sendable.addDefault("Center to Airship", Choices.CENTER_TO_AIRSHIP)

    //gets input from the chooser. Selects routine
    when(sendable.selected){
        Choices.CENTER_TO_AIRSHIP -> centerToAirship()
        Choices.LEFT_TO_AIRSHIP -> leftToAirship()
        Choices.RIGHT_TO_AIRSHIP -> rightToAirship()
        else -> centerToAirship()
    }

    tank.SetPosition(0f)
    imu.SetYaw(0.0)

    //activate auto
    schedule.Start()
}
fun centerToAirship(){
    //need more accurate values
    val centerToAirship = ServoGoStraightWithImu(imu, tank, Styles.Basic.PercentOutput, drive, 10f, 0f, 50f)

    schedule.Add(centerToAirship)
}
fun leftToAirship() {
    val drive = ServoParameters()
    val driveTurn = ServoParameters()

    //insert PID to drive and driveTurn here

    val driveForward = ServoGoStraightWithImu(imu, tank, Styles.Basic.PercentOutput, drive, 10f, 0f, 50f)

    val turnToAirship = ServoZeroTurnWithImu(imu, tank, Styles.Basic.PercentOutput, -60f, driveTurn)

    //heading is not understood Test
    val driveToAirship = ServoGoStraightWithImu(imu, tank, Styles.Basic.PercentOutput, drive, 3f, -60f, 25f)

    schedule.Add(driveForward)
    schedule.Add(turnToAirship)
    schedule.Add(driveToAirship)
}
fun rightToAirship(){
    val drive = ServoParameters()
    val driveTurn = ServoParameters()

    val driveForward = ServoGoStraightWithImu(imu, tank, Styles.Basic.PercentOutput, drive, 10f, 0f, 50f)

    val turnToAirship = ServoZeroTurnWithImu(imu, tank, Styles.Basic.PercentOutput, 60f, driveTurn)

    val driveToAirship = ServoGoStraightWithImu(imu, tank, Styles.Basic.PercentOutput, drive, 3f, 60f, 25f)

    schedule.Add(driveForward)
    schedule.Add(turnToAirship)
    schedule.Add(driveToAirship)
}