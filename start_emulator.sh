#!/bin/bash

AVD_NAME="integration-tests"
AVD_DEVICE_ID="Nexus 5X"
PORT=5566

##############################################################################

#Calculate the Serial Number of the emulator instance
SERIAL=emulator-${PORT}

echo "Creating (forceful) AVD with name ${AVD_NAME}"
# We have to echo "no" because it will ask us if we want to use a custom hardware profile, and we don't.

echo "no" | $ANDROID_HOME/tools/bin/avdmanager create avd \
    -f \
    -n "${AVD_NAME}" \
    -k 'system-images;android-25;google_apis;x86_64' \
    -d "${AVD_DEVICE_ID}"
echo "AVD ${AVD_NAME} created."

#Start the Android Emulator
#"2>&1" combines stderr and stdout into the stdout stream
START_EMULATOR="$ANDROID_HOME/tools/emulator \
    -avd ${AVD_NAME} \
    -netspeed full \
    -gpu on \
    -netdelay none \
    -no-boot-anim -no-audio -no-window \
    -port ${PORT}"

echo $START_EMULATOR
$START_EMULATOR 2>&1 &

#Ensure Android Emulator has booted successfully before continuing
EMU_BOOTED='unknown'
while [[ ${EMU_BOOTED} != *"stopped"* ]]; do
    sleep 5
    EMU_BOOTED=`adb -s ${SERIAL} shell getprop init.svc.bootanim || echo unknown`
done

duration=$(( SECONDS - start ))
echo "Android Emulator started after $duration seconds."

sleep 6 # PackageManager needs some time to init
adb shell settings put global window_animation_scale 0
adb shell settings put global transition_animation_scale 0
adb shell settings put global animator_duration_scale 0
adb shell input keyevent 82
echo "Disabled animations for easier testing. You're good to go."