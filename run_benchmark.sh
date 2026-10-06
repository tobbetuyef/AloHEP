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

ILC_LEFT="-left electron-linac/ILC-125"
ILC_RIGHT="-right positron-linac/ILC-125"
ILC_RIGHT_EE="-right electron-linac/ILC-125"
LHC_LEFT="-left proton/LHC"
LHC_RIGHT="-right proton/LHC"

HEADER="NumMacPar,XRes,YRes,ZRes,interp,total_ms,initMPs_ms,updateQ_ms,updateFi_ms,updateForce_ms,collision_ms,pinch_ms,bunchUpdate_ms,L_eff,L_nom,sqrtS,peakMem_bytes"

mkdir -p "$OUTDIR"

echo "=== ALOHEP Benchmark Suite ===" >&2
echo "Start: $(date)" >&2

run_sim() {
    local run_id=$1; local extra_args=$2; shift 2
    "$JAVA" $JVM -cp "$CP" org.hepforge.alohep.HeadlessLauncher "$@" -simulation -profile $extra_args
}

warmup() {
    echo "  Warmup run: $@" >&2
    "$JAVA" $JVM -cp "$CP" org.hepforge.alohep.HeadlessLauncher "$@" -simulation -nosimulation > /dev/null 2>&1 || true
}

OUT1="$OUTDIR/results_np_scaling.csv"
echo "test,run_id,$HEADER" > "$OUT1"
echo "[Test 1] Np Scaling (ILC e+e-, 30x30x30) — $(date)" >&2
warmup $ILC_LEFT $ILC_RIGHT -macrop 50000 -xres 30 -yres 30 -zres 30
for n in 10000 50000 100000 500000 1000000; do
    echo "  Np=$n..." >&2
    for r in 1 2 3; do
        run_sim $r "" $ILC_LEFT $ILC_RIGHT -macrop $n -xres 30 -yres 30 -zres 30 \
            | sed "s/^/np_scaling,$r,/" | tee -a "$OUT1"
    done
done

OUT2="$OUTDIR/results_grid_convergence.csv"
echo "test,run_id,$HEADER" > "$OUT2"
echo "[Test 2] Grid Convergence (ILC e+e-, 50K mp) — $(date)" >&2
warmup $ILC_LEFT $ILC_RIGHT -macrop 50000 -xres 30 -yres 30 -zres 30
for g in 10 30 50 100; do
    echo "  Grid=${g}x${g}x${g}..." >&2
    for r in 1 2 3; do
        run_sim $r "" $ILC_LEFT $ILC_RIGHT -macrop 50000 -xres $g -yres $g -zres $g \
            | sed "s/^/grid_conv,$r,/" | tee -a "$OUT2"
    done
done

OUT3="$OUTDIR/results_ee_minus_sign.csv"
echo "test,run_id,$HEADER" > "$OUT3"
echo "[Test 3] e-e- Sign Verification (defocusing) — $(date)" >&2
warmup $ILC_LEFT $ILC_RIGHT_EE -macrop 50000 -xres 50 -yres 50 -zres 50 -pinch
for interp_args in "" "-linearinterp"; do
    label=""
    [ -z "$interp_args" ] || label=" (bilinear)"
    echo "  Running $interp_args${label}..." >&2
    for r in 1 2 3; do
        run_sim $r "$interp_args" $ILC_LEFT $ILC_RIGHT_EE \
            -macrop 50000 -xres 50 -yres 50 -zres 50 -pinch \
            | sed "s/^/ee_minus_sign,$r,/" | tee -a "$OUT3"
    done
done

OUT4="$OUTDIR/results_lhc_scaling.csv"
echo "test,run_id,$HEADER" > "$OUT4"
echo "[Test 4] LHC Proton Scaling (30x30x30, pinch) — $(date)" >&2
warmup $LHC_LEFT $LHC_RIGHT -macrop 50000 -xres 30 -yres 30 -zres 30 -pinch
for n in 10000 50000 100000 500000; do
    echo "  Np=$n (proton)..." >&2
    for r in 1 2 3; do
        run_sim $r "" $LHC_LEFT $LHC_RIGHT -macrop $n -xres 30 -yres 30 -zres 30 -pinch \
            | sed "s/^/lhc_scaling,$r,/" | tee -a "$OUT4"
    done
done

echo "" >&2
echo "End: $(date)" >&2
echo "Results in $OUTDIR/" >&2
echo "  results_np_scaling.csv       — 15 rows (5 configs x 3 runs)" >&2
echo "  results_grid_convergence.csv — 15 rows (5 configs x 3 runs)" >&2
echo "  results_ee_minus_sign.csv   —  6 rows (2 configs x 3 runs)" >&2
echo "  results_lhc_scaling.csv     — 12 rows (4 configs x 3 runs)" >&2
