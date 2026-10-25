#!/bin/bash

set -e
cd "$(dirname "$0")"

RELEASE=""
if javac --help 2>&1 | grep -q -- "--release"; then
    RELEASE="--release 8 -Xlint:-options"
fi

mkdir -p bin
echo "Compiling src/ -> bin/ (Java 8 bytecode) ..."
find src -name '*.java' -print0 | xargs -0 javac $RELEASE -encoding UTF-8 -cp "gson-2.9.1.jar" -d bin
echo "Build OK. Entry points:"
echo "  GUI : org.hepforge.alohep.Launcher"
echo "  CLI : org.hepforge.alohep.HeadlessLauncher"
