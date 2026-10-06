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

HEADER="NumMacPar,XRes,YRes,ZRes,interp,total_ms,initMPs_ms,updateQ_ms,updateFi_ms,updateForce_ms,collision_ms,pinch_ms,bunchUpdate_ms,L_eff,L_nom,sqrtS,peakMem_bytes"

mkdir -p "$OUTDIR"

echo "=== ALOHEP Asymmetric Rerun ===" >&2
echo "Start: $(date)" >&2

run_sim() {
    local run_id=$1; local extra_args=$2; shift 2
    "$JAVA" $JVM -cp "$CP" org.hepforge.alohep.HeadlessLauncher "$@" -simulation -profile $extra_args
}

warmup() {
    echo "  Warmup run: $@" >&2
    "$JAVA" $JVM -cp "$CP" org.hepforge.alohep.HeadlessLauncher "$@" -simulation -nosimulation > /dev/null 2>&1 || true
}

OUT="$OUTDIR/results_asymmetric.csv"
echo "test,run_id,$HEADER" > "$OUT"

HERA_LEFT="-left proton-ele/HERA"
HERA_RIGHT="-right electron-ring/HERA"
echo "  HERA ep (hourglass + pinch)..." >&2
warmup $HERA_LEFT $HERA_RIGHT -macrop 50000 -xres 50 -yres 50 -zres 50 -pinch
for r in 1 2 3; do
    echo "    run $r..." >&2
    run_sim $r "" $HERA_LEFT $HERA_RIGHT \
        -macrop 50000 -xres 50 -yres 50 -zres 50 -hourglass -pinch \
        | sed "s/^/hera_ep,$r,/" | tee -a "$OUT"
done

EIC_LEFT="-left proton-ele/EIC-PDG1"
EIC_RIGHT="-right electron-ring/EIC-PDG"
echo "  EIC ep (hourglass + pinch)..." >&2
warmup $EIC_LEFT $EIC_RIGHT -macrop 50000 -xres 50 -yres 50 -zres 50 -pinch
for r in 1 2 3; do
    echo "    run $r..." >&2
    run_sim $r "" $EIC_LEFT $EIC_RIGHT \
        -macrop 50000 -xres 50 -yres 50 -zres 50 -hourglass -pinch \
        | sed "s/^/eic_ep,$r,/" | tee -a "$OUT"
done

echo "" >&2
echo "End: $(date)" >&2
echo "Results in $OUTDIR/results_asymmetric.csv" >&2
echo "  6 rows (2 colliders x 3 runs)" >&2
