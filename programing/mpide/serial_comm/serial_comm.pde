/* Simple Serial ECHO script : Written by ScottC 03/07/2012 */

/* Use a variable called byteRead to temporarily store
   the data coming from the computer */
byte byteRead;
byte byteWrite = 4;

void setup() {                
// Turn the Serial Protocol ON
  //Serial.begin(9600);
  Serial1.begin(9600);
  pinMode(13, OUTPUT);
}

void loop() {
  readSerial();
  //writeSerial();
}

void readSerial(){
   /*  check if data has been sent from the computer: */
  if (Serial1.available()) {
    /* read the most recent byte */
    byteRead = Serial1.read();
    /*ECHO the value that was read, back to the serial port. */
    //Serial1.write(byteRead);
  }
  
  if(byteRead == (byte)2){
    digitalWrite(13, HIGH);
  }
}


void writeSerial(){
  if (Serial1.available()) {
  Serial1.write(byteWrite);
  }
  //myInt++;
  //byteWrite = myInt;
  delay(100);
}

