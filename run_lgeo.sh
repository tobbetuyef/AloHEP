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
OUT="$OUTDIR/results_lgeo.csv"

echo "label,L_geo,sqrtS" > "$OUT"
echo "=== ALOHEP geometric luminosity L_geo (deterministic, no simulation) ===" >&2

emit() {
    local label=$1; shift
    echo "  $label ..." >&2
    local out
    out=$("$JAVA" $JVM -cp "$CP" org.hepforge.alohep.HeadlessLauncher "$@" -nosimulation -csv) \
        || { echo "ERROR: $label failed" >&2; exit 1; }
    printf '%s\n' "$out" | tr -d '\r' | cut -d, -f8,9 | sed "s/^/$label,/" | tee -a "$OUT"
}

emit "epinu_ILC"  -left electron-linac/ILC-125  -right positron-linac/ILC-125
emit "pp_LHC"     -left proton/LHC              -right proton/LHC
emit "HERA_ep"    -left proton-ele/HERA         -right electron-ring/HERA
emit "EIC_ep"     -left proton-ele/EIC-PDG1     -right electron-ring/EIC-PDG
emit "PbPb_LHC"   -left Pb/LHC                  -right Pb/LHC
emit "AuAu_RHIC"  -left Au/RHIC                 -right Au/RHIC
emit "mumu_10TeV" -left muon/MuCol-10TeV        -right muon+/MuCol-10TeV

echo "" >&2
echo "Done. Results in $OUT" >&2
