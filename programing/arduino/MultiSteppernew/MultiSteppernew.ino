// MultiStepper.pde
// -*- mode: C++ -*-
//
// Shows how to multiple simultaneous steppers
// Runs one stepper forwards and backwards, accelerating and decelerating
// at the limits. Runs other steppers at the same time
//
// Copyright (C) 2009 Mike McCauley
// $Id: MultiStepper.pde,v 1.1 2011/01/05 01:51:01 mikem Exp mikem $

#include <AccelStepper.h>

// Define some steppers and the pins the will use
//AccelStepper stepper1; // Defaults to AccelStepper::FULL4WIRE (4 pins) on 2, 3, 4, 5
//AccelStepper stepper2(AccelStepper::FULL4WIRE, 6, 7, 8, 9);
AccelStepper stepper1(AccelStepper::FULL2WIRE, 9, 10);

void setup()
{  
    stepper1.setMaxSpeed(5);
    //stepper1.setAcceleration(100.0);
    
    stepper1.moveTo(100000);
    stepper1.setMinPulseWidth(2000);
}

void loop()
{
  stepper1.setSpeed(5);
    // Change direction at the limits
    if (stepper1.distanceToGo() == 0)
	stepper1.moveTo(-stepper1.currentPosition());
    stepper1.run();
}
