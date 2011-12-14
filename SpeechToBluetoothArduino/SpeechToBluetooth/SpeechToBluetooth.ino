  /*
    Receives robot commands from your phone
    After it gets a message the led will blink
    and a digital signal will be sent to the
    HandyBoard for 100ms.
  */
  
  int onboardLed = 13;
  int counter = 0;
  int incomingByte;
  /*
   * This method is called constantly.
   * note: flag is in this case 'A' and numOfValues is 0 (since test event doesn't send any data)
   */
  
  // digital output pins to represent bit0, bit1, and bit2 of input to Handy Board
  // These output pins go to pins 10, 11, and 12 on handy board
  int bit0 = 2;   
  int bit1 = 3;
  int bit2 = 4;
  
  // blink LED (labeled L) for a certain time
  void flushLed(int time)
  {
    digitalWrite(onboardLed, HIGH);
    delay(time);
    digitalWrite(onboardLed, LOW);
    delay(time);
  }
  
  void setup()  
  {
    // use the baud rate your bluetooth module is configured to 
    // ours we programmed to work in 115,200 baud, because the
    // ardruino only works with 57k or 115k bauds
    Serial.begin(115200); 
    
    // on board led
    pinMode(onboardLed, OUTPUT);
    
      // set all color leds as output pins
    pinMode(bit0, OUTPUT);
    pinMode(bit1, OUTPUT);
    pinMode(bit2, OUTPUT);
    
    // just set all leds to high so that we see they are working well
    digitalWrite(bit0, HIGH);
    digitalWrite(bit1, HIGH);
    digitalWrite(bit2, HIGH);
    
    // start up program LED flash
    flushLed(1000);
  }
  
  void loop()
  {
     // see if there's incoming serial data:
    if (Serial.available() > 0) {
  
      // read the oldest byte in the serial buffer:
      incomingByte = Serial.read();
  
      // if it's a capital A, we want to do the Action Command
      if (incomingByte == 'A') {
        // Quickly flash the LED to know we received a command
        flushLed(10);
        
        if (Serial.available() > 0) {
          
            // read the oldest byte in the serial buffer:
          incomingByte = Serial.read();
          
          // Drive will actually be 110 on Handy Board becuase it 
          // inverts digital ins
          if( incomingByte == 'D' ) {
              digitalWrite(bit0, LOW);
              digitalWrite(bit1, LOW);
              digitalWrite(bit2, HIGH);
          }
          // Left (101 on Handy)
          else if( incomingByte == 'L' ) {
              digitalWrite(bit0, LOW);
              digitalWrite(bit1, HIGH);
              digitalWrite(bit2, LOW);
          }
          // Right ( 100 on Handy )
          else if( incomingByte == 'R' ) {
              digitalWrite(bit0, LOW);
              digitalWrite(bit1, HIGH);
              digitalWrite(bit2, HIGH);
          }
          // Stop (011 on Handy)
          else if( incomingByte == 'S' ) {
              digitalWrite(bit0, HIGH);
              digitalWrite(bit1, LOW);
              digitalWrite(bit2, LOW);
          }
          // Speak (010 on Handy)
          else if( incomingByte == 'T' ) {
              digitalWrite(bit0, HIGH);
              digitalWrite(bit1, LOW);
              digitalWrite(bit2, HIGH);
          }
          // reverse (001 on H
          else if( incomingByte == 'E' ) {
              digitalWrite(bit0, HIGH);
              digitalWrite(bit1, HIGH);
              digitalWrite(bit2, LOW);
          }
      }
      
      // allow the handy board 100 ms to read the signal, then change it to null (111) 
      delay(100);
      digitalWrite(bit0, HIGH);
      digitalWrite(bit1, HIGH);
      digitalWrite(bit2, HIGH);
      }
    }
  }
  
  

