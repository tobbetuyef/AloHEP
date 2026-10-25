#!/bin/bash
# Sample run of AloHEP: EIC electron-proton bunch crossing with the hourglass effect and the
# beam-beam pinch, 50x50x50 grid, 50000 macroparticles, fixed random seed 1. The output is
# compared line by line with expected_output.txt; the timing and memory lines, which depend on
# the machine, are excluded from the comparison. The run takes 1.5-5 min on a desktop CPU.
#
# Usage:  bash test_run/run_sample.sh
# from the extracted release folder (next to AloHEP.jar and save/) or from a source checkout
# after `bash build.sh` (bin/ and gson-2.9.1.jar). On Windows use Git Bash or WSL.
set -e
HERE="$(cd "$(dirname "$0")" && pwd)"
cd "$HERE/.."
SEP=":"
case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) SEP=";";; esac
if [ -f AloHEP.jar ]; then
    CP="AloHEP.jar"
elif [ -d bin ] && [ -f gson-2.9.1.jar ]; then
    CP=".${SEP}gson-2.9.1.jar${SEP}bin"
else
    echo "Run this script from the AloHEP release folder (AloHEP.jar) or from a built source checkout (bin/)." >&2
    exit 2
fi
OUT="$HERE/last_output.txt"
java -cp "$CP" org.hepforge.alohep.HeadlessLauncher \
  -left proton-ele/EIC-PDG1 -right electron-ring/EIC-PDG \
  -hourglass -pinch -macrop 50000 -xres 50 -yres 50 -zres 50 -seed 1 > "$OUT"
filter() { grep -v -E ' ms$|^Peak memory' "$1"; }
if diff <(filter "$HERE/expected_output.txt") <(filter "$OUT") > "$HERE/diff.txt"; then
    rm -f "$HERE/diff.txt"
    echo "PASS: output matches expected_output.txt (timing and memory lines excluded); full output in $OUT"
else
    echo "FAIL: output differs from expected_output.txt (see $HERE/diff.txt)"
    cat "$HERE/diff.txt"
    exit 1
fi
