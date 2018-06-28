package eeb.patient_information_leaflet.ModelInteraction;

import com.Configuration.ModelAccessConfiguration;
import com.WordEmbeddings.ModelAccess.Provider.ModelAccessProvider;
import com.ea.async.instrumentation.InitializeAsync;

import Model.Medikament;
import Utility.Utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.ea.async.Async.await;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ModelInteractor {
	private static final Logger _log = LoggerFactory.getLogger(ModelInteractor.class);
	private static final String MODEL_ACCESS_CONFIG_FILE_PATH = "configuration/modelAccess_config.cfg";
	ModelAccessProvider provider = null;
	private static Collection<Medikament> medikamenteDetails = new ArrayList<Medikament>();

	public ModelInteractor (){
		ModelAccessConfiguration config = new ModelAccessConfiguration(MODEL_ACCESS_CONFIG_FILE_PATH, _log);
		this.provider = await(ModelAccessProvider.getNewInstanceAsync(config, _log));
		init();
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
			Collection<String> searchRes = this.provider.getSimilarMedicaments(medikament, 20);
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

			Collection<String> res = this.provider.findSemanticallySimilarWordsTo(posTermsColl, negTermsColl, 10);
			return res;
		}
	}

	/**
	 * Suche nach Anwendungsgebiet
	 * @param symptom
	 * @return
	 */
	public Collection<String> searchSymptom(String symptom) {
		//        ModelAccessConfiguration config = new ModelAccessConfiguration(MODEL_ACCESS_CONFIG_FILE_PATH, _log);
		//        ModelAccessProvider modelAccessProvider = await(ModelAccessProvider.getNewInstanceAsync(config, _log));
		if(this.provider == null)
		{
			_log.error("ModelAccessProvider null. Terminating...");
			return null;
		} else {
			Collection<String> res = this.provider.findSemanticallySimilarWordsTo(symptom, 10);
			return res;
		}
	}

	/**
	 * Details zu Medikamenten in ArrayList laden
	 * @param searchRes Suchergebnis aus dem Word2Vec Modell für ein Medikament als Liste von Strings
	 * @return Liste von Medikamenten mit Details
	 */
	public static ArrayList<Medikament> loadMedikamenteDetails(Collection<String> searchRes) {

		ArrayList<Medikament> resArrList = new ArrayList<Medikament>();
		if(searchRes != null && searchRes.size()>0) {
			try {
				resArrList = loadDetailsForMedikamente(searchRes);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return resArrList;
		} else {
			return null;
		}

	}

	/**
	 * Details zu Medikamenten zuordnen
	 * @param medikamente Suchergebnis aus Word2Vec-Modell
	 * @return ArrayList aus Mediakmenten
	 */
	private static ArrayList<Medikament> loadDetailsForMedikamente(Collection<String> medikamente){
		ArrayList<Medikament> res = new ArrayList<Medikament>();
		for(int i = 0; i<medikamente.size(); i++) {
			for(Medikament m : medikamenteDetails) {
				if(medikamente.contains(m.getName())) {
					res.add(m);
					break;
				}
			}
		}
		return res;
	}
	
	private ArrayList<Medikament> loadMedikamenteDetailsFile() {
		String medikamenteFile = Utilities.readFile("src//main//resources//static//data//fam.csv");
		String[] medikamenteArr = medikamenteFile.split(System.lineSeparator());
		String[][] medikamenteArrSplit = new String[medikamenteArr.length][8];
		ArrayList<Medikament> res = new ArrayList<Medikament>();
		for(int i = 0; i<medikamenteArr.length; i++) {
			medikamenteArrSplit[i] = medikamenteArr[i].split(";");
		}
		for(String[] str : medikamenteArrSplit) {
			Medikament m = new Medikament(str[1]);
			String[] nebenwirkungen = Arrays.copyOfRange(str, 2, 6);
			m.setNebenwirkungen(nebenwirkungen);
			m.setAnwendungsgebiet(str[7]);
			res.add(m);
		}
		return res;
	}

}
