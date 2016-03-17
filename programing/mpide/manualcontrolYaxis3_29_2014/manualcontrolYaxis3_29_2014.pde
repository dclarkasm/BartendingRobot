#include <AccelStepper.h>
const int pulX = 3;          //pulse output for X axis
const int dirX = 2;         //direction output for X axis
const int pulY = 7;         //pulse output for Y axis
const int dirY = 6;         //direction output for Y axis
const int pulZ = 9;         //pulse output for Z axis
const int dirZ = 8;         //direction output for Z axis

AccelStepper stepperX(AccelStepper::FULL2WIRE, pulX, dirX);
AccelStepper stepperY(AccelStepper::FULL2WIRE, pulY, dirY);
AccelStepper stepperZ(AccelStepper::FULL2WIRE, pulZ, dirZ);

int pos = 4000;
int Zpos = 0;

void setup() {
  pinMode(pulX, OUTPUT);
  pinMode(dirX, OUTPUT);
  
  pinMode(pulY, OUTPUT);
  pinMode(dirY, OUTPUT);
  
  pinMode(pulZ, OUTPUT);
  pinMode(dirZ, OUTPUT);
  
  stepperX.setMaxSpeed(6000.0);
  stepperX.setAcceleration(25000.0);
  stepperX.moveTo(0); 
  
  stepperY.setMaxSpeed(25000.0);
  stepperY.setAcceleration(75000.0);
  stepperY.moveTo(0); 
  
  stepperZ.setMaxSpeed(25000.0);
  stepperZ.setAcceleration(100000.0);
  stepperZ.moveTo(0);
}

void loop(){
  while(1){
  //change position
  if(Zpos==pos) Zpos=0; 
  else Zpos=pos;
  
  stepperZ.moveTo(Zpos);    //set the Z position
  
  while((stepperY.distanceToGo() != 0)){    
    stepperY.run();
    }
  delay(50);
  }  
}

