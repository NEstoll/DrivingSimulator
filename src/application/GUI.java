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
    private static Component advancedPanel;
    private static Component basicPanel;
    public static JTextField name;

    public static void main(String[] args) {
        //create frame
        GUI frame = new GUI();
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.close();
            }
        });

        //data interface calls
        if (DataInterface.getAssetto() == null) {
            while (!chooseAssetto()) ;
        }

        try {
            DataInterface.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        basicPanelSetup();

        //content layout
        JPanel layout = new JPanel(new BorderLayout());
        layout.add(basicPanel, BorderLayout.CENTER);

        //info button added to file import class

        //add name field
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.LINE_AXIS));
        header.add(new JLabel("Name:"));
        name = new JTextField();
        header.add(name);
        JButton load = new JButton("Load");
        load.addActionListener((e) -> {
            JFileChooser choose = new JFileChooser();
            choose.setDialogTitle("Please select folder containing car");
            choose.setCurrentDirectory(DataInterface.getAssetto());
            choose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            choose.setAcceptAllFileFilterUsed(false);
            choose.setVisible(true);
            choose.showOpenDialog(null);
            try {
                DataInterface.loadDefaultFiles(choose.getSelectedFile());
            } catch (IOException io) {
                io.printStackTrace();
            }
        });

        header.add(load);

        layout.add(header, BorderLayout.PAGE_START);

        //add "Submit" button
        JButton build =  new JButton("Build");
        build.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!name.getText().equals("")) {
                    DataInterface.setName(name.getText());
                    DataInterface.outputFiles(new File(DataInterface.getAssetto(), "content\\cars\\" + name.getText()));
                    frame.close();
                }
            }
        });
        layout.add(build, BorderLayout.PAGE_END);

        //add content, make visible
        frame.setContentPane(layout);
        frame.pack();
        frame.setVisible(true);
    }

    public static void setNameText(String text) {
        if (name != null) {
            name.setText(text);
            name.validate();
        }
    }

    public void close() {
        DataInterface.save();
        this.dispose();
    }

    private static boolean chooseAssetto() {
        //prompt user for assetto location
        JFileChooser chooser = new JFileChooser();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception g) {
        }
        chooser.setDialogTitle("Please select assettocorsa folder");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setVisible(true);
        switch (chooser.showOpenDialog(null)) {
            case JFileChooser.APPROVE_OPTION:
                if (DataInterface.verifyAssetto(chooser.getSelectedFile())) {
                    DataInterface.setAssetto(chooser.getSelectedFile());
                    break;
                }
            case JFileChooser.CANCEL_OPTION:
            case JFileChooser.ERROR_OPTION:
                //reprompt?
                return false;
        }
        return true;
    }

    private static void basicPanelSetup() {
        //add basic content
        JPanel basicContent = new JPanel();
        //upload buttons (added in a row top to bottom in the center of the window)
        basicContent.setLayout(new BoxLayout(basicContent, BoxLayout.PAGE_AXIS));
        basicContent.add(new FileImport("Aero data", DataInterface.Type.AERO));
        basicContent.add(new FileImport("Powertrain data", DataInterface.Type.TORQUE));
        basicContent.add(new FileImport("Gear data", DataInterface.Type.GEARS));
        basicContent.add(new FileImport("Suspension data", DataInterface.Type.SUSPENSION));
        basicPanel = basicContent;
    }


}
