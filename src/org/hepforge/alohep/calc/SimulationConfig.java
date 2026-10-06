package org.hepforge.alohep.calc;

import java.util.LinkedHashMap;

public class SimulationConfig {

    public boolean simulation = true;
    public boolean decay = false;
    public boolean hourglass = false;
    public boolean pinch = false;
    public boolean beamstrahlung = false;
    public boolean crossingAngle = true;
    public boolean cubicInterpolation = true;

    public LinkedHashMap<String, Double> settings = new LinkedHashMap<String, Double>();

    public SimulationConfig() {
        settings.put("NumMacPar", 50000.0);
        settings.put("IPScale", 4.0);
        settings.put("XRes", 30.0);
        settings.put("YRes", 30.0);
        settings.put("ZRes", 30.0);
        settings.put("chargeDimScaleMP", 1.0);
        settings.put("crossingAngle", 0.0);
    }

    public double getSettingsData(String name) {
        Double val = settings.get(name);
        return val != null ? val : 0.0;
    }

    public boolean getCheckbox(String name) {
        if ("simulation".equals(name)) return simulation;
        if ("decay".equals(name)) return decay;
        if ("hourglass".equals(name)) return hourglass;
        if ("pinch".equals(name)) return pinch;
        if ("beamstrahlung".equals(name)) return beamstrahlung;
        return false;
    }

    public boolean hasCrossingAngle() {
        return crossingAngle;
    }
}
