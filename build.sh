#!/usr/bin/env bash
# Builds the mod into a .jar  (Linux / macOS / Git Bash)
set -e
cd "$(dirname "$0")"

# 1) Java 17 check
if ! command -v java >/dev/null; then echo "ERROR: Java 17 is not installed."; exit 1; fi
JV=$(java -version 2>&1 | head -n1 | sed -E 's/.*"([0-9]+).*/\1/')
if [ "$JV" != "17" ]; then echo "WARNING: Java $JV detected, Forge 1.20.1 needs Java 17."; fi

# 2) Gradle wrapper (created automatically if missing)
if [ ! -f gradlew ]; then
  if command -v gradle >/dev/null; then
    echo ">> No wrapper found, generating one with Gradle 8.1.1..."
    gradle wrapper --gradle-version 8.1.1
  else
    echo "ERROR: no ./gradlew and no 'gradle' installed. Install Gradle or copy the wrapper from the Forge 1.20.1 MDK."
    exit 1
  fi
fi
chmod +x gradlew

# 3) Build
./gradlew clean build

echo
echo "Done. Your jar is here:"
ls -1 build/libs/*.jar
echo "Copy it to your .minecraft/mods folder (with Forge 1.20.1 + Refurbished Furniture)."
