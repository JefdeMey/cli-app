package be.ucll.cliapp.menu;

import be.ucll.cliapp.client.UserClient;
import be.ucll.cliapp.dto.UserUpdateDTO;
import be.ucll.cliapp.util.ConsoleUtil;

import java.time.LocalDate;
import java.util.Scanner;

public class UserMenu {

    private final UserClient userClient = new UserClient();

    public void toonMenu(Scanner scanner) {
        while (true) {
            ConsoleUtil.clearScreen();
            System.out.println("\n👤 Gebruikersmenu:");
            System.out.println("1. Toon alle gebruikers");
            System.out.println("2. Zoek gebruiker op ID");
            System.out.println("3. Maak nieuwe gebruiker aan");
            System.out.println("4. Pas gebruiker aan");
            System.out.println("5. Verwijder gebruiker");
            System.out.println("6. Zoek gebruiker op naamfragment");
            System.out.println("0. Terug naar hoofdmenu");
            System.out.print("➤ Kies een optie: ");
            String keuze = scanner.nextLine();

            switch (keuze) {
                case "1" -> {
                    userClient.toonAlleGebruikers();
                    ConsoleUtil.pressEnterToContinue(scanner);
                }

                case "2" -> {
                    userClient.toonAlleGebruikers();
                    System.out.print("Gebruiker-ID: ");
                    String input = scanner.nextLine();
                    if (!input.isBlank()) {
                        try {
                            long id = Long.parseLong(input);
                            userClient.zoekGebruikerOpId(id);
                        } catch (NumberFormatException ex) {
                            System.out.println("❌ Ongeldig ID-formaat.");
                        }
                    } else {
                        System.out.println("⚠️ Geen ID ingegeven.");
                    }
                    ConsoleUtil.pressEnterToContinue(scanner);
                }

                case "3" -> {
                    ConsoleUtil.clearScreen();

                    // Voornaam
                    String voornaam = "";
                    while (voornaam.isBlank()) {
                        System.out.print("Voornaam: ");
                        voornaam = scanner.nextLine();
                        if (voornaam.isBlank()) System.out.println("❌ Voornaam mag niet leeg zijn.");
                    }

                    // Achternaam
                    String achternaam = "";
                    while (achternaam.isBlank()) {
                        System.out.print("Achternaam: ");
                        achternaam = scanner.nextLine();
                        if (achternaam.isBlank()) System.out.println("❌ Achternaam mag niet leeg zijn.");
                    }

                    // Email
                    String email = "";
                    while (email.isBlank() || !email.contains("@")) {
                        System.out.print("Email: ");
                        email = scanner.nextLine();
                        if (email.isBlank() || !email.contains("@")) {
                            System.out.println("❌ Ongeldig e-mailadres.");
                        }
                    }

                    // Geboortedatum
                    String geboortedatum = "";
                    boolean geldig = false;
                    while (!geldig) {
                        System.out.print("Geboortedatum (yyyy-mm-dd): ");
                        geboortedatum = scanner.nextLine();
                        try {
                            LocalDate.parse(geboortedatum);
                            geldig = true;
                        } catch (Exception e) {
                            System.out.println("❌ Fout formaat geboortedatum. Verwacht: yyyy-mm-dd");
                        }
                    }

                    // Alles ok → gebruiker toevoegen
                    userClient.voegGebruikerToe(voornaam, achternaam, email, geboortedatum);
                    ConsoleUtil.pressEnterToContinue(scanner);
                }

                case "4" -> {
                    ConsoleUtil.clearScreen();
                    userClient.toonAlleGebruikers();
                    System.out.print("ID van te wijzigen gebruiker: ");
                    try {
                        Long id = Long.parseLong(scanner.nextLine());
                        UserUpdateDTO bestaand = userClient.getUserForUpdate(id);
                        if (bestaand == null) {
                            System.out.println("❌ Gebruiker niet gevonden.");
                            ConsoleUtil.pressEnterToContinue(scanner);
                            break;
                        }

                        // Voornaam
                        System.out.print("Nieuwe voornaam (leeg om te behouden): ");
                        String voornaam = scanner.nextLine();
                        if (!voornaam.isBlank()) {
                            bestaand.setVoornaam(voornaam);
                        }

                        // Achternaam
                        System.out.print("Nieuwe achternaam (leeg om te behouden): ");
                        String achternaam = scanner.nextLine();
                        if (!achternaam.isBlank()) {
                            bestaand.setAchternaam(achternaam);
                        }

                        // Email
                        System.out.print("Nieuwe email (leeg om te behouden): ");
                        String email = scanner.nextLine();
                        if (!email.isBlank()) {
                            if (!email.contains("@")) {
                                System.out.println("❌ Ongeldig e-mailadres. Aanpassing wordt overgeslagen.");
                            } else {
                                bestaand.setMail(email);
                            }
                        }

                        // Geboortedatum
                        System.out.print("Nieuwe geboortedatum (yyyy-mm-dd, leeg om te behouden): ");
                        String geboortedatum = scanner.nextLine();
                        if (!geboortedatum.isBlank()) {
                            try {
                                bestaand.setGeboortedatum(LocalDate.parse(geboortedatum));
                            } catch (Exception e) {
                                System.out.println("❌ Ongeldig datumformaat. Aanpassing wordt overgeslagen.");
                            }
                        }

                        userClient.pasGebruikerAan(id, bestaand);
                    } catch (NumberFormatException ex) {
                        System.out.println("❌ Ongeldig ID-formaat.");
                    }
                    ConsoleUtil.pressEnterToContinue(scanner);
                }

                case "5" -> {
                    ConsoleUtil.clearScreen();
                    userClient.toonAlleGebruikers();
                    System.out.print("ID: ");
                    try {
                        Long id = Long.parseLong(scanner.nextLine());
                        userClient.verwijderGebruiker(id);
                    } catch (NumberFormatException ex) {
                        System.out.println("❌ Ongeldig ID-formaat.");
                    }
                    ConsoleUtil.pressEnterToContinue(scanner);
                }

                case "6" -> {
                    ConsoleUtil.clearScreen();
                    System.out.print("Naamfragment: ");
                    String fragment = scanner.nextLine();
                    userClient.zoekGebruikersOpNaam(fragment);
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
