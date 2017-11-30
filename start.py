import os
import random
import re
import subprocess
import telnetlib
import time
from os.path import expanduser

if os.name == 'posix':
    from fcntl import fcntl, F_GETFL, F_SETFL
    from os import O_NONBLOCK
    gradleCmd = './gradlew'
else:
    gradleCmd = 'gradlew'

NUMBER_OF_EXECUTIONS = 10
AVD_NAME = "Nexus_5_API_25_google"

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
    print command
    result = os.system(command)
    if (result != 0):
        raise OSError("Execution failed")

def runTest(test):
    print ("Running %s..." % test)
    result = subprocess.check_output(
        "adb shell am instrument -w -r  -e debug false -e class name.alexy.test.tinderauto.AppsAuto#%s name.alexy.test.tinderauto.test/android.support.test.runner.AndroidJUnitRunner" % test,
        shell=True,
        stderr=subprocess.STDOUT)
    print result

    regex = re.compile("java.lang.RuntimeException: (.+)")
    match = regex.search(result)
    if match:
        raise ValueError("Number fetch error: %s" % match.groups(0))

    # if result.find("RuntimeException") >= 0:
    #     raise ValueError("Number fetch error")

    if result.find("No phone number") >= 0:
        raise ValueError("No phone number")

    if result.find("FAILURES!!!") >= 0:
        raise OSError("Execution failed")


def runEmulator(proxy):
    emulatorCommand = "%s/tools/emulator" % os.environ["ANDROID_HOME"]
    print "%s -avd %s -http-proxy http://%s" % (emulatorCommand, AVD_NAME, proxy)
    process = subprocess.Popen("%s -avd %s -http-proxy %s" % (emulatorCommand, AVD_NAME, proxy), shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)

    if os.name == 'posix':
        flags = fcntl(process.stdout, F_GETFL)  # get current p.stdout flags
        fcntl(process.stdout, F_SETFL, flags | O_NONBLOCK)
        runCommand("adb wait-for-device shell 'while [[ -z $(getprop sys.boot_completed) ]]; do sleep 1; done;'")
        while True:
            try:
                out = process.stdout.readline()
                if out == '' and process.poll() != None:
                    break
                if out != '':
                    print (out)
                    if (out.find("Proxy will be ignored") >= 0):
                        return False
            except IOError:
                break
    else:
        runCommand("adb wait-for-device shell \"while [[ -z $(getprop sys.boot_completed) ]]; do sleep 1; done;\"")

    time.sleep(5)
    return True

def runNextProxy():
    try:
        killEmulator()
    except OSError:
        print "No emulator running"

    lines = open('proxy.txt').read().splitlines()
    while (True):
        proxy = random.choice(lines)
        print "Proxy %s" % proxy
        if (runEmulator(proxy)):
            break
        killEmulator()

def killEmulator():
    runCommand("adb emu kill")
    time.sleep(5)

runNextProxy()
print("Installing...")
runCommand("%s installDebug installDebugAndroidTest" % gradleCmd)

execution = 0

while True:
    #execution < NUMBER_OF_EXECUTIONS:
    print("========= Run number %d" % execution)
    print "Run emulator"

    if (execution > 0):
        runNextProxy()

    print("Clear data...")
    runCommand("%s app:clearData" % gradleCmd)
    execution = execution + 1

    print("Set location...")
    setRandomLocation()

    print("Restarting...")
    runCommand("adb shell am force-stop name.alexy.test.tinderauto")
    runCommand("adb shell am force-stop name.alexy.test.tinderauto.test")

    try:
        runTest("play")
    except ValueError as error:
        print error.message
        exit(0)
    except Exception as exception:
        print "Error %s" % exception.message
        continue

    print("Set location...")
    setRandomLocation()

    try:
        runTest("continueAfterPhoneReg")
    except Exception as exception:
        print "Error %s" % exception.message
    finally:
        killEmulator()

print "Finished"