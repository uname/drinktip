#include <SoftwareSerial.h>
#include <Adafruit_NeoPixel.h>
#include <Timer.h>

#define BAUD          9600
#define PIN           5
#define TONE_PIN      6
#define PIXEL_NUM     3
#define TIP_DURATION  10000

#define TEST_CMD     't'
#define DRINK_CMD    'd'
#define MISS_CMD     'm'
#define REST_CMD     'r'
#define DRINK_COLOR  0x0000FF00
#define MISS_COLOR   0x00FF0000
#define REST_COLOR   0x00FF00CC

#define BREATH_IN    1
#define BREATH_OUT   -1

#define SET_COLOR(COLOR) do { \
        for(int i = 0; i < PIXEL_NUM; i++) { \
            pixels.setPixelColor(i, COLOR); \
        } \
        pixels.show(); \
    }while(0)
        
#define PLAY_SOUND() do { \
        for(int i = 0; i < 180; i++) { \
            tone(TONE_PIN, 900 + sin(i * 3.14 / 180) * 1800, 10); \
            delay(2); \
        } \
    }while(0)

SoftwareSerial mySerial(2, 3); // RX, TX
Adafruit_NeoPixel pixels = Adafruit_NeoPixel(PIXEL_NUM, PIN, NEO_GRB + NEO_KHZ800);
Timer timer;
uint32_t color;
int8_t breathTimerId;
int brightness = 0;
int breathType = BREATH_IN;
bool stopBreathFlag = true;

char startCodes[] = {'A', 'P', 'A', 'C', 'H', 'E'};
int index = 0;
bool started = false;

void setup()
{
    Serial.begin(BAUD);
    mySerial.begin(BAUD);
    pixels.begin();
    pixels.show();
}

void breathLight()
{
    pixels.setBrightness(brightness);
    brightness += breathType;
    SET_COLOR(color);
    if(brightness > 255) {
        breathType = BREATH_OUT;
        brightness = 255;
    } else if(brightness < 0) {
        if(stopBreathFlag) {
            timer.stop(breathTimerId);
        }
        breathType = BREATH_IN;
        brightness = 0;
    }
}

void stopBreathLight()
{
    stopBreathFlag = true;   
}

inline void protocolHandler(char cmd)
{
    bool rightCmd = true;
    
    switch(cmd) {
    case DRINK_CMD:
        color = DRINK_COLOR;
        break;
        
    case MISS_CMD:
        color = MISS_COLOR;
        break;
        
    case REST_CMD:
        color = REST_COLOR;
        break;
        
    case TEST_CMD:
        PLAY_SOUND();
        goto End;
        break;
        
    default:
        rightCmd = false;
        break;
    }
    
    if(rightCmd) {
        stopBreathFlag = false;
        breathTimerId = timer.every(5, breathLight);
        timer.after(TIP_DURATION, stopBreathLight);
        PLAY_SOUND();
    }

End:
    // DO NOTHING NOW
    (void)0;
}

inline void serialHandler()
{
    if(mySerial.available() < 1) {
        return;
    }
    
    char ch = mySerial.read();
    if(!started) {
        if(ch == startCodes[index]) {
            if(++index == sizeof(startCodes)) {
                started = true;
                PLAY_SOUND();
            }
        } else {
            index = 0;
        }
    } else {
        if(!stopBreathFlag) {
            while(mySerial.read() > 0);
        }
        protocolHandler(ch);
    }
}

void loop()
{
    serialHandler();
    timer.update();
}




