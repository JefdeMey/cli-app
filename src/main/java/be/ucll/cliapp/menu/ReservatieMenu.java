package be.ucll.cliapp.menu;

import be.ucll.cliapp.client.LokaalClient;
import be.ucll.cliapp.client.ReservatieClient;
import be.ucll.cliapp.client.UserClient;
import be.ucll.cliapp.dto.ReservatieCreateDTO;
import be.ucll.cliapp.dto.ReservatieDTO;
import be.ucll.cliapp.dto.ReservatieUpdateDTO;
import be.ucll.cliapp.util.ConsoleUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ReservatieMenu {

    private final ReservatieClient client = new ReservatieClient();
    private final UserClient userClient = new UserClient();
    private final LokaalClient lokaalClient = new LokaalClient();

    public void toonMenu(Scanner scanner) {
        while (true) {
            ConsoleUtil.clearScreen();
            System.out.println("\n\uD83D\uDCC5 Reservatie-menu:");
            System.out.println("1. Toon alle reservaties");
            System.out.println("2. Zoek reservatie op ID");
            System.out.println("3. Maak nieuwe reservatie aan");
            System.out.println("4. Pas reservatie aan");
            System.out.println("5. Verwijder reservatie");
            System.out.println("0. Terug naar hoofdmenu");
            System.out.print("\u27A4 Kies een optie: ");
            String keuze = scanner.nextLine();

            switch (keuze) {
                case "1" -> {
                    client.getAll();
                    ConsoleUtil.pressEnterToContinue(scanner);
                }

                case "2" -> {
                    client.getAll();
                    System.out.print("Reservatie ID: ");
                    try {
                        client.getById(Long.parseLong(scanner.nextLine()));
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Ongeldige ID.");
                    }
                    ConsoleUtil.pressEnterToContinue(scanner);
                }

                case "3" -> {
                    ConsoleUtil.clearScreen();
                    ReservatieCreateDTO dto = new ReservatieCreateDTO();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                    // Starttijd
                    while (dto.getStartTijd() == null) {
                        System.out.print("Starttijd (yyyy-MM-dd HH:mm): ");
                        try {
                            LocalDateTime parsed = LocalDateTime.parse(scanner.nextLine(), formatter);
                            if (parsed.isBefore(LocalDateTime.now())) {
                                System.out.println("❌ Starttijd mag niet in het verleden liggen.");
                            } else {
                                dto.setStartTijd(parsed);
                            }
                        } catch (Exception e) {
                            System.out.println("❌ Ongeldig formaat. Gebruik yyyy-MM-dd HH:mm.");
                        }
                    }

                    // Eindtijd
                    while (dto.getEindTijd() == null) {
                        System.out.print("Eindtijd (yyyy-MM-dd HH:mm): ");
                        try {
                            LocalDateTime parsed = LocalDateTime.parse(scanner.nextLine(), formatter);
                            if (parsed.isBefore(LocalDateTime.now())) {
                                System.out.println("❌ Eindtijd mag niet in het verleden liggen.");
                            } else if (parsed.isBefore(dto.getStartTijd())) {
                                System.out.println("❌ Eindtijd moet na starttijd liggen.");
                            } else {
                                dto.setEindTijd(parsed);
                            }
                        } catch (Exception e) {
                            System.out.println("❌ Ongeldig formaat. Gebruik yyyy-MM-dd HH:mm.");
                        }
                    }

                    // Aantal personen
                    Integer aantal = null;
                    while (aantal == null || aantal < 1) {
                        System.out.print("Aantal personen: ");
                        try {
                            aantal = Integer.parseInt(scanner.nextLine());
                            if (aantal < 1) System.out.println("❌ Moet minstens 1 zijn.");
                        } catch (NumberFormatException e) {
                            System.out.println("❌ Ongeldig getal.");
                        }
                    }
                    dto.setAantalPersonen(aantal);

                    // Commentaar
                    System.out.print("Commentaar (optioneel): ");
                    dto.setCommentaar(scanner.nextLine());

                    // Gebruiker ID
                    while (true) {
                        userClient.toonAlleGebruikers();
                        System.out.print("Kies gebruiker (ID): ");
                        String input = scanner.nextLine();
                        try {
                            Long id = Long.parseLong(input);
                            if (userClient.getUserForUpdate(id) != null) {
                                dto.setGebruikerId(id);
                                break;
                            } else {
                                System.out.println("❌ Geen gebruiker met ID " + id + " gevonden.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("❌ Ongeldig ID. Probeer opnieuw.");
                        }
                    }

                    boolean succesvol = false;

                    while (!succesvol) {
                        // Lokalen opvragen
                        while (dto.getLokaalIds() == null || dto.getLokaalIds().isEmpty()) {
                            lokaalClient.toonAlleLokalen();
                            System.out.print("Lokaal ID(s) (gescheiden door komma): ");
                            try {
                                List<Long> lokaalIds = Arrays.stream(scanner.nextLine().split(","))
                                        .map(s -> Long.parseLong(s.trim()))
                                        .collect(Collectors.toList());

                                Set<Long> unieke = new HashSet<>(lokaalIds);
                                if (unieke.size() != lokaalIds.size()) {
                                    System.out.println("❌ Een lokaal mag maar één keer gekozen worden.");
                                    continue;
                                }

                                dto.setLokaalIds(lokaalIds);
                            } catch (Exception e) {
                                System.out.println("❌ Ongeldige invoer. Probeer opnieuw.");
                            }
                        }

                        // Create-probe
                        try {
                            client.create(dto);  // ENKEL HIER word create opgeroepen
                            System.out.println("✅ Reservatie succesvol aangemaakt.");
                            succesvol = true;
                        } catch (Exception e) {
                            System.out.println("❌ Aanmaken mislukt: " + e.getMessage());
                            System.out.println("🔁 Wat wil je doen?");
                            System.out.println("1. Kies andere lokalen");
                            System.out.println("2. Annuleer reservatie");

                            System.out.print("Keuze: ");
                            String keuze2 = scanner.nextLine();

                            if (keuze2.equals("1")) {
                                dto.setLokaalIds(null); // opnieuw laten kiezen
                            } else {
                                System.out.println("🚫 Reservatie geannuleerd.");
                                break;
                            }
                        }
                    }

                    ConsoleUtil.pressEnterToContinue(scanner);
                }

                case "4" -> {
                    ConsoleUtil.clearScreen();
                    client.getAll();
                    Long id = null;
                    while (true) {
                        System.out.print("Reservatie ID: ");
                        try {
                            Long ingegevenId = Long.parseLong(scanner.nextLine());
                            ReservatieDTO reservatie = client.getByIdReturn(ingegevenId);
                            if (reservatie != null) {
                                id = ingegevenId;
                                break;
                            } else {
                                System.out.println("❌ Geen reservatie gevonden met ID " + ingegevenId);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("❌ Ongeldig ID-formaat.");
                        }

                        // Vraag of de gebruiker wil stoppen
                        System.out.print("⛔ Stoppen met bewerken? (Y/N): ");
                        String keuze3 = scanner.nextLine().trim().toLowerCase();
                        if (keuze3.equals("y")) {
                            System.out.println("🚫 Bewerking geannuleerd.");
                            ConsoleUtil.pressEnterToContinue(scanner);
                            return;
                        }
                    }

                    ReservatieDTO origineel = client.getByIdReturn(id);
                    ReservatieUpdateDTO dto = new ReservatieUpdateDTO();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    // Starttijd
                    while (dto.getStartTijd() == null) {
                        System.out.print("Starttijd (yyyy-MM-dd HH:mm) [" + origineel.getStartTijd().format(formatter) + "] (leeg om huidig te behouden): ");
                        String input = scanner.nextLine().trim();

                        if (input.isEmpty()) {
                            dto.setStartTijd(origineel.getStartTijd()); // behoud originele waarde
                        } else {
                            try {
                                LocalDateTime parsed = LocalDateTime.parse(input, formatter);

                                if (parsed.isBefore(LocalDateTime.now())) {
                                    System.out.println("❌ Starttijd mag niet in het verleden liggen.");
                                } else {
                                    dto.setStartTijd(parsed);
                                }
                            } catch (Exception e) {
                                System.out.println("❌ Ongeldig formaat. Gebruik yyyy-MM-dd HH:mm.");
                            }
                        }
                    }


                    // Eindtijd
                    while (dto.getEindTijd() == null) {
                        System.out.print("Eindtijd (yyyy-MM-dd HH:mm) [" + origineel.getEindTijd().format(formatter) + "] (leeg om huidig te behouden): ");
                        String input = scanner.nextLine().trim();

                        if (input.isEmpty()) {
                            dto.setEindTijd(origineel.getEindTijd()); // behoud originele waarde
                        } else {
                            try {
                                LocalDateTime parsed = LocalDateTime.parse(input, formatter);
                                LocalDateTime start = dto.getStartTijd() != null ? dto.getStartTijd() : origineel.getStartTijd();

                                if (parsed.isBefore(LocalDateTime.now())) {
                                    System.out.println("❌ Eindtijd mag niet in het verleden liggen.");
                                } else if (parsed.isBefore(start)) {
                                    System.out.println("❌ Eindtijd moet na starttijd liggen.");
                                } else {
                                    dto.setEindTijd(parsed);
                                }
                            } catch (Exception e) {
                                System.out.println("❌ Ongeldig formaat. Gebruik yyyy-MM-dd HH:mm.");
                            }
                        }
                    }


                    // Aantal personen
                    Integer aantal = null;
                    while (aantal == null) {
                        System.out.print("Aantal personen (leeg om huidig te behouden): ");
                        String input = scanner.nextLine();
                        if (input.isBlank()) {
                            aantal = origineel.getAantalPersonen(); // bestaande = ReservatieDTO van eerder
                            dto.setAantalPersonen(aantal);
                            break;
                        }

                        try {
                            int parsed = Integer.parseInt(input);
                            if (parsed < 1) {
                                System.out.println("❌ Aantal moet minstens 1 zijn.");
                            } else {
                                aantal = parsed;
                                dto.setAantalPersonen(aantal);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("❌ Ongeldig getal. Geef een positief geheel getal in.");
                        }
                    }


                    System.out.print("Commentaar (leeg om huidige te behouden): ");
                    String commentaarInput = scanner.nextLine();
                    dto.setCommentaar(commentaarInput.isBlank() ? origineel.getCommentaar() : commentaarInput);

                    // Lokalen
                    List<Long> origineleLokaalIds = origineel.getLokaalNamen().stream()
                            .map(lokaalClient::getIdByNaam)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    while (dto.getLokaalIds() == null || dto.getLokaalIds().isEmpty()) {
                        lokaalClient.toonAlleLokalen();
                        System.out.print("Nieuwe lokaal ID(s) (Enter om te behouden): ");
                        String input = scanner.nextLine();
                        if (input.isBlank()) {
                            dto.setLokaalIds(origineleLokaalIds); // behoud
                            break;
                        }
                        try {
                            List<Long> lokaalIds = Arrays.stream(input.split(","))
                                    .map(s -> Long.parseLong(s.trim()))
                                    .collect(Collectors.toList());

                            Set<Long> unieke = new HashSet<>(lokaalIds);
                            if (unieke.size() != lokaalIds.size()) {
                                System.out.println("❌ Een lokaal mag maar één keer gekozen worden.");
                                continue;
                            }

                            dto.setLokaalIds(lokaalIds);
                        } catch (Exception e) {
                            System.out.println("❌ Ongeldige invoer. Probeer opnieuw.");
                        }
                    }



                    try {
                        client.update(id, dto);
                        System.out.println("✅ Reservatie succesvol aangepast.");
                    } catch (Exception e) {
                        System.out.println("❌ Aanpassen mislukt: " + e.getMessage());
                    }
                    ConsoleUtil.pressEnterToContinue(scanner);
                }

                case "5" -> {
                    client.getAll();

                    Long id = null;

                    // Herhaal tot geldige ID
                    while (id == null) {
                        System.out.print("ID te verwijderen: ");
                        String input = scanner.nextLine();

                        try {
                            id = Long.parseLong(input);
                        } catch (NumberFormatException e) {
                            System.out.println("❌ Ongeldige ID. Probeer opnieuw.");
                        }
                    }

                    // Bevestiging
                    System.out.print("❓ Ben je zeker dat je reservatie " + id + " wil verwijderen? (Y/N): ");
                    String bevestiging = scanner.nextLine().trim().toLowerCase();

                    if (bevestiging.equals("y")) {
                        client.delete(id);
                    } else {
                        System.out.println("🚫 Verwijderen geannuleerd.");
                    }

                    ConsoleUtil.pressEnterToContinue(scanner);
                }

                case "0" -> { return; }

                default -> {
                    System.out.println("❌ Ongeldige keuze.");
                    ConsoleUtil.pressEnterToContinue(scanner);
                }
            }
        }
    }
}
