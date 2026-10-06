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
OUT="$OUTDIR/results_heavyion.csv"

HEADER="NumMacPar,XRes,YRes,ZRes,interp,total_ms,initMPs_ms,updateQ_ms,updateFi_ms,updateForce_ms,collision_ms,pinch_ms,bunchUpdate_ms,L_eff,L_nom,sqrtS,peakMem_bytes"
echo "test,run_id,$HEADER" > "$OUT"

echo "=== ALOHEP Heavy-ion Verification ===" >&2
echo "Start: $(date)" >&2

run_set() {
    local label="$1" left="$2" right="$3" extra="$4"
    echo "  $label ($extra) ..." >&2
    for r in 1 2 3; do
        "$JAVA" $JVM -cp "$CP" org.hepforge.alohep.HeadlessLauncher \
            -left "$left" -right "$right" -macrop 50000 -xres 50 -yres 50 -zres 50 \
            $extra -simulation -profile \
            | tr -d '\r' | awk -F, 'NF>=17' | awk -v pre="$label,$r," '{print pre $0}' | tee -a "$OUT"
    done
}

run_set "pbpb_lhc_geom"  "Pb/LHC"  "Pb/LHC"  ""
run_set "pbpb_lhc_pinch" "Pb/LHC"  "Pb/LHC"  "-pinch"
run_set "auau_rhic_geom" "Au/RHIC" "Au/RHIC" ""

echo "" >&2
echo "End: $(date)" >&2
echo "Results in $OUT" >&2
