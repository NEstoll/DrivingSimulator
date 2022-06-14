package application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Main class for UI, will handle all subcomponents and displaying the application
 */
public class GUI extends JFrame {
    public static JTextField name;
    public static GUI gui;

    public static void main(String[] args) {
        //create frame
        GUI frame = new GUI();
        GUI.gui = frame;
        frame.setTitle("MF Car Builder");
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.close();
            }
        });
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch
        (Exception g) {
        }


        //data interface calls
        if (DataInterface.getAssetto() == null) {
            while
            (!chooseAssetto()) ;
        }

        try {
            DataInterface.load();
//            DataInterface.loadConfig();
//            DataInterface.loadDefaultFiles(new File( DataInterface.getConfigs().get("data-folder") + "\\default"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //content layout
        JPanel layout = new JPanel(new BorderLayout());
        layout.add(new TabMenu(), BorderLayout.CENTER); // Uses TabMenu class

        //add content, make visible
        frame.setContentPane(layout);
        frame.pack();
        frame.pack();
        frame.setVisible(true);
    }

    // Set name of car file
    public static void setNameText(String text) {
        if (text != null && TabMenu.name != null) {
            TabMenu.name.setText(text);
        }
    }

    public static void setVersionText(String text) {
        if (text != null && TabMenu.build != null) {
            TabMenu.build.setText(text);
        }
    }

    // Close
    public void close() {
        DataInterface.save();
        this.dispose();
    }

    // Find Assetto on the computer by asking user if not found
    private static boolean chooseAssetto() { //prompt user for assetto location
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Please select Assetto Corsa folder");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setVisible(true);
        switch (chooser.showOpenDialog(null)) {
            case JFileChooser.APPROVE_OPTION:
                if
                (DataInterface.verifyAssetto(chooser.getSelectedFile())) {
                    DataInterface.setAssetto(chooser.getSelectedFile());
                    break;
                }
            case JFileChooser.CANCEL_OPTION:
            case JFileChooser.ERROR_OPTION: //reprompt?
                return false;
        }
        return true;
    }
}
