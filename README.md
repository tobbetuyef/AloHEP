# AloHEP: A Luminosity Optimizer for High Energy Physics

AloHEP simulates the interaction region of symmetric and asymmetric particle colliders and
computes their luminosity. Each bunch is represented by macroparticles on a slice-based
particle-in-cell grid; the hourglass effect, a crossing angle and the beam–beam pinch can be
switched on independently, and the two beams can be any pairing of electrons, positrons,
protons, muons and heavy ions. The methods and the verification of the code are described in
B. Dağlı, Y. Karaca, *AloHEP Software: A luminosity optimizer for High Energy Physics
colliders*, [arXiv:2610.09839](https://arxiv.org/abs/2610.09839).

This repository holds version 2.0. The previous version of AloHEP (v1.0) is available at
[github.com/yefetu/ALOHEP](https://github.com/yefetu/ALOHEP).

## Installation (Windows, Linux, macOS)

Requirement: Java Runtime Environment (JRE) 8 or later.

1. Download [`AloHEP-2.0.zip`](https://github.com/tobbetuyef/AloHEP/releases/download/v2.0/AloHEP-2.0.zip)
   from the [v2.0 release](https://github.com/tobbetuyef/AloHEP/releases/tag/v2.0) of this
   repository (the [Releases](https://github.com/tobbetuyef/AloHEP/releases) page lists every
   version).
2. Extract the zip file. The `AloHEP` folder contains `AloHEP.jar`, `gson-2.9.1.jar`, the
   `save` folder with the parameter database, the `test_run` folder with a sample run and its
   expected output, this README and the licences.
3. Open a terminal in the extracted `AloHEP` folder and start the program with
   `java -jar AloHEP.jar`. On Windows, double-clicking `AloHEP.jar` also works.

The parameter files are read from `./save` relative to the folder the program is started
from, so `AloHEP.jar` must stay next to `save` and `gson-2.9.1.jar`. On macOS and Linux a
double-click may start Java in another folder (e.g. the home folder); the program then finds
no `save` folder and exits without opening a window, so use the terminal command above.

## User guide
AloHEP, developed in the Java programming language, consists of the "save" folder, in which the accelerator parameter files are stored, and the executable "AloHEP.jar" file that contains the program. In the save folder, accelerators are classified into subfolders according to the particle type used. If a further subfolder for accelerators with the same particle is desired, a "-" can be added after the particle name followed by any name. The JSON files of the accelerators are located in the particle subfolders; they contain the accelerator parameters and can be edited with a text editor. The save folder also includes the "settings.JSON" file with the simulation parameters, which can be edited both in this file and through the AloHEP interface. When "AloHEP.jar" is run, the main panel of AloHEP opens (Figure 1).

![image](https://user-images.githubusercontent.com/102833131/213978632-c10b790e-43cf-41b6-baa7-f3cacdaaa679.png)

Figure 1. AloHEP user interface

The main panel consists of three sub-panels. The top two panels are the accelerator panels, where the beams that are to collide in the simulation are defined, and the bottom panel is the settings panel, where the parameters of the simulation are entered. To select an accelerator, the particle type is selected first; the accelerators listed at the bottom of the panel are then updated automatically, and one of them can be selected. If accelerator parameters that are not in the save folder are to be used, the accelerator type "Custom" is selected. The parameters of the last selected accelerator can then be modified, and the edited parameters can be added to the save folder under a chosen name with the "save" button at the top of the panel. After the accelerators are selected, the number of macroparticles, the resolution and the other simulation parameters can be adjusted in the settings panel, the desired effects are selected, and the simulation is started with the "Calculate" button. A graphic panel that shows the macroparticles step by step appears when the simulation starts, so the changes in the shape of the bunches can be followed.

![image](https://user-images.githubusercontent.com/102833131/213979261-b313f598-8ed5-42b0-a934-eb36ee889cd5.png)

Figure 2. AloHEP graphic interface

While the graphic panel is running, the axis along which the collision is observed can be changed with the x, y and z keys, and the view can be zoomed with the mouse wheel. Pressing the r key shows the boundaries of the collision region, the slices and the cells. At the end of the simulation the graphic panel closes and the result panel opens. It reports the centre-of-mass energy and the luminosity of the collision, the transverse sizes of the bunches, and the beam–beam tune shifts or the disruption parameters.

![image](https://user-images.githubusercontent.com/102833131/213979993-2e0b15a5-b228-4281-aa76-072ccd0ee5c1.png)

Figure 3. AloHEP result panel

The luminosity is reported in two ways, nominal and effective. The nominal luminosity is the
analytic geometric luminosity of the two Gaussian beams,

$$\mathcal{L}_{\rm nom}=\frac{N_1N_2\,f_c}{2\pi\sqrt{\sigma_{x1}^2+\sigma_{x2}^2}\,\sqrt{\sigma_{y1}^2+\sigma_{y2}^2}},$$

where $N_{1,2}$ are the bunch populations, $\sigma_{x,y}$ the transverse beam sizes at the
interaction point and $f_c$ the collision frequency (the smaller of the two beams' values, see
below). For identical beams it reduces to $N_1N_2f_c/(4\pi\sigma_x\sigma_y)$. The effective
luminosity is the result of the simulation, and the ratio of the two is the
enhancement/reduction factor due to the effects included in the simulation. When the
simulation is switched off (all effects cleared), the analytic value is computed for the beams
exactly as entered; the *Matched Beams* button equalises the transverse spot sizes of the two
beams only when it is pressed.

Versions before 2.0 reported instead the equal-beam approximation
$N_1N_2f_c/(4\pi\max(\sigma_{x1},\sigma_{x2})\max(\sigma_{y1},\sigma_{y2}))$, which coincides
with the expression above for identical beams and is smaller for beams of unequal size, and
applied the Matched Beams helper automatically when the simulation was switched off.

## Parameter reference

This section is the complete list of the quantities AloHEP reads; the paper describes the
panels only briefly and refers here for the details.

### Accelerator parameter files (`save/<species>-<tag>/<machine>.JSON`)

Each file holds one `"variables"` map. The **particle species** is the folder-name prefix
before the first `-` (`electron`, `positron`, `proton`, `muon`, `muon+`, `Pb`, `Au`); a folder
name containing `linac` marks the machine as *linear* (the results window then reports the
disruption parameters D<sub>x,y</sub>), any other folder is treated as *circular* (beam–beam
tune shifts ξ<sub>x,y</sub>). Built-in species (charge, mass, lifetime): electron (−1,
0.511 MeV, stable), positron (+1, 0.511 MeV, stable), proton (+1, 938.28 MeV, stable), muon
(−1, 105.658 MeV, 2.197 µs), muon+ (+1, 105.658 MeV, 2.197 µs), Pb (+82, 193.687 GeV, the
<sup>208</sup>Pb nuclear mass, stable), Au (+79, 183.433 GeV, <sup>197</sup>Au, stable).

| Key | GUI label | Unit | How the engine uses it |
|---|---|---|---|
| `NumParInBun` | Number of particles per bunch (N) | – | bunch population N: luminosity, D, ξ and the beam–beam force |
| `EnBeam` | Particle beam energy | GeV | Lorentz factor γ = E/mc² |
| `BetaHor`, `BetaVer` | Beta function at IP (horizontal, vertical) | m | β*<sub>x,y</sub>: σ = √(ε β*); with the hourglass effect the angular spread √(ε/β*) |
| `EmNorHor`, `EmNorVer` | Norm. emittance (horizontal, vertical) | m·rad | normalised emittance; ε = ε<sub>n</sub>/γ (used whenever present) |
| `EmHor`, `EmVer` | Trans. emittance (horizontal, vertical) | m·rad | geometric emittance, used only when the normalised one is absent |
| `BunLen` | Particle beam bunch length | m | RMS bunch length σ<sub>z</sub> |
| `NumBunInBeam` | Bunches in particle beam | – | multiplies the per-bunch collision frequency (required unless `ColFrq` is given) |
| `ColFrq` | Collision frequency of beams | Hz | collision frequency f<sub>c</sub> used as given; overrides every other frequency key and `NumBunInBeam` |
| `RevFrq` | Revolution frequency of beam | Hz | f<sub>c</sub> = RevFrq × NumBunInBeam |
| `PulFrq` | Pulse frequency of beams | Hz | f<sub>c</sub> = PulFrq × NumBunInBeam (when `RevFrq` is absent) |
| `RepFrq` | Repetition rate of beam | Hz | f<sub>c</sub> = RepFrq × turns × NumBunInBeam, with turns = `Turn` if given, otherwise γτ<sub>0</sub>c/Circum (unstable species only) |
| `Circum` | Circumference | km | last-resort f<sub>c</sub> = c/Circum × NumBunInBeam; with `Turn` it sets the storage time |
| `Turn` | Number of turns | – | turns per store: storage time t = Circum × Turn / c for the decay factor, and the `RepFrq` option above |
| `DutyFac` | Duty factor | – | multiplies the effective luminosity (the smaller of the two beams' values; default 1) |

Precedence for the collision frequency of one beam: `ColFrq` > `RevFrq` > `PulFrq` >
`RepFrq` × turns > c/`Circum`; the pair uses the smaller of the two beams' values. Keys
accepted by the beam editor but not read by the engine: `Beta`, `EmNor`, `BunSpace`,
`PowLim`, `DisrLim`, `BeamParLim`.

**Decay** (`-decay`, *Decay* checkbox): for an unstable species the bunch population is
replaced by its average over the store, N → N γτ<sub>0</sub>(1 − e<sup>−t/γτ<sub>0</sub></sup>)/t
with t = Circum × Turn / c (t = γτ<sub>0</sub>, one dilated lifetime, when `Turn` is absent);
when both beams are unstable the product N<sub>1</sub>N<sub>2</sub> is averaged jointly. Stable
species are unaffected. The decay factor and the duty factor enter the effective luminosity
only; the nominal luminosity is the plain geometric value above.

### Provenance of the parameter sets used in the paper

| File(s) in `save/` | Machine | Source |
|---|---|---|
| `electron-linac/ILC-125`, `positron-linac/ILC-125` | ILC, √s = 250 GeV (TDR set) | ILC Technical Design Report Vol. 3.II (2013), arXiv:1306.6328; ILC Machine Staging Report 2017, arXiv:1711.00568, Table 5-1 (β*<sub>y</sub> = 0.40 mm here, 0.41 mm in the report) |
| `proton/LHC` | LHC pp, nominal | L. Evans, P. Bryant (Eds.), *LHC machine*, JINST 3 (2008) S08001 (σ<sub>z</sub> = 7.7 cm here, 7.55 cm nominal) |
| `proton-ele/HERA`, `electron-ring/HERA` | HERA-II ep | Particle Data Group, *Review of Particle Physics* (2024), "High-energy collider parameters" |
| `proton-ele/EIC-PDG1`, `electron-ring/EIC-PDG` | EIC ep, 275 GeV × 10 GeV | EIC Conceptual Design Report 2021, doi:10.2172/1765663, as tabulated by the Particle Data Group |
| `Pb/LHC` | LHC Pb–Pb | L. Evans, P. Bryant (Eds.), *LHC machine*, JINST 3 (2008) S08001, heavy-ion parameters |
| `Au/RHIC` | RHIC Au–Au, 100 GeV/nucleon | H. Hahn et al., *The RHIC design overview*, Nucl. Instrum. Methods A 499 (2003) 245; bunch population 2×10⁹ as reached in later runs |
| `muon/MuCol-10TeV`, `muon+/MuCol-10TeV` | √s = 10 TeV muon collider, MuCol/IMCC consolidated baseline | R. Taylor et al. (IMCC/MuCol), *MuCol Milestone Report No. 7: Consolidated Parameters* (2025), arXiv:2510.27437, doi:10.5281/zenodo.17476875, Table 1.1; `Turn` = 5260 is one repetition period (1/f<sub>r</sub> = 0.2 s) per store, the storage assumption of its Appendix A.1 |

The other parameter sets in `save/` are examples taken from the authors' earlier collider
studies and were not used in the paper; their sources are not yet recorded here.

### Simulation settings (`save/settings.JSON`, *Simulation Settings* panel, command line)

| Key | GUI label | Default | Flag | Meaning |
|---|---|---|---|---|
| `NumMacPar` | Number of Macroparticles | 50000 | `-macrop N` | macroparticles per bunch |
| `IPScale` | Scale of Sigma | 4 | `-ipscale k` | half-size of the simulated region in units of σ (x, y: k·max(σ<sub>1</sub>, σ<sub>2</sub>); z: k(σ<sub>z1</sub>+σ<sub>z2</sub>)/2) and truncation of the Gaussian sampling in position and angle; with the hourglass effect the vertical half-size is further multiplied by 2σ<sub>z</sub>/β*<sub>y</sub> when that factor exceeds 1 |
| `XRes`, `YRes`, `ZRes` | Resolution of X/Y/Z-axis | 30 | `-xres/-yres/-zres N` | grid cells along each axis of the simulated region; the crossing is advanced in 2·ZRes steps |
| `chargeDimScaleMP` | Cloud scale of Macroparticle | 1 | `-chargeDimScaleMP s` | side of the macroparticle charge cloud in cells (1 = cloud-in-cell) |
| `crossingAngle` | Crossing angle of Collision | 0 | `-crossing θ` | half crossing angle θ in radians: each bunch is rotated by ±θ in the (z, y) plane, so the full crossing angle is 2θ |

The command-line engine does not read `save/settings.JSON`: it always starts from the
defaults listed above and changes them only through the flags; `settings.JSON` sets the
initial values of the GUI's *Simulation Settings* panel.

Beams on the command line: `-left <folder>/<name>` and `-right <folder>/<name>` select
`save/<folder>/<name>.JSON` (default `electron-linac/ILC-125` for both; with a folder only, the
first file in it is used). Physical effects (*Checkboxes* panel / flags): hourglass
(`-hourglass`), beam–beam pinch (`-pinch`), beamstrahlung (`-beamstrahlung`; on the command
line it acts only together with `-pinch`), decay (`-decay`). In the GUI, enabling any of the
first three turns on the macroparticle simulation; on the command line the simulation is on
by default and `-nosimulation` switches it off (the result is then the analytic value:
`L_effective` equals `L_nominal` times the decay and duty factors, if any; a `-simulation`
flag is accepted but redundant). Force interpolation is bicubic Catmull–Rom by default
(`-linearinterp` selects bilinear, for comparison only); `-nocrossing` ignores the crossing
angle. `-betax`, `-betay`, `-bunlen` and `-npar <value>` override β*<sub>x</sub>,
β*<sub>y</sub>, σ<sub>z</sub> and N of *both* beams for one run without editing files.
`-seed <n>` (a non-negative integer) fixes the random-number stream of the macroparticle
sampling, so that the run is reproduced exactly; without it every run draws a new sample (the
GUI always samples without a fixed seed). Output modes: human-readable block (default), `-csv`
(short: `NumMacPar,XRes,YRes,ZRes,interp,time_ms,L_eff,L_nom,sqrtS,peakMem`), `-profile`
(per-phase timing CSV), `-fom` (deterministic D<sub>y</sub> and ξ only), `-bsreport`
(beamstrahlung report).

### Known limitations

- The transverse field solver evaluates a point-charge Green's function at the cell corners.
  For flat beams it underestimates the vertical beam–beam force when the horizontal cell size
  exceeds σ<sub>y</sub> (central focusing gradient about 12% low for the EIC and 76% low for
  the ILC at a 50×50 grid; round beams and HERA are accurate to 0.5–2%). Pinch results for
  very flat beams are therefore not converged in `XRes`. A cell-averaged Green's function that
  removes this limitation is planned for the successor code AloHEPy.
- The strongly disrupted regime (D<sub>y</sub> ≫ 1) is not validated, the beamstrahlung model
  is preliminary, only a single bunch crossing is simulated, and crossing angles must stay
  small enough that the rotated bunches remain inside the transverse simulation box.
- With all effects off, the ratio `L_effective`/`L_nominal` measures the discretisation of the
  grid (0.993 at 30³ and 0.903 at 10³ for the ILC), not a physical effect.

## Building from source and running the headless engine (reproducibility)

AloHEP is pure Java (Java 7-compatible syntax; builds and runs unchanged on OpenJDK 8–21,
benchmarks were run with OpenJDK 17). Its only dependency is the bundled `gson-2.9.1.jar`
(Apache License 2.0, https://github.com/google/gson). The GUI can be started from the compiled
classes with `java -cp ".:gson-2.9.1.jar:bin" org.hepforge.alohep.Launcher`; the command-line
(headless) engine used to produce the published benchmarks is built and run from the source
tree as follows.

### Package contents

| Path | Content |
|---|---|
| `src/` | Java sources: `Launcher` (GUI) and `HeadlessLauncher` (command line) in `org.hepforge.alohep`, physics engine in `calc/`, GUI panels in `gui/`, animation in `gfx/`, JSON data layer in `database/` |
| `bin/` | compiled classes (Java 8 bytecode), rebuilt by `build.sh` |
| `save/` | parameter database (one JSON file per machine in particle-type folders) and `settings.JSON` with the GUI defaults |
| `gson-2.9.1.jar`, `LICENSE-gson.txt` | JSON library and its licence |
| `build.sh`, `make_release.sh` | build the classes and the release package (need a JDK ≥ 8 and bash) |
| `run_*.sh`, `rerun_asymmetric.sh`, `run_all.sh` | benchmark drivers that write the CSV files of `bench_results/` |
| `bench_results/` | raw benchmark results of the paper, with their own README |
| `test_run/` | `run_sample.sh`, which runs the sample run below and compares its output with `expected_output.txt` |
| `generate_plots.py`, `media/` | script (Python 3 with numpy and matplotlib) that draws the data figures of the paper, and the figures |
| `benchmarks/` | optional JMH micro-benchmark of the interpolation (Maven, Java 17); not needed to run AloHEP |
| `.classpath`, `.project`, `.settings/` | Eclipse project files |
| `README.md`, `LICENSE`, `CITATION.cff` | this file, the licence and the citation metadata |

### Build
```
bash build.sh        # compiles src/ into bin/ (Java 8 bytecode) with gson-2.9.1.jar on the classpath
bash make_release.sh # rebuilds bin/ and packages dist/AloHEP-2.0.zip (AloHEP.jar, gson, save/, test_run/, README, licences)
                     # and dist/AloHEP-2.0-source.zip (the committed source tree)
```
On Windows without bash (PowerShell), build with
`javac --release 8 -encoding UTF-8 -cp gson-2.9.1.jar -d bin (Get-ChildItem -Recurse src -Filter *.java).FullName`.

### Sample run (headless)
A full electron–proton bunch crossing for the EIC configuration (50×50×50 grid,
5×10⁴ macroparticles, hourglass effect and beam–beam pinch enabled, fixed random seed),
run inside the extracted release folder:
```
java -cp AloHEP.jar org.hepforge.alohep.HeadlessLauncher \
  -left proton-ele/EIC-PDG1 -right electron-ring/EIC-PDG \
  -hourglass -pinch -macrop 50000 -xres 50 -yres 50 -zres 50 -seed 1
```
In a source checkout, after `bash build.sh`, use `-cp ".:gson-2.9.1.jar:bin"` instead of
`-cp AloHEP.jar` (on Windows `-cp ".;gson-2.9.1.jar;bin"`, with the command typed on one line,
since the trailing `\` is bash syntax). The same applies to the commands below. The script
`bash test_run/run_sample.sh` runs this command from either location and compares the output
with `test_run/expected_output.txt`, ignoring the timing and memory lines.

### Expected output
```
=== ALOHEP Headless Results ===
Left:  proton-ele/EIC-PDG1 (proton)
Right: electron-ring/EIC-PDG (electron)
Macroparticles: 50000
Grid: 50x50x50
Crossing:     true
Interp:       bicubic
Simulation:   true
Pinch:        true
Hourglass:    true
Beamstrahlung:false
Decay:        false
Seed:         1
sqrt(s):      1.048809e+02 GeV
L_nominal:    1.046781e+34 cm^-2 s^-1
L_effective:  1.021153e+34 cm^-2 s^-1
Ratio eff/nom: 0.975518
Compute time: 294713.492 ms
Peak memory:  69 MB
--- Per-Phase Breakdown ---
initMPs:       56.242 ms
updateQ:       457.437 ms
updateFi:      293464.215 ms
updateForce:   17.996 ms
collision:     4.114 ms
pinch:         669.473 ms
bunchUpdate:   97.598 ms
```
With `-seed 1` every line above except `Compute time`, `Peak memory` and the per-phase
timings is reproduced exactly; the same output was obtained with Eclipse Temurin 8u504,
Eclipse Temurin 17.0.20.1 and OpenJDK 21.0.12 on x86-64 Linux. The timings shown are from a
laptop (Intel Core i7-13700H) and depend on the machine; on the benchmark machine of the paper
(AMD Ryzen 9 9900X) this run takes about 1.5 min. `Peak memory` is the growth of the used Java
heap between a forced garbage collection before the run and the end of the run, so it mostly
reflects allocation churn and is not a memory requirement. The run also prints one line to
the error stream, `WARNING: macroparticle outside the force grid; zero force applied ...`:
a few macroparticles of the diverging (hourglass) beams leave the simulated region, where
they receive no beam–beam force.

`L_nominal` is the analytic geometric luminosity of the two beams. Without `-seed` every run
draws a new macroparticle sample and `L_effective` varies by a few tenths of a percent. The
three unseeded runs behind the paper (rows `eic_ep` of `bench_results/results_asymmetric.csv`,
recorded before version 2.0, so that their `L_nom` column still holds the older equal-beam
value 1.043825e+34) give `L_eff`/`L_geo` = 0.9736 ± 0.0017 (mean ± s.d.) with
`L_geo` = 1.046781e+34 from `results_lgeo.csv`; the seeded run above (0.9755) lies within
this scatter.

Two further exact checks are deterministic and take a fraction of a second. The analytic
luminosity alone (`L_nominal` = `L_effective` = 1.046781e+34 cm^-2 s^-1):
```
java -cp AloHEP.jar org.hepforge.alohep.HeadlessLauncher \
  -left proton-ele/EIC-PDG1 -right electron-ring/EIC-PDG -nosimulation
```
and the figures of merit printed by `-fom`:
```
java -cp AloHEP.jar org.hepforge.alohep.HeadlessLauncher \
  -left proton-ele/EIC-PDG1 -right electron-ring/EIC-PDG -fom
```
output — `D_y`, `ξ_x`, `ξ_y` of the `-right` beam (here the electron beam) caused by the
`-left` beam, followed by `ξ_x`, `ξ_y` recomputed with the routine of the GUI results window
as a consistency check (swap `-left` and `-right` for the other beam):
```
1.341981,0.072447,0.099672,0.072447,0.099672
```

### Reproducing the paper benchmarks
The `run_*.sh` scripts regenerate the CSV files in `bench_results/` that back the paper's
tables and figures (mapped in the paper's "Benchmark data mapping" appendix and in
`bench_results/README.md`). For example, `bash run_asymmetric.sh` regenerates the HERA and EIC
hourglass+pinch results and the ILC e+e- sign and interpolation runs (it rewrites
`results_asymmetric.csv` and `results_epinu_sign.csv`), after which
`bash run_asymmetric_hgonly.sh` appends the hourglass-only control rows, `bash run_lgeo.sh`
writes the analytic geometric luminosities, and `bash run_benchmark.sh` regenerates the
grid-convergence and scaling tables. `bash run_all.sh` chains all of them in the right order
(more than 16 h on the benchmark machine, dominated by the 100³ runs and the hourglass sweep),
and `python3 generate_plots.py` redraws the figures from the CSV files. The bilinear row of
the paper's Table 4 comes from the `epinu_bilinear` runs of `run_asymmetric.sh`
(`results_epinu_sign.csv`, recorded before the 2026-09-17 correction of the bilinear
interpolation weights, as the table caption states); `results_epinu_bilinear_fixed.csv` is the
cross-check with the corrected weights quoted in the paper's appendix. The published runs use
the default, unseeded random generator, so a rerun reproduces them within the quoted
run-to-run scatter rather than digit by digit.

## Citing AloHEP

If you use AloHEP, please cite B. Dağlı, Y. Karaca, *AloHEP Software: A luminosity optimizer
for High Energy Physics colliders*, arXiv:2610.09839 (2026). Citation metadata for the software
is given in `CITATION.cff`.

## Licence

AloHEP is released under the MIT License (`LICENSE`). The bundled `gson-2.9.1.jar` (Google
Gson) is distributed under the Apache License, Version 2.0 (`LICENSE-gson.txt`).
