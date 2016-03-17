int pul 3;
int dir 5;


void setup() {
  // initialize the screw driver order pin as an input:
  pinMode(pul, OUTPUT); 
  pinMode(dir, OUTPUT); 
}

void loop(){  
    while(1) { 
      digitalWrite(pul, HIGH);
      digitalWrite(dir, HIGH);
      delayMicroseconds(500);
      digitalWrite(pul, LOW);
      digitalWrite(dir, LOW);
      delayMicroseconds(500);
    }
  }

