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
"$JAVA" -version 2>&1 | head -1 >&2

JVM="-Xms512m -Xmx4g -XX:+UseSerialGC"
OUTDIR="bench_results"

HEADER="NumMacPar,XRes,YRes,ZRes,interp,total_ms,initMPs_ms,updateQ_ms,updateFi_ms,updateForce_ms,collision_ms,pinch_ms,bunchUpdate_ms,L_eff,L_nom,sqrtS,peakMem_bytes"

mkdir -p "$OUTDIR"

echo "=== ALOHEP Asymmetric Benchmark Suite ===" >&2
echo "Start: $(date)" >&2

warmup() {
    echo "  Warmup run: $@" >&2
    "$JAVA" $JVM -cp "$CP" org.hepforge.alohep.HeadlessLauncher "$@" -simulation -nosimulation > /dev/null 2>&1 || true
}

run_sim() {
    local run_id=$1; local extra_args=$2; shift 2
    "$JAVA" $JVM -cp "$CP" org.hepforge.alohep.HeadlessLauncher "$@" -simulation -profile $extra_args
}

EP_LEFT="-left electron-linac/ILC-125"
EP_RIGHT="-right positron-linac/ILC-125"
OUT1="$OUTDIR/results_epinu_sign.csv"
echo "test,run_id,$HEADER" > "$OUT1"
echo "[Set 1] e+e- ILC-125 pinch (cubic + bilinear) — $(date)" >&2
warmup $EP_LEFT $EP_RIGHT -macrop 50000 -xres 50 -yres 50 -zres 50 -pinch

echo "  Cubic, pinch ON..." >&2
for r in 1 2 3; do
    echo "    run $r..." >&2
    run_sim $r "" $EP_LEFT $EP_RIGHT \
        -macrop 50000 -xres 50 -yres 50 -zres 50 -pinch \
        | sed "s/^/epinu_cubic,$r,/" | tee -a "$OUT1"
done

echo "  Bilinear, pinch ON..." >&2
for r in 1 2 3; do
    echo "    run $r..." >&2
    run_sim $r "-linearinterp" $EP_LEFT $EP_RIGHT \
        -macrop 50000 -xres 50 -yres 50 -zres 50 -pinch \
        | sed "s/^/epinu_bilinear,$r,/" | tee -a "$OUT1"
done

OUT2="$OUTDIR/results_asymmetric.csv"
echo "test,run_id,$HEADER" > "$OUT2"
echo "[Set 2] Asymmetric ep colliders (HERA + EIC) — $(date)" >&2

HERA_LEFT="-left proton-ele/HERA"
HERA_RIGHT="-right electron-ring/HERA"
echo "  HERA ep (hourglass + pinch)..." >&2
warmup $HERA_LEFT $HERA_RIGHT -macrop 50000 -xres 50 -yres 50 -zres 50 -pinch
for r in 1 2 3; do
    echo "    run $r..." >&2
    run_sim $r "" $HERA_LEFT $HERA_RIGHT \
        -macrop 50000 -xres 50 -yres 50 -zres 50 -hourglass -pinch \
        | sed "s/^/hera_ep,$r,/" | tee -a "$OUT2"
done

EIC_LEFT="-left proton-ele/EIC-PDG1"
EIC_RIGHT="-right electron-ring/EIC-PDG"
echo "  EIC ep (hourglass + pinch)..." >&2
warmup $EIC_LEFT $EIC_RIGHT -macrop 50000 -xres 50 -yres 50 -zres 50 -pinch
for r in 1 2 3; do
    echo "    run $r..." >&2
    run_sim $r "" $EIC_LEFT $EIC_RIGHT \
        -macrop 50000 -xres 50 -yres 50 -zres 50 -hourglass -pinch \
        | sed "s/^/eic_ep,$r,/" | tee -a "$OUT2"
done

echo "" >&2
echo "End: $(date)" >&2
echo "Results in $OUTDIR/" >&2
echo "  results_epinu_sign.csv   — 6 rows (2 interp x 3 runs)" >&2
echo "  results_asymmetric.csv   — 6 rows (2 colliders x 3 runs)" >&2
