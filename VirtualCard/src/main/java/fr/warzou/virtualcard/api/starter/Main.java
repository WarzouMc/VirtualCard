package fr.warzou.virtualcard.api.starter;

import org.fusesource.jansi.AnsiConsole;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.print("Need to use the Windows shell !!!\npress enter to continue.");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        System.out.println("... Launching ! Please wait a little time !");

        AnsiConsole.systemInstall();
        Launcher launcher = new Launcher();
        launcher.launch();
    }
}
