    package be.ucll.cliapp.menu;

    import be.ucll.cliapp.client.CampusClient;
    import be.ucll.cliapp.dto.CampusDTO;
    import be.ucll.cliapp.util.ConsoleUtil;

    import java.util.Scanner;

    public class CampusMenu {

        private final CampusClient campusClient = new CampusClient();


        public void toonMenu(Scanner scanner) {
            while (true) {
                System.out.println("\n📍 Campusmenu:");
                System.out.println("1. Toon alle campussen");
                System.out.println("2. Zoek campus op naam");
                System.out.println("3. Maak nieuwe campus aan");
                System.out.println("4. Bewerk campus");
                System.out.println("5. Verwijder campus");
                System.out.println("6. Toon alle lokalen binnen campus");
                System.out.println("0. Terug naar hoofdmenu");
                System.out.print("➤ Kies een optie: ");
                String keuze = scanner.nextLine();

                switch (keuze) {
                    case "1" -> {
                        ConsoleUtil.clearScreen();
                        campusClient.haalAlleCampussenOp();
                        ConsoleUtil.pressEnterToContinue(scanner);
                    }

                    case "2" -> {
                        ConsoleUtil.clearScreen();
                        campusClient.haalAlleCampussenOp();
                        System.out.print("Naam: ");
                        campusClient.zoekCampusOpNaam(scanner.nextLine());
                        ConsoleUtil.pressEnterToContinue(scanner);
                    }

                    case "3" -> {
                        ConsoleUtil.clearScreen();
                        String naam = "";
                        while (naam.isBlank()) {
                            System.out.print("Naam: ");
                            naam = scanner.nextLine();
                            if (naam.isBlank()) System.out.println("❌ Naam mag niet leeg zijn.");
                        }

                        String adres = "";
                        while (adres.isBlank()) {
                            System.out.print("Adres: ");
                            adres = scanner.nextLine();
                            if (adres.isBlank()) System.out.println("❌ Adres mag niet leeg zijn.");
                        }
                        int plaatsen = -1;
                        while (plaatsen < 1) {
                            System.out.print("Aantal parkeerplaatsen: ");
                            String input = scanner.nextLine();
                            try {
                                plaatsen = Integer.parseInt(input);
                                if (plaatsen < 1) System.out.println("❌ Minstens 1 plaats vereist.");
                            } catch (NumberFormatException e) {
                                System.out.println("❌ Ongeldig getal.");
                            }
                        }

                        campusClient.voegCampusToe(naam, adres, plaatsen);
                        ConsoleUtil.pressEnterToContinue(scanner);
                    }

                    case "4" -> {
                        ConsoleUtil.clearScreen();
                        System.out.print("Naam van te bewerken campus: ");
                        String naam = scanner.nextLine();

                        CampusDTO bestaand = campusClient.zoekCampusOpNaamEnGeefTerug(naam);
                        if (bestaand == null) {
                            System.out.println("❌ Campus niet gevonden");
                            ConsoleUtil.pressEnterToContinue(scanner);
                            break;
                        }

                        System.out.print("Nieuw adres (leeg laten om huidig te behouden): ");
                        String adres = scanner.nextLine();
                        if (adres.isBlank()) adres = bestaand.getAdres();

                        Integer parkeerplaatsen = null;
                        while (parkeerplaatsen == null) {
                            System.out.print("Aantal parkeerplaatsen (leeg laten om huidig te behouden): ");
                            String input = scanner.nextLine();
                            if (input.isBlank()) {
                                parkeerplaatsen = bestaand.getAantalParkeerplaatsen();
                            } else {
                                try {
                                    int p = Integer.parseInt(input);
                                    if (p < 1) {
                                        System.out.println("❌ Moet minstens 1 zijn.");
                                    } else {
                                        parkeerplaatsen = p;
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println("❌ Ongeldig getal.");
                                }
                            }
                        }
                        campusClient.wijzigCampus(naam, adres, parkeerplaatsen);
                        ConsoleUtil.pressEnterToContinue(scanner);
                    }

                    case "5" -> {
                        ConsoleUtil.clearScreen();
                        String naam = "";
                        while (naam.isBlank()) {
                            System.out.print("Naam te verwijderen: ");
                            naam = scanner.nextLine();
                            if (naam.isBlank()) System.out.println("❌ Naam mag niet leeg zijn.");
                        }

                        System.out.print("Ben je zeker dat je '" + naam + "' wil verwijderen? (y/n): ");
                        String confirm = scanner.nextLine();
                        if (confirm.equalsIgnoreCase("y")) {
                            boolean gelukt = campusClient.verwijderCampus(naam);
                            if (!gelukt) {
                                System.out.println("⚠️ Verwijderen is mislukt of campus bestond niet.");
                            }
                        } else {
                            System.out.println("❎ Verwijderen geannuleerd.");
                        }

                        ConsoleUtil.pressEnterToContinue(scanner);
                    }

                    case "6" -> {
                        ConsoleUtil.clearScreen();
                        System.out.print("Campusnaam: ");
                        String naam = scanner.nextLine();

                        if(!campusClient.bestaatCampus(naam)) {
                            System.out.println("❌ Campus bestaat niet");
                        } else {
                            campusClient.toonLokalenBinnenCampus(naam);
                        }
                        ConsoleUtil.pressEnterToContinue(scanner);
                    }

                    case "0" -> { return; }

                    default -> System.out.println("❌ Ongeldige keuze.");
                }
            }
        }
    }

