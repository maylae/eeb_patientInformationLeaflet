package com.ModelInteraction;

import com.Configuration.ModelAccessConfiguration;
import com.Model.Medikament;
import com.Utility.Utilities;
import com.WordEmbeddings.ModelAccess.Provider.ModelAccessProvider;
import com.ea.async.instrumentation.InitializeAsync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.ea.async.Async.await;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ModelInteractor {
	private static final Logger _log = LoggerFactory.getLogger(ModelInteractor.class);
	private static final String MODEL_ACCESS_CONFIG_FILE_PATH = "configuration/modelAccess_config.cfg";
	public ModelAccessProvider provider = null;
	private static Collection<Medikament> medikamenteDetails = new ArrayList<Medikament>();

	public ModelInteractor (){
		init();
		ModelAccessConfiguration config = new ModelAccessConfiguration(MODEL_ACCESS_CONFIG_FILE_PATH, _log);
		this.provider = await(ModelAccessProvider.getNewInstanceAsync(config, _log));
		medikamenteDetails = loadMedikamenteDetailsFile();
	}

	private static void init()
	{
		InitializeAsync.init();
	}

	/**
	 * Sucht nach ähnlichen Medikamenten zum gesuchten Medikament im Word2Vec-Modell
	 * @param medikament Gesuchtes Medikament
	 * @return Max. 20 ähnlichste Medikamente zum gesuchten aus dem Modell, null bei Fehler
	 */
	public Collection<Medikament> searchMedikament(String medikament)
	{
		if(this.provider == null)
		{
			_log.error("ModelAccessProvider null. Terminating...");
			return null;
		} else {
			Collection<String> searchRes = provider.getSimilarMedicaments(medikament, 30);
			searchRes.add(medikament);
			Collection<Medikament> res = loadMedikamenteDetails(searchRes);
			if(res == null) {
				res = new ArrayList<Medikament>();
			}
			return res;
		}
	}

	public Collection<String> calcTerms(String[] posTerms, String[] negTerms)
	{
		//        ModelAccessConfiguration config = new ModelAccessConfiguration(MODEL_ACCESS_CONFIG_FILE_PATH, _log);
		//        ModelAccessProvider modelAccessProvider = await(ModelAccessProvider.getNewInstanceAsync(config, _log));
		if(this.provider == null)
		{
			_log.error("ModelAccessProvider null. Terminating...");
			return null;
		} else {
			Collection<String> posTermsColl = new ArrayList<String>(Arrays.asList(posTerms));
			Collection<String> negTermsColl = new ArrayList<String>(Arrays.asList(negTerms));

			Collection<String> res = this.provider.findSemanticallySimilarWordsTo(posTermsColl, negTermsColl, 20);
			return res;
		}
	}

	/**
	 * Suche nach Anwendungsgebiet
	 * @param symptom "+"-getrennte liste von symptomen/anwendungsgebieten
	 * @param medikamente Wenn auch ein Medikament als Suchparameter angegeben wurde, ist medikamente die Ergebnisliste dieser Suche
	 * @return Medikamente, bei denen das bzw. eines der Symptome in der Beschreibung des Anwendungsgebiets vorkommt. Wenn Suchparameter Medikament nicht leer ist, werden nur die Suchergebnisse dieser Suche gefiltert
	 */
	public Collection<Medikament> searchSymptom(String symptom, Collection<Medikament> medikamente) {

		Collection<Medikament> res = new ArrayList<Medikament>();
		Collection<Medikament> checkAgainst = medikamente.size()>0 ? medikamente : this.medikamenteDetails;
		String[] symptomArr = {symptom};
		String[] symptomSplit = symptom.contains("+") ? symptom.split("+") : symptomArr;
		for(Medikament m : checkAgainst) {
			for(String s : symptomSplit) {
				if(m.getAnwendungsgebiet().contains(s)) {
					res.add(m);
				}
			}
		}
		return res;
	}

	/**
	 * Suche nach Wirkstoff
	 * @param wirkstoff Gesuchter Wirkstoff
	 * @param medikamente Wenn auch ein Medikament als Suchparameter angegeben wurde, ist medikamente die Ergebnisliste dieser Suche
	 * @return Medikamente, bei denen der Wirkstoff in der Beschreibung des Anwendungsgebiets vorkommt. Wenn Suchparameter Medikament nicht leer ist, werden nur die Suchergebnisse dieser Suche gefiltert
	 */
	public Collection<Medikament> searchWirkstoff(String wirkstoff, Collection<Medikament> medikamente) {

		Collection<Medikament> res = new ArrayList<Medikament>();
		Collection<Medikament> checkAgainst = medikamente.size()>0 ? medikamente : this.medikamenteDetails;
		for(Medikament m : checkAgainst) {
			if(m.getWirkstoffe().contains(wirkstoff)) {
				res.add(m);
			}
		}
		return res;
	}

	/**
	 * Details zu Medikamenten in ArrayList laden
	 * @param searchRes Suchergebnis aus dem Word2Vec Modell für ein Medikament als Liste von Strings
	 * @return Liste von Medikamenten mit Details
	 */
	public static ArrayList<Medikament> loadMedikamenteDetails(Collection<String> searchRes) {

		ArrayList<Medikament> res = new ArrayList<Medikament>();
		if(searchRes != null && searchRes.size()>0) {
			for(String str : searchRes) {
				for(Medikament m : medikamenteDetails) {
					if(m.getName().toLowerCase().contains(str.toLowerCase())) {
						res.add(m);
					} 
				}
			}
			return res;
		} else {
			return null;
		}

	}

	/**
	 * Medikamente Details aus Datei laden
	 * @return
	 */
	private ArrayList<Medikament> loadMedikamenteDetailsFile() {
		ArrayList<Medikament> medikamenteList = new ArrayList<Medikament>();
		try {
			BufferedReader br = new BufferedReader(new FileReader("src//main//resources//static//data//tempexport.txt"));
			// Überschriften überspringen
			br.readLine();
			String line = br.readLine();
			while (line != null) {
				String[] lineSplit = line.split("\t");
				if(lineSplit.length == 9){
					Medikament m = new Medikament(lineSplit[1]);
					String[] nebenwirkungen = Arrays.copyOfRange(lineSplit, 3, 8);
					for (int i = 0; i<5; i++) {
						nebenwirkungen[i] = nebenwirkungen[i].replaceAll("\\.\\-", "-");
						nebenwirkungen[i] = nebenwirkungen[i].replaceAll("\\-\\-", System.lineSeparator()+"- ");
						nebenwirkungen[i] = nebenwirkungen[i].replaceAll("\\s\\-\\s", System.lineSeparator()+"- ");
					}
					m.setNebenwirkungen(nebenwirkungen);
					m.setAnwendungsgebiet(lineSplit[8]);
					medikamenteList.add(m);
				} else {
					System.out.println(lineSplit[0]);
				}
				line = br.readLine();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return medikamenteList;
	}

}
