package be.ucll.cliapp.dto;

public class CampusCreateDTO {

    private String naam;

    private String adres;

    private int aantalParkeerPlaatsen;

    // Getters en setters

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public String getAdres() {
        return adres;
    }

    public void setAdres(String adres) {
        this.adres = adres;
    }


    public int getAantalParkeerPlaatsen() {
        return aantalParkeerPlaatsen;
    }

    public void setAantalParkeerPlaatsen(int aantalParkeerPlaatsen) {
        this.aantalParkeerPlaatsen = aantalParkeerPlaatsen;
    }
}

