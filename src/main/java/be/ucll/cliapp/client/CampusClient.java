package be.ucll.cliapp.client;

import be.ucll.cliapp.ApiClient;
import be.ucll.cliapp.dto.CampusCreateDTO;
import be.ucll.cliapp.dto.CampusDTO;
import be.ucll.cliapp.dto.LokaalDTO;
import be.ucll.cliapp.dto.ReservatieDTO;
import be.ucll.cliapp.util.ConsoleUtil;
import reactor.core.publisher.Mono;

public class CampusClient extends ApiClient {
    //ok
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

    public void zoekCampusOpNaam(String naam) {
        try {
            CampusDTO campus = webClient.get()
                    .uri("/campussen/{naam}", naam)
                    .retrieve()
                    .bodyToMono(CampusDTO.class)
                    .block();
            if(campus != null) {
                System.out.println("🏫 " + campus.getNaam() + " - " + campus.getAdres()
                        + " (" + campus.getAantalParkeerplaatsen() + " parkeerplaatsen)");
            } else {
                System.out.println("❌ Campus niet gevonden");
            }
        } catch (Exception e) {
            System.out.println("❌ Er ging iets fout: " + e.getMessage());
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
            //
            if (created == null) {
                System.out.println("❌ Backend gaf geen geldige response.");
                return;
            }
            //
            System.out.println("✅ Campus aangemaakt: " + created.getNaam());

        } catch (Exception e) {
            System.out.println("❌ Fout bij aanmaken campus: " + e.getMessage());
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

//    public void bewerkCampusFlexibel(String naam, String nieuwAdres, Integer nieuwePlaatsen) {
//        try {
//            // Eerst ophalen huidige campusinfo
//            CampusDTO bestaand = webClient.get()
//                    .uri("/campussen/" + naam)
//                    .retrieve()
//                    .bodyToMono(CampusDTO.class)
//                    .block();
//
//            if (bestaand == null) {
//                System.out.println("❌ Campus niet gevonden.");
//                return;
//            }
//
//            // Gegevens behouden als niet ingevuld
//            String adres = nieuwAdres.isBlank() ? bestaand.getAdres() : nieuwAdres;
//            int parkeerplaatsen = (nieuwePlaatsen != null) ? nieuwePlaatsen : bestaand.getAantalParkeerplaatsen();
//
//            CampusCreateDTO dto = new CampusCreateDTO();
//            dto.setNaam(bestaand.getNaam());
//            dto.setAdres(adres);
//            dto.setAantalParkeerPlaatsen(parkeerplaatsen);
//
//            webClient.put()
//                    .uri("/campussen/" + naam)
//                    .bodyValue(dto)
//                    .retrieve()
//                    .bodyToMono(CampusDTO.class)
//                    .block();
//
//            System.out.println("✅ Campus bijgewerkt.");
//        } catch (Exception e) {
//            System.out.println("❌ Bewerken mislukt: " + e.getMessage());
//        }
//    }

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

    public void toonLokaalBinnenCampus(String campusNaam, Long lokaalId) {
        try {
            LokaalDTO lokaal = webClient.get()
                    .uri("/campussen/{campusNaam}/rooms/{roomId}", campusNaam, lokaalId)
                    .retrieve()
                    .bodyToMono(LokaalDTO.class)
                    .block();
            if (lokaal == null) {
                System.out.println("❌ Lokaal niet gevonden.");
                return;
            }
            System.out.println("🏫 Lokaal " + lokaal.getId() + ": " + lokaal.getNaam()
                    + " - " + lokaal.getAantalPersonen() + " personen"
                    + ", verdieping: " + lokaal.getVerdieping());

        } catch (Exception e) {
            System.out.println("❌ Lokaal niet gevonden: " + e.getMessage());
        }
    }

    public void toonReservatiesVoorLokaal(String campusNaam, Long lokaalId) {
        try {
            ReservatieDTO[] reservaties = webClient.get()
                    .uri("/campussen/{campusNaam}/rooms/{roomId}/reservaties", campusNaam, lokaalId)
                    .retrieve()
                    .bodyToMono(ReservatieDTO[].class)
                    .block();

            if (reservaties != null && reservaties.length > 0) {
                System.out.println("📅 Reservaties voor lokaal " + lokaalId + " op campus " + campusNaam + ":");
                for (ReservatieDTO r : reservaties) {
                    System.out.println(" - Van " + r.getStartTijd() + " tot " + r.getEindTijd()
                            + " (" + r.getAantalPersonen() + " personen) door " + r.getGebruikerNaam());
                }
            } else {
                System.out.println("⚠️ Geen reservaties gevonden.");
            }
        } catch (Exception e) {
            System.out.println("❌ Reservaties ophalen mislukt: " + e.getMessage());
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

