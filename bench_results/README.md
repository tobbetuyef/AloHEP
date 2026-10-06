# Benchmark Results

Each CSV file contains raw output from `HeadlessLauncher -profile` (3 runs per configuration).

## CSV column format

```
test,run_id,NumMacPar,XRes,YRes,ZRes,interp,total_ms,initMPs_ms,updateQ_ms,
updateFi_ms,updateForce_ms,collision_ms,pinch_ms,bunchUpdate_ms,
L_eff,L_nom,sqrtS,peakMem_bytes
```

- `L_eff` / `L_nom` are in cm^-2 s^-1
- `peakMem_bytes` is the growth of the used Java heap between a forced GC before the run and the end of the run (no GC at the end); it mostly reflects allocation churn and is neither a peak-RSS figure nor a memory requirement
- `interp` is `cubic` (Catmull-Rom bicubic) or `linear` (bilinear)

## Paper table mapping

| Paper table / figure | CSV file | Configuration |
|---|---|---|
| Table 1 (grid convergence) | `results_grid_convergence.csv` | ILC e+e-, no pinch, N_g = 10-100 |
| Table 2 (N_mp scaling) | `results_np_scaling.csv` | ILC e+e-, 30^3 grid, 1e4-1e6 mp |
| Table 3 row 1 (pp sign) | `results_lhc_scaling.csv` | LHC pp, like-charge, 30^3 |
| Table 3 pp baseline | `results_lhc_nopinch.csv` | LHC pp, pinch off, 30^3 (2026-10-02) |
| Table 3 row 2 (e-e- sign) | `results_ee_minus_sign.csv` | ILC e-e-, like-charge, 50^3 |
| Table 3 row 3 (e+e- sign) | `results_epinu_sign.csv` | ILC e+e-, unlike-charge, 50^3 |
| Tables 3-5 D_y, xi | `results_fom.csv` | deterministic, all 5 configs |
| Table 4 (interp comparison) | `results_epinu_sign.csv`; `results_epinu_bilinear_fixed.csv` is a cross-check of the bilinear rows after the 2026-09-17 `Slice.LIP` weight fix (3 runs on a different machine: L_eff 8.342e33 vs 8.334e33 published, i.e. within the run-to-run scatter; timing/memory columns not comparable) | e+e- ILC, 50^3, cubic vs bilinear |
| Tables 5-6 (asymmetric) | `results_asymmetric.csv` | HERA + EIC ep, 50^3: `*_hgonly` rows (hourglass only, pinch off) and `hera_ep`/`eic_ep` rows (hourglass+pinch). All twelve rows were regenerated on 2026-10-02 on the benchmark machine after correcting the electron beta* in `save/electron-ring/EIC-PDG.JSON` to the design 45/5.6 cm |
| Fig. hourglass | `results_hourglass.csv` | ILC e+e-, A = 0.1-5 sweep, 50x100x50, pinch/crossing off |
| Sect. 6.3 N_y=50 check | `results_hourglass_ny50.csv` | ILC e+e-, A = 5 at 50x50x50 (2026-10-02) |
| Sect. 6.5 ILC hourglass+pinch | `results_ilc_hgpinch.csv` | ILC e+e-, hourglass and pinch, 50^3 (5e4 and 5e5 macroparticles), 50x100x50, 50x50x100 and 75x75x50 with -ipscale 6 (2026-10-02/03) |
| Fig. crossing angle | `results_crossing.csv` | ILC e+e-, theta = 0-1e-4 rad sweep, 50^3, pinch off |
| Fig. asymmetric summary | `results_asymmetric.csv` | same runs; L_eff/L_geo per run, hourglass-only vs hourglass+pinch shown as matched pairs against a single hourglass-only analytic curve |
| Fig. species (heavy ion) | `results_heavyion.csv` | Pb-Pb LHC, Au-Au RHIC, 50^3 |
| Fig. species (muon) | `results_muon.csv` | mu+mu- 10 TeV (MuCol consolidated baseline, `save/muon*/MuCol-10TeV`), +/- decay (2026-10-06) |
| Fig. species (e+e-) | `results_grid_convergence.csv` | 50^3 rows of the grid-convergence run |

## Regenerating

```bash
bash run_benchmark.sh        # Tables 1-3 (symmetric + LHC)
bash run_asymmetric.sh       # Tables 3(e+e-) - 6 (asymmetric, hourglass+pinch)
bash run_asymmetric_hgonly.sh # Tables 5-6 / Fig. asymmetric (hourglass-only control, matches analytic curve)
bash run_extra.sh            # LHC pinch-off baseline, hourglass A=5 at N_y=50, ILC hourglass+pinch (2026-10-02 control sets)
bash run_fom.sh              # D_y, xi figures of merit
bash run_hourglass.sh        # Fig. hourglass (~6 h)
bash run_crossing.sh         # Fig. crossing angle
bash run_heavyion.sh         # Fig. species (Pb-Pb, Au-Au)
bash run_muon.sh             # Fig. species (mu+mu-)
bash run_beamstrahlung.sh    # results_beamstrahlung.csv / fig_beamstrahlung.png (not used in the paper)
python3 generate_plots.py    # all media/fig_*.png from the CSVs
```
