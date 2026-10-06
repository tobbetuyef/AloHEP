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
OUT="$OUTDIR/results_beamstrahlung.csv"
echo "sweep,value,run_id,gamma_init_L,gamma_fin_L,deltaE_L,gamma_init_R,gamma_fin_R,deltaE_R,total_ms" > "$OUT"

ILC_LEFT="-left electron-linac/ILC-125"
ILC_RIGHT="-right positron-linac/ILC-125"
RES="-xres 40 -yres 40 -zres 40 -macrop 30000"

echo "=== ALOHEP Beamstrahlung Sweeps ===" >&2
echo "Start: $(date)" >&2

run_pt() {
    local sweep="$1" flag="$2" val="$3"
    echo "  $sweep=$val ..." >&2
    for r in 1 2; do
        "$JAVA" $JVM -cp "$CP" org.hepforge.alohep.HeadlessLauncher \
            $ILC_LEFT $ILC_RIGHT $RES $flag "$val" -pinch -beamstrahlung -bsreport \
            | tail -n 1 | tr -d '\r' | sed "s/^/$sweep,$val,$r,/" | tee -a "$OUT"
    done
}

for N in 0.5e10 1.0e10 1.5e10 2.0e10 3.0e10; do
    run_pt "N" -npar "$N"
done

for BL in 1.5e-4 3.0e-4 4.5e-4 6.0e-4 9.0e-4; do
    run_pt "sigma_z" -bunlen "$BL"
done

echo "" >&2
echo "End: $(date)" >&2
echo "Results in $OUT" >&2
echo "Figure: python3 generate_plots.py   (produces media/fig_beamstrahlung.png, two panels)" >&2
