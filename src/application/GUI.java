package application;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Main class for UI, will handle all subcomponents and displaying the application
 *
 */
public class GUI extends JFrame {
    public static void main(String[] args) {
        //create frame
        JFrame frame = new GUI();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //create data interface
        DataInterface data = DataInterface.getInstance();

        //add content
        JPanel content = new JPanel(new BorderLayout());
        //upload buttons (added in a row top to bottom in the center of the window)
        JPanel uploads = new JPanel();
        uploads.setLayout(new BoxLayout(uploads, BoxLayout.PAGE_AXIS));
        uploads.add(new FileImport("Aero"));
        uploads.add(new FileImport("Drivetrain"));
        uploads.add(new FileImport("Suspension"));
        content.add(uploads, BorderLayout.CENTER);

        //add name field
        content.add(new TextField(), BorderLayout.PAGE_START);

        //add "Submit" button
        content.add(new JButton("Build"), BorderLayout.PAGE_END);

        //add content, make visible
        frame.setContentPane(content);
        frame.pack();
        frame.setVisible(true);
    }

}
