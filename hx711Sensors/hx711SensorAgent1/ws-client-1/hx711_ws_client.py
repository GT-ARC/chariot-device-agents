import websocket
import thread
import time
import sys
import json
from datetime import datetime

EMULATE_HX711 = False
if not EMULATE_HX711:
    import RPi.GPIO as GPIO
    from hx711 import HX711
else:
    from emulated_hx711 import HX711

port = sys.argv[1]



def clean_and_exit():
    print "Cleaning..."

    if not EMULATE_HX711:
        GPIO.cleanup()

    print "Bye!"
    sys.exit()


def on_message(ws, message):
    print message


def on_error(ws, error):
    print error


def on_close(ws):
    print "### closed ###"


def on_open(ws):

    def run(*args):
        hx = HX711(5, 6)

        # I've found out that, for some reason, the order of the bytes is not always the same between versions of python, numpy and the hx711 itself.
        # Still need to figure out why does it change.
        # If you're experiencing super random values, change these values to MSB or LSB until to get more stable values.
        # There is some code below to debug and log the order of the bits and the bytes.
        # The first parameter is the order in which the bytes are used to build the "long" value.
        # The second paramter is the order of the bits inside each byte.
        # According to the HX711 Datasheet, the second parameter is MSB so you shouldn't need to modify it.
        hx.set_reading_format("MSB", "MSB")

        # HOW TO CALCULATE THE REFFERENCE UNIT
        # To set the reference unit to 1. Put 1kg on your sensor or anything you have and know exactly how much it weights.
        # In this case, 92 is 1 gram because, with 1 as a reference unit I got numbers near 0 without any weight
        # and I got numbers around 184000 when I added 2kg. So, according to the rule of thirds:
        # If 2000 grams is 184000 then 1000 grams is 184000 / 2000 = 92.
        # hx.set_reference_unit(-800)
        hx.set_reference_unit(308)

        hx.reset()

        hx.tare()

        print "Tare done! Add weight now..."

        # to use both channels, you'll need to tare them both
        # hx.tare_A()
        # hx.tare_B()

        num_of_objects = 0
        last_value = 0
        while True:
            try:
                # These three lines are usefull to debug wether to use MSB or LSB in the reading formats
                # for the first parameter of "hx.set_reading_format("LSB", "MSB")".
                # Comment the two lines "val = hx.get_weight(5)" and "print val" and uncomment these three lines to see what it prints.

                # np_arr8_string = hx.get_np_arr8_string()
                # binary_string = hx.get_binary_string()
                # print binary_string + " " + np_arr8_string

                # Prints the weight. Comment if you're debbuging the MSB and LSB issue.
                val = hx.get_weight(5)

                if val > last_value:
                    if 30 <= abs(val - last_value) <= 75:
                        # print "Difference: %d" % (val - last_value)
                        num_of_objects += 1
                        last_value = val
                elif last_value > val:
                    if 30 <= abs(last_value - val):
                        num_of_objects = max(0, num_of_objects - 1)
                        last_value = val

                print("Sensors values: %s" % val)
                print("Number of objects: %s" % num_of_objects)
                # To get weight from both channels (if you have load cells hooked up
                # to both channel A and B), do something like this
                # val_A = hx.get_weight_A(5)
                # val_B = hx.get_weight_B(5)
                # print "A: %s  B: %s" % ( val_A, val_B )

                hx.power_down()
                hx.power_up()

                jsonMessageAsDict = {
	                	"header": {
	                		"url" : "ws://localhost:" + port + "/hx711sensor1",  
			                "timeSent": datetime.now().strftime('%Y-%m-%d %H:%M:%S'), 
			                "messageType" : "de.gtarc.chariot.hx711sensoragent.payload.PayloadHx711Sensor"
			            }, 
	                	"payload": {
							"objectType": "Sensor",
							"uuid": "61bff476",
							"id": "hx711-sensor",
							"name": "hx711-sensor",
							"securitykey": "",
							"groupId": "",
							"reId": "",
							"ip": "",
							"manufacturer": "Adafruit",
							"location": {
							  "idenfitifier": "",
							  "type": "",
							  "name": "",
							  "level": "Number",
							  "poisition": {
							    "lat": "Number",
							    "lng": "Number"
							  },
							  "indoorposition": {
							    "indoorlat": "Number",
							    "indoorlng": "Number"
							  }
							},
							"properties": [
							  {
							    "timestamp": "",
							    "key": "weight",
							    "type": "Number",
							    "value": val,
							    "unit": "gram",
							    "writable": "false"
							  },
							  {
							    "timestamp": "",
							    "key": "numOfObjects",
							    "type": "Number",
							    "value": num_of_objects,
							    "unit": "",
							    "writable": "false"
							  }
							]
							} 
                	}
                jsonMessage = json.dumps(jsonMessageAsDict)

                ws.send(jsonMessage)
                time.sleep(0.1)

            except (KeyboardInterrupt, SystemExit):
                clean_and_exit()
        # time.sleep(1)
    #     ws.close()
    thread.start_new_thread(run, ())


if __name__ == "__main__":
    websocket.enableTrace(True)
    ws = websocket.WebSocketApp("ws://localhost:" + port + "/hx711sensor1",
                                on_message=on_message,
                                on_error=on_error,
                                on_close=on_close)
    ws.on_open = on_open
    ws.run_forever()
