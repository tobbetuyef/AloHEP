#!/bin/bash

set -e
cd "$(dirname "$0")"

mkdir -p bin
echo "Compiling src/ -> bin/ ..."
find src -name '*.java' -print0 | xargs -0 javac -encoding UTF-8 -cp "gson-2.9.1.jar" -d bin
echo "Build OK. Entry points:"
echo "  GUI : org.hepforge.alohep.Launcher"
echo "  CLI : org.hepforge.alohep.HeadlessLauncher"
