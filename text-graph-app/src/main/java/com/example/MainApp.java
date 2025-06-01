package com.example;

import com.example.gui.MainFrame;
import javax.swing.SwingUtilities;

/** The main entry point of the application. Launches the graphical user interface. */
public class MainApp {

  /**
   * The main method. Starts the GUI main window.
   *
   * @param args the command-line arguments
   */
  public static void main(String[] args) {
    SwingUtilities.invokeLater(
        () -> {
          MainFrame frame = new MainFrame(args);
          frame.setVisible(true);
        });
  }
}
