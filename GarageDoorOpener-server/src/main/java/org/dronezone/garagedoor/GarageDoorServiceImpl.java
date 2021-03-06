package org.dronezone.garagedoor;

import com.pi4j.io.gpio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Default implementation of the interface.
 *
 * @author Nicholas Drone
 * @since 1.0
 */
public class GarageDoorServiceImpl implements GarageDoorService
{
    private static final Logger log = LoggerFactory.getLogger(GarageDoorServiceImpl.class);

    private final String               keyCode;
    private final GpioPinDigitalOutput garageDoorPin;

    /**
     * Setting up Service of the challenging {@code keyCode} and how the garage door is connected.
     *
     * @param garageDoorProperties
     * @param gpioController
     */
    public GarageDoorServiceImpl(GarageDoorProperties garageDoorProperties,
            GpioController gpioController)
    {
        Assert.notNull(garageDoorProperties, "GarageDoorProperties can't be null");
        this.keyCode = garageDoorProperties.getKeycodeSecret();

        log.debug("Getting pin to be used for door operation: {}",
                garageDoorProperties.getActionPin());
        Pin pin = RaspiPin.getPinByAddress(garageDoorProperties.getActionPin());
        PinState pinState = garageDoorProperties.getActionPinState();

        Assert.notNull(gpioController, "GpioController can't be null");
        log.debug("provision GPIO pin: {} with state: {}", pin.getAddress(), pinState);
        garageDoorPin = gpioController.provisionDigitalOutputPin(pin, pinState);
        garageDoorPin.setShutdownOptions(true, pinState, PinPullResistance.OFF);
    }

    public void doorOperation(String keyCode) throws Exception
    {
        log.info("interaction with garage door");
        if (this.keyCode.equals(keyCode))
        {
            log.debug("key code matches");
            //synchronized so more than one request isn't being fired at the same time.
            synchronized (this)
            {
                log.debug("pin: {} state: {} toggling...", garageDoorPin.getPin().getAddress(),
                        garageDoorPin.getState().getValue());
                garageDoorPin.toggle();
                log.debug("pin: {} state: {} after toggling", garageDoorPin.getPin().getAddress(),
                        garageDoorPin.getState().getValue());
                Thread.sleep(1000);
                log.debug("pin: {} state: {} toggling...", garageDoorPin.getPin().getAddress(),
                        garageDoorPin.getState().getValue());
                garageDoorPin.toggle();
                log.debug("pin: {} state: {}", garageDoorPin.getPin().getAddress(),
                        garageDoorPin.getState().getValue());
            }
        }
        else
        {
            throw new Exception("Invalid key code");
        }
    }
}
