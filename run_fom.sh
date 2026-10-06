#!/bin/bash

set -e

if command -v java &>/dev/null; then
    JAVA="java"
    case "$(uname -s)" in
        MINGW*|MSYS*|CYGWIN*) CP=".;gson-2.9.1.jar;bin" ;;
        *)                    CP=".:gson-2.9.1.jar:bin" ;;
    esac
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
OUT="$OUTDIR/results_fom.csv"

echo "label,Dy,xi_x,xi_y,xi_code_x,xi_code_y" > "$OUT"
echo "=== ALOHEP Figures of Merit (deterministic) ===" >&2

emit() {
    local label=$1; shift
    echo "  $label ..." >&2
    "$JAVA" $JVM -cp "$CP" org.hepforge.alohep.HeadlessLauncher "$@" -fom \
        | sed "s/^/$label,/" | tee -a "$OUT"
}

emit "pp_LHC_30"        -left proton/LHC             -right proton/LHC
emit "ee_minus_ILC_50"  -left electron-linac/ILC-125  -right electron-linac/ILC-125
emit "epinu_ILC_50"     -left electron-linac/ILC-125  -right positron-linac/ILC-125

emit "HERA_ep"  -left proton-ele/HERA      -right electron-ring/HERA
emit "EIC_ep"   -left proton-ele/EIC-PDG1  -right electron-ring/EIC-PDG

echo "" >&2
echo "Done. Results in $OUT" >&2
