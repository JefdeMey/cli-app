package be.ucll.cliapp.menu;

import be.ucll.cliapp.client.CampusClient;
import be.ucll.cliapp.client.LokaalClient;
import be.ucll.cliapp.dto.CampusDTO;
import be.ucll.cliapp.dto.LokaalCreateDTO;
import be.ucll.cliapp.dto.LokaalDTO;
import be.ucll.cliapp.util.ConsoleUtil;

import java.util.List;
import java.util.Scanner;

public class LokaalMenu {

    private final LokaalClient lokaalClient = new LokaalClient();
    private final CampusClient campusClient = new CampusClient();

    public void toonMenu(Scanner scanner) {
        while (true) {
            ConsoleUtil.clearScreen();
            System.out.println("\n\uD83C\uDFEB Lokaalmenu:");
            System.out.println("1. Toon alle lokalen");
            System.out.println("2. Zoek lokaal op ID");
            System.out.println("3. Maak nieuw lokaal aan");
            System.out.println("4. Pas lokaal aan");
            System.out.println("5. Verwijder lokaal");
            System.out.println("0. Terug naar hoofdmenu");
            System.out.print("\u27A4 Kies een optie: ");
            String keuze = scanner.nextLine();

            switch (keuze) {
                case "1" -> {
                    lokaalClient.toonAlleLokalen();
                    ConsoleUtil.pressEnterToContinue(scanner);
                }

                case "2" -> {
                    lokaalClient.toonAlleLokalen();
                    System.out.print("Lokaal-ID: ");
                    try {
                        long id = Long.parseLong(scanner.nextLine());
                        lokaalClient.zoekLokaalOpId(id);
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Ongeldig ID.");
                    }
                    ConsoleUtil.pressEnterToContinue(scanner);
                }

                case "3" -> {
                    String campusNaam = "";
                    CampusDTO campus = null;

                    // ⛔️ Herhaal zolang geen geldige campus werd gevonden
                    while (campus == null) {
                        System.out.print("Campusnaam: ");
                        campusNaam = scanner.nextLine();
                        campus = campusClient.zoekCampusOpNaamEnGeefTerug(campusNaam);
                    }

                    // ✅ Campus bestaat — ga verder
                    lokaalClient.toonLokalenBinnenCampus(campusNaam);

                    LokaalCreateDTO dto = maakLokaalDTO(scanner);
                    lokaalClient.voegLokaalToe(campusNaam, dto);
                    ConsoleUtil.pressEnterToContinue(scanner);
                }

                case "4" -> {
                    ConsoleUtil.clearScreen();
                    System.out.print("Naam van de campus waarvan je een lokaal wil bewerken: ");
                    String campusNaam = scanner.nextLine();

                    // ✅ Controleer of de campus bestaat
                    CampusDTO campus = campusClient.zoekCampusOpNaamEnGeefTerug(campusNaam);
                    if (campus == null) {
                        ConsoleUtil.pressEnterToContinue(scanner);
                        break;
                    }

                    // ✅ Toon lokalen
                    List<LokaalDTO> lijst = lokaalClient.haalLokalenBinnenCampus(campusNaam);
                    if (lijst.isEmpty()) {
                        System.out.println("⚠️ Geen lokalen gevonden in deze campus.");
                        ConsoleUtil.pressEnterToContinue(scanner);
                        break;
                    }

                    lijst.forEach(l -> System.out.printf("ID %d - %s%n", l.getId(), l.getNaam()));

                    // ✅ Kies ID van lokaal
                    System.out.print("ID van lokaal om aan te passen (enter om te annuleren): ");
                    String input = scanner.nextLine();
                    if (input.isBlank()) {
                        System.out.println("❎ Bewerken geannuleerd.");
                        ConsoleUtil.pressEnterToContinue(scanner);
                        break;
                    }
                    try {
                        long id = Long.parseLong(input);
                        LokaalDTO bestaand = lokaalClient.zoekLokaalOpIdEnGeefTerug(id);
                        if (bestaand == null) {
                            System.out.println("❌ Lokaal niet gevonden.");
                            ConsoleUtil.pressEnterToContinue(scanner);
                            break;
                        }

                        // ✅ Vraag nieuwe gegevens, leeg = behoud huidig
                        LokaalCreateDTO dto = new LokaalCreateDTO();

                        System.out.print("Nieuwe naam (leeg = behouden): ");
                        String naam = scanner.nextLine();
                        dto.setNaam(naam.isBlank() ? bestaand.getNaam() : naam);

                        System.out.print("Nieuw type (leeg = behouden): ");
                        String type = scanner.nextLine();
                        dto.setType(type.isBlank() ? bestaand.getType() : type);

                        System.out.print("Aantal personen (leeg = behouden): ");
                        String aantal = scanner.nextLine();
                        if (aantal.isBlank()) {
                            dto.setAantalPersonen(bestaand.getAantalPersonen());
                        } else {
                            try {
                                int aantalParsed = Integer.parseInt(aantal);
                                dto.setAantalPersonen(aantalParsed);
                            } catch (NumberFormatException e) {
                                System.out.println("❌ Ongeldig getal. Origineel aantal behouden.");
                                dto.setAantalPersonen(bestaand.getAantalPersonen());
                            }
                        }

                        System.out.print("Voornaam verantwoordelijke (leeg = behouden): ");
                        String voornaam = scanner.nextLine();
                        dto.setVoornaam(voornaam.isBlank() ? bestaand.getVoornaam() : voornaam);

                        System.out.print("Achternaam verantwoordelijke (leeg = behouden): ");
                        String achternaam = scanner.nextLine();
                        dto.setAchternaam(achternaam.isBlank() ? bestaand.getAchternaam() : achternaam);

                        System.out.print("Verdieping (leeg = behouden): ");
                        String verdieping = scanner.nextLine();
                        if (verdieping.isBlank()) {
                            dto.setVerdieping(bestaand.getVerdieping());
                        } else {
                            try {
                                dto.setVerdieping(Integer.parseInt(verdieping));
                            } catch (NumberFormatException e) {
                                System.out.println("❌ Ongeldige verdieping. Originele waarde behouden.");
                                dto.setVerdieping(bestaand.getVerdieping());
                            }
                        }

                        lokaalClient.pasLokaalAan(id, dto);
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Ongeldig ID-formaat.");
                    }

                    ConsoleUtil.pressEnterToContinue(scanner);
                }

                case "5" -> {
                    lokaalClient.toonAlleLokalen();
                    System.out.print("Lokaal-ID: ");
                    try {
                        long id = Long.parseLong(scanner.nextLine());
                        lokaalClient.verwijderLokaal(id);
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Ongeldig ID.");
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

    private LokaalCreateDTO maakLokaalDTO(Scanner scanner) {
        LokaalCreateDTO dto = new LokaalCreateDTO();

        // Naam
        String naam = "";
        while (naam.isBlank()) {
            System.out.print("Lokaalnaam: ");
            naam = scanner.nextLine();
            if (naam.isBlank()) System.out.println("❌ Naam mag niet leeg zijn.");
        }
        dto.setNaam(naam);

        // Type
        String type = "";
        while (type.isBlank()) {
            System.out.print("Type: ");
            type = scanner.nextLine();
            if (type.isBlank()) System.out.println("❌ Type mag niet leeg zijn.");
        }
        dto.setType(type);

        // Aantal personen
        Integer personen = null;
        while (personen == null || personen <= 0) {
            System.out.print("Aantal personen: ");
            String input = scanner.nextLine();
            try {
                personen = Integer.parseInt(input);
                if (personen <= 0) System.out.println("❌ Moet groter zijn dan 0.");
            } catch (NumberFormatException e) {
                System.out.println("❌ Ongeldig getal.");
            }
        }
        dto.setAantalPersonen(personen);

        // Voornaam
        String voornaam = "";
        while (voornaam.isBlank()) {
            System.out.print("Voornaam verantwoordelijke: ");
            voornaam = scanner.nextLine();
            if (voornaam.isBlank()) System.out.println("❌ Voornaam mag niet leeg zijn.");
        }
        dto.setVoornaam(voornaam);

        // Achternaam
        String achternaam = "";
        while (achternaam.isBlank()) {
            System.out.print("Achternaam verantwoordelijke: ");
            achternaam = scanner.nextLine();
            if (achternaam.isBlank()) System.out.println("❌ Achternaam mag niet leeg zijn.");
        }
        dto.setAchternaam(achternaam);

        // Verdieping
        Integer verdieping = null;
        while (verdieping == null) {
            System.out.print("Verdieping: ");
            String input = scanner.nextLine();
            try {
                verdieping = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("❌ Ongeldig getal.");
            }
        }
        dto.setVerdieping(verdieping);

        return dto;
    }

}
