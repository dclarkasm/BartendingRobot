/*
void setup() {                
  // initialize the digital pin as an output.
  // Pin 13 has an LED connected on most Arduino boards:
  pinMode(9, OUTPUT);     
}

void loop() {
  digitalWrite(9, HIGH);   // set the LED on
  delay(1000);              // wait for a second
  digitalWrite(9, LOW);    // set the LED off
  delay(1000);              // wait for a second
}
*/



// constants won't change. They're used here to 
// set pin numbers:
const int orderSD = 2;     // Screw Driver order has been placed
const int orderCL = 4;     // Cuba Libre order has been placed
const int orderST = 7;     // Shirley Temple order has been placed
const int orderMT = 8;     // Martini order has been placed

const int outSD = 9;
const int outCL = 10;
const int outST = 11;
const int outMT = 12;

int countSD = 0;      // the count of Screw Driver orders
int countCL = 0;      // the count of Cuba Libre orders
int countST = 0;      // the count of Shirley Temple orders
int countMT = 0;      // the count of Martini orders

int crntSD=0, crntCL=0, crntST=0, crntMT=0, newSD=0, newCL=0, newST=0, newMT=0;

void setup() {
  Serial.begin(9600);
  // initialize the screw driver order pin as an input:
  pinMode(2, INPUT); 
  pinMode(4, INPUT);
  pinMode(7, INPUT);
  pinMode(8, INPUT);
  
  pinMode(9, OUTPUT);
  pinMode(10, OUTPUT);
  pinMode(11, OUTPUT);
  pinMode(12, OUTPUT);
}

void loop(){
  // read the state of the pushbutton value:
  newSD = digitalRead(2);
  newCL = digitalRead(4);
  newST = digitalRead(7);
  newMT = digitalRead(8);

  // check if the pushbutton is pressed.
  // if it is, the buttonState is HIGH:
  if ((newSD==HIGH) && (crntSD==LOW)) {    
     countSD++;
     crntSD = true;
     digitalWrite(9, HIGH);
  }
  else if ((newSD==LOW) && (crntSD==HIGH)) { 
     crntSD = false;
     digitalWrite(9, LOW);
  }
  
  if ((newCL==HIGH) && (crntCL==LOW)) {    
     countCL++;
     crntCL = true;
     digitalWrite(10, HIGH);
  } 
  else if ((newCL==LOW) && (crntCL==HIGH)) { 
     crntCL = false;
     digitalWrite(10, LOW);
  }
  
  if ((newST==HIGH) && (crntST==LOW)) {    
     countST++;
     crntST = true;
     digitalWrite(11, HIGH);
  } 
  else if ((newST==LOW) && (crntST==HIGH)) { 
     crntST = false;
     digitalWrite(11, LOW);
  }
  
  if ((newMT==HIGH) && (crntMT==LOW)) {    
     countMT++;
     crntMT = true;
     digitalWrite(12, HIGH);
  } 
  else if ((newMT==LOW) && (crntMT==HIGH)) { 
     crntMT = false;
     digitalWrite(12, LOW);
  }
  
  Serial.print("SD: ");
  Serial.print(newSD);
  Serial.print(", CL: ");
  Serial.print(newCL);
  Serial.print(", ST: ");
  Serial.print(newST);
  Serial.print(", MT: ");
  Serial.print(newMT);
  Serial.println();
  delay(250);
}

