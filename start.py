import os
import random
import telnetlib
import time
from os.path import expanduser


def nextCoords():
    lines = open('coordinates.txt').read().splitlines()
    location = random.choice(lines)
    coords = location.split()
    return coords

def setRandomLocation():
    telnet = telnetlib.Telnet(host="localhost", port="5554")
    token = open(expanduser("~") + "/.emulator_console_auth_token").read()
    print token
    print telnet.read_until("OK")
    telnet.write("auth %s\n" % token)
    time.sleep(3)
    print telnet.read_eager()
    coords = nextCoords()
    print "geo fix %s %s\n" % (coords[1], coords[0])
    telnet.write("geo fix %s %s\n" % (coords[1], coords[0]))
    telnet.write("exit\n")
    time.sleep(3)
    print telnet.read_eager()
    telnet.close()

os.system("./gradlew app:clearData")
setRandomLocation()

os.system("adb shell am force-stop name.alexy.test.tinderauto")
os.system("adb shell am force-stop name.alexy.test.tinderauto.test")
os.system("adb shell am instrument -w -r   -e debug false -e class name.alexy.test.tinderauto.AppsAuto#play name.alexy.test.tinderauto.test/android.support.test.runner.AndroidJUnitRunner")

setRandomLocation()

os.system("adb shell am instrument -w -r   -e debug false -e class name.alexy.test.tinderauto.AppsAuto#continueAfterPhoneReg name.alexy.test.tinderauto.test/android.support.test.runner.AndroidJUnitRunner")
