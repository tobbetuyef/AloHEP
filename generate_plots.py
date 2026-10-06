#!/usr/bin/env python3


import csv
import os
import sys
import numpy as np
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
BENCH_DIR = os.path.join(SCRIPT_DIR, 'bench_results')
MEDIA_DIR = os.path.join(SCRIPT_DIR, 'media')
DPI = 300

os.makedirs(MEDIA_DIR, exist_ok=True)

plt.rcParams.update({
    'font.family': 'serif',
    'font.serif': ['Times New Roman', 'DejaVu Serif', 'Computer Modern Roman'],
    'font.size': 11,
    'axes.labelsize': 12,
    'axes.titlesize': 11,
    'legend.fontsize': 9.5,
    'xtick.labelsize': 10,
    'ytick.labelsize': 10,
    'figure.dpi': DPI,
    'savefig.dpi': DPI,
    'axes.linewidth': 0.8,
    'lines.linewidth': 1.5,
    'lines.markersize': 6,
    'mathtext.fontset': 'cm',
})


def read_csv(filename):
    path = os.path.join(BENCH_DIR, filename)
    with open(path, newline='') as f:
        reader = csv.DictReader(f)
        rows = []
        for row in reader:
            parsed = {}
            for k, v in row.items():
                if v is None or k is None:
                    continue
                if isinstance(v, list):
                    v = v[0] if v else ''
                try:
                    parsed[k] = float(v)
                except (ValueError, TypeError):
                    parsed[k] = v
            rows.append(parsed)
        return rows


def group_and_mean(rows, key, value_cols):
    groups = {}
    for r in rows:
        groups.setdefault(r[key], []).append(r)
    keys = sorted(groups.keys())
    means = {col: [] for col in value_cols}
    individuals = {col: {} for col in value_cols}
    for k in keys:
        for col in value_cols:
            vals = [r[col] for r in groups[k]]
            means[col].append(np.mean(vals))
            individuals[col][k] = vals
    return keys, means, individuals


grid_rows = read_csv('results_grid_convergence.csv')
np_rows = read_csv('results_np_scaling.csv')

grid_ng, grid_means, grid_indiv = group_and_mean(
    grid_rows, 'XRes',
    ['L_eff', 'L_nom', 'total_ms', 'updateFi_ms']
)
grid_ratio = np.array([
    grid_means['L_eff'][i] / grid_means['L_nom'][i]
    for i in range(len(grid_ng))
])
grid_ratio_scatter = {}
for i, ng in enumerate(grid_ng):
    nom = grid_means['L_nom'][i]
    grid_ratio_scatter[ng] = [v / nom for v in grid_indiv['L_eff'][ng]]

np_nmp, np_means, _ = group_and_mean(
    np_rows, 'NumMacPar',
    ['total_ms', 'updateQ_ms', 'updateFi_ms']
)

ng_arr = np.array(grid_ng, dtype=float)
fi_ms = np.array(grid_means['updateFi_ms'])


fig1, ax1 = plt.subplots(figsize=(4.8, 3.6))

for ng in grid_ng:
    pts = grid_ratio_scatter[ng]
    ax1.scatter([ng] * len(pts), pts, color='#4477AA', alpha=0.30, s=28,
                zorder=2, edgecolors='none', label='Individual runs'
                if ng == grid_ng[0] else '')

ax1.plot(grid_ng, grid_ratio, 'o-', color='#4477AA',
         markerfacecolor='#4477AA', markeredgecolor='white',
         markeredgewidth=0.8, zorder=3, label='Mean of 3 runs')

ax1.axhline(1.0, color='#888888', linestyle='--', linewidth=1.0, zorder=1,
            label='Exact agreement')

ax1.set_xlabel(r'$N_g$  (grid points per axis)')
ax1.set_ylabel(r'$\mathcal{L}_{\rm eff}\,/\,\mathcal{L}_{\rm nom}$')
ax1.set_xlim(-5, 115)
ax1.set_xticks([10, 30, 50, 100])
ax1.set_ylim(0.93, 1.03)
ax1.legend(loc='lower right', framealpha=0.9, edgecolor='#cccccc')
ax1.grid(True, alpha=0.25, linestyle='-')

fig1.tight_layout()
fig1.savefig(os.path.join(MEDIA_DIR, 'fig_convergence.png'), bbox_inches='tight')
print('Saved media/fig_convergence.png')
plt.close(fig1)


fig2, (ax2a, ax2b) = plt.subplots(1, 2, figsize=(10.0, 3.9))

nmp = np.array(np_nmp)
total_s = np.array(np_means['total_ms']) / 1e3
q_s = np.array(np_means['updateQ_ms']) / 1e3
fi_s_np = np.array(np_means['updateFi_ms']) / 1e3

ax2a.loglog(nmp, total_s, 'o-', color='#4477AA', markeredgecolor='white',
            markeredgewidth=0.6, label='Total runtime')
ax2a.loglog(nmp, fi_s_np, 's-', color='#EE6677', markeredgecolor='white',
            markeredgewidth=0.6, label='Potential solve (updateFi)')
ax2a.loglog(nmp, q_s, '^-', color='#228833', markeredgecolor='white',
            markeredgewidth=0.6, label='Charge assignment (updateQ)')

ax2a.set_xlabel(r'$N_{mp}$  (macroparticles)')
ax2a.set_ylabel('Time per bunch crossing  (s)')
ax2a.set_title(r'(a)  Macroparticle scaling  ($30^3$ grid)', pad=8)
ax2a.legend(loc='upper left', framealpha=0.9, edgecolor='#cccccc')
ax2a.grid(True, which='both', alpha=0.25, linestyle='-')

fi_s_grid = fi_ms / 1e3

ax2b.loglog(ng_arr, fi_s_grid, 's-', color='#EE6677', markeredgecolor='white',
            markeredgewidth=0.6, label='Potential solve (updateFi)')

ref = fi_s_grid[-1] * (ng_arr / ng_arr[-1]) ** 6
ax2b.loglog(ng_arr, ref, 'k:', linewidth=1.4, label=r'$\propto N_g^6$ reference')

ax2b.set_xlabel(r'$N_g$  (grid points per axis)')
ax2b.set_ylabel('Potential-solve time  (s)')
ax2b.set_title(r'(b)  Grid-resolution scaling  ($5\!\times\!10^4$ macroparticles)', pad=8)
ax2b.legend(loc='upper left', framealpha=0.9, edgecolor='#cccccc')
ax2b.grid(True, which='both', alpha=0.25, linestyle='-')

fig2.tight_layout(w_pad=3.5)
fig2.savefig(os.path.join(MEDIA_DIR, 'fig_scaling.png'), bbox_inches='tight')
print('Saved media/fig_scaling.png')
plt.close(fig2)


import math

def hourglass_H(A):
    A = np.asarray(A, dtype=float)
    H = np.empty_like(A)
    small = A < 1e-6
    H[small] = 1.0
    idx = ~small
    a = A[idx]
    H[idx] = (np.sqrt(np.pi / 2.0) / a) * np.exp(1.0 / (2.0 * a**2)) \
        * np.array([math.erfc(1.0 / (math.sqrt(2.0) * float(x))) for x in a])
    return H

def hourglass_H_flat(A_y_arr, A_x=0.0):
    u = np.linspace(-20, 20, 10000)
    gauss = np.exp(-u**2 / 2) / np.sqrt(2 * np.pi)
    A_y_grid = np.asarray(A_y_arr, dtype=float)[:, np.newaxis]
    u_grid = u[np.newaxis, :]
    weight = 1.0 / (np.sqrt(1 + A_x**2 * u_grid**2)
                    * np.sqrt(1 + A_y_grid**2 * u_grid**2))
    _trapz = getattr(np, 'trapezoid', None) or np.trapz
    return _trapz(gauss[np.newaxis, :] * weight, u, axis=1)

BUNLEN = 3.0e-4
BETA_X_STAR = 0.013
SIGMA_Z = BUNLEN
A_X = SIGMA_Z / BETA_X_STAR

hg_path = os.path.join(BENCH_DIR, 'results_hourglass.csv')
if os.path.exists(hg_path):
    hg_rows = read_csv('results_hourglass.csv')
    hg_groups = {}
    for r in hg_rows:
        hg_groups.setdefault(r['A'], []).append(r['L_eff'] / r['L_nom'])
    A_keys = sorted(hg_groups.keys())
    A_arr = np.array(A_keys, dtype=float)
    H_mean = np.array([np.mean(hg_groups[k]) for k in A_keys])
    H_std = np.array([np.std(hg_groups[k]) for k in A_keys])

    A_dense = np.logspace(np.log10(A_arr.min()), np.log10(A_arr.max()), 200)

    fig3, ax3 = plt.subplots(figsize=(4.8, 3.6))
    ax3.errorbar(A_arr, H_mean, yerr=H_std, fmt='o', color='#4477AA',
                 markeredgecolor='white', markeredgewidth=0.8, capsize=3,
                 zorder=3, label=r'Simulation (mean $\pm$ std)')

    SQRT2 = math.sqrt(2.0)
    ax3.plot(A_dense, hourglass_H_flat(A_dense / SQRT2, A_x=A_X / SQRT2), '-',
             color='#228833', linewidth=1.5, zorder=2,
             label=r'Two-beam $H\!\left(\sigma_z/\sqrt{2}\,\beta_y^*\right)$')

    ax3.set_xlabel(r'$A = \mathrm{BunLen}/\beta_{y}^{*}$')
    ax3.set_ylabel(r'$\mathcal{L}_{\rm eff}\,/\,\mathcal{L}_{0}$')
    ax3.set_xscale('log')
    ax3.legend(loc='lower left', framealpha=0.9, edgecolor='#cccccc')
    ax3.grid(True, which='both', alpha=0.25, linestyle='-')
    fig3.tight_layout()
    fig3.savefig(os.path.join(MEDIA_DIR, 'fig_hourglass.png'), bbox_inches='tight')
    print('Saved media/fig_hourglass.png')
    plt.close(fig3)
else:
    print('Skipping fig_hourglass.png: results_hourglass.csv not found '
          '(run run_hourglass.sh first).', file=sys.stderr)


bs_path = os.path.join(BENCH_DIR, 'results_beamstrahlung.csv')
if os.path.exists(bs_path):
    bs_rows = read_csv('results_beamstrahlung.csv')

    def collect(sweep):
        g = {}
        for r in bs_rows:
            if str(r.get('sweep')) == sweep:
                v = float(r['value'])
                g.setdefault(v, []).extend([r['deltaE_L'], r['deltaE_R']])
        xs = sorted(g)
        return (np.array(xs),
                np.array([np.mean(g[x]) for x in xs]),
                np.array([np.std(g[x]) for x in xs]))

    fig4, (axN, axZ) = plt.subplots(1, 2, figsize=(10.0, 3.9))

    Nx, Ny, Ne = collect('N')
    if len(Nx):
        a = len(Nx) // 2
        guide = Ny[a] * (Nx / Nx[a]) ** 2
        axN.plot(Nx / 1e10, guide, 'k--', linewidth=1.2, zorder=2, label=r'$\propto N^{2}$')
        axN.errorbar(Nx / 1e10, Ny, yerr=Ne, fmt='o', color='#4477AA', capsize=3, zorder=3,
                     markeredgecolor='white', markeredgewidth=0.8, label='Simulation')
        axN.set_xscale('log'); axN.set_yscale('log')
    axN.set_xlabel(r'$N$  ($10^{10}$ particles/bunch)')
    axN.set_ylabel(r'mean $\Delta E / E$')
    axN.set_title(r'(a)  $\Delta E/E$ vs $N$', pad=8)
    axN.legend(loc='upper left', framealpha=0.9, edgecolor='#cccccc')
    axN.grid(True, which='both', alpha=0.25, linestyle='-')

    Zx, Zy, Ze = collect('sigma_z')
    if len(Zx):
        a = len(Zx) // 2
        guide = Zy[a] * (Zx[a] / Zx)
        axZ.plot(Zx * 1e6, guide, 'k--', linewidth=1.2, zorder=2, label=r'$\propto 1/\sigma_z$')
        axZ.errorbar(Zx * 1e6, Zy, yerr=Ze, fmt='s', color='#EE6677', capsize=3, zorder=3,
                     markeredgecolor='white', markeredgewidth=0.8, label='Simulation')
        axZ.set_xscale('log'); axZ.set_yscale('log')
    axZ.set_xlabel(r'$\sigma_z = \mathrm{BunLen}$  ($\mu$m)')
    axZ.set_ylabel(r'mean $\Delta E / E$')
    axZ.set_title(r'(b)  $\Delta E/E$ vs $\sigma_z$', pad=8)
    axZ.legend(loc='upper right', framealpha=0.9, edgecolor='#cccccc')
    axZ.grid(True, which='both', alpha=0.25, linestyle='-')

    fig4.tight_layout(w_pad=3.0)
    fig4.savefig(os.path.join(MEDIA_DIR, 'fig_beamstrahlung.png'), bbox_inches='tight')
    print('Saved media/fig_beamstrahlung.png')
    plt.close(fig4)
else:
    print('Skipping fig_beamstrahlung.png: results_beamstrahlung.csv not found '
          '(run run_beamstrahlung.sh first).', file=sys.stderr)


cr_path = os.path.join(BENCH_DIR, 'results_crossing.csv')
if os.path.exists(cr_path):
    cr_rows = read_csv('results_crossing.csv')
    cr_groups = {}
    for r in cr_rows:
        cr_groups.setdefault(r['theta'], []).append(r['L_eff'] / r['L_nom'])
    th_keys = sorted(cr_groups.keys(), key=float)
    th_arr = np.array([float(k) for k in th_keys])
    R_mean = np.array([np.mean(cr_groups[k]) for k in th_keys])
    R_std = np.array([np.std(cr_groups[k]) for k in th_keys])

    gamma_e = 125.0e9 / 0.510998950e6
    eps_y = 3.5e-8 / gamma_e
    sig_y = math.sqrt(eps_y * 4.0e-4)
    sig_z = 3.0e-4
    th_dense = np.linspace(th_arr.min(), th_arr.max(), 300)
    Phi = (sig_z / sig_y) * np.tan(th_dense)
    S = 1.0 / np.sqrt(1.0 + Phi**2)

    fig5, ax5 = plt.subplots(figsize=(4.8, 3.6))
    ax5.plot(th_dense * 1e6, S, '-', color='#228833', linewidth=1.5, zorder=2,
             label=r'Piwinski $S=1/\sqrt{1+\Phi^{2}}$')
    ax5.errorbar(th_arr * 1e6, R_mean, yerr=R_std, fmt='o', color='#4477AA',
                 markeredgecolor='white', markeredgewidth=0.8, capsize=3, zorder=3,
                 label=r'Simulation (mean $\pm$ std)')
    ax5.set_xlabel(r'Half crossing angle $\theta$  ($\mu$rad)')
    ax5.set_ylabel(r'$\mathcal{L}_{\rm eff}\,/\,\mathcal{L}_{0}$')
    ax5.legend(loc='upper right', framealpha=0.9, edgecolor='#cccccc')
    ax5.grid(True, alpha=0.25, linestyle='-')
    fig5.tight_layout()
    fig5.savefig(os.path.join(MEDIA_DIR, 'fig_crossing.png'), bbox_inches='tight')
    print('Saved media/fig_crossing.png')
    plt.close(fig5)
else:
    print('Skipping fig_crossing.png: results_crossing.csv not found '
          '(run run_crossing.sh first).', file=sys.stderr)


def _mean_ratio(fname, filt=None):
    p = os.path.join(BENCH_DIR, fname)
    if not os.path.exists(p):
        return None
    vals = []
    for r in read_csv(fname):
        if filt is not None and not filt(r):
            continue
        try:
            vals.append(float(r['L_eff']) / float(r['L_nom']))
        except (KeyError, ValueError, TypeError, ZeroDivisionError):
            pass
    return float(np.mean(vals)) if vals else None

_specs = [
    (r'$e^{+}e^{-}$' + '\n(ILC)',  'results_grid_convergence.csv',
        lambda r: int(float(r.get('XRes', 0))) == 50),
    ('Pb-Pb\n(LHC)',  'results_heavyion.csv', lambda r: str(r.get('test')) == 'pbpb_lhc_geom'),
    ('Au-Au\n(RHIC)', 'results_heavyion.csv', lambda r: str(r.get('test')) == 'auau_rhic_geom'),
    (r'$\mu^{+}\mu^{-}$' + '\n(10 TeV)', 'results_muon.csv',
        lambda r: str(r.get('test')) == 'muon_nodecay'),
]
_summary = [(lab, _mean_ratio(f, flt)) for lab, f, flt in _specs]
_summary = [(lab, v) for lab, v in _summary if v is not None]
if _summary:
    labels = [s[0] for s in _summary]
    ratios = [s[1] for s in _summary]
    fig6, ax6 = plt.subplots(figsize=(5.4, 3.6))
    ax6.bar(range(len(labels)), ratios, color='#4477AA', edgecolor='white', width=0.6, zorder=3)
    ax6.axhline(1.0, color='#888888', linestyle='--', linewidth=1.0, zorder=2,
                label='analytic geometric $\\mathcal{L}$')
    ax6.set_xticks(range(len(labels)))
    ax6.set_xticklabels(labels)
    ax6.set_ylabel(r'$\mathcal{L}_{\rm eff}\,/\,\mathcal{L}_{\rm nom}$  (head-on)')
    ax6.set_ylim(0.9, 1.1)
    ax6.legend(loc='upper right', framealpha=0.9, edgecolor='#cccccc')
    ax6.grid(True, axis='y', alpha=0.25, linestyle='-')
    fig6.tight_layout()
    fig6.savefig(os.path.join(MEDIA_DIR, 'fig_species.png'), bbox_inches='tight')
    print('Saved media/fig_species.png')
    plt.close(fig6)
else:
    print('Skipping fig_species.png: no heavy-ion/muon CSVs yet '
          '(run run_heavyion.sh / run_muon.sh first).', file=sys.stderr)


import json as _json

def _beam_sigmas(json_path, mass_eV):
    with open(os.path.join(SCRIPT_DIR, json_path)) as f:
        v = _json.load(f)['variables']
    gamma = v['EnBeam'] * 1.0e9 / mass_eV
    ex = v['EmNorHor'] / gamma if 'EmNorHor' in v else v['EmHor']
    ey = v['EmNorVer'] / gamma if 'EmNorVer' in v else v['EmVer']
    return (math.sqrt(ex * v['BetaHor']), math.sqrt(ey * v['BetaVer']),
            v['BunLen'], v['BetaHor'], v['BetaVer'])

def _convolved_factor(b1, b2):
    return (2 * max(b1[0], b2[0]) * max(b1[1], b2[1])
            / (math.hypot(b1[0], b2[0]) * math.hypot(b1[1], b2[1])))

def _H_unequal(b1, b2):
    szc = math.sqrt(b1[2]**2 + b2[2]**2) / 2.0
    u = np.linspace(-8, 8, 20001)
    w = np.exp(-u**2 / 2) / math.sqrt(2 * math.pi)
    s = u * szc
    sx2 = b1[0]**2 * (1 + s**2 / b1[3]**2) + b2[0]**2 * (1 + s**2 / b2[3]**2)
    sy2 = b1[1]**2 * (1 + s**2 / b1[4]**2) + b2[1]**2 * (1 + s**2 / b2[4]**2)
    _trapz = getattr(np, 'trapezoid', None) or np.trapz
    return float(_trapz(w * math.sqrt(sx2[u == 0][0] * sy2[u == 0][0])
                        / np.sqrt(sx2 * sy2), u))

_M_P = 938.28e6
_M_E = 0.510998950e6

asym_path = os.path.join(BENCH_DIR, 'results_asymmetric.csv')
if os.path.exists(asym_path):
    _asym_specs = [
        ('HERA ($ep$)\n$\\sqrt{s}=318.7$ GeV', 'hera_ep_hgonly', 'hera_ep',
         _beam_sigmas('save/proton-ele/HERA.JSON', _M_P),
         _beam_sigmas('save/electron-ring/HERA.JSON', _M_E)),
        ('EIC ($ep$)\n$\\sqrt{s}=105$ GeV', 'eic_ep_hgonly', 'eic_ep',
         _beam_sigmas('save/proton-ele/EIC-PDG1.JSON', _M_P),
         _beam_sigmas('save/electron-ring/EIC-PDG.JSON', _M_E)),
    ]
    asym_rows = read_csv('results_asymmetric.csv')
    _labels, _hg_means, _hg_stds, _fp_means, _fp_stds, _analytic = [], [], [], [], [], []
    for lab, test_hg, test_fp, bp, be in _asym_specs:
        fac = _convolved_factor(bp, be)
        hg_ratios = [r['L_eff'] / (r['L_nom'] * fac) for r in asym_rows
                     if str(r.get('test')) == test_hg]
        fp_ratios = [r['L_eff'] / (r['L_nom'] * fac) for r in asym_rows
                     if str(r.get('test')) == test_fp]
        if not hg_ratios or not fp_ratios:
            continue
        _labels.append(lab)
        _hg_means.append(float(np.mean(hg_ratios)))
        _hg_stds.append(float(np.std(hg_ratios)))
        _fp_means.append(float(np.mean(fp_ratios)))
        _fp_stds.append(float(np.std(fp_ratios)))
        _analytic.append(_H_unequal(bp, be))
    if _labels:
        x = np.arange(len(_labels))
        w = 0.32
        fig7, ax7 = plt.subplots(figsize=(5.6, 3.9))
        ax7.bar(x - w / 2 - 0.02, _hg_means, yerr=_hg_stds, capsize=3, color='#88BBDD',
                edgecolor='white', width=w, zorder=3, error_kw={'zorder': 4},
                label='Simulation (hourglass only)')
        ax7.bar(x + w / 2 + 0.02, _fp_means, yerr=_fp_stds, capsize=3, color='#4477AA',
                edgecolor='white', width=w, zorder=3, error_kw={'zorder': 4},
                hatch='///', label='Simulation (hourglass + pinch)')
        for i, h in enumerate(_analytic):
            ax7.hlines(h, x[i] - w - 0.05, x[i] + w + 0.05, color='#228833',
                       linewidth=2.2, zorder=5,
                       label='Analytic hourglass-only' if i == 0 else '')
        ax7.axhline(1.0, color='#888888', linestyle='--', linewidth=1.0, zorder=2,
                    label='Geometric $\\mathcal{L}_{\\rm geo}$ (rigid beams)')
        ax7.set_xticks(x)
        ax7.set_xticklabels(_labels)
        ax7.set_ylabel(r'$\mathcal{L}_{\rm eff}\,/\,\mathcal{L}_{\rm geo}$')
        ax7.set_ylim(0.85, 1.05)
        _h, _l = ax7.get_legend_handles_labels()
        _order = [_l.index('Simulation (hourglass only)'),
                  _l.index('Simulation (hourglass + pinch)'),
                  _l.index('Analytic hourglass-only'),
                  _l.index('Geometric $\\mathcal{L}_{\\rm geo}$ (rigid beams)')]
        ax7.legend([_h[i] for i in _order], [_l[i] for i in _order],
                   loc='lower left', framealpha=0.9, edgecolor='#cccccc', fontsize=7.5)
        ax7.grid(True, axis='y', alpha=0.25, linestyle='-')
        fig7.tight_layout()
        fig7.savefig(os.path.join(MEDIA_DIR, 'fig_asymmetric.png'), bbox_inches='tight')
        print('Saved media/fig_asymmetric.png')
        plt.close(fig7)
else:
    print('Skipping fig_asymmetric.png: results_asymmetric.csv not found '
          '(run run_asymmetric.sh and run_asymmetric_hgonly.sh first).', file=sys.stderr)


print('\nDone.  Figures written to media/')
