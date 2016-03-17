/**********************************************************
This is example code for using the Adafruit liquid flow meters. 

Tested and works great with the Adafruit plastic and brass meters
    ------> http://www.adafruit.com/products/828
    ------> http://www.adafruit.com/products/833

Connect the red wire to +5V, 
the black wire to common ground 
and the yellow sensor wire to pin #2

Adafruit invests time and resources providing this open source code, 
please support Adafruit and open-source hardware by purchasing 
products from Adafruit!

Written by Limor Fried/Ladyada  for Adafruit Industries.  
BSD license, check license.txt for more information
All text above must be included in any redistribution
**********************************************************/

// which pin to use for reading the sensor? can use any pin!
#define FLOWSENSORPIN 30

// count how many pulses!
volatile uint16_t pulses = 0;
// track the state of the pulse pin
volatile uint8_t lastflowpinstate;
// you can try to keep time of how long it is between pulses
volatile uint32_t lastflowratetimer = 0;
// and use that to calculate a flow rate
volatile float flowrate;

float tru_liters = 0;
// Interrupt is called once a millisecond, looks for any pulses from the sensor!
int cnt = 0; //sensor check counter, increment each sensor check, print screen when 100 is reached

void setup() {
   Serial.begin(9600);
   Serial.print("Flow sensor test!");
   
   pinMode(70,OUTPUT);
   
   
  pinMode(71, OUTPUT);
  pinMode(72, OUTPUT);
  pinMode(73, OUTPUT);
  pinMode(74, OUTPUT);
  pinMode(75, OUTPUT);
  pinMode(76, OUTPUT);
  pinMode(77, OUTPUT);
   pinMode(FLOWSENSORPIN, INPUT);
   digitalWrite(FLOWSENSORPIN, HIGH);
   lastflowpinstate = digitalRead(FLOWSENSORPIN);
   ;
}

void loop()                     // run over and over again
{ 
  for(int k =0; k < 1000;k++){
    Serial.println(k);
}
  digitalWrite(70,HIGH);
  while(1){
  while(tru_liters < .1){
  checkSens();
  tru_liters += flowClcPrnt();
  }
  digitalWrite(70,LOW);
  }
}

void checkSens(){
  uint8_t x = digitalRead(FLOWSENSORPIN);
  
  if (x == lastflowpinstate) {
    lastflowratetimer++;
    cnt++;
    return; // nothing changed!
  }
  
  if (x == HIGH) {
    //low to high transition!
    pulses++;
  }
  lastflowpinstate = x;
  flowrate = 1000.0;
  flowrate /= lastflowratetimer;  // in hertz
  lastflowratetimer = 0;
  cnt++;
}

float flowClcPrnt(){
  Serial.print("Freq: "); Serial.println(flowrate);
  Serial.print("Pulses: "); Serial.println(pulses, DEC);

  // if a brass sensor use the following calculation
  float liters = pulses;
  liters /= 7.5;
  liters /= 60.0;

  Serial.print(liters); Serial.println(" Liters");
  cnt = 0;
  return liters;
}
