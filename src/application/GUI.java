package application;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 *
 */
public class GUI {
    public static void main(String[] args) {
        //create frame
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //add content
        JPanel content = new JPanel(new BorderLayout());
        //upload buttons (added in a row top to bottom in the center of the window)
        JPanel uploads = new JPanel();
        uploads.setLayout(new BoxLayout(uploads, BoxLayout.PAGE_AXIS));
        uploads.add(new FileImport("Please Upload File"));
        uploads.add(new FileImport("Please Upload File"));
        uploads.add(new FileImport("Please Upload File"));
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
