#include <AccelStepper.h>
// constants won't change. They're used here to 
// set pin numbers:
//AccelStepper stepper1(AccelStepper::FULL2WIRE, 9, 10);

const int fwd = 2;     // Screw Driver order has been placed
const int rit = 4;     // Cuba Libre order has been placed
const int dwn = 7;     // Shirley Temple order has been placed
const int lft = 8;     // Martini order has been placed

int countUP = 0;      // the count of Screw Driver orders
int countDN = 0;      // the count of Cuba Libre orders

int stepDelay = 400;
int stepCnt = 0;

void setup() {
  // initialize the screw driver order pin as an input:
  pinMode(2, INPUT); 
  pinMode(3, INPUT);
  pinMode(4, INPUT);
  pinMode(7, INPUT);
  pinMode(8, INPUT);
  
  pinMode(9, OUTPUT);
  pinMode(10, OUTPUT);
  //pinMode(11, OUTPUT);
  
  //stepper1.setMaxSpeed(9000);
  //stepper1.setSpeed(8500);
  //stepper1.moveTo(100);
}

void loop(){  
  if(digitalRead(2)){
    while(digitalRead(2)) { 
      //stepper1.runSpeed();
      digitalWrite(9, HIGH);
      digitalWrite(10, HIGH);
      stepCnt++;
      delayMicroseconds(stepDelay);
      digitalWrite(9, LOW);
      digitalWrite(10, LOW);
      delayMicroseconds(stepDelay);
    }
    //delay(250);
  }
  
  if(digitalRead(7)){
    //digitalWrite(11, HIGH);
    while(digitalRead(7)) {
      //stepper1.moveTo(-stepper1.currentPosition());
      //stepper1.runSpeed();
      digitalWrite(9, HIGH);
      digitalWrite(10, LOW);
      stepCnt--;
      delayMicroseconds(stepDelay);
      digitalWrite(9, LOW);
      digitalWrite(10, HIGH);
      delayMicroseconds(stepDelay);
    }
    //delay(250);
  }
    
  if(digitalRead(3)){
    while(stepCnt>0){
      digitalWrite(9, HIGH);
      digitalWrite(10, LOW);
      stepCnt--;
      delayMicroseconds(stepDelay);
      digitalWrite(9, LOW);
      digitalWrite(10, HIGH);
      delayMicroseconds(stepDelay);
    }
    while(stepCnt<0){
      digitalWrite(9, HIGH);
      digitalWrite(10, HIGH);
      stepCnt++;
      delayMicroseconds(stepDelay);
      digitalWrite(9, LOW);
      digitalWrite(10, LOW);
      delayMicroseconds(stepDelay);
    }  
  }  
}

