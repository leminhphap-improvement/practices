#!/bin/sh
set -eu
cd "$(dirname "$0")/.."
tmp_dir="$(mktemp -d)"
trap 'rm -rf "$tmp_dir"' EXIT
javac -encoding UTF-8 -d "$tmp_dir" \
  app/src/main/java/dev/lyonpro/tiktoklandscape/EndDetector.java \
  app/src/main/java/dev/lyonpro/tiktoklandscape/EpisodeCounter.java \
  app/src/main/java/dev/lyonpro/tiktoklandscape/EpisodePolicy.java \
  app/src/main/java/dev/lyonpro/tiktoklandscape/PlaybackProgress.java \
  verification/EndDetectorTest.java \
  verification/EpisodePolicyTest.java \
  verification/PlaybackProgressTest.java
java -cp "$tmp_dir" EndDetectorTest
java -cp "$tmp_dir" EpisodePolicyTest
java -cp "$tmp_dir" PlaybackProgressTest
