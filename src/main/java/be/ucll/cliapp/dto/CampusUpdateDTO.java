package be.ucll.cliapp.dto;

public class CampusUpdateDTO {

    private String adres;

    private int aantalParkeerplaatsen;

    public String getAdres() {
        return adres;
    }

    public void setAdres(String adres) {
        this.adres = adres;
    }

    public int getAantalParkeerplaatsen() {
        return aantalParkeerplaatsen;
    }

    public void setAantalParkeerplaatsen(int aantalParkeerplaatsen) {
        this.aantalParkeerplaatsen = aantalParkeerplaatsen;
    }
}

