package application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Main class for UI, will handle all subcomponents and displaying the application
 */
public class GUI extends JFrame {
    private static Component advancedPanel;
    private static Component basicPanel;

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

        //create data interface
        try {
            DataInterface.getAssetto();
        } catch (IOException e) {
            while (!chooseAssetto()) ;
        }

        try {
            DataInterface.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            //TODO ask user for car, don't hardcode
            advancedPanelSetup("test_car2");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        basicPanelSetup();

        //content layout
        JPanel layout = new JPanel(new BorderLayout());
        layout.add(DataInterface.getConfigs().get("mode").equals("basic") ? basicPanel : advancedPanel, BorderLayout.CENTER);

        //add name field
        layout.add(new TextField(), BorderLayout.PAGE_START);

        //add "Submit" button
        JButton build =  new JButton("Build");
        build.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DataInterface.parseFiles();
                frame.close();
            }
        });
        layout.add(build, BorderLayout.PAGE_END);

        //add content, make visible
        frame.setContentPane(layout);
        frame.pack();
        frame.setVisible(true);
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

    private static void advancedPanelSetup(String loadedCar) throws IOException {
        //advanced window
        JScrollPane scrollPane = new JScrollPane();
        JPanel advancedContent = new JPanel();
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setViewportView(advancedContent);
        advancedContent.setLayout(new BoxLayout(advancedContent, BoxLayout.PAGE_AXIS));
        Map<File, Map<String, ArrayList<String[]>>> configs = null;
        configs = DataInterface.generateConfigs(loadedCar);
        for (Map.Entry<File, Map<String, ArrayList<String[]>>> item : configs.entrySet()) {
            JPanel file = new JPanel();
            file.setLayout(new BoxLayout(file, BoxLayout.PAGE_AXIS));
            file.setBorder(BorderFactory.createTitledBorder(item.getKey().getName()));
            for (Map.Entry<String, ArrayList<String[]>> section : item.getValue().entrySet()) {
                JLabel l = new JLabel(section.getKey());
                l.setAlignmentX(Component.LEFT_ALIGNMENT);
                l.setFont(new Font(null, Font.PLAIN, 18));
                file.add(l);
                for (String[] value : section.getValue()) {
                    JPanel textChooser = new JPanel();
                    textChooser.setLayout(new GridLayout(0, 2));
                    textChooser.add(new JLabel(value[0]));
                    textChooser.add(new JTextField(value.length == 3 ? value[1] : ""));
                    textChooser.setToolTipText(value[2]);
                    textChooser.setAlignmentX(Component.LEFT_ALIGNMENT);
                    file.add(textChooser);
                }
            }
            advancedContent.add(file);
        }
        advancedPanel = scrollPane;
    }

}
