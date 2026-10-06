#!/bin/bash

set -e

if command -v java &>/dev/null; then
    JAVA="java"
    case "$(uname -s)" in
        MINGW*|MSYS*|CYGWIN*) SEP=";" ;;
        *) SEP=":" ;;
    esac
    CP=".${SEP}gson-2.9.1.jar${SEP}bin"
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
"$JAVA" -version 2>&1 | head -1 >&2

JVM="-Xms512m -Xmx4g -XX:+UseSerialGC"
OUTDIR="bench_results"
mkdir -p "$OUTDIR"
OUT="$OUTDIR/results_hourglass.csv"

HEADER="NumMacPar,XRes,YRes,ZRes,interp,total_ms,initMPs_ms,updateQ_ms,updateFi_ms,updateForce_ms,collision_ms,pinch_ms,bunchUpdate_ms,L_eff,L_nom,sqrtS,peakMem_bytes"
echo "test,run_id,$HEADER,A" > "$OUT"

ILC_LEFT="-left electron-linac/ILC-125"
ILC_RIGHT="-right positron-linac/ILC-125"
BUNLEN="3.0e-4"

echo "=== ALOHEP Hourglass Verification ===" >&2
echo "Start: $(date)" >&2

for A in 0.1 0.3 0.5 0.75 1.0 1.5 2.0 3.0 5.0; do
    BETAY=$(awk -v bl="$BUNLEN" -v a="$A" 'BEGIN{printf "%.6e", bl/a}')
    echo "  A=$A  (beta*_y=$BETAY) ..." >&2
    for r in 1 2 3; do
        "$JAVA" $JVM -cp "$CP" org.hepforge.alohep.HeadlessLauncher \
            $ILC_LEFT $ILC_RIGHT -macrop 50000 -xres 50 -yres 100 -zres 50 \
            -hourglass -nocrossing -betay "$BETAY" -simulation -profile \
            | tr -d '\r' | grep '^[0-9]' | awk -v pre="hourglass,$r," -v suf=",$A" '{print pre $0 suf}' | tee -a "$OUT"
    done
done

echo "" >&2
echo "End: $(date)" >&2
echo "Results in $OUT" >&2
echo "Figure: python3 generate_plots.py   (produces media/fig_hourglass.png)" >&2
