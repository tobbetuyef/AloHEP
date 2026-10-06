package org.hepforge.alohep;

import org.hepforge.alohep.calc.Bunch;
import org.hepforge.alohep.calc.LuminosityCalc;
import org.hepforge.alohep.calc.MacroParticle;
import org.hepforge.alohep.calc.Particle;
import org.hepforge.alohep.calc.SimulationConfig;
import org.hepforge.alohep.calc.Vector3d;
import org.hepforge.alohep.database.AcceleratorData;
import org.hepforge.alohep.database.DataManager;
import org.hepforge.alohep.database.ParticleData;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class HeadlessLauncher {

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        try {
            LinkedHashMap<String, String> params = parseArgs(args);

            DataManager dataManager = new DataManager();

            SimulationConfig config = new SimulationConfig();
            if (params.containsKey("macrop"))
                config.settings.put("NumMacPar", Double.parseDouble(params.get("macrop")));
            if (params.containsKey("xres"))
                config.settings.put("XRes", Double.parseDouble(params.get("xres")));
            if (params.containsKey("yres"))
                config.settings.put("YRes", Double.parseDouble(params.get("yres")));
            if (params.containsKey("zres"))
                config.settings.put("ZRes", Double.parseDouble(params.get("zres")));
            if (params.containsKey("ipscale"))
                config.settings.put("IPScale", Double.parseDouble(params.get("ipscale")));
            if (params.containsKey("chargeDimScaleMP"))
                config.settings.put("chargeDimScaleMP", Double.parseDouble(params.get("chargeDimScaleMP")));
            if (params.containsKey("crossing"))
                config.settings.put("crossingAngle", Double.parseDouble(params.get("crossing")));

            config.simulation = !params.containsKey("nosimulation");
            config.decay = params.containsKey("decay");
            config.hourglass = params.containsKey("hourglass");
            config.pinch = params.containsKey("pinch");
            config.beamstrahlung = params.containsKey("beamstrahlung");
            config.crossingAngle = !params.containsKey("nocrossing");
            config.cubicInterpolation = !params.containsKey("linearinterp");

            String leftSpec = params.containsKey("left") ? params.get("left") : "electron-linac/ILC-125";
            String rightSpec = params.containsKey("right") ? params.get("right") : "electron-linac/ILC-125";

            String[] leftParts = leftSpec.split("/", 2);
            String[] rightParts = rightSpec.split("/", 2);

            String leftFolder = leftParts[0];
            String leftAcc = leftParts.length > 1 ? leftParts[1] : null;
            String rightFolder = rightParts[0];
            String rightAcc = rightParts.length > 1 ? rightParts[1] : null;

            ParticleData leftPD = dataManager.getParticleDataMap().get(leftFolder);
            ParticleData rightPD = dataManager.getParticleDataMap().get(rightFolder);
            if (leftPD == null) {
                System.err.println("ERROR: particle folder '" + leftFolder + "' not found in save/");
                System.exit(1);
            }
            if (rightPD == null) {
                System.err.println("ERROR: particle folder '" + rightFolder + "' not found in save/");
                System.exit(1);
            }

            if (leftAcc == null) {
                Map.Entry<String, AcceleratorData> first = leftPD.getAccelerators().entrySet().iterator().next();
                leftAcc = first.getKey();
            }
            if (rightAcc == null) {
                Map.Entry<String, AcceleratorData> first = rightPD.getAccelerators().entrySet().iterator().next();
                rightAcc = first.getKey();
            }

            AcceleratorData leftAccData = leftPD.getAccelerators().get(leftAcc);
            AcceleratorData rightAccData = rightPD.getAccelerators().get(rightAcc);
            if (leftAccData == null) {
                System.err.println("ERROR: accelerator '" + leftAcc + "' not found in " + leftFolder);
                System.exit(1);
            }
            if (rightAccData == null) {
                System.err.println("ERROR: accelerator '" + rightAcc + "' not found in " + rightFolder);
                System.exit(1);
            }

            String leftParticleType = leftFolder.contains("-") ? leftFolder.substring(0, leftFolder.indexOf("-")) : leftFolder;
            String rightParticleType = rightFolder.contains("-") ? rightFolder.substring(0, rightFolder.indexOf("-")) : rightFolder;

            LuminosityCalc lumCalc = new LuminosityCalc(config);

            Particle leftParticle = lumCalc.getParticles().get(leftParticleType);
            Particle rightParticle = lumCalc.getParticles().get(rightParticleType);
            if (leftParticle == null) {
                System.err.println("ERROR: particle type '" + leftParticleType + "' not recognized. Known: " + lumCalc.getParticles().keySet());
                System.exit(1);
            }
            if (rightParticle == null) {
                System.err.println("ERROR: particle type '" + rightParticleType + "' not recognized. Known: " + lumCalc.getParticles().keySet());
                System.exit(1);
            }

            if (params.containsKey("betax")) {
                double v = Double.parseDouble(params.get("betax"));
                leftAccData.put("BetaHor", v);
                rightAccData.put("BetaHor", v);
            }
            if (params.containsKey("betay")) {
                double v = Double.parseDouble(params.get("betay"));
                leftAccData.put("BetaVer", v);
                rightAccData.put("BetaVer", v);
            }
            if (params.containsKey("bunlen")) {
                double v = Double.parseDouble(params.get("bunlen"));
                leftAccData.put("BunLen", v);
                rightAccData.put("BunLen", v);
            }
            if (params.containsKey("npar")) {
                double v = Double.parseDouble(params.get("npar"));
                leftAccData.put("NumParInBun", v);
                rightAccData.put("NumParInBun", v);
            }

            lumCalc.setHeadlessLeft(leftParticle, leftAccData);
            lumCalc.setHeadlessRight(rightParticle, rightAccData);

            if (params.containsKey("fom")) {
                lumCalc.init();
                Bunch L = lumCalc.getBunchL();
                Bunch R = lumCalc.getBunchR();
                double dy = lumCalc.getDisruption(L, R)[1];
                double Z = Math.abs(L.getParticle().getCharge());
                double N = L.getNumberP();
                double rad = R.getParticle().getRad();
                double gamma = R.getGamma();
                Vector3d sL = L.getSigma();
                double xix = (Z * N * rad * R.getBeta().x) / (2 * Math.PI * gamma * sL.x * (sL.x + sL.y));
                double xiy = (Z * N * rad * R.getBeta().y) / (2 * Math.PI * gamma * sL.y * (sL.x + sL.y));
                double[] bb = lumCalc.getBeamBeam(L, R);
                System.out.println(String.format(Locale.US, "%.6f,%.6f,%.6f,%.6f,%.6f",
                        dy, xix, xiy, bb[0], bb[1]));
                return;
            }

            if (params.containsKey("bsreport")) {
                config.beamstrahlung = true;
                if (!config.pinch) {
                    config.pinch = true;
                }
                lumCalc.start();
                lumCalc.run();
                double giL = lumCalc.getBunchL().getGamma();
                double gfL = meanGamma(lumCalc.getBunchL());
                double giR = lumCalc.getBunchR().getGamma();
                double gfR = meanGamma(lumCalc.getBunchR());
                double dL = (giL > 0) ? 1.0 - gfL / giL : 0.0;
                double dR = (giR > 0) ? 1.0 - gfR / giR : 0.0;
                double tMs = lumCalc.getRunTimeMs();
                System.out.println(String.format(Locale.US, "%.6e,%.6e,%.6e,%.6e,%.6e,%.6e,%.3f",
                        giL, gfL, dL, giR, gfR, dR, tMs));
                return;
            }

            lumCalc.start();
            lumCalc.run();

            double luminosity = lumCalc.getLuminosity();
            double luminosityRaw = lumCalc.getLuminosityRaw();
            double sqrtS = lumCalc.getSqrtS();
            double timeMs = lumCalc.getRunTimeMs();
            long peakMemBytes = lumCalc.getPeakMemoryBytes();

            boolean csv = params.containsKey("csv");
            boolean profile = params.containsKey("profile");

            if (csv || profile) {
                int numMacPar = (int) config.getSettingsData("NumMacPar");
                int xRes = (int) config.getSettingsData("XRes");
                int yRes = (int) config.getSettingsData("YRes");
                int zRes = (int) config.getSettingsData("ZRes");
                if (profile) {
                    LuminosityCalc.Timings t = lumCalc.getTimings();
                    String interp = config.cubicInterpolation ? "cubic" : "linear";
                    System.out.println(numMacPar + "," + xRes + "," + yRes + "," + zRes + ","
                        + interp + ","
                        + String.format("%.3f", t.totalMs()) + ","
                        + String.format("%.3f", t.initMPsMs()) + ","
                        + String.format("%.3f", t.updateQMs()) + ","
                        + String.format("%.3f", t.updateFiMs()) + ","
                        + String.format("%.3f", t.updateForceMs()) + ","
                        + String.format("%.3f", t.collisionMs()) + ","
                        + String.format("%.3f", t.pinchMs()) + ","
                        + String.format("%.3f", t.bunchUpdateMs()) + ","
                        + String.format("%.6e", luminosity) + ","
                        + String.format("%.6e", luminosityRaw) + ","
                        + String.format("%.6e", sqrtS) + ","
                        + t.peakMemoryBytes);
                } else {
                    String interp = config.cubicInterpolation ? "cubic" : "linear";
                    System.out.println(numMacPar + "," + xRes + "," + yRes + "," + zRes + ","
                        + interp + ","
                        + String.format("%.3f", timeMs) + ","
                        + String.format("%.6e", luminosity) + ","
                        + String.format("%.6e", luminosityRaw) + ","
                        + String.format("%.6e", sqrtS) + ","
                        + peakMemBytes);
                }
            } else {
                System.out.println("=== ALOHEP Headless Results ===");
                System.out.println("Left:  " + leftFolder + "/" + leftAcc + " (" + leftParticleType + ")");
                System.out.println("Right: " + rightFolder + "/" + rightAcc + " (" + rightParticleType + ")");
                System.out.println("Macroparticles: " + (int) config.getSettingsData("NumMacPar"));
                System.out.println("Grid: " + (int) config.getSettingsData("XRes") + "x" + (int) config.getSettingsData("YRes") + "x" + (int) config.getSettingsData("ZRes"));
                System.out.println("Crossing:     " + config.crossingAngle);
                System.out.println("Interp:       " + (config.cubicInterpolation ? "bicubic" : "bilinear"));
                System.out.println("Simulation:   " + config.simulation);
                System.out.println("Pinch:        " + config.pinch);
                System.out.println("Hourglass:    " + config.hourglass);
                System.out.println("Beamstrahlung:" + config.beamstrahlung);
                System.out.println("Decay:        " + config.decay);
                System.out.println("sqrt(s):      " + String.format("%.6e", sqrtS) + " GeV");
                System.out.println("L_nominal:    " + String.format("%.6e", luminosityRaw) + " cm^-2 s^-1");
                System.out.println("L_effective:  " + String.format("%.6e", luminosity) + " cm^-2 s^-1");
                if (luminosityRaw != 0) {
                    System.out.println("Ratio eff/nom: " + String.format("%.6f", luminosity / luminosityRaw));
                }
                System.out.println("Compute time: " + String.format("%.3f", timeMs) + " ms");
                System.out.println("Peak memory:  " + (peakMemBytes / 1024 / 1024) + " MB");
                LuminosityCalc.Timings t = lumCalc.getTimings();
                if (t != null) {
                    System.out.println("--- Per-Phase Breakdown ---");
                    System.out.println("initMPs:       " + String.format("%.3f", t.initMPsMs()) + " ms");
                    System.out.println("updateQ:       " + String.format("%.3f", t.updateQMs()) + " ms");
                    System.out.println("updateFi:      " + String.format("%.3f", t.updateFiMs()) + " ms");
                    System.out.println("updateForce:   " + String.format("%.3f", t.updateForceMs()) + " ms");
                    System.out.println("collision:     " + String.format("%.3f", t.collisionMs()) + " ms");
                    System.out.println("pinch:         " + String.format("%.3f", t.pinchMs()) + " ms");
                    System.out.println("bunchUpdate:   " + String.format("%.3f", t.bunchUpdateMs()) + " ms");
                }
            }

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            System.exit(2);
        }
    }

    private static LinkedHashMap<String, String> parseArgs(String[] args) {
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                String key = args[i].substring(1);
                if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                    params.put(key, args[i + 1]);
                    i++;
                } else {
                    params.put(key, "");
                }
            }
        }
        return params;
    }

    private static double meanGamma(Bunch b) {
        double sum = 0;
        double cnt = 0;
        for (MacroParticle mp : b.getMPs()) {
            sum += mp.getGamma();
            cnt += 1;
        }
        return cnt > 0 ? sum / cnt : 0;
    }
}
