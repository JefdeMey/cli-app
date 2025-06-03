package be.ucll.cliapp.menu;

import be.ucll.cliapp.util.ConsoleUtil;

import java.util.Scanner;

public class MenuService {
    private final Scanner scanner = new Scanner(System.in);
    private final CampusMenu campusMenu = new CampusMenu();
    private final UserMenu userMenu = new UserMenu();
    private final LokaalMenu lokaalMenu = new LokaalMenu();
    private final ReservatieMenu reservatieMenu = new ReservatieMenu();

    public void start() {
        while (true) {
            System.out.println("\n🎯 Hoofdmenu:");
            System.out.println("1. Campussen");
            System.out.println("2. Lokalen");
            System.out.println("3. Gebruikers");
            System.out.println("4. Reservaties");
            System.out.println("0. Stoppen");
            System.out.print("➤ Maak een keuze: ");
            String keuze = scanner.nextLine();

            switch (keuze) {
                case "1" -> {
                    ConsoleUtil.clearScreen();
                    campusMenu.toonMenu(scanner);
                }

                case "2" -> {
                    ConsoleUtil.clearScreen();
                    lokaalMenu.toonMenu(scanner);
                }

                case "3" -> {
                    ConsoleUtil.clearScreen();
                    userMenu.toonMenu(scanner);
                }

                case "4" -> {
                    ConsoleUtil.clearScreen();
                    reservatieMenu.toonMenu(scanner);
                }

                case "0" -> {
                    System.out.println("👋 Tot de volgende!");
                    return;
                }

                default -> System.out.println("❌ Ongeldige keuze.");
            }
        }
    }
}