#!/bin/sh
set -eu
cd "$(dirname "$0")/.."
mkdir -p /tmp/tiktok-end-detector-classes
javac -d /tmp/tiktok-end-detector-classes app/src/main/java/dev/lyonpro/tiktoklandscape/EndDetector.java verification/EndDetectorTest.java
java -cp /tmp/tiktok-end-detector-classes EndDetectorTest
