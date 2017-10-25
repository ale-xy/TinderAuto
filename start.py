import os
import random
import subprocess
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

def runCommand(command):
    result = os.system(command)
    if (result != 0):
        raise OSError("Execution failed")

print("Installing...")
runCommand("gradlew installDebug installDebugAndroidTest")
print("Clear data...")
runCommand("gradlew app:clearData")

print("Set location...")
setRandomLocation()

print("Restarting...")
runCommand("adb shell am force-stop name.alexy.test.tinderauto")
runCommand("adb shell am force-stop name.alexy.test.tinderauto.test")

print("Runing scenario step 1...")


def runTest(test):
    print ("Running %s..." % test)
    result = subprocess.check_output(
        "adb shell am instrument -w -r  -e debug false -e class name.alexy.test.tinderauto.AppsAuto#%s name.alexy.test.tinderauto.test/android.support.test.runner.AndroidJUnitRunner" % test,
        shell=True,
        stderr=subprocess.STDOUT)
    print result
    if result.find("FAILURES!!!") >= 0:
        raise OSError("Execution failed")


runTest("play")

print("Set location...")
setRandomLocation()

runTest("continueAfterPhoneReg")