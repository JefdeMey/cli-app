package be.ucll.cliapp;

import be.ucll.cliapp.menu.MenuService;

public class CliApp {
    public static void main(String[] args) {
        System.out.println("CLI-app gestart.");
        new MenuService().start();

    }
}