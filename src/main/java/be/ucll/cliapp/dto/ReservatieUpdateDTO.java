package be.ucll.cliapp.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ReservatieUpdateDTO {

    private LocalDateTime startTijd;

    private LocalDateTime eindTijd;

    private String commentaar;

    private int aantalPersonen;

    private List<Long> lokaalIds;

    // GebruikerId weglaten – die wordt niet aangepast bij update

    // Getters en setters
    public LocalDateTime getStartTijd() { return startTijd; }
    public void setStartTijd(LocalDateTime startTijd) { this.startTijd = startTijd; }

    public LocalDateTime getEindTijd() { return eindTijd; }
    public void setEindTijd(LocalDateTime eindTijd) { this.eindTijd = eindTijd; }

    public String getCommentaar() { return commentaar; }
    public void setCommentaar(String commentaar) { this.commentaar = commentaar; }

    public int getAantalPersonen() { return aantalPersonen; }
    public void setAantalPersonen(int aantalPersonen) { this.aantalPersonen = aantalPersonen; }

    public List<Long> getLokaalIds() { return lokaalIds; }
    public void setLokaalIds(List<Long> lokaalIds) { this.lokaalIds = lokaalIds; }
}
