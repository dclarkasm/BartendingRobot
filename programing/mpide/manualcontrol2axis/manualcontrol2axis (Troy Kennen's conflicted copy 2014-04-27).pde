#include <AccelStepper.h>

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
const int fwd_SD = 2;        // Screw Driver order has been placed
const int hom = 3;           // home position button pressed
const int rit_CL = 4;        // Cuba Libre order has been placed
const int rev_ST = 7;        // Shirley Temple order has been placed
const int lft_MT = 8;        // Martini order has been placed
const int manual = 5;
const int y_hom_sens = 30;
const int x_hom_sens = 31;
const int glassSens = 32;

//outputs
const int stepDelay = 350;   // microsecond delay between each pulse
const int dripDelay = 2000;  //time alotted for drink to stop dripping
const int pulX = 9;          //pulse output for X axis
const int dirX = 10;         //direction output for X axis
const int pulY = 11;         //pulse output for Y axis
const int dirY = 12;         //direction output for Y axis
const int vodOut = 22;
const int rumOut = 23;
const int ginOut = 24;
const int verOut = 25;
const int ojOut = 26;
const int cokOut = 27;
const int sprOut = 28;
const int greOut = 29;

typedef enum{homePos, vodka, rum, gin, vermouth, OJ, coke, sprite, grenadine, mixer, endPos}index;
typedef enum{unkn1, unkn2, screwDriver, cubaLibre, shirleyTemple, martini}d_code;
// x/y axis positions:          home, vodka, rum , gin , vermouth, OJ  , coke , sprite, grenadine, mixer, end
const int x_yAxisPos[2][11] = {{0   , -5000  , -5000 , -10000, -10000    , -15000, -15000 , -20000  , -20000     , -30000 , -40000},
                              {0    , 5000  , 10000, 5000 , 10000    , 5000 , 10000 , 5000   , 10000     , 7500  , 7500}}; 
                           //N/U , vodka, rum, gin, vermouth, OJ, coke, sprite, grenadine 
const int flow_amt[4][9] = {{0   , 0    , 0  , 250, 50      , 0 , 0   , 0     , 0        },  //screw driver
                            {0   , 0    , 200, 0  , 0       , 0 , 400 , 0     , 0        },  //Cuba Libre
                            {0   , 100  , 0  , 0  , 0       , 0 , 0   , 500   , 50       },  //Shirley temple
                            {0   , 200  , 0  , 0  , 0       , 500 , 0   , 0   , 0        };  //Martini
AccelStepper stepperX(AccelStepper::FULL2WIRE, pulX, dirX);
AccelStepper stepperY(AccelStepper::FULL2WIRE, pulY, dirY);

int stepCntX = 0;
int stepCntY = 0;
boolean homeStat = false;  //robot home status

void setup() {
  // initialize the screw driver order pin as an input:
  pinMode(fwd_SD, INPUT); 
  pinMode(hom, INPUT);
  pinMode(rit_CL, INPUT);
  pinMode(rev_ST, INPUT);
  pinMode(lft_MT, INPUT);
  pinMode(manual, INPUT);
  pinMode(y_hom_sens, INPUT);
  pinMode(x_hom_sens, INPUT);
  pinMode(glassSens, INPUT);
  
  pinMode(pulX, OUTPUT);
  pinMode(dirX, OUTPUT);
  pinMode(pulY, OUTPUT);
  pinMode(dirY, OUTPUT);
  
  pinMode(vodOut, OUTPUT);
  pinMode(rumOut, OUTPUT);
  pinMode(ginOut, OUTPUT);
  pinMode(verOut, OUTPUT);
  pinMode(ojOut, OUTPUT);
  pinMode(cokOut, OUTPUT);
  pinMode(sprOut, OUTPUT);
  pinMode(greOut, OUTPUT);
  
  stepperX.setMaxSpeed(15000.0);
  stepperX.setAcceleration(100000.0);
  stepperX.moveTo(0); 
  
  stepperY.setMaxSpeed(15000.0);
  stepperY.setAcceleration(100000.0);
  stepperY.moveTo(0); 
}

void loop(){  
  
  while(digitalRead(manual)){        //manual control
    if(digitalRead(fwd_SD)) forward();
    if(digitalRead(rev_ST)) reverse();
    if(digitalRead(rit_CL)) right();
    if(digitalRead(lft_MT)) left();
    if(digitalRead(hom)) HOME();
  }
  
    while(!digitalRead(manual) && homeStat){      //normal opperation
    homeStat = false;
    if(digitalRead(fwd_SD)) screwDriver();
    if(digitalRead(rev_ST)) shirleyTemple();
    if(digitalRead(rit_CL)) cubaLibre();
    if(digitalRead(lft_MT)) martini();
  }
}

void forward(){
  while(digitalRead(fwd_SD) && digitalRead(manual)) { 
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
    while(digitalRead(rev_ST) && digitalRead(manual)) {
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
  while(digitalRead(rit_CL) && digitalRead(manual)) { 
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
    while(digitalRead(lft_MT) && digitalRead(manual)) {
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
   while(!digitalRead(y_hom_sens)) {
      digitalWrite(pulY, HIGH);
      digitalWrite(dirY, LOW);
      stepCntY--;
      delayMicroseconds(stepDelay);
      digitalWrite(pulY, LOW);
      digitalWrite(dirY, HIGH);
      delayMicroseconds(stepDelay);
   } 
   
  while(!digitalRead(x_hom_sens)) {
      digitalWrite(pulX, HIGH);
      digitalWrite(dirX, LOW);
      stepCntX--;
      delayMicroseconds(stepDelay);
      digitalWrite(pulX, LOW);
      digitalWrite(dirX, HIGH);
      delayMicroseconds(stepDelay);
   }
   homeStat = true;
}

//x/y axis positions:    homePos, vodka, rum, gin, vermouth, OJ , coke, sprite, grenadine, mixer, endPos
void MOVE(int posIdx){    //position index of X/Y axis
    stepperX.moveTo(x_yAxisPos[0][posIdx]);
    stepperY.moveTo(x_yAxisPos[1][posIdx]);

    while((stepperX.distanceToGo() != 0) || (stepperY.distanceToGo() != 0)){    
    stepperX.run();
    stepperY.run();
    }
}

void screwDriver(){
  /*
    2oz. Vodka
    5oz. OJ
  */
  MOVE(vodka);
  dispence(screwDriver, vodka);
  MOVE(OJ);
  dispence(screwDriver, OJ);
  MOVE(mixer);
  delay(dripDelay);
  MOVE(endPos);
  delay(dripDelay);
  MOVE(homePos);
}

void shirleyTemple(){
  /*
    1oz. Vodka
    5oz. Sprite
    .5oz. Grenadine
  */
  MOVE(vodka);
  dispence(shirleyTemple, vodka);
  MOVE(sprite);
  dispence(shirleyTemple, sprite);
  MOVE(grenadine);
  dispence(shirleyTemple, grenadine);
  MOVE(mixer);
  delay(dripDelay);
  MOVE(endPos);
  delay(dripDelay);
  MOVE(homePos);
}

void cubaLibre(){
  /*
    2oz. Light Rum
    4oz. Cola
  */
  MOVE(rum);
  dispence(cubaLibre, rum);
  MOVE(coke);
  dispence(cubaLibre, coke);
  MOVE(mixer);
  delay(dripDelay);
  MOVE(endPos);
  delay(dripDelay);
  MOVE(homePos);
}

void martini(){
  /*
    2.5oz. Gin
    .5oz. Dry Vermouth
  */
  MOVE(gin);
  dispence(marini, gin);
  MOVE(vermouth);
  dispence(marini, vermouth);
  MOVE(mixer);
  delay(dripDelay);
  MOVE(endPos);
  delay(dripDelay);
  MOVE(homePos);
}

void dispense(int d_code, int posIdx){
  for(d_vol=0; d_vol<flow_amt[d_code][posIdx]; d_vol++){
    //dispense
    //add dlow meter code here
  }
  delay(dripDelay);
}


