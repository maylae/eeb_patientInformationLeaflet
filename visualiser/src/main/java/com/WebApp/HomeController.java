package com.WebApp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.Model.Medikament;
import com.ModelInteraction.ModelInteractor;
import com.Utility.Utilities;

@Controller
public class HomeController {
	ModelInteractor mi;
	
	public HomeController() {
		this.mi = VisualiserApplication.getModelInteractor();
	}
	
	
	@GetMapping("/")
	public String home(Model model) {
		return "home";	
	}
	
	@GetMapping("/search")
	public String search(@RequestParam(value = "symptom", required = false, defaultValue = "") String symptom, @RequestParam(value = "medikamentId", required = false, defaultValue = "") String medikamentId,
					@RequestParam(value = "medikamentName", required = false, defaultValue = "") String medikament, @RequestParam(value = "wirkstoff", required = false, defaultValue = "") String wirkstoff, Model model) {
		Collection<String> res = new ArrayList<String>();
		Collection<Medikament> medikamente = new ArrayList<>();
		if(!medikament.isEmpty()) {
			medikamente = this.mi.searchMedikament(medikament);	
		} 
		if(!symptom.isEmpty()) {
			medikamente = this.mi.searchSymptom(symptom, medikamente);
		}
		if(!wirkstoff.isEmpty()) {
			medikamente = this.mi.searchWirkstoff(wirkstoff, medikamente);
		}
		model.addAttribute("searchResults", medikamente);
        model.addAttribute("symptom", Utilities.capitalizeFirstLetter(symptom));
        model.addAttribute("medikamentName", Utilities.capitalizeFirstLetter(medikament));
        return "search";
    }
	
	@GetMapping("/searchIdee")
	public String searchIdeal(@RequestParam(value = "symptom", required = false, defaultValue = "") String symptom, @RequestParam(value = "medikamentId", required = false, defaultValue = "") String medikamentId,
					@RequestParam(value = "medikamentName", required = false, defaultValue = "") String medikament, Model model) {
		model.addAttribute("searchResults", null);
        model.addAttribute("symptom", Utilities.capitalizeFirstLetter(symptom));
        model.addAttribute("medikamentName", Utilities.capitalizeFirstLetter(medikament));
        return "searchIdee";
    }
	
	@GetMapping("/wechselwirkungen")
	public String wechselwirkungen(	@RequestParam(value = "medikament", required = false, defaultValue = "") String medikament, Model model) {
        model.addAttribute("medikament", Utilities.capitalizeFirstLetter(medikament));
        return "wechselwirkungen";
    }
	
	@GetMapping("/uebersicht")
	public String uebersicht( Model model) {
        return "uebersicht";
    }
	
	@GetMapping("/medikament")
	public String medikament(@RequestParam(value = "medikament", required = true, defaultValue = "") String medikament, Model model) {
		model.addAttribute("medikament", Utilities.capitalizeFirstLetter(medikament));
        return "medikament";
    }
	

	@GetMapping("/w2vDemo")
	public String w2vDemo(@RequestParam(value = "posTerms", required = false, defaultValue = "") String[] posTerms, @RequestParam(value = "negTerms", required = false, defaultValue = "") String[] negTerms, Model model) {
		
		Collection<String> res = new ArrayList<String>();
		if(posTerms.length>0) {
			res = this.mi.calcTerms(posTerms, negTerms);
		} else {
			res.add("Keine Ergebnisse gefunden. Bitte geben Sie min. einen pos. Suchbegriff ein.");
		}
//		double res = mi.searchMedikament(medikament);
		model.addAttribute("posTerms", Arrays.toString(posTerms).replaceAll("[\\[\\]\\s]", ""));
		model.addAttribute("negTerms", Arrays.toString(negTerms).replaceAll("[\\[\\]\\s]", ""));
		model.addAttribute("searchResults", res);
        return "w2vDemo";
    }
	
}
