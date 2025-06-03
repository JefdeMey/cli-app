package be.ucll.cliapp.client;

import be.ucll.cliapp.ApiClient;
import be.ucll.cliapp.dto.CampusDTO;
import be.ucll.cliapp.dto.LokaalDTO;
import be.ucll.cliapp.dto.LokaalCreateDTO;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

public class LokaalClient extends ApiClient {

    public void toonAlleLokalen() {
        try {
            LokaalDTO[] lokalen = webClient.get()
                    .uri("/lokalen")
                    .retrieve()
                    .bodyToMono(LokaalDTO[].class)
                    .block();
            if(lokalen != null && lokalen.length != 0) {
                Arrays.stream(lokalen).forEach(this::printLokaal);
            } else {
                System.out.println("❌ Geen lokalen in de db: ");
            }

        } catch (Exception e) {
            System.out.println("❌ Fout bij ophalen van lokalen: " + e.getMessage());
        }
    }

    public void zoekLokaalOpId(long id) {
        try {
            LokaalDTO lokaal = webClient.get()
                    .uri("/lokalen/{id}", id)
                    .retrieve()
                    .bodyToMono(LokaalDTO.class)
                    .block();
            if(lokaal != null) {
                printLokaal(lokaal);
            } else {
                System.out.println("❌ Lokaal niet gevonden");
            }

        } catch (Exception e) {
            System.out.println("❌ Lokaal niet gevonden: " + e.getMessage());
        }
    }

    public LokaalDTO zoekLokaalOpIdEnGeefTerug(long id) {
        try {
            return webClient.get()
                    .uri("/lokalen/{id}", id)
                    .retrieve()
                    .bodyToMono(LokaalDTO.class)
                    .block();
        } catch (Exception e) {
            return null; // Geen lokaal gevonden of fout
        }
    }

    public Long getIdByNaam(String naam) {
        try {
            return webClient.get()
                    .uri("/lokalen/naam/" + naam)
                    .retrieve()
                    .bodyToMono(Long.class)
                    .block();
        } catch (Exception e) {
            System.out.println("❌ Kon lokaal-ID niet ophalen voor naam: " + naam);
            return null;
        }
    }

    public void voegLokaalToe(String campusNaam, LokaalCreateDTO dto) {
        try {
            LokaalDTO lokaal = webClient.post()
                    .uri("/lokalen/campus/{campusNaam}", campusNaam)
                    .bodyValue(dto)
                    .retrieve()
                    .bodyToMono(LokaalDTO.class)
                    .block();

            System.out.println("✅ Lokaal aangemaakt:");
            printLokaal(lokaal);
        } catch (Exception e) {
            System.out.println("❌ Aanmaken mislukt: " + e.getMessage());
        }
    }

    public void pasLokaalAan(long lokaalId, LokaalCreateDTO dto) {
        try {
            LokaalDTO lokaal = webClient.put()
                    .uri("/lokalen/{id}", lokaalId)
                    .bodyValue(dto)
                    .retrieve()
                    .bodyToMono(LokaalDTO.class)
                    .block();

            System.out.println("✅ Lokaal aangepast:");
            printLokaal(lokaal);
        } catch (Exception e) {
            System.out.println("❌ Aanpassen mislukt: " + e.getMessage());
        }
    }

    public void verwijderLokaal(long id) {
        try {
            webClient.delete()
                    .uri("/lokalen/{id}", id)
                    .retrieve()
                    .onStatus(status -> status.value() == 404,
                            response -> {
                                System.out.println("❌ Lokaal met ID " + id + " werd niet gevonden.");
                                return Mono.error(new RuntimeException("Niet gevonden")); // voorkomt dubbele melding
                            })
                    .onStatus(status -> status.value() == 409,
                            response -> {
                                System.out.println("⚠️ Lokaal kon niet verwijderd worden omdat er nog koppelingen bestaan (bv. reservaties).");
                                return Mono.error(new RuntimeException("Conflict"));
                            })
                    .toBodilessEntity()
                    .block();

            System.out.println("🗑️ Lokaal verwijderd.");
        } catch (Exception e) {
            if (!e.getMessage().equals("Niet gevonden")) {
                System.out.println("❌ Verwijderen mislukt: " + e.getMessage());
            }
        }
    }

    public List<LokaalDTO> haalLokalenBinnenCampus(String campusNaam) {
        try {
            return List.of(webClient.get()
                    .uri("/campussen/{naam}/rooms", campusNaam)
                    .retrieve()
                    .bodyToMono(LokaalDTO[].class)
                    .block());
        } catch (Exception e) {
            System.out.println("❌ Kan lokalen van campus niet ophalen: " + e.getMessage());
            return List.of();
        }
    }

    public void toonLokalenBinnenCampus(String campusNaam) {
        try {
            List<LokaalDTO> lokalen = webClient.get()
                    .uri("/campussen/{naam}/rooms", campusNaam)
                    .retrieve()
                    .bodyToFlux(LokaalDTO.class)
                    .collectList()
                    .block();

            if (lokalen == null || lokalen.isEmpty()) {
                System.out.println("ℹ️ Geen lokalen gevonden voor campus '" + campusNaam + "'.");
            } else {
                System.out.println("📋 Lokalen in campus '" + campusNaam + "':");
                for (LokaalDTO l : lokalen) {
                    System.out.printf("ID: %d | Naam: %s | Capaciteit: %d%n", l.getId(), l.getNaam(), l.getAantalPersonen());
                }
            }
        } catch (Exception ex) {
            System.out.println("❌ Fout bij ophalen lokalen: " + ex.getMessage());
        }
    }

    private void printLokaal(LokaalDTO l) {
        System.out.printf("ID: %d | Naam: %s | Campus: %s | Type: %s | Plaatsen: %d | Verantwoordelijke: %s %s | Verdieping: %s%n",
                l.getId(),
                l.getNaam(),
                l.getCampusNaam(),
                l.getType(),
                l.getAantalPersonen(),
                l.getVoornaam(),
                l.getAchternaam(),
                l.getVerdieping()

        );
    }
}
