#!/bin/bash

set -e

if command -v java &>/dev/null; then
    JAVA="java"
    CP=".:gson-2.9.1.jar:bin"
else
    WINJAVA="/mnt/c/Program Files/Microsoft/jdk-17.0.19.10-hotspot/bin/java.exe"
    if [ -f "$WINJAVA" ]; then
        JAVA="$WINJAVA"
        REPO="$(cd "$(dirname "$0")" && pwd)"
        CPWIN=$(echo "$REPO" | sed 's|^/mnt/\([a-z]\)/|\1:\\|' | sed 's|/|\\|g')
        CP="${CPWIN}\\gson-2.9.1.jar;${CPWIN}\\bin"
    else
        echo "ERROR: java not found on PATH or at $WINJAVA" >&2
        exit 1
    fi
fi
echo "Using Java: $JAVA" >&2

JVM="-Xms512m -Xmx4g -XX:+UseSerialGC"
OUTDIR="bench_results"
mkdir -p "$OUTDIR"
OUT="$OUTDIR/results_crossing.csv"

HEADER="NumMacPar,XRes,YRes,ZRes,interp,total_ms,initMPs_ms,updateQ_ms,updateFi_ms,updateForce_ms,collision_ms,pinch_ms,bunchUpdate_ms,L_eff,L_nom,sqrtS,peakMem_bytes"
echo "test,run_id,$HEADER,theta" > "$OUT"

ILC_LEFT="-left electron-linac/ILC-125"
ILC_RIGHT="-right positron-linac/ILC-125"

echo "=== ALOHEP Crossing-angle Verification ===" >&2
echo "Start: $(date)" >&2

for TH in 0 5e-6 1e-5 2e-5 3e-5 5e-5 7e-5 1e-4; do
    echo "  theta=$TH ..." >&2
    for r in 1 2 3; do
        "$JAVA" $JVM -cp "$CP" org.hepforge.alohep.HeadlessLauncher \
            $ILC_LEFT $ILC_RIGHT -macrop 50000 -xres 50 -yres 50 -zres 50 \
            -crossing "$TH" -simulation -profile \
            | tr -d '\r' | awk -F, 'NF>=17' | awk -v pre="crossing,$r," -v suf=",$TH" '{print pre $0 suf}' | tee -a "$OUT"
    done
done

echo "" >&2
echo "End: $(date)" >&2
echo "Results in $OUT" >&2
echo "Figure: python3 generate_plots.py   (produces media/fig_crossing.png)" >&2
