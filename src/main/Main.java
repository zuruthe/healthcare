package main;
import healthcare.view.MainFrame;

public class Main {
    public static void main(String[] args) {
 System.out.println("Starting app...");
javax.swing.SwingUtilities.invokeLater(() -> { new MainFrame();});}}
