#include <SoftwareSerial.h>
#include <Adafruit_NeoPixel.h>

#define BAUD          9600
#define PIN           5
#define TONE_PIN      6
#define PIXEL_NUM     3
#define TIP_DURATION  10000

#define DRINK_CMD    'd'
#define MISS_CMD     'm'
#define REST_CMD     'r'
#define DRINK_COLOR  0x0000FF00
#define MISS_COLOR   0x00FF0000
#define REST_COLOR   0x00FF00CC

SoftwareSerial mySerial(2, 3); // RX, TX
Adafruit_NeoPixel pixels = Adafruit_NeoPixel(PIXEL_NUM, PIN, NEO_GRB + NEO_KHZ800);
uint32_t lastTime = 0;

void setColor(uint32_t color)
{
    pixels.setBrightness(255);
    for(int i = 0; i < PIXEL_NUM; i++) {
        pixels.setPixelColor(i, color);
    }
    pixels.show();
}

void setup()
{
    Serial.begin(BAUD);
    mySerial.begin(BAUD);
    pixels.begin();
    pixels.show();
}

inline void playSound()
{
    for(int i = 0; i < 180; i++) {
        tone(TONE_PIN, 900 + sin(i * 3.1412 / 180) * 1800, 10);
        delay(2);
    }
}

inline void protocolHandler(char cmd)
{
    bool rightCmd = true;
    
    switch(cmd) {
    case DRINK_CMD:
        setColor(DRINK_COLOR);
        break;
        
    case MISS_CMD:
        setColor(MISS_COLOR);
        break;
        
    case REST_CMD:
        setColor(REST_COLOR);
        break;
        
    default:
        rightCmd = false;
        break;
    }
    
    if(rightCmd) {
        playSound();
    }
}

inline void serialHandler()
{
    if(Serial.available() < 1) {
        return;
    }
    
    char cmd = Serial.read();
    lastTime = millis();
    protocolHandler(cmd);
}

void loop()
{
    uint32_t t = millis();
    if(lastTime != 0 &&  t - lastTime > TIP_DURATION) {
        pixels.setBrightness(0);
        pixels.show();
        lastTime = t;
    }
    serialHandler();
}
