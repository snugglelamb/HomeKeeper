#define trigPin 13
#define echoPin 12
#define alarm 2
#define tempin A0
#define lightPin A11

int incomingByte = 0;  

void setup() {
  // put your setup code here, to run once:
  Serial.begin (9600);
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
  pinMode(tempin,INPUT);
  pinMode(lightPin,INPUT);
  pinMode(alarm,OUTPUT);
   digitalWrite(alarm, LOW); 
}

void loop() {
  // read sensors 
 
  readtemp();
  readlight();
  readdistance();
  
 
  delay(5000);
  
}

void readlight(){
  Serial.print("light: ");
  Serial.print(analogRead(lightPin));
  Serial.println(" lux");
}

void readtemp(){
   int rawvoltage= analogRead(A0);
   // float millivolts= (rawvoltage/1024.0) *  5000;
   float fahrenheit= rawvoltage*4.88/10;//millivolts/10;
   //Serial.print(fahrenheit);
   //Serial.println(" degrees Fahrenheit, ");

   float celsius= (fahrenheit - 32) *0.5555-30;

   Serial.print ("temperature: ");
   Serial.print(celsius);
   Serial.println( " degrees Celsius " );

}

void readdistance(){
  long duration, distance;
  long duration1, distance1;
 
  //Create a pulse to trigPin
  digitalWrite(trigPin, LOW);            
  delayMicroseconds(2);                  
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);               
  digitalWrite(trigPin, LOW);
                                
  duration = pulseIn(echoPin, HIGH);
  distance = (duration/2) / 29.1;
  
  //Create another pulse to trigPin
  digitalWrite(trigPin, LOW);            
  delayMicroseconds(2);                  
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);               
  digitalWrite(trigPin, LOW);
                                 
  duration1 = pulseIn(echoPin, HIGH);
  distance1 = (duration/2) / 29.1;

  distance = (distance1+distance)/2;
  Serial.print("distance: ");
  Serial.print(distance);
  Serial.println(" cm");

}

