package org.team401.offseason2017

import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj.DriverStation
import org.snakeskin.component.LightLink
import org.snakeskin.dsl.Sensors

val LightBar = LightLink()

val GearSensor = DigitalInput(0)

val Last30Sensor = Sensors.booleanSensor({
    DriverStation.getInstance().matchTime <= 30
            && !DriverStation.getInstance().isAutonomous
            && DriverStation.getInstance().isOperatorControl
            && DriverStation.getInstance().isFMSAttached
}, {
    whenTriggered {
        LightBar.bounce(LightLink.Color.YELLOW)
    }
    pollAt(1000L)
})