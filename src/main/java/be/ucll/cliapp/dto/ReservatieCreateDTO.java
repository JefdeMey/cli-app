package be.ucll.cliapp.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ReservatieCreateDTO {
    private LocalDateTime startTijd;

    private LocalDateTime eindTijd;

    private String commentaar;

    private int aantalPersonen;

    private Long gebruikerId;

    private List<Long> lokaalIds;
    public ReservatieCreateDTO(){

    }
    public ReservatieCreateDTO(LocalDateTime start, LocalDateTime eind, int aantalPersonen, String commentaar, Long gebruikerId, List<Long> lokaalIds) {
        this.startTijd = start;
        this.eindTijd = eind;
        this.aantalPersonen = aantalPersonen;
        this.commentaar = commentaar;
        this.gebruikerId = gebruikerId;
        this.lokaalIds = lokaalIds;
    }

    // Getters en setters
    public LocalDateTime getStartTijd() { return startTijd; }
    public void setStartTijd(LocalDateTime startTijd) { this.startTijd = startTijd; }

    public LocalDateTime getEindTijd() { return eindTijd; }
    public void setEindTijd(LocalDateTime eindTijd) { this.eindTijd = eindTijd; }

    public String getCommentaar() { return commentaar; }
    public void setCommentaar(String commentaar) { this.commentaar = commentaar; }

    public int getAantalPersonen() { return aantalPersonen; }
    public void setAantalPersonen(int aantalPersonen) { this.aantalPersonen = aantalPersonen; }

    public Long getGebruikerId() { return gebruikerId; }
    public void setGebruikerId(Long gebruikerId) { this.gebruikerId = gebruikerId; }

    public List<Long> getLokaalIds() { return lokaalIds; }
    public void setLokaalIds(List<Long> lokaalIds) { this.lokaalIds = lokaalIds; }
}




