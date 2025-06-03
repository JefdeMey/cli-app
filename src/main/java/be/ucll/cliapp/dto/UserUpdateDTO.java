package be.ucll.cliapp.dto;

import java.time.LocalDate;

public class UserUpdateDTO {
    private String voornaam;

    private String achternaam;

    private String mail;

    private LocalDate geboortedatum;

    // Getters en setters
    public String getVoornaam() { return voornaam; }
    public void setVoornaam(String voornaam) { this.voornaam = voornaam; }

    public String getAchternaam() { return achternaam; }
    public void setAchternaam(String achternaam) { this.achternaam = achternaam; }

    public String getMail() { return mail; }

    public void setMail(String mail) { this.mail = mail; }

    public LocalDate getGeboortedatum() { return geboortedatum; }

    public void setGeboortedatum(LocalDate geboortedatum) { this.geboortedatum = geboortedatum; }
}
