int pin = 10;
volatile int state = HIGH;

void setup()
{
  pinMode(pin, OUTPUT);
  attachInterrupt(2, blink, CHANGE);
}

void loop()
{
  digitalWrite(pin, state);
}

void blink()
{
  state = !state;
}
