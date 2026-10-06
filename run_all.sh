#!/bin/bash

set -u
cd "$(dirname "$0")"

TS=$(date +%Y%m%d_%H%M%S)
mkdir -p bench_results
LOG="bench_results/run_all_${TS}.log"

echo "ALOHEP run_all  —  started $(date '+%F %T')" | tee "$LOG"
echo "Master log: $LOG" | tee -a "$LOG"
echo "============================================" | tee -a "$LOG"

FAILED=()

run_step() {
    local name="$1"; shift
    echo "" | tee -a "$LOG"
    echo ">>> [$(date '+%F %T')] START  $name" | tee -a "$LOG"
    if "$@" >> "$LOG" 2>&1; then
        echo "<<< [$(date '+%F %T')] OK     $name" | tee -a "$LOG"
    else
        local rc=$?
        echo "!!! [$(date '+%F %T')] FAIL   $name (exit $rc)" | tee -a "$LOG"
        FAILED+=("$name (exit $rc)")
    fi
}

run_step "build"          bash build.sh

run_step "fom"            bash run_fom.sh
run_step "hourglass"      bash run_hourglass.sh
run_step "crossing"       bash run_crossing.sh
run_step "heavyion"       bash run_heavyion.sh
run_step "muon"           bash run_muon.sh
run_step "asymmetric"     bash run_asymmetric.sh
run_step "asymmetric-hgonly" bash run_asymmetric_hgonly.sh
run_step "benchmark"      bash run_benchmark.sh

run_step "plots"          python3 generate_plots.py

echo "" | tee -a "$LOG"
echo "============================================" | tee -a "$LOG"
echo "ALOHEP run_all  —  finished $(date '+%F %T')" | tee -a "$LOG"
if [ ${#FAILED[@]} -eq 0 ]; then
    echo "RESULT: all steps PASSED" | tee -a "$LOG"
else
    echo "RESULT: ${#FAILED[@]} step(s) FAILED:" | tee -a "$LOG"
    for f in "${FAILED[@]}"; do echo "  - $f" | tee -a "$LOG"; done
fi
echo "Full log: $LOG" | tee -a "$LOG"
