  const int pul = 43;

void setup() {
  // initialize the screw driver order pin as an input:
  pinMode(pul, INPUT); 
  Serial.begin(9600);
}

void loop(){  
  if(digitalRead(pul)) Serial.println("ON");
  else Serial.println("OFF");
  delay(100);
}

