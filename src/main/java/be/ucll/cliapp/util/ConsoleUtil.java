package be.ucll.cliapp.util;

import java.util.Scanner;

public class ConsoleUtil {

    public static void pressEnterToContinue(Scanner scanner) {
        System.out.print("\n⏎ Druk op Enter om verder te gaan...");
        scanner.nextLine();
        clearScreen();
    }

    public static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            System.out.println("\n".repeat(20));
        }
    }
}
