package main;

import view.GamePanel;
import view.ResourceManager;

import javax.swing.*;

public class  Main {

    public static void main(String[] args) {
        // 1. Load resources BEFORE creating the window
        ResourceManager.loadImages();

        // 2. Setup the game window
        JFrame frame = new JFrame("Shooting Game MVC");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new GamePanel());
        frame.pack();
        frame.setLocationRelativeTo(null); // ウィンドウを画面中央に
        frame.setVisible(true);

        System.out.println("hello");

    }

}
