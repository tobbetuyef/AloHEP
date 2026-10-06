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
OUTDIR="bench_results"; mkdir -p "$OUTDIR"
HEADER="NumMacPar,XRes,YRes,ZRes,interp,total_ms,initMPs_ms,updateQ_ms,updateFi_ms,updateForce_ms,collision_ms,pinch_ms,bunchUpdate_ms,L_eff,L_nom,sqrtS,peakMem_bytes"
run() { "$JAVA" $JVM -cp "$CP" org.hepforge.alohep.HeadlessLauncher "$@" -simulation -profile | tr -d '\r' | grep '^[0-9]'; }
ILC_LEFT="-left electron-linac/ILC-125"; ILC_RIGHT="-right positron-linac/ILC-125"

OUT="$OUTDIR/results_lhc_nopinch.csv"; echo "test,run_id,$HEADER" > "$OUT"
echo "[1/3] LHC pp 30^3, pinch off — $(date)" >&2
for r in 1 2 3; do run -left proton/LHC -right proton/LHC -macrop 50000 -xres 30 -yres 30 -zres 30 | sed "s/^/lhc_nopinch,$r,/" | tee -a "$OUT"; done

OUT="$OUTDIR/results_hourglass_ny50.csv"; echo "test,run_id,$HEADER,A" > "$OUT"
echo "[2/3] ILC hourglass A=5, 50x50x50 (beta*_y = 6e-5 m) — $(date)" >&2
for r in 1 2 3; do run $ILC_LEFT $ILC_RIGHT -macrop 50000 -xres 50 -yres 50 -zres 50 -hourglass -nocrossing -betay 6.000000e-05 | awk -v pre="hourglass_ny50,$r," '{print pre $0 ",5.0"}' | tee -a "$OUT"; done

OUT="$OUTDIR/results_ilc_hgpinch.csv"; echo "test,run_id,$HEADER" > "$OUT"
echo "[3/3] ILC hourglass+pinch 50^3 — $(date)" >&2
for r in 1 2 3; do run $ILC_LEFT $ILC_RIGHT -macrop 50000 -xres 50 -yres 50 -zres 50 -hourglass -pinch | sed "s/^/ilc_hgpinch,$r,/" | tee -a "$OUT"; done
echo "Done — $(date)" >&2
