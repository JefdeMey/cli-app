package be.ucll.cliapp.client;

import be.ucll.cliapp.ApiClient;
import be.ucll.cliapp.dto.CampusCreateDTO;
import be.ucll.cliapp.dto.CampusDTO;
import be.ucll.cliapp.dto.LokaalDTO;
import reactor.core.publisher.Mono;

public class CampusClient extends ApiClient {
    public void haalAlleCampussenOp() {
        try {
            CampusDTO[] campussen = webClient.get()
                    .uri("/campussen")
                    .retrieve()
                    .bodyToMono(CampusDTO[].class)
                    .block();

            if (campussen != null && campussen.length > 0) {
                System.out.println("📍 Campussen:");
                for (CampusDTO c : campussen) {
                    System.out.println(" - " + c.getNaam() + " (" + c.getAantalLokalen() + " lokalen)");
                }
            } else {
                System.out.println("⚠️ Geen campussen gevonden.");
            }
        } catch (Exception e) {
            System.out.println("❌ Fout bij ophalen campussen: " + e.getMessage());
        }
    }

    public void zoekCampusOpNaam(String naam) {
        try {
            CampusDTO campus = webClient.get()
                    .uri("/campussen/{naam}", naam)
                    .retrieve()
                    .bodyToMono(CampusDTO.class)
                    .block();
            if (campus != null) {
                System.out.println("🏫 " + campus.getNaam() + " - " + campus.getAdres()
                        + " (" + campus.getAantalParkeerplaatsen() + " parkeerplaatsen)");
            } else {
                System.out.println("❌ Campus niet gevonden");
            }
        } catch (Exception e) {
            System.out.println("❌ Er ging iets fout: " + e.getMessage());
        }
    }

    public CampusDTO zoekCampusOpNaamEnGeefTerug(String naam) {
        try {
            return webClient.get()
                    .uri("/campussen/{naam}", naam)
                    .retrieve()
                    .bodyToMono(CampusDTO.class)
                    .block();
        } catch (Exception e) {
            System.out.println("❌ Campus '" + naam + "' niet gevonden.");
            return null;
        }
    }

    public void voegCampusToe(String naam, String adres, int aantalParkeerplaatsen) {
        try {
            CampusCreateDTO dto = new CampusCreateDTO();
            dto.setNaam(naam);
            dto.setAdres(adres);
            dto.setAantalParkeerPlaatsen(aantalParkeerplaatsen);

            CampusDTO created = webClient.post()
                    .uri("/campussen")
                    .bodyValue(dto)
                    .retrieve()
                    .bodyToMono(CampusDTO.class)
                    .block();

            if (created == null) {
                System.out.println("❌ Backend gaf geen geldige response.");
                return;
            }

            System.out.println("✅ Campus aangemaakt: " + created.getNaam());

        } catch (Exception e) {
            System.out.println("❌ Fout bij aanmaken campus: " + e.getMessage());
        }
    }

    public void wijzigCampus(String naam, String nieuwAdres, int nieuweParkeerplaatsen) {
        try {
            CampusCreateDTO dto = new CampusCreateDTO();
            dto.setNaam(naam);
            dto.setAdres(nieuwAdres);
            dto.setAantalParkeerPlaatsen(nieuweParkeerplaatsen);

            CampusDTO updated = webClient.put()
                    .uri("/campussen/{naam}", naam)
                    .bodyValue(dto)
                    .retrieve()
                    .bodyToMono(CampusDTO.class)
                    .block();

            System.out.println("🔁 Campus geüpdatet: " + updated.getNaam());

        } catch (Exception e) {
            System.out.println("❌ Bewerken mislukt: " + e.getMessage());
        }
    }

    public boolean verwijderCampus(String naam) {
        try {
            webClient.delete()
                    .uri("/campussen/{naam}", naam)
                    .retrieve()
                    .onStatus(status -> status.value() == 404,
                            clientResponse -> {
                                System.out.println("❌ Campus '" + naam + "' bestaat niet.");
                                return Mono.error(new RuntimeException("404"));
                            })
                    .toBodilessEntity()
                    .block();

            System.out.println("🗑️ Campus '" + naam + "' is verwijderd.");
            return true;
        } catch (Exception e) {
            if (!e.getMessage().contains("404")) {
                System.out.println("❌ Verwijderen mislukt: " + e.getMessage());
            }
            return false;
        }
    }

    public void toonLokalenBinnenCampus(String campusNaam) {
        try {
            LokaalDTO[] lokalen = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/campussen/{naam}/rooms")
                            .build(campusNaam))
                    .retrieve()
                    .bodyToMono(LokaalDTO[].class)
                    .block();

            if (lokalen != null && lokalen.length > 0) {
                System.out.println("🏢 Lokalen binnen campus " + campusNaam + ":");
                for (LokaalDTO l : lokalen) {
                    System.out.println(" - " + l.getId() + ": " + l.getNaam() + " (" + l.getAantalPersonen() + " personen)");
                }
            } else {
                System.out.println("⚠️ Geen lokalen gevonden.");
            }
        } catch (Exception e) {
            System.out.println("❌ Lokalen ophalen mislukt: " + e.getMessage());
        }
    }

    public boolean bestaatCampus(String naam) {
        try {
            CampusDTO campus = webClient.get()
                    .uri("/campussen/{naam}", naam)
                    .retrieve()
                    .bodyToMono(CampusDTO.class)
                    .block();
            return campus != null;
        } catch (Exception e) {
            return false;
        }
    }
}

