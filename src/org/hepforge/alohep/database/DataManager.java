package org.hepforge.alohep.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

public class DataManager {

	private LinkedHashMap<String, VariableData> variableDataMap;
	private LinkedHashMap <String, Double> settingsData;
	private HashMap<String, ParticleData> particleDataMap;
	private String save = "save";
	public DataManager()
	{
		variableDataMap = new LinkedHashMap<String, VariableData>();
		settingsData = new LinkedHashMap<String, Double>();
		particleDataMap = new HashMap<String, ParticleData>();

		loadVariableData();
		try {
			loadFromJSON();
			loadSettingsFromJSON();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void loadFromJSON() throws IOException
	{
		Gson gson = new Gson();
		File file = new File(save);
		if(!file.exists())
			file.mkdir();
		for(String folder1 : file.list())
		{
			String path1 = save+"/"+folder1;
			if(path1.contains("."))
				continue;
			file = new File(path1);
			ParticleData pd = new ParticleData();
			if(folder1.contains("linac"))
				pd.setLinear();
			particleDataMap.put(folder1, pd);

			if(file.list().length == 0)
			{
				file.delete();
				continue;
			}
			for(String folder2 : file.list())
			{
				String path2 = path1+"/"+folder2;
				file = new File(path2);
				FileReader fileReader = new FileReader(file);
				JsonReader jsonReader = new JsonReader(fileReader);
				AcceleratorData acc = gson.fromJson(jsonReader, AcceleratorData.class);

				particleDataMap.get(folder1).getAccelerators().put(folder2.substring(0,folder2.indexOf('.')), acc);
			}
		}
	}
	public void removeAccFromJSON(String particle, String name)
	{
		File file = new File(save+"/"+particle+"/"+name+".JSON");
		if(file.exists())
			file.delete();
	}
	public void saveNewAccToJSON(String particle, String name, AcceleratorData accData) throws IOException
	{
		String path = save+"/"+particle;
		File file = new File(path);

		if(!file.exists())
		{
			file.mkdir();
		}

		try (Writer writer = new FileWriter(path+"/"+name+".JSON")) {
		    Gson gson = new GsonBuilder().create();
		    gson.toJson(accData, writer);
		}
	}

	public void loadSettingsFromJSON() throws FileNotFoundException
	{
		Gson gson = new Gson();
		File file = new File(save+"/settings.JSON");
		FileReader fileReader;
		fileReader = new FileReader(file);

		JsonReader jsonReader = new JsonReader(fileReader);
		settingsData = gson.fromJson(jsonReader, settingsData.getClass());

	}

	public void saveSettingsToJSON() throws IOException
	{
		File file = new File(save);
		if(!file.exists())
			file.mkdir();
		try (Writer writer = new FileWriter(save+"/settings.JSON")) {
		    Gson gson = new GsonBuilder().create();
		    gson.toJson(settingsData, writer);
		}

	}
	public void loadVariableData()
	{
		variableDataMap.put("NumParInBun", new VariableData("Number of particle per bunch(N):",""));
		variableDataMap.put("EnBeam", new VariableData("Particle beam energy:","GeV"));
		variableDataMap.put("BetaVer", new VariableData("Vertical Beta function of particle beam at IP:","m"));
		variableDataMap.put("BetaHor", new VariableData("Horizontal Beta function of particle beam at IP:","m"));
		variableDataMap.put("Beta", new VariableData("Beta function of particle beam at IP:","m"));
		variableDataMap.put("EmNorVer", new VariableData("Norm. Vertical Emittance of particle beam:","m"));
		variableDataMap.put("EmNorHor", new VariableData("Norm. Horizontal Emittance of particle beam:","m"));
		variableDataMap.put("EmVer", new VariableData("Trans. Vertical Emittance of particle beam:","m"));
		variableDataMap.put("EmHor", new VariableData("Trans. Horizontal Emittance of particle beam:","m"));
		variableDataMap.put("EmNor", new VariableData("Norm. Emittance of particle beam:","m"));
		variableDataMap.put("PulFrq", new VariableData("Pulse Frequency of beams:","Hz"));
		variableDataMap.put("RevFrq", new VariableData("Revolution Frequency of beam:","Hz"));
		variableDataMap.put("RepFrq", new VariableData("Repetition Rate of beam:","Hz"));
		variableDataMap.put("ColFrq", new VariableData("Collision Frequency of beams:","Hz"));

		variableDataMap.put("NumBunInBeam", new VariableData("Bunches in particle beam:",""));
		variableDataMap.put("BunLen", new VariableData("Particle Beam Bunch Length:","m"));
		variableDataMap.put("BunSpace", new VariableData("Bunch Spacing of Particle Beam:","m"));
		variableDataMap.put("Circum", new VariableData("Circumference:","km"));
		variableDataMap.put("Turn", new VariableData("Number of Turns:","turn"));
		variableDataMap.put("DutyFac", new VariableData("Duty Factor:",""));

		variableDataMap.put("PowLim", new VariableData("Power Limit for Particle Beam:","MW"));
		variableDataMap.put("DisrLim", new VariableData("Disruption Limit:",""));
		variableDataMap.put("BeamParLim", new VariableData("Beam-Beam Parameter Limit:",""));
		variableDataMap.put("NumMacPar", new VariableData("Number of Macroparticles:",""));
		variableDataMap.put("IPScale", new VariableData("Scale of Sigma:",""));
		variableDataMap.put("XRes", new VariableData("Resolution of X-axis:",""));
		variableDataMap.put("YRes", new VariableData("Resolution of Y-axis:",""));
		variableDataMap.put("ZRes", new VariableData("Resolution of Z-axis:",""));
		variableDataMap.put("chargeDimScaleMP", new VariableData("Cloud scale of Macroparticle:",""));
		variableDataMap.put("crossingAngle", new VariableData("Crossing angle of Collision:","rad"));

		variableDataMap.put("sqrtS", new VariableData("<html>Center-of-mass, &radic;<span style=\"text-decoration: overline\">S</span>: <html>","GeV"));
		variableDataMap.put("noLum", new VariableData("Nominal Luminosity: ","<html>cm<sup style=\"font-size:8px\">-2</sup>s<sup style=\"font-size:8px\">-1</sup><html>"));
		variableDataMap.put("efLum", new VariableData("Effective Luminosity: ","<html>cm<sup style=\"font-size:8px\">-2</sup>s<sup style=\"font-size:8px\">-1</sup><html>"));
		variableDataMap.put("reFac", new VariableData("Enhancement/Reduction Factor: ",""));

		variableDataMap.put("sigmaX", new VariableData("<html>  SigmaX (&sigma<sub>x</sub>): <html>","m"));
		variableDataMap.put("sigmaY", new VariableData("<html>  SigmaY (&sigma<sub>y</sub>): <html>","m"));

		variableDataMap.put("disX", new VariableData("<html>  Disruption (D<sub>x</sub>): <html>",""));
		variableDataMap.put("disY", new VariableData("<html>  Disruption (D<sub>y</sub>): <html>",""));
		variableDataMap.put("divX", new VariableData("<html>  Divergence (A<sub>x</sub>): <html>",""));
		variableDataMap.put("divY", new VariableData("<html>  Divergence (A<sub>y</sub>): <html>",""));
		variableDataMap.put("bbTunX", new VariableData("<html>  BB Tuneshift (&xi<sub>x</sub>): <html>",""));
		variableDataMap.put("bbTunY", new VariableData("<html>  BB Tuneshift (&xi<sub>y</sub>): <html>",""));

	}

	public void loadSettingsData()
	{

		settingsData.put("NumMacPar", 50000.0);
		settingsData.put("IPScale", 3.0);
		settingsData.put("XRes", 30.0);
		settingsData.put("YRes", 30.0);
		settingsData.put("ZRes", 30.0);
		settingsData.put("chargeDimScaleMP", 1.0);
		settingsData.put("crossingAngle", 0.0);

	}

	public String getTitle(String key)
	{
		return variableDataMap.get(key).getTitle();
	}
	public String getUnit(String key)
	{
		return variableDataMap.get(key).getUnit();
	}
	public LinkedHashMap<String, VariableData> getVariableDataMap() {
		return variableDataMap;
	}
	public void setVariableDataMap(LinkedHashMap<String, VariableData> variableDataMap) {
		this.variableDataMap = variableDataMap;
	}
	public LinkedHashMap<String, Double> getSettingsData() {
		return settingsData;
	}
	public void setSettingsData(LinkedHashMap<String, Double> settingsData) {
		this.settingsData = settingsData;
	}
	public HashMap<String, ParticleData> getParticleDataMap() {
		return particleDataMap;
	}
	public void setParticleDataMap(HashMap<String, ParticleData> particleDataMap) {
		this.particleDataMap = particleDataMap;
	}

}
