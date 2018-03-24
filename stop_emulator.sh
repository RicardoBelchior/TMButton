#!/bin/bash

PORT=5566
SERIAL=emulator-${PORT}

#Stop the Android Emulator
echo "Killing the Android Emulator with serial: ${SERIAL}"
adb -s ${SERIAL} emu kill
