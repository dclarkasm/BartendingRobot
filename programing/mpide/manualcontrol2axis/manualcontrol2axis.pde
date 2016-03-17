#include <binary.h>
#include <Sprite.h>

#include <AccelStepper.h>

#define FLOWSENSORPIN 2

//MARTINI = ADIOS
//VERMOUTH = COO
/*
  Marini:
    2.5oz. Gin
    .5oz. Dry Vermouth
    
  Cuba Libre:
    2oz. Light Rum
    4oz. Cola
    
  Shirley Temple:
    1oz. Vodka
    5oz. Sprite
    .5oz. Grenadine
    
  Screw Driver:
    2oz. Vodka
    5oz. OJ
  */
    
//inputs
const int SC = 42;        // Screw Driver order has been placed
const int CL = 43;        // Cuba Libre order has been placed
const int ST = 44;        // Shirley Temple order has been placed
const int MT = 45;        // Martini order has been placed
const int hom = 50;           // home position button pressed  
const int fwd = 42;
const int rit = 43;
const int rev = 44;
const int lft = 45;
const int manual = 80;        //may need another pin
const int y_hom_sens = 39;
const int x_hom_sens = 38;
const int z_hom_sens = 41;
const int flow1 = 30;
const int flow2 = 31;
const int flow3 = 49; //32
const int flow4 = 33;
const int flow5 = 34;
const int flow6 = 35;
const int flow7 = 36;
const int flow8 = 37;


//outputs
const int pulX = 10;          //pulse output for X axis
const int dirX = 11;         //direction output for X axis
const int pulY = 6;         //pulse output for Y axis
const int dirY = 7;         //direction output for Y axis
const int pulZ = 8;         //pulse output for Z axis
const int dirZ = 9;         //direction output for Z axis
const int vodOut = 78;
const int rumOut = 71;
const int ginOut = 72;
const int verOut = 73;
const int ojOut = 74;
const int cokOut = 75;
const int sprOut = 76;
const int greOut = 77;
const int ackTra = 48;      //acknowledgement transmission pin

//Constant Values
const int stepDelay = 500;   // microsecond delay between each pulse
const int dripDelay = 2000;  //time alotted for drink to stop dripping
typedef enum{homePos, vodka, rum, gin, vermouth, OJ, coke, sprite, grenadine, mixer, endPos}posIdx;
typedef enum{unkn1, sDriver, cLibre, sTemple, mTini}d_code;
// x/y axis positions:          home, vodka, rum      , gin     , vermouth    , OJ       , coke      , sprite     , grenadine    , mixer    , end
const int x_yAxisPos[2][11] = {{0   , 8000  , 8000    , 12500    , 12500      , 16750    , 16750     , 21500      , 21500        , 26250    , 26500},
                              {0    , -13000, -65000  , -13000  , -65000      , -13000   , -65000    , -13000     , -65000       , -42500   , -2750}}; 
                           //N/U , vodka, rum, gin,vermouth, OJ, coke, sprite, grenadine 
const float flow_amt[4][9] = {{0  , 10   , 0  , 0   , 0    , 33 , 0    , 0     , 0        },  //screw driver
                            {0    , 0    , 13 , 0   , 0    , 0  , 27   , 0     , 0        },  //Cuba Libre
                            {0    , 5    , 0  , 0   , 0    , 0  , 0    , 39    , 4        },  //Shirley temple
                            {0    , 1    , 2  , 2   , 2    , 0  , 0    , 23    , 7        }};  //Martini (Adios)
                            
                            
 /*const float flow_amt[4][9] = {{0  , 0   , 0  , 0   , 0    , 0 , 0    , 0     , 0        },  //screw driver
                            {0    , 0    , 0 , 0   , 0    , 0  , 0   , 0     , 0        },  //Cuba Libre
                            {0    , 0    , 0  , 0   , 0    , 0  , 0    , 0    , 0        },  //Shirley temple
                            {0    , 0    , 0  , 0   , 0    , 0  , 0    , 0    , 0        }};  //Martini (Adios)*/                           
                            
AccelStepper stepperX(AccelStepper::FULL2WIRE, pulX, dirX);
AccelStepper stepperY(AccelStepper::FULL2WIRE, pulY, dirY);
AccelStepper stepperZ(AccelStepper::FULL2WIRE, pulZ, dirZ);

int stepCntX = 0;
int stepCntY = 0;
int stepCntZ = 0;
int Zpos = 0;    //distance to move mixer
const int nMixes = 6;    //number of mixes (up down motion of Z actuator in glass)
int inCode = 0;

boolean homeStat = false;  //robot home status

// count how many pulses!
volatile uint16_t pulses = 0;
// track the state of the pulse pin
volatile uint8_t lastflowpinstate;
// you can try to keep time of how long it is between pulses
volatile uint32_t lastflowratetimer = 0;
// and use that to calculate a flow rate
volatile float flowrate;
// Interrupt is called once a millisecond, looks for any pulses from the sensor!
int cnt = 0; //sensor check counter, increment each sensor check, print screen when 100 is reached

void setup() {
  // initialize the screw driver order pin as an input:
  //SC, CL, ST, MT
  Serial.begin(9600);
  pinMode(SC, INPUT);
  pinMode(CL, INPUT);
  pinMode(ST, INPUT);
  pinMode(MT, INPUT);
  
  pinMode(fwd, INPUT); 
  pinMode(hom, INPUT);
  pinMode(rit, INPUT);
  pinMode(rev, INPUT);
  pinMode(lft, INPUT);
  pinMode(manual, INPUT);
  pinMode(ackTra, OUTPUT);
  pinMode(x_hom_sens, INPUT);
  pinMode(y_hom_sens, INPUT);
  pinMode(z_hom_sens, INPUT);
  
  pinMode(flow1, INPUT);
  pinMode(flow2, INPUT);
  pinMode(flow3, INPUT);
  pinMode(flow4, INPUT);
  pinMode(flow5, INPUT);
  pinMode(flow6, INPUT);
  pinMode(flow7, INPUT);
  pinMode(flow8, INPUT);
  
  pinMode(pulX, OUTPUT);
  pinMode(dirX, OUTPUT);
  
  pinMode(pulY, OUTPUT);
  pinMode(dirY, OUTPUT);
  
  pinMode(pulZ, OUTPUT);
  pinMode(dirZ, OUTPUT);
  
  pinMode(vodOut, OUTPUT);
  pinMode(rumOut, OUTPUT);
  pinMode(ginOut, OUTPUT);
  pinMode(verOut, OUTPUT);
  pinMode(ojOut, OUTPUT);
  pinMode(cokOut, OUTPUT);
  pinMode(sprOut, OUTPUT);
  pinMode(greOut, OUTPUT);
  
  stepperX.setMaxSpeed(6000.0);
  stepperX.setAcceleration(25000.0);
  stepperX.moveTo(0); 
  
  stepperY.setMaxSpeed(25000.0);
  stepperY.setAcceleration(75000.0);
  stepperY.moveTo(0); 
  
  stepperZ.setMaxSpeed(25000.0);
  stepperZ.setAcceleration(100000.0);
  stepperZ.moveTo(0); 
  
  //========================================================Flow Sensors
  pinMode(FLOWSENSORPIN, INPUT);
  digitalWrite(FLOWSENSORPIN, HIGH);
  lastflowpinstate = digitalRead(FLOWSENSORPIN);
  
  //========================================================
}

void loop(){  
  isHome();
  if(!homeStat){      //home the robot before execution if not homed
    digitalWrite(vodOut, LOW);
    digitalWrite(rumOut, LOW);
    digitalWrite(ginOut, LOW);
    digitalWrite(verOut, LOW);
    digitalWrite(ojOut, LOW);
    digitalWrite(cokOut, LOW);
    digitalWrite(sprOut, LOW);
    digitalWrite(greOut, LOW);
    //HOME();
    homeStat = true;    
  }
  Serial.println("0");
  opMode();
  
}

//==================================================================================================== Op mode (manual/normal)
void opMode(){
  Serial.print("manual ");
  Serial.print(digitalRead(manual));
  Serial.println("");
  while(digitalRead(manual)){        //manual control
    //digitalWrite(ackTra, HIGH);
    if(digitalRead(fwd)) forward();
    else if(digitalRead(rev)) reverse();
    else if(digitalRead(rit)) right();
    else if(digitalRead(lft)) left();
    else if(digitalRead(hom)) HOME();    //Serial.println("take me home!");  
    else Serial.println("waiting for user input MANUAL");
  }
  
  Serial.println("here");
  
  
  while(!digitalRead(manual) && homeStat){ //normal opperation
    //digitalWrite(ackTra, LOW);
    Serial.println("INSIDE NORMAL");
    homeStat = false;
    if(digitalRead(SC)) inCode = 1;                          //screwDriver();
    else if(digitalRead(CL)) inCode = 2;                     //shirleyTemple();
    else if(digitalRead(ST)) inCode = 3;                     //cubaLibre();
    else if(digitalRead(MT)) inCode = 4;                     //martini();
    else{
      inCode = 0;
      Serial.println("waiting");
      digitalWrite(ackTra, LOW);
    }
    
    if(inCode > 0)
  {
    Serial.println(inCode);
    digitalWrite(ackTra, HIGH);
    Serial.println("ACK sent");
    //execute process
    switch(inCode){
      case 1: Serial.println("q1"); screwDriver(); break;    //screwDriver(); 
      case 2: Serial.println("q2"); cubaLibre(); break;    //shirleyTemple(); 
      case 3: Serial.println("q3"); shirleyTemple(); break;    //cubaLibre(); 
      case 4: Serial.println("q4"); martini(); break;    //martini(); 
      default: Serial.println("NO");
    }
  }  
}
}

//===================================================================================================== Manual Control
void forward(){
  while(digitalRead(fwd) && digitalRead(manual)) { 
    //Serial.println("3");
      digitalWrite(pulX, HIGH);
      digitalWrite(dirX, HIGH);
      stepCntX++;
      delayMicroseconds(stepDelay);
      digitalWrite(pulX, LOW);
      digitalWrite(dirX, LOW);
      delayMicroseconds(stepDelay);
    }
}

void reverse(){
    while(digitalRead(rev) && digitalRead(manual)) {
     // Serial.println("4");
      digitalWrite(pulX, HIGH);
      digitalWrite(dirX, LOW);
      stepCntX--;
      delayMicroseconds(stepDelay);
      digitalWrite(pulX, LOW);
      digitalWrite(dirX, HIGH);
      delayMicroseconds(stepDelay);
    }
}

void right(){
  while(digitalRead(rit) && digitalRead(manual)) { 
    //Serial.println("5");
      digitalWrite(pulY, HIGH);
      digitalWrite(dirY, HIGH);
      stepCntY++;
      delayMicroseconds(stepDelay);
      digitalWrite(pulY, LOW);
      digitalWrite(dirY, LOW);
      delayMicroseconds(stepDelay);
    }
}

void left(){
    while(digitalRead(lft) && digitalRead(manual)) {
     // Serial.println("6");
      digitalWrite(pulY, HIGH);
      digitalWrite(dirY, LOW);
      stepCntY--;
      delayMicroseconds(stepDelay);
      digitalWrite(pulY, LOW);
      digitalWrite(dirY, HIGH);
      delayMicroseconds(stepDelay);
    } 
}

void HOME(){
   while(!digitalRead(y_hom_sens)) {                  //***remove step count condition
    // Serial.println("7");
      digitalWrite(pulY, HIGH);
      digitalWrite(dirY, LOW);
      stepCntY--;
      delayMicroseconds(stepDelay);
      digitalWrite(pulY, LOW);
      digitalWrite(dirY, HIGH);
      delayMicroseconds(stepDelay);
   } 
   
  while(!digitalRead(x_hom_sens)) {                   //***remove step count condition
    //Serial.println("8");
      digitalWrite(pulX, HIGH);
      digitalWrite(dirX, HIGH);
      stepCntX--;
      delayMicroseconds(stepDelay);
      digitalWrite(pulX, LOW);
      digitalWrite(dirX, LOW);
      delayMicroseconds(stepDelay);
   }
   
   while(!digitalRead(z_hom_sens)) {                   //***remove step count condition
    //Serial.println("9");
      digitalWrite(pulZ, HIGH);
      digitalWrite(dirZ, LOW);
      stepCntZ--;
      delayMicroseconds(stepDelay);
      digitalWrite(pulZ, LOW);
      digitalWrite(dirZ, HIGH);
      delayMicroseconds(stepDelay);
   }
}


boolean isHome(){
  
  
  //Serial.print("X Sensor: "); Serial.println(digitalRead(x_hom_sens));
  //Serial.print("Y Sensor: "); Serial.println(digitalRead(y_hom_sens));
  //Serial.print("Z Sensor: "); Serial.println(digitalRead(z_hom_sens));
  if(digitalRead(x_hom_sens) && digitalRead(y_hom_sens) && digitalRead(z_hom_sens)){
   //  Serial.println("isHome");
    return true;
  }  
  else{
    //Serial.println("notHome");
    return false;
  }
}

//===============================================================================================================
//x/y axis positions:    homePos, vodka, rum, gin, vermouth, OJ , coke, sprite, grenadine, mixer, endPos
void MOVE(int posIdx){    //position index of X/Y axis
    stepperX.moveTo(x_yAxisPos[0][posIdx]);
    stepperY.moveTo(x_yAxisPos[1][posIdx]);
    //Serial.print(posIdx);
    //Serial.print(", ");
    //Serial.print(x_yAxisPos[0][posIdx]);
    //Serial.println("");
    while((stepperX.distanceToGo() != 0) || (stepperY.distanceToGo() != 0)){    
    stepperX.run();
    stepperY.run();
    //Serial.println("t");
    }
}

void moveHome(){
    stepperX.moveTo(0);
    stepperY.moveTo(0);
    stepperZ.moveTo(0);
    
     while(!digitalRead(x_hom_sens)){
    //Serial.print("X is :"); Serial.println(digitalRead(x_hom_sens));  
    stepperX.run();
    //Serial.print("X is :"); Serial.println(digitalRead(x_hom_sens));  
    }
     while(!digitalRead(y_hom_sens)){   
    //  Serial.print("Y is :"); Serial.println(digitalRead(y_hom_sens)); 
    stepperY.run();
    }
    while(!digitalRead(z_hom_sens)){ 
      digitalWrite(pulZ, HIGH);
      digitalWrite(dirZ, LOW);
      stepCntZ--;
      delayMicroseconds(stepDelay);
      digitalWrite(pulZ, LOW);
      digitalWrite(dirZ, HIGH);
      delayMicroseconds(stepDelay);
    }
}

void mix(){
  Serial.println("11");
  for(int i=0; i<nMixes; i++){    //nMixes=number of mixes
  //change position
  if(Zpos==-85000) Zpos=0;
  else Zpos=-85000;
  Serial.println("j");
  stepperZ.moveTo(Zpos);    //set the Z position
  
  while((stepperZ.distanceToGo() != 0)){    
    stepperZ.run();
    //Serial.println("d");
    }
  }  
  delay(dripDelay);
}

void screwDriver(){
  /*
    2oz. Vodka
    5oz. OJ
  */
  MOVE(homePos);
  Serial.println("SD");
  MOVE(vodka);
  Serial.println("mov vod");
  dispense(sDriver, vodka);
  Serial.println("disp vod");
  MOVE(OJ);
  Serial.println("mov oj");
  dispense(sDriver, OJ);
  Serial.println("disp oj");
  MOVE(mixer);
  Serial.println("mov mix");
  mix();
  Serial.println("mix");
  //MOVE(endPos);
  Serial.println("end");
  moveHome();
  Serial.println("home");
}

void shirleyTemple(){
  /*
    1oz. Vodka
    5oz. Sprite
    .5oz. Grenadine
  */
  Serial.println("13");
  MOVE(vodka);
  dispense(sTemple, vodka);
 
  MOVE(sprite);
  dispense(sTemple, sprite);
  MOVE(grenadine);
  dispense(sTemple, grenadine);
  MOVE(mixer);
  mix();
  //MOVE(endPos);
    moveHome();
    Serial.println("after home");
}

void cubaLibre(){
  /*
    2oz. Light Rum
    4oz. Cola
  */
  Serial.println("14");
  MOVE(rum);
  dispense(cLibre, rum);
  MOVE(coke);
  dispense(cLibre, coke);
  MOVE(mixer);
  mix();
 // MOVE(endPos);
  moveHome();
}

void martini(){
  /*
    2.5oz. Gin
    .5oz. Dry Vermouth
  */
  Serial.println("15");
  MOVE(vodka);
  dispense(mTini, vodka);
  MOVE(gin);
  dispense(mTini, gin);
  MOVE(rum);
  dispense(mTini, rum);
  MOVE(vermouth);
  dispense(mTini, vermouth);
  MOVE(sprite);
  dispense(mTini, sprite);
  MOVE(grenadine);
  dispense(mTini,grenadine );
  MOVE(mixer);
  mix();
  //MOVE(endPos);
  moveHome();
}

void dispense(int d_code, int posIdx){
  if(posIdx == 1) digitalWrite(78, HIGH);
  else digitalWrite(69 + posIdx, HIGH);
  //***bring back in to read flow sensors
  Serial.print("dcode "); Serial.println(d_code);
  Serial.print("posx "); Serial.println(posIdx);
  Serial.print("flow_amt "); Serial.println(flow_amt[d_code-1][posIdx]);
  for(float k=0; pulses<flow_amt[d_code-1][posIdx];){
    if(posIdx == 3){
       k = checkSens(flow3); //check flow
    }
    else{
    k = checkSens(29 + posIdx); //check flow
    }
    Serial.println(k);
  }
  digitalWrite(69 + posIdx, LOW);
  Serial.println("flow");
  pulses = 0;
lastflowratetimer = 0;
cnt = 0;

  delay(dripDelay);
}

float checkSens(int sensor){
 // Serial.println("17");
  uint8_t x = digitalRead(sensor);
  
  if (x == lastflowpinstate) {
    lastflowratetimer++;
    cnt++;
    return 0; // nothing changed!
  }
  
  if (x == LOW) {
    //low to high transition!
    pulses++;
  }
  lastflowpinstate = x;
  flowrate = 1000.0;
  flowrate /= lastflowratetimer;  // in hertz
  lastflowratetimer = 0;
  cnt++;
  
  float liters = pulseToLiters(pulses);

  Serial.print("Liters "); Serial.println(liters);
  Serial.print("Pulses "); Serial.println(pulses);

  return pulses;
}

float pulseToLiters(int pulse){
  return (pulse*.0072) + .0406;
  
}
