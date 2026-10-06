#!/bin/bash

set -e

CP=".:gson-2.9.1.jar:bin"
LEFT="-left electron-linac/ILC-125"
RIGHT="-right electron-linac/ILC-125"

echo "=== ALOHEP Benchmark Results ===" >&2
echo "Start: $(date)" >&2

echo "=== Grid Convergence Test (50000 macroparticles, symmetric e+e-) ==="
echo "NumMacPar,XRes,YRes,ZRes,total_ms,initMPs_ms,updateQ_ms,updateFi_ms,updateForce_ms,collision_ms,pinch_ms,bunchUpdate_ms,L_eff,L_nom,sqrtS,peakMem_bytes"

for g in 10 30 50 100 200; do
    echo "Grid ${g}x${g}x${g}..." >&2
    java -cp "$CP" org.hepforge.alohep.HeadlessLauncher \
        $LEFT $RIGHT -macrop 50000 -xres $g -yres $g -zres $g \
        -simulation -profile
done

echo ""
echo "=== Np Scaling Test (30x30x30 grid, symmetric e+e-) ==="
echo "NumMacPar,XRes,YRes,ZRes,total_ms,initMPs_ms,updateQ_ms,updateFi_ms,updateForce_ms,collision_ms,pinch_ms,bunchUpdate_ms,L_eff,L_nom,sqrtS,peakMem_bytes"

for n in 10000 50000 100000 500000; do
    echo "Macroparticles: ${n}..." >&2
    java -cp "$CP" org.hepforge.alohep.HeadlessLauncher \
        $LEFT $RIGHT -macrop $n -xres 30 -yres 30 -zres 30 \
        -simulation -profile
done

echo ""
echo "=== e-e- Sign Verification Test (50000 macroparticles, pinch enabled) ==="
echo "NumMacPar,XRes,YRes,ZRes,total_ms,initMPs_ms,updateQ_ms,updateFi_ms,updateForce_ms,collision_ms,pinch_ms,bunchUpdate_ms,L_eff,L_nom,sqrtS,peakMem_bytes"

echo "Grid 50x50x50 with pinch..." >&2
java -cp "$CP" org.hepforge.alohep.HeadlessLauncher \
    $LEFT $RIGHT -macrop 50000 -xres 50 -yres 50 -zres 50 \
    -simulation -pinch -profile

echo "Grid 100x100x100 with pinch..." >&2
java -cp "$CP" org.hepforge.alohep.HeadlessLauncher \
    $LEFT $RIGHT -macrop 50000 -xres 100 -yres 100 -zres 100 \
    -simulation -pinch -profile

echo ""
echo "=== JFR Macro-Profile (200x200x200, 50000 macroparticles) ==="
echo "JFR profile written to profile_200x200.jfr" >&2
java -XX:StartFlightRecording=filename=profile_200x200.jfr,settings=profile,dumponexit=true \
    -cp "$CP" org.hepforge.alohep.HeadlessLauncher \
    $LEFT $RIGHT -macrop 50000 -xres 200 -yres 200 -zres 200 \
    -simulation -nosimulation
echo "Reminder: -nosimulation used for JFR to skip the heavy grid loop and capture init overhead separately" >&2

echo "=== JFR Macro-Profile with simulation (30x30x30, 50000 macroparticles) ==="
java -XX:StartFlightRecording=filename=profile_30x30_sim.jfr,settings=profile,dumponexit=true \
    -cp "$CP" org.hepforge.alohep.HeadlessLauncher \
    $LEFT $RIGHT -macrop 50000 -xres 30 -yres 30 -zres 30 \
    -simulation

echo "" >&2
echo "End: $(date)" >&2
echo "JFR files: open with 'jmc profile_*.jfr' (JDK Mission Control)" >&2
