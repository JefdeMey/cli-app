package be.ucll.cliapp.client;

import be.ucll.cliapp.ApiClient;
import be.ucll.cliapp.dto.ReservatieCreateDTO;
import be.ucll.cliapp.dto.ReservatieDTO;
import be.ucll.cliapp.dto.ReservatieUpdateDTO;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

public class ReservatieClient extends ApiClient {

    public void getAll() {
        try {
            List<ReservatieDTO> lijst = webClient.get()
                    .uri("/reservaties")
                    .retrieve()
                    .bodyToFlux(ReservatieDTO.class)
                    .collectList()
                    .block();

            if (lijst == null || lijst.isEmpty()) {
                System.out.println("ℹ️ Geen reservaties gevonden.");
            } else {
                lijst.forEach(this::print);
            }
        } catch (Exception e) {
            System.out.println("❌ Fout bij ophalen reservaties: " + e.getMessage());
        }
    }

    public void getById(Long id) {
        try {
            ReservatieDTO r = webClient.get()
                    .uri("/reservaties/" + id)
                    .retrieve()
                    .bodyToMono(ReservatieDTO.class)
                    .block();

            print(r);
        } catch (Exception e) {
            System.out.println("❌ Reservatie niet gevonden: " + e.getMessage());
        }
    }

    public ReservatieDTO getByIdReturn(Long id) {
        try {
            return webClient.get()
                    .uri("/reservaties/" + id)
                    .retrieve()
                    .bodyToMono(ReservatieDTO.class)
                    .block();
        } catch (Exception e) {
            return null; // Als niet gevonden of fout → return null
        }
    }

    public void create(ReservatieCreateDTO dto) {
        try {
            ReservatieDTO r = webClient.post()
                    .uri("/reservaties")
                    .bodyValue(dto)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class).flatMap(msg ->
                                    Mono.error(new RuntimeException("Fout bij maken reservatie: " + msg))))
                    .bodyToMono(ReservatieDTO.class)
                    .block();

            System.out.println("✅ Reservatie aangemaakt voor: " + r.getGebruikerNaam());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void update(Long id, ReservatieUpdateDTO dto) {
        try {
            ReservatieDTO r = webClient.put()
                    .uri("/reservaties/" + id)
                    .bodyValue(dto)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class).flatMap(msg ->
                                    Mono.error(new RuntimeException("Fout bij aanpassen reservatie: " + msg))))
                    .bodyToMono(ReservatieDTO.class)
                    .block();

            System.out.println("✅ Reservatie aangepast: " + r.getId());
        } catch (Exception e) {
            throw new RuntimeException("Aanpassen mislukt: " + e.getMessage(), e);
        }
    }

    public void delete(Long id) {
        try {
            webClient.delete()
                    .uri("/reservaties/{id}", id)
                    .retrieve()
                    .onStatus(status -> status.value() == 404,
                            response -> {
                                System.out.println("❌ Reservatie met ID " + id + " bestaat niet.");
                                return Mono.error(new RuntimeException("Niet gevonden"));
                            })
                    .toBodilessEntity()
                    .block();

            System.out.println("✅ Reservatie verwijderd.");
        } catch (Exception e) {
            if (!e.getMessage().equals("Niet gevonden")) {
                System.out.println("❌ Verwijderen mislukt: " + e.getMessage());
            }
        }
    }

    private void print(ReservatieDTO r) {
        System.out.printf("ID: %d | Gebruiker: %s | Start: %s | Eind: %s | Aantal: %d | Lokalen: %s\n",
                r.getId(),
                r.getGebruikerNaam(),
                r.getStartTijd(),
                r.getEindTijd(),
                r.getAantalPersonen(),
                r.getLokaalNamen() != null ? String.join(", ", r.getLokaalNamen()) : "-");
    }
}
