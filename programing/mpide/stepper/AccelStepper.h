 // AccelStepper.h
    2 //
    3 /// \mainpage AccelStepper library for Arduino
    4 ///
    5 /// This is the Arduino AccelStepper library.
    6 /// It provides an object-oriented interface for 2, 3 or 4 pin stepper motors.
    7 ///
    8 /// The standard Arduino IDE includes the Stepper library
    9 /// (http://arduino.cc/en/Reference/Stepper) for stepper motors. It is
   10 /// perfectly adequate for simple, single motor applications.
   11 ///
   12 /// AccelStepper significantly improves on the standard Arduino Stepper library in several ways:
   13 /// \li Supports acceleration and deceleration
   14 /// \li Supports multiple simultaneous steppers, with independent concurrent stepping on each stepper
   15 /// \li API functions never delay() or block
   16 /// \li Supports 2, 3 and 4 wire steppers, plus 3 and 4 wire half steppers.
   17 /// \li Supports alternate stepping functions to enable support of AFMotor (https://github.com/adafruit/Adafruit-Motor-Shield-library)
   18 /// \li Supports stepper drivers such as the Sparkfun EasyDriver (based on 3967 driver chip)
   19 /// \li Very slow speeds are supported
   20 /// \li Extensive API
   21 /// \li Subclass support
   22 ///
   23 /// The latest version of this documentation can be downloaded from 
   24 /// http://www.airspayce.com/mikem/arduino/AccelStepper
   25 /// The version of the package that this documentation refers to can be downloaded 
   26 /// from http://www.airspayce.com/mikem/arduino/AccelStepper/AccelStepper-1.37.zip
   27 ///
   28 /// Example Arduino programs are included to show the main modes of use.
   29 ///
   30 /// You can also find online help and discussion at http://groups.google.com/group/accelstepper
   31 /// Please use that group for all questions and discussions on this topic. 
   32 /// Do not contact the author directly, unless it is to discuss commercial licensing.
   33 ///
   34 /// Tested on Arduino Diecimila and Mega with arduino-0018 & arduino-0021 
   35 /// on OpenSuSE 11.1 and avr-libc-1.6.1-1.15,
   36 /// cross-avr-binutils-2.19-9.1, cross-avr-gcc-4.1.3_20080612-26.5.
   37 ///
   38 /// \par Installation
   39 /// Install in the usual way: unzip the distribution zip file to the libraries
   40 /// sub-folder of your sketchbook. 
   41 ///
   42 /// This software is Copyright (C) 2010 Mike McCauley. Use is subject to license
   43 /// conditions. The main licensing options available are GPL V2 or Commercial:
   44 ///
   45 /// \par Theory
   46 /// This code uses speed calculations as described in 
   47 /// "Generate stepper-motor speed profiles in real time" by David Austin 
   48 /// http://fab.cba.mit.edu/classes/MIT/961.09/projects/i0/Stepper_Motor_Speed_Profile.pdf
   49 /// with the exception that AccelStepper uses steps per second rather than radians per second
   50 /// (because we dont know the step angle of the motor)
   51 /// An initial step interval is calculated for the first step, based on the desired acceleration
   52 /// Subsequent shorter step intervals are calculated based 
   53 /// on the previous step until max speed is acheived.
   54 /// 
   55 /// \par Open Source Licensing GPL V2
   56 /// This is the appropriate option if you want to share the source code of your
   57 /// application with everyone you distribute it to, and you also want to give them
   58 /// the right to share who uses it. If you wish to use this software under Open
   59 /// Source Licensing, you must contribute all your source code to the open source
   60 /// community in accordance with the GPL Version 2 when your application is
   61 /// distributed. See http://www.gnu.org/copyleft/gpl.html
   62 /// 
   63 /// \par Commercial Licensing
   64 /// This is the appropriate option if you are creating proprietary applications
   65 /// and you are not prepared to distribute and share the source code of your
   66 /// application. Contact info@airspayce.com for details.
   67 ///
   68 /// \par Revision History
   69 /// \version 1.0 Initial release
   70 ///
   71 /// \version 1.1 Added speed() function to get the current speed.
   72 /// \version 1.2 Added runSpeedToPosition() submitted by Gunnar Arndt.
   73 /// \version 1.3 Added support for stepper drivers (ie with Step and Direction inputs) with _pins == 1
   74 /// \version 1.4 Added functional contructor to support AFMotor, contributed by Limor, with example sketches.
   75 /// \version 1.5 Improvements contributed by Peter Mousley: Use of microsecond steps and other speed improvements
   76 ///              to increase max stepping speed to about 4kHz. New option for user to set the min allowed pulse width.
   77 ///              Added checks for already running at max speed and skip further calcs if so. 
   78 /// \version 1.6 Fixed a problem with wrapping of microsecond stepping that could cause stepping to hang. 
   79 ///              Reported by Sandy Noble.
   80 ///              Removed redundant _lastRunTime member.
   81 /// \version 1.7 Fixed a bug where setCurrentPosition() did not always work as expected. 
   82 ///              Reported by Peter Linhart.
   83 /// \version 1.8 Added support for 4 pin half-steppers, requested by Harvey Moon
   84 /// \version 1.9 setCurrentPosition() now also sets motor speed to 0.
   85 /// \version 1.10 Builds on Arduino 1.0
   86 /// \version 1.11 Improvments from Michael Ellison:
   87 ///   Added optional enable line support for stepper drivers
   88 ///   Added inversion for step/direction/enable lines for stepper drivers
   89 /// \version 1.12 Announce Google Group
   90 /// \version 1.13 Improvements to speed calculation. Cost of calculation is now less in the worst case, 
   91 ///    and more or less constant in all cases. This should result in slightly beter high speed performance, and
   92 ///    reduce anomalous speed glitches when other steppers are accelerating. 
   93 ///    However, its hard to see how to replace the sqrt() required at the very first step from 0 speed.
   94 /// \version 1.14 Fixed a problem with compiling under arduino 0021 reported by EmbeddedMan
   95 /// \version 1.15 Fixed a problem with runSpeedToPosition which did not correctly handle
   96 ///    running backwards to a smaller target position. Added examples
   97 /// \version 1.16 Fixed some cases in the code where abs() was used instead of fabs().
   98 /// \version 1.17 Added example ProportionalControl
   99 /// \version 1.18 Fixed a problem: If one calls the funcion runSpeed() when Speed is zero, it makes steps 
  100 ///    without counting. reported by  Friedrich, Klappenbach.
  101 /// \version 1.19 Added MotorInterfaceType and symbolic names for the number of pins to use
  102 ///               for the motor interface. Updated examples to suit.
  103 ///               Replaced individual pin assignment variables _pin1, _pin2 etc with array _pin[4].
  104 ///               _pins member changed to _interface.
  105 ///               Added _pinInverted array to simplify pin inversion operations.
  106 ///               Added new function setOutputPins() which sets the motor output pins.
  107 ///               It can be overridden in order to provide, say, serial output instead of parallel output
  108 ///               Some refactoring and code size reduction.
  109 /// \version 1.20 Improved documentation and examples to show need for correctly
  110 ///               specifying AccelStepper::FULL4WIRE and friends.
  111 /// \version 1.21 Fixed a problem where desiredSpeed could compute the wrong step acceleration
  112 ///               when _speed was small but non-zero. Reported by Brian Schmalz.
  113 ///               Precompute sqrt_twoa to improve performance and max possible stepping speed
  114 /// \version 1.22 Added Bounce.pde example
  115 ///               Fixed a problem where calling moveTo(), setMaxSpeed(), setAcceleration() more 
  116 ///               frequently than the step time, even
  117 ///               with the same values, would interfere with speed calcs. Now a new speed is computed 
  118 ///               only if there was a change in the set value. Reported by Brian Schmalz.
  119 /// \version 1.23 Rewrite of the speed algorithms in line with 
  120 ///               http://fab.cba.mit.edu/classes/MIT/961.09/projects/i0/Stepper_Motor_Speed_Profile.pdf
  121 ///               Now expect smoother and more linear accelerations and decelerations. The desiredSpeed()
  122 ///               function was removed.
  123 /// \version 1.24  Fixed a problem introduced in 1.23: with runToPosition, which did never returned
  124 /// \version 1.25  Now ignore attempts to set acceleration to 0.0
  125 /// \version 1.26  Fixed a problem where certina combinations of speed and accelration could cause
  126 ///                oscillation about the target position.
  127 /// \version 1.27  Added stop() function to stop as fast as possible with current acceleration parameters.
  128 ///                Also added new Quickstop example showing its use.
  129 /// \version 1.28  Fixed another problem where certain combinations of speed and accelration could cause
  130 ///                oscillation about the target position.
  131 ///                Added support for 3 wire full and half steppers such as Hard Disk Drive spindle.
  132 ///                Contributed by Yuri Ivatchkovitch.
  133 /// \version 1.29  Fixed a problem that could cause a DRIVER stepper to continually step
  134 ///                with some sketches. Reported by Vadim.
  135 /// \version 1.30  Fixed a problem that could cause stepper to back up a few steps at the end of
  136 ///                accelerated travel with certain speeds. Reported and patched by jolo.
  137 /// \version 1.31  Updated author and distribution location details to airspayce.com
  138 /// \version 1.32  Fixed a problem with enableOutputs() and setEnablePin on Arduino Due that
  139 ///                prevented the enable pin changing stae correctly. Reported by Duane Bishop.
  140 /// \version 1.33  Fixed an error in example AFMotor_ConstantSpeed.pde did not setMaxSpeed();
  141 ///                Fixed a problem that caused incorrect pin sequencing of FULL3WIRE and HALF3WIRE.
  142 ///                Unfortunately this meant changing the signature for all step*() functions.
  143 ///                Added example MotorShield, showing how to use AdaFruit Motor Shield to control
  144 ///                a 3 phase motor such as a HDD spindle motor (and without using the AFMotor library.
  145 /// \version 1.34  Added setPinsInverted(bool pin1Invert, bool pin2Invert, bool pin3Invert, bool pin4Invert, bool enableInvert) 
  146 ///                to allow inversion of 2, 3 and 4 wire stepper pins. Requested by Oleg.
  147 /// \version 1.35  Removed default args from setPinsInverted(bool, bool, bool, bool, bool) to prevent ambiguity with 
  148 ///                setPinsInverted(bool, bool, bool). Reported by Mac Mac.
  149 /// \version 1.36  Changed enableOutputs() and disableOutputs() to be virtual so can be overridden.
  150 ///                Added new optional argument 'enable' to constructor, which allows you toi disable the 
  151 ///                automatic enabling of outputs at construction time. Suggested by Guido.
  152 /// \version 1.37  Fixed a problem with step1 that could cause a rogue step in the 
  153 ///                wrong direction (or not,
  154 ///                depending on the setup-time requirements of the connected hardware). 
  155 ///                Reported by Mark Tillotson.
  156 ///
  157 /// \author  Mike McCauley (mikem@airspayce.com) DO NOT CONTACT THE AUTHOR DIRECTLY: USE THE LISTS
  158 // Copyright (C) 2009-2013 Mike McCauley
  159 // $Id: AccelStepper.h,v 1.19 2013/08/02 01:53:21 mikem Exp mikem $
  160 
  161 #ifndef AccelStepper_h
  162 #define AccelStepper_h
  163 
  164 #include <stdlib.h>
  165 #if ARDUINO >= 100
  166 #include <Arduino.h>
  167 #else
  168 #include <WProgram.h>
  169 #include <wiring.h>
  170 #endif
  171 
  172 // These defs cause trouble on some versions of Arduino
  173 #undef round
  174 
  175 /////////////////////////////////////////////////////////////////////
  176 /// \class AccelStepper AccelStepper.h <AccelStepper.h>
  177 /// \brief Support for stepper motors with acceleration etc.
  178 ///
  179 /// This defines a single 2 or 4 pin stepper motor, or stepper moter with fdriver chip, with optional
  180 /// acceleration, deceleration, absolute positioning commands etc. Multiple
  181 /// simultaneous steppers are supported, all moving 
  182 /// at different speeds and accelerations. 
  183 ///
  184 /// \par Operation
  185 /// This module operates by computing a step time in microseconds. The step
  186 /// time is recomputed after each step and after speed and acceleration
  187 /// parameters are changed by the caller. The time of each step is recorded in
  188 /// microseconds. The run() function steps the motor if a new step is due.
  189 /// The run() function must be called frequently until the motor is in the
  190 /// desired position, after which time run() will do nothing.
  191 ///
  192 /// \par Positioning
  193 /// Positions are specified by a signed long integer. At
  194 /// construction time, the current position of the motor is consider to be 0. Positive
  195 /// positions are clockwise from the initial position; negative positions are
  196 /// anticlockwise. The curent position can be altered for instance after
  197 /// initialization positioning.
  198 ///
  199 /// \par Caveats
  200 /// This is an open loop controller: If the motor stalls or is oversped,
  201 /// AccelStepper will not have a correct 
  202 /// idea of where the motor really is (since there is no feedback of the motor's
  203 /// real position. We only know where we _think_ it is, relative to the
  204 /// initial starting point).
  205 ///
  206 /// \par Performance
  207 /// The fastest motor speed that can be reliably supported is about 4000 steps per
  208 /// second at a clock frequency of 16 MHz on Arduino such as Uno etc. 
  209 /// Faster processors can support faster stepping speeds. 
  210 /// However, any speed less than that
  211 /// down to very slow speeds (much less than one per second) are also supported,
  212 /// provided the run() function is called frequently enough to step the motor
  213 /// whenever required for the speed set.
  214 /// Calling setAcceleration() is expensive,
  215 /// since it requires a square root to be calculated.
  216 class AccelStepper
  217 {
  218 public:
  219     /// \brief Symbolic names for number of pins.
  220     /// Use this in the pins argument the AccelStepper constructor to 
  221     /// provide a symbolic name for the number of pins
  222     /// to use.
  223     typedef enum
  224     {
  225         FUNCTION  = 0, ///< Use the functional interface, implementing your own driver functions (internal use only)
  226         DRIVER    = 1, ///< Stepper Driver, 2 driver pins required
  227         FULL2WIRE = 2, ///< 2 wire stepper, 2 motor pins required
  228         FULL3WIRE = 3, ///< 3 wire stepper, such as HDD spindle, 3 motor pins required
  229         FULL4WIRE = 4, ///< 4 wire full stepper, 4 motor pins required
  230         HALF3WIRE = 6, ///< 3 wire half stepper, such as HDD spindle, 3 motor pins required
  231         HALF4WIRE = 8  ///< 4 wire half stepper, 4 motor pins required
  232     } MotorInterfaceType;
  233 
  234     /// Constructor. You can have multiple simultaneous steppers, all moving
  235     /// at different speeds and accelerations, provided you call their run()
  236     /// functions at frequent enough intervals. Current Position is set to 0, target
  237     /// position is set to 0. MaxSpeed and Acceleration default to 1.0.
  238     /// The motor pins will be initialised to OUTPUT mode during the
  239     /// constructor by a call to enableOutputs().
  240     /// \param[in] interface Number of pins to interface to. 1, 2, 4 or 8 are
  241     /// supported, but it is preferred to use the \ref MotorInterfaceType symbolic names. 
  242     /// AccelStepper::DRIVER (1) means a stepper driver (with Step and Direction pins).
  243     /// If an enable line is also needed, call setEnablePin() after construction.
  244     /// You may also invert the pins using setPinsInverted().
  245     /// AccelStepper::FULL2WIRE (2) means a 2 wire stepper (2 pins required). 
  246     /// AccelStepper::FULL3WIRE (3) means a 3 wire stepper, such as HDD spindle (3 pins required). 
  247     /// AccelStepper::FULL4WIRE (4) means a 4 wire stepper (4 pins required). 
  248     /// AccelStepper::HALF3WIRE (6) means a 3 wire half stepper, such as HDD spindle (3 pins required)
  249     /// AccelStepper::HALF4WIRE (8) means a 4 wire half stepper (4 pins required)
  250     /// Defaults to AccelStepper::FULL4WIRE (4) pins.
  251     /// \param[in] pin1 Arduino digital pin number for motor pin 1. Defaults
  252     /// to pin 2. For a AccelStepper::DRIVER (pins==1), 
  253     /// this is the Step input to the driver. Low to high transition means to step)
  254     /// \param[in] pin2 Arduino digital pin number for motor pin 2. Defaults
  255     /// to pin 3. For a AccelStepper::DRIVER (pins==1), 
  256     /// this is the Direction input the driver. High means forward.
  257     /// \param[in] pin3 Arduino digital pin number for motor pin 3. Defaults
  258     /// to pin 4.
  259     /// \param[in] pin4 Arduino digital pin number for motor pin 4. Defaults
  260     /// to pin 5.
  261     /// \param[in] enable If this is true (the default), enableOutpuys() will be called to enable
  262     /// the output pins at construction time.
  263     AccelStepper(uint8_t interface = AccelStepper::FULL4WIRE, uint8_t pin1 = 2, uint8_t pin2 = 3, uint8_t pin3 = 4, uint8_t pin4 = 5, bool enable = true);
  264 
  265     /// Alternate Constructor which will call your own functions for forward and backward steps. 
  266     /// You can have multiple simultaneous steppers, all moving
  267     /// at different speeds and accelerations, provided you call their run()
  268     /// functions at frequent enough intervals. Current Position is set to 0, target
  269     /// position is set to 0. MaxSpeed and Acceleration default to 1.0.
  270     /// Any motor initialization should happen before hand, no pins are used or initialized.
  271     /// \param[in] forward void-returning procedure that will make a forward step
  272     /// \param[in] backward void-returning procedure that will make a backward step
  273     AccelStepper(void (*forward)(), void (*backward)());
  274     
  275     /// Set the target position. The run() function will try to move the motor
  276     /// from the current position to the target position set by the most
  277     /// recent call to this function. Caution: moveTo() also recalculates the speed for the next step. 
  278     /// If you are trying to use constant speed movements, you should call setSpeed() after calling moveTo().
  279     /// \param[in] absolute The desired absolute position. Negative is
  280     /// anticlockwise from the 0 position.
  281     void    moveTo(long absolute); 
  282 
  283     /// Set the target position relative to the current position
  284     /// \param[in] relative The desired position relative to the current position. Negative is
  285     /// anticlockwise from the current position.
  286     void    move(long relative);
  287 
  288     /// Poll the motor and step it if a step is due, implementing
  289     /// accelerations and decelerations to acheive the target position. You must call this as
  290     /// frequently as possible, but at least once per minimum step interval,
  291     /// preferably in your main loop.
  292     /// \return true if the motor is at the target position.
  293     boolean run();
  294 
  295     /// Poll the motor and step it if a step is due, implmenting a constant
  296     /// speed as set by the most recent call to setSpeed(). You must call this as
  297     /// frequently as possible, but at least once per step interval,
  298     /// \return true if the motor was stepped.
  299     boolean runSpeed();
  300 
  301     /// Sets the maximum permitted speed. the run() function will accelerate
  302     /// up to the speed set by this function.
  303     /// \param[in] speed The desired maximum speed in steps per second. Must
  304     /// be > 0. Caution: Speeds that exceed the maximum speed supported by the processor may
  305     /// Result in non-linear accelerations and decelerations.
  306     void    setMaxSpeed(float speed);
  307 
  308     /// Sets the acceleration and deceleration parameter.
  309     /// \param[in] acceleration The desired acceleration in steps per second
  310     /// per second. Must be > 0.0. This is an expensive call since it requires a square 
  311     /// root to be calculated. Dont call more ofthen than needed
  312     void    setAcceleration(float acceleration);
  313 
  314     /// Sets the desired constant speed for use with runSpeed().
  315     /// \param[in] speed The desired constant speed in steps per
  316     /// second. Positive is clockwise. Speeds of more than 1000 steps per
  317     /// second are unreliable. Very slow speeds may be set (eg 0.00027777 for
  318     /// once per hour, approximately. Speed accuracy depends on the Arduino
  319     /// crystal. Jitter depends on how frequently you call the runSpeed() function.
  320     void    setSpeed(float speed);
  321 
  322     /// The most recently set speed
  323     /// \return the most recent speed in steps per second
  324     float   speed();
  325 
  326     /// The distance from the current position to the target position.
  327     /// \return the distance from the current position to the target position
  328     /// in steps. Positive is clockwise from the current position.
  329     long    distanceToGo();
  330 
  331     /// The most recently set target position.
  332     /// \return the target position
  333     /// in steps. Positive is clockwise from the 0 position.
  334     long    targetPosition();
  335 
  336     /// The currently motor position.
  337     /// \return the current motor position
  338     /// in steps. Positive is clockwise from the 0 position.
  339     long    currentPosition();  
  340 
  341     /// Resets the current position of the motor, so that wherever the motor
  342     /// happens to be right now is considered to be the new 0 position. Useful
  343     /// for setting a zero position on a stepper after an initial hardware
  344     /// positioning move.
  345     /// Has the side effect of setting the current motor speed to 0.
  346     /// \param[in] position The position in steps of wherever the motor
  347     /// happens to be right now.
  348     void    setCurrentPosition(long position);  
  349     
  350     /// Moves the motor at the currently selected constant speed (forward or reverse) 
  351     /// to the target position and blocks until it is at
  352     /// position. Dont use this in event loops, since it blocks.
  353     void    runToPosition();
  354 
  355     /// Runs at the currently selected speed until the target position is reached
  356     /// Does not implement accelerations.
  357     /// \return true if it stepped
  358     boolean runSpeedToPosition();
  359 
  360     /// Moves the motor to the new target position and blocks until it is at
  361     /// position. Dont use this in event loops, since it blocks.
  362     /// \param[in] position The new target position.
  363     void    runToNewPosition(long position);
  364 
  365     /// Sets a new target position that causes the stepper
  366     /// to stop as quickly as possible, using to the current speed and acceleration parameters.
  367     void stop();
  368 
  369     /// Disable motor pin outputs by setting them all LOW
  370     /// Depending on the design of your electronics this may turn off
  371     /// the power to the motor coils, saving power.
  372     /// This is useful to support Arduino low power modes: disable the outputs
  373     /// during sleep and then reenable with enableOutputs() before stepping
  374     /// again.
  375     virtual void    disableOutputs();
  376 
  377     /// Enable motor pin outputs by setting the motor pins to OUTPUT
  378     /// mode. Called automatically by the constructor.
  379     virtual void    enableOutputs();
  380 
  381     /// Sets the minimum pulse width allowed by the stepper driver. The minimum practical pulse width is 
  382     /// approximately 20 microseconds. Times less than 20 microseconds
  383     /// will usually result in 20 microseconds or so.
  384     /// \param[in] minWidth The minimum pulse width in microseconds. 
  385     void    setMinPulseWidth(unsigned int minWidth);
  386 
  387     /// Sets the enable pin number for stepper drivers.
  388     /// 0xFF indicates unused (default).
  389     /// Otherwise, if a pin is set, the pin will be turned on when 
  390     /// enableOutputs() is called and switched off when disableOutputs() 
  391     /// is called.
  392     /// \param[in] enablePin Arduino digital pin number for motor enable
  393     /// \sa setPinsInverted
  394     void    setEnablePin(uint8_t enablePin = 0xff);
  395 
  396     /// Sets the inversion for stepper driver pins
  397     /// \param[in] directionInvert True for inverted direction pin, false for non-inverted
  398     /// \param[in] stepInvert      True for inverted step pin, false for non-inverted
  399     /// \param[in] enableInvert    True for inverted enable pin, false (default) for non-inverted
  400     void    setPinsInverted(bool directionInvert = false, bool stepInvert = false, bool enableInvert = false);
  401 
  402     /// Sets the inversion for 2, 3 and 4 wire stepper pins
  403     /// \param[in] pin1Invert True for inverted pin1, false for non-inverted
  404     /// \param[in] pin2Invert True for inverted pin2, false for non-inverted
  405     /// \param[in] pin3Invert True for inverted pin3, false for non-inverted
  406     /// \param[in] pin4Invert True for inverted pin4, false for non-inverted
  407     /// \param[in] enableInvert    True for inverted enable pin, false (default) for non-inverted
  408     void    setPinsInverted(bool pin1Invert, bool pin2Invert, bool pin3Invert, bool pin4Invert, bool enableInvert);
  409 
  410 protected:
  411 
  412     /// \brief Direction indicator
  413     /// Symbolic names for the direction the motor is turning
  414     typedef enum
  415     {
  416         DIRECTION_CCW = 0,  ///< Clockwise
  417         DIRECTION_CW  = 1   ///< Counter-Clockwise
  418     } Direction;
  419 
  420     /// Forces the library to compute a new instantaneous speed and set that as
  421     /// the current speed. It is called by
  422     /// the library:
  423     /// \li  after each step
  424     /// \li  after change to maxSpeed through setMaxSpeed()
  425     /// \li  after change to acceleration through setAcceleration()
  426     /// \li  after change to target position (relative or absolute) through
  427     /// move() or moveTo()
  428     void           computeNewSpeed();
  429 
  430     /// Low level function to set the motor output pins
  431     /// bit 0 of the mask corresponds to _pin[0]
  432     /// bit 1 of the mask corresponds to _pin[1]
  433     /// You can override this to impment, for example serial chip output insted of using the
  434     /// output pins directly
  435     virtual void   setOutputPins(uint8_t mask);
  436 
  437     /// Called to execute a step. Only called when a new step is
  438     /// required. Subclasses may override to implement new stepping
  439     /// interfaces. The default calls step1(), step2(), step4() or step8() depending on the
  440     /// number of pins defined for the stepper.
  441     /// \param[in] step The current step phase number (0 to 7)
  442     virtual void   step(long step);
  443 
  444     /// Called to execute a step using stepper functions (pins = 0) Only called when a new step is
  445     /// required. Calls _forward() or _backward() to perform the step
  446     /// \param[in] step The current step phase number (0 to 7)
  447     virtual void   step0(long step);
  448 
  449     /// Called to execute a step on a stepper driver (ie where pins == 1). Only called when a new step is
  450     /// required. Subclasses may override to implement new stepping
  451     /// interfaces. The default sets or clears the outputs of Step pin1 to step, 
  452     /// and sets the output of _pin2 to the desired direction. The Step pin (_pin1) is pulsed for 1 microsecond
  453     /// which is the minimum STEP pulse width for the 3967 driver.
  454     /// \param[in] step The current step phase number (0 to 7)
  455     virtual void   step1(long step);
  456 
  457     /// Called to execute a step on a 2 pin motor. Only called when a new step is
  458     /// required. Subclasses may override to implement new stepping
  459     /// interfaces. The default sets or clears the outputs of pin1 and pin2
  460     /// \param[in] step The current step phase number (0 to 7)
  461     virtual void   step2(long step);
  462 
  463     /// Called to execute a step on a 3 pin motor, such as HDD spindle. Only called when a new step is
  464     /// required. Subclasses may override to implement new stepping
  465     /// interfaces. The default sets or clears the outputs of pin1, pin2,
  466     /// pin3
  467     /// \param[in] step The current step phase number (0 to 7)
  468     virtual void   step3(long step);
  469 
  470     /// Called to execute a step on a 4 pin motor. Only called when a new step is
  471     /// required. Subclasses may override to implement new stepping
  472     /// interfaces. The default sets or clears the outputs of pin1, pin2,
  473     /// pin3, pin4.
  474     /// \param[in] step The current step phase number (0 to 7)
  475     virtual void   step4(long step);
  476 
  477     /// Called to execute a step on a 3 pin motor, such as HDD spindle. Only called when a new step is
  478     /// required. Subclasses may override to implement new stepping
  479     /// interfaces. The default sets or clears the outputs of pin1, pin2,
  480     /// pin3
  481     /// \param[in] step The current step phase number (0 to 7)
  482     virtual void   step6(long step);
  483 
  484     /// Called to execute a step on a 4 pin half-steper motor. Only called when a new step is
  485     /// required. Subclasses may override to implement new stepping
  486     /// interfaces. The default sets or clears the outputs of pin1, pin2,
  487     /// pin3, pin4.
  488     /// \param[in] step The current step phase number (0 to 7)
  489     virtual void   step8(long step);
  490 
  491 private:
  492     /// Number of pins on the stepper motor. Permits 2 or 4. 2 pins is a
  493     /// bipolar, and 4 pins is a unipolar.
  494     uint8_t        _interface;          // 0, 1, 2, 4, 8, See MotorInterfaceType
  495 
  496     /// Arduino pin number assignments for the 2 or 4 pins required to interface to the
  497     /// stepper motor or driver
  498     uint8_t        _pin[4];
  499 
  500     /// Whether the _pins is inverted or not
  501     uint8_t        _pinInverted[4];
  502 
  503     /// The current absolution position in steps.
  504     long           _currentPos;    // Steps
  505 
  506     /// The target position in steps. The AccelStepper library will move the
  507     /// motor from the _currentPos to the _targetPos, taking into account the
  508     /// max speed, acceleration and deceleration
  509     long           _targetPos;     // Steps
  510 
  511     /// The current motos speed in steps per second
  512     /// Positive is clockwise
  513     float          _speed;         // Steps per second
  514 
  515     /// The maximum permitted speed in steps per second. Must be > 0.
  516     float          _maxSpeed;
  517 
  518     /// The acceleration to use to accelerate or decelerate the motor in steps
  519     /// per second per second. Must be > 0
  520     float          _acceleration;
  521     float          _sqrt_twoa; // Precomputed sqrt(2*_acceleration)
  522 
  523     /// The current interval between steps in microseconds.
  524     /// 0 means the motor is currently stopped with _speed == 0
  525     unsigned long  _stepInterval;
  526 
  527     /// The last step time in microseconds
  528     unsigned long  _lastStepTime;
  529 
  530     /// The minimum allowed pulse width in microseconds
  531     unsigned int   _minPulseWidth;
  532 
  533     /// Is the direction pin inverted?
  534     ///bool           _dirInverted; /// Moved to _pinInverted[1]
  535 
  536     /// Is the step pin inverted?
  537     ///bool           _stepInverted; /// Moved to _pinInverted[0]
  538 
  539     /// Is the enable pin inverted?
  540     bool           _enableInverted;
  541 
  542     /// Enable pin for stepper driver, or 0xFF if unused.
  543     uint8_t        _enablePin;
  544 
  545     /// The pointer to a forward-step procedure
  546     void (*_forward)();
  547 
  548     /// The pointer to a backward-step procedure
  549     void (*_backward)();
  550 
  551     /// The step counter for speed calculations
  552     long _n;
  553 
  554     /// Initial step size in microseconds
  555     float _c0;
  556 
  557     /// Last step size in microseconds
  558     float _cn;
  559 
  560     /// Min step size in microseconds based on maxSpeed
  561     float _cmin; // at max speed
  562 
  563     /// Current direction motor is spinning in
  564     boolean _direction; // 1 == CW
  565 
  566 };
  567 
  568 /// @example Random.pde
  569 /// Make a single stepper perform random changes in speed, position and acceleration
  570 
  571 /// @example Overshoot.pde
  572 ///  Check overshoot handling
  573 /// which sets a new target position and then waits until the stepper has 
  574 /// achieved it. This is used for testing the handling of overshoots
  575 
  576 /// @example MultiStepper.pde
  577 /// Shows how to multiple simultaneous steppers
  578 /// Runs one stepper forwards and backwards, accelerating and decelerating
  579 /// at the limits. Runs other steppers at the same time
  580 
  581 /// @example ConstantSpeed.pde
  582 /// Shows how to run AccelStepper in the simplest,
  583 /// fixed speed mode with no accelerations
  584 
  585 /// @example Blocking.pde 
  586 /// Shows how to use the blocking call runToNewPosition
  587 /// Which sets a new target position and then waits until the stepper has 
  588 /// achieved it.
  589 
  590 /// @example AFMotor_MultiStepper.pde
  591 /// Control both Stepper motors at the same time with different speeds
  592 /// and accelerations. 
  593 
  594 /// @example AFMotor_ConstantSpeed.pde
  595 /// Shows how to run AccelStepper in the simplest,
  596 /// fixed speed mode with no accelerations
  597 
  598 /// @example ProportionalControl.pde
  599 /// Make a single stepper follow the analog value read from a pot or whatever
  600 /// The stepper will move at a constant speed to each newly set posiiton, 
  601 /// depending on the value of the pot.
  602 
  603 /// @example Bounce.pde
  604 /// Make a single stepper bounce from one limit to another, observing
  605 /// accelrations at each end of travel
  606 
  607 /// @example Quickstop.pde
  608 /// Check stop handling.
  609 /// Calls stop() while the stepper is travelling at full speed, causing
  610 /// the stepper to stop as quickly as possible, within the constraints of the
  611 /// current acceleration.
  612 
  613 /// @example MotorShield.pde
  614 /// Shows how to use AccelStepper to control a 3-phase motor, such as a HDD spindle motor
  615 /// using the Adafruit Motor Shield http://www.ladyada.net/make/mshield/index.html.
  616 
  617 #endif 