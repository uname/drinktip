#include <SoftwareSerial.h>
#include <Adafruit_NeoPixel.h>

#define BAUD       9600
#define PIN        5
#define PIXEL_NUM  3

#define DRINK_CMD    'd'
#define MISS_CMD     'm'
#define REST_CMD     'r'
#define DRINK_COLOR  0x0000FF00
#define MISS_COLOR   0x00FF0000
#define REST_COLOR   0x00FF00CC

SoftwareSerial mySerial(2, 3); // RX, TX
Adafruit_NeoPixel pixels = Adafruit_NeoPixel(PIXEL_NUM, PIN, NEO_GRB + NEO_KHZ800);

void setColor(uint32_t color)
{
    for(int i = 0; i < PIXEL_NUM; i++) {
        pixels.setPixelColor(i, color);
    }
    pixels.show();
}

void setup()
{
    mySerial.begin(BAUD);
    pixels.begin();
    pixels.show();
}

void loop()
{
    if(mySerial.available() > 0) {
        char cmd = mySerial.read();
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
            break;
        }
    }
}
