# Welcome to the AloHEP Readme!!

The previous version of AloHEP (v1.0) is available at [github.com/yefetu/ALOHEP](https://github.com/yefetu/ALOHEP).

## Alohep installation for Windows

Requirement: Java Runtime Environment (JRE) 8 or later (tested with OpenJDK 17 and 21)  
First you will go to releases 
![image](https://user-images.githubusercontent.com/102833131/213984651-2e7cebfe-6096-4ab6-8279-ef8a54ebb867.png)

Secondly you will download AloHEP.zip
![image](https://user-images.githubusercontent.com/102833131/213984943-bd7073f3-b6ee-4447-8359-cc647c620bc0.png)

Thirdly you will extract zip 

Finally you will select Alohep.jar
![image](https://user-images.githubusercontent.com/102833131/213985244-7959dedf-10cb-4f92-9b35-7ac6f5c3d00d.png)


## Alohep userguide 
The AloHEP software, developed using the Java programming language, is composed of the "save" folder where accelerator-related save files are stored and the executable "AloHEP.jar" file that contains the software codes. In the save file, accelerators are classified and distributed into subfolders according to the particle type used. If a subfolder for the same particle-containing accelerators is desired, a "-" can be added after the particle name and desired naming can be done. JSON files belonging to each accelerator are located in the particle subfolders. These JSON files contain accelerator-related parameters and can be edited using text editors. The record file also includes the "settings.JSON" file containing the simulation parameters. These simulation parameters can be edited both in this file and through the AloHEP interface. The AloHEP.jar file must be located in the same folder as the save folder to be able to run. When the "AloHEP.jar" is run, the main panel of the AloHEP software opens (Figure 1).

![image](https://user-images.githubusercontent.com/102833131/213978632-c10b790e-43cf-41b6-baa7-f3cacdaaa679.png)

Figure 1 AloHEP Userinterface

The main panel consists of three sub-panels. The top two panels are accelerator panels where information about the accelerators in which particle collisions are to be performed in the simulation is entered, and the bottom panel is the settings panel where the parameters of the simulation are entered. When selecting the accelerator to be used in the collision simulation, the particle type used is selected first. After the particle type is selected, the accelerators in the bottom of the panel are automatically updated accordingly, so the accelerator can be selected from the ones listed in the bottom of the panel. If accelerator parameters not found in the AloHEP software's save file are desired to be used, the accelerator type "Custom" is selected. After this selection, modifications can be made based on the last selected accelerator parameters, and the edited parameters can be added to the AloHEP software's save file with the desired name using the "save" button at the top of the panel. After selecting the accelerators in the AloHEP software, the number of macro-particles in the simulation, the simulation resolution, and other simulation parameters can be adjusted from the settings panel at the bottom of the interface, and after selecting the desired effects in the simulation, the simulation can be started by pressing the "calculate" button. The AloHEP software has a graphic panel that shows the progress of macro-particles step by step and appears when the simulation starts. The changes in the shape of the packets can be tracked through this interface.

![image](https://user-images.githubusercontent.com/102833131/213979261-b313f598-8ed5-42b0-a934-eb36ee889cd5.png)

Figure 2. Alohep Graphic Interface

While the graphic panel is running, the axis on which the collision is being observed can be changed by pressing the x, y and z keys on the keyboard. The selected axis can be zoomed in  using the mouse wheel. This way, the change in shape of the bunch can be easily tracked during the simulation. Additionally, by pressing the r key on the keyboard, the boundaries of the collision region, slices and cells can be seen on the graphic panel during the simulation. At the end of the simulation process, the graphic panel closes and the result panel opens. Here, the results obtained for the center of mass energy and luminosity of the collision; the cross-sections of the bunches, the beam-beam tuneshift and the disruption parameters are displayed.

![image](https://user-images.githubusercontent.com/102833131/213979993-2e0b15a5-b228-4281-aa76-072ccd0ee5c1.png)

Figure 3. Alohep Result Panel

 The luminosity value is calculated in two different ways, nominal and effective. The nominal luminosity value is obtained analytically using Equation 1, while the effective luminosity value is obtained from the results of the simulation. Additionally, by comparing the two luminosity values, factors that increase/decrease the luminosity due to the effects added in the simulation are obtained.

![image](https://user-images.githubusercontent.com/102833131/213977517-c75a03a4-9241-45ef-aecf-7b7e2c56745a.png)                         (Equation 1)

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
| `DutyFac` | Duty factor | – | multiplies the luminosity (the smaller of the two beams' values; default 1) |

Precedence for the collision frequency of one beam: `ColFrq` > `RevFrq` > `PulFrq` >
`RepFrq` × turns > c/`Circum`; the pair uses the smaller of the two beams' values. Keys
accepted by the beam editor but not read by the engine: `Beta`, `EmNor`, `BunSpace`,
`PowLim`, `DisrLim`, `BeamParLim`.

**Decay** (`-decay`, *Decay* checkbox): for an unstable species the bunch population is
replaced by its average over the store, N → N γτ<sub>0</sub>(1 − e<sup>−t/γτ<sub>0</sub></sup>)/t
with t = Circum × Turn / c (t = γτ<sub>0</sub>, one dilated lifetime, when `Turn` is absent);
when both beams are unstable the product N<sub>1</sub>N<sub>2</sub> is averaged jointly. Stable
species are unaffected.

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

Physical effects (*Checkboxes* panel / flags): hourglass (`-hourglass`), beam–beam pinch
(`-pinch`), beamstrahlung (`-beamstrahlung`, requires the pinch), decay (`-decay`). In the
GUI, enabling any of the first three turns on the macroparticle simulation; on the command
line the simulation is on by default and `-nosimulation` switches it off (the analytic
nominal value is then the only output; a `-simulation` flag is accepted but redundant).
Force interpolation is bicubic Catmull–Rom by default (`-linearinterp` selects bilinear, for
comparison only); `-nocrossing` ignores the crossing angle. `-betax`, `-betay`, `-bunlen` and
`-npar <value>` override β*<sub>x</sub>, β*<sub>y</sub>, σ<sub>z</sub> and N of *both* beams for one run without
editing files. Output modes: human-readable block (default), `-csv` (short), `-profile`
(per-phase timing CSV), `-fom` (deterministic D<sub>y</sub> and ξ only), `-bsreport`
(beamstrahlung report).

## Building from source and running the headless engine (reproducibility)

AloHEP is pure Java (Java 7-compatible syntax; builds and runs unchanged on OpenJDK 8–21,
benchmarks were run with OpenJDK 17). Its only dependency is the bundled `gson-2.9.1.jar`
(Apache License 2.0, https://github.com/google/gson). The GUI described above can be started
from the compiled classes with `java -cp ".:gson-2.9.1.jar:bin" org.hepforge.alohep.Launcher`
(a packaged `AloHEP.jar` built from the same classes is equivalent); the
command-line (headless) engine used to produce the published benchmarks is built and run
from the source tree as follows.

### Build
```
bash build.sh        # compiles src/ into bin/ with gson-2.9.1.jar on the classpath
```
On Windows (PowerShell/cmd) use `;` instead of `:` as the classpath separator in the
commands below.

### Sample run (headless)
A full electron–proton bunch crossing for the EIC configuration (50×50×50 grid,
5×10⁴ macroparticles, hourglass and beam–beam pinch enabled):
```
java -cp ".:gson-2.9.1.jar:bin" org.hepforge.alohep.HeadlessLauncher \
  -left proton-ele/EIC-PDG1 -right electron-ring/EIC-PDG \
  -hourglass -pinch -macrop 50000 -xres 50 -yres 50 -zres 50
```

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
sqrt(s):      1.048809e+02 GeV
L_nominal:    1.043825e+34 cm^-2 s^-1
L_effective:  1.02e+34 cm^-2 s^-1
Ratio eff/nom: 0.976
Compute time: ~9.2e+04 ms
Peak memory:  <used-heap growth over the run, machine dependent> MB
--- Per-Phase Breakdown ---
initMPs:       ~44 ms
updateQ:       ~300 ms
updateFi:      ~9.1e+04 ms
updateForce:   ~10 ms
collision:     ~2 ms
pinch:         ~410 ms
bunchUpdate:   ~56 ms
```
(`L_effective` and `Ratio eff/nom` are the means of the three committed runs in
`bench_results/results_asymmetric.csv`, rows `eic_ep`; the compute time is for one core of
an AMD Ryzen 9 9900X.) `sqrt(s)`, `L_nominal` and the figures of merit are deterministic.
`L_effective` is a Monte-Carlo estimate (Gaussian macroparticle sampling with no fixed seed)
and varies by a few tenths of a percent (~0.4% for this EIC case) from run to run, so the
exact `L_effective`, `Ratio eff/nom` and `Compute time` differ slightly each time. `Peak memory`
is the growth of the used Java heap between a forced garbage collection before the run and the
end of the run (no collection at the end), so it mostly reflects allocation churn and is not a
memory requirement; every published run fits within the `-Xmx4g` heap used by the scripts.

For an exactly reproducible check, the deterministic figures of merit (disruption `D_y`
and the beam–beam tune shifts `ξ`) are printed by `-fom`:
```
java -cp ".:gson-2.9.1.jar:bin" org.hepforge.alohep.HeadlessLauncher \
  -left proton-ele/EIC-PDG1 -right electron-ring/EIC-PDG -fom
```
output — `D_y, ξ_x, ξ_y, ξ_x(code), ξ_y(code)`:
```
1.341981,0.072447,0.099672,0.072447,0.099672
```

### Reproducing the paper benchmarks
The `run_*.sh` scripts regenerate the CSV files in `bench_results/` that back the paper's
tables and figures (mapped in the paper's "Benchmark data mapping" appendix). For example,
`bash run_asymmetric.sh` regenerates the HERA and EIC hourglass+pinch results (it rewrites
`results_asymmetric.csv`), after which `bash run_asymmetric_hgonly.sh` appends the
hourglass-only control rows, and `bash run_benchmark.sh` regenerates the grid-convergence and
scaling tables. `bash run_all.sh` chains all of them in the right order.
