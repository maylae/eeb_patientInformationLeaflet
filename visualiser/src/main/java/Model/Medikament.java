package Model;

public class Medikament {
	String name = "";
	String anwendungsgebiet = "";
	String[] nebenwirkungen = new String[5];
	
	public Medikament(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAnwendungsgebiet() {
		return anwendungsgebiet;
	}

	public void setAnwendungsgebiet(String anwendungsgebiet) {
		this.anwendungsgebiet = anwendungsgebiet;
	}

	public String[] getNebenwirkungen() {
		return nebenwirkungen;
	}

	public void setNebenwirkungen(String[] nebenwirkungen) {
		this.nebenwirkungen = nebenwirkungen;
	}
	
	
}
