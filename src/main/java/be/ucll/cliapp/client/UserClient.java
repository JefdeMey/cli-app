package be.ucll.cliapp.client;

import be.ucll.cliapp.ApiClient;
import be.ucll.cliapp.dto.UserDTO;
import be.ucll.cliapp.dto.UserCreateDTO;
import be.ucll.cliapp.dto.UserUpdateDTO;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.util.Arrays;

public class UserClient extends ApiClient {

    public void toonAlleGebruikers() {
        try {
            UserDTO[] gebruikers = webClient.get()
                    .uri("/gebruikers")
                    .retrieve()
                    .bodyToMono(UserDTO[].class)
                    .block();
            if (gebruikers == null || gebruikers.length == 0) {
                System.out.println("⚠️ Geen gebruikers gevonden.");
                return;
            }
            System.out.println("\n📋 Gebruikerslijst:");
            Arrays.stream(gebruikers).forEach(this::printGebruiker);
        } catch (Exception e) {
            System.out.println("❌ Fout bij ophalen van gebruikers: " + e.getMessage());
        }
    }

    public void zoekGebruikerOpId(Long id) {
        try {
            UserDTO gebruiker = webClient.get()
                    .uri("/gebruikers/{id}", id)
                    .retrieve()
                    .bodyToMono(UserDTO.class)
                    .block();
            if (gebruiker != null) {
                printGebruiker(gebruiker);
            } else {
                System.out.println("❌ Gebruiker niet gevonden.");
            }
        } catch (Exception e) {
            System.out.println("❌ Gebruiker niet gevonden: " + e.getMessage());
        }
    }

    public void zoekGebruikersOpNaam(String fragment) {
        try {
            UserDTO[] gebruikers = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/gebruikers")
                            .queryParam("nameMatches", fragment)
                            .build())
                    .retrieve()
                    .bodyToMono(UserDTO[].class)
                    .block();
            if (gebruikers == null || gebruikers.length == 0) {
                System.out.println("geen users gevonden");
                return;
            }

            Arrays.stream(gebruikers).forEach(this::printGebruiker);
        } catch (Exception e) {
            System.out.println("❌ Fout bij zoeken: " + e.getMessage());
        }
    }

    public void voegGebruikerToe(String voornaam, String achternaam, String email, String geboortedatum) {
        try {
            UserCreateDTO dto = new UserCreateDTO();
            dto.setVoornaam(voornaam);
            dto.setAchternaam(achternaam);
            dto.setMail(email);
            dto.setGeboortedatum(LocalDate.parse(geboortedatum));

            UserDTO created = webClient.post()
                    .uri("/gebruikers")
                    .bodyValue(dto)
                    .retrieve()
                    .bodyToMono(UserDTO.class)
                    .block();

            System.out.println("✅ Gebruiker aangemaakt:");
            printGebruiker(created);
        } catch (Exception e) {
            System.out.println("❌ Aanmaken mislukt: " + e.getMessage());
        }
    }

    public void pasGebruikerAan(Long id, UserUpdateDTO dto) {
        try {
            UserDTO updated = webClient.put()
                    .uri("/gebruikers/{id}", id)
                    .bodyValue(dto)
                    .retrieve()
                    .bodyToMono(UserDTO.class)
                    .block();

            System.out.println("✅ Gebruiker aangepast:");
            printGebruiker(updated);
        } catch (Exception e) {
            System.out.println("❌ Aanpassen mislukt: " + e.getMessage());
        }
    }

    public UserUpdateDTO getUserForUpdate(Long id) {
        try {
            return webClient.get()
                    .uri("/gebruikers/{id}", id)
                    .retrieve()
                    .bodyToMono(UserUpdateDTO.class)
                    .block();
        } catch (Exception e) {
            return null;
        }
    }

    public void verwijderGebruiker(Long id) {
        try {
            webClient.delete()
                    .uri("/gebruikers/{id}", id)
                    .retrieve()
                    .onStatus(status -> status.value() == 404,
                            response -> {
                                System.out.println("❌ Gebruiker met ID " + id + " bestaat niet.");
                                return response.createException(); // blijft nodig om flow te onderbreken
                            })
                    .onStatus(status -> status.value() == 409,
                            response -> {
                                System.out.println("⚠️ Gebruiker kon niet verwijderd worden omdat er nog koppelingen bestaan.");
                                return response.createException();
                            })
                    .toBodilessEntity()
                    .block();

            System.out.println("🗑️ Gebruiker verwijderd.");
        } catch (WebClientResponseException e) {
            // Geen extra fout tonen, want dit werd al afgehandeld in onStatus
        } catch (Exception e) {
            System.out.println("❌ Onverwachte fout bij verwijderen: " + e.getMessage());
        }
    }

    private void printGebruiker(UserDTO gebruiker) {
        System.out.printf("ID: %d | %s %s | %s | %s%n",
                gebruiker.getId(),
                gebruiker.getVoornaam(),
                gebruiker.getAchternaam(),
                gebruiker.getMail(),
                gebruiker.getGeboortedatum()
        );
    }
}
