/*
  Blink
  Turns on an LED on for one second, then off for one second, repeatedly.
 
  This example code is in the public domain.
 */
int actTra = 40;
int SDin = 42;
int CLin = 43;
int STin = 44;
int MTin = 45;
int homePin = 46;
int ackRec = 47;
int inCode = 0;

int sol1 = 70;
int sol2 = 71;
int sol3 = 72;
int sol4 = 73;
int sol5 = 74;
int sol6 = 75;
int sol7 = 76;
int sol8 = 77;

void setup() {                
  // initialize the digital pin as an output.
  // Pin 13 has an LED connected on most Arduino boards:
  Serial.begin(9600);
  pinMode(actTra, OUTPUT);  
  pinMode(SDin, INPUT); 
  pinMode(CLin, INPUT); 
  pinMode(STin, INPUT); 
  pinMode(MTin, INPUT); 
  pinMode(homePin, INPUT); 
  pinMode(ackRec, INPUT); 
  pinMode(sol1, OUTPUT);
  pinMode(sol2, OUTPUT);
  pinMode(sol3, OUTPUT);
  pinMode(sol4, OUTPUT);
  pinMode(sol5, OUTPUT);
  pinMode(sol6, OUTPUT);
  pinMode(sol7, OUTPUT);
  pinMode(sol8, OUTPUT);
}

void loop() {
  digitalWrite(sol1, LOW);
  digitalWrite(sol2, LOW);
  digitalWrite(sol3, LOW);
  digitalWrite(sol4, LOW);
  digitalWrite(sol5, LOW);
  digitalWrite(sol6, LOW);
  digitalWrite(sol7, LOW);
  digitalWrite(sol8, LOW);
  
  if(digitalRead(SDin)) inCode = 1;
  else if(digitalRead(CLin)) inCode = 2;
  else if(digitalRead(STin)) inCode = 3;
  else if(digitalRead(MTin)) inCode = 4;
  else if(digitalRead(homePin)) inCode = 5;
  //else if(digitalRead(ackRec)) inCode = 6;
  else {
    inCode = 0;
    Serial.println("waiting");
    digitalWrite(actTra, LOW);
  }
  
  if(inCode > 0)
  {
    Serial.println(inCode);
    digitalWrite(actTra, HIGH);
    pulseSol();
    //execute process
    Serial.println("ACK sent");
  }
}

void pulseSol(){
  digitalWrite(sol2, HIGH);
  delay(1000);
  digitalWrite(sol2, LOW);
  delay(1000);
}
