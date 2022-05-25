package application;

import javax.swing.*;
import java.awt.*;
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
            while (!chooseAssetto());
        }

        try {
            DataInterface.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            advancedPanelSetup("test_car");
        } catch (IOException e) {
            System.out.println("test_car not found");
        }
        basicPanelSetup();

        //content layout
        JPanel layout = new JPanel(new BorderLayout());
        layout.add(basicPanel, BorderLayout.CENTER);

        //add name field
        layout.add(new TextField(), BorderLayout.PAGE_START);

        //add "Submit" button
        layout.add(new JButton("Build"), BorderLayout.PAGE_END);

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
                }
                break;
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
        basicContent.add(new FileImport("Powertrain data", DataInterface.Type.GEARS));
        basicContent.add(new FileImport("Suspension data", DataInterface.Type.SUSPENSION));
        basicPanel = basicContent;
    }

    private static Component advancedPanelSetup(String loadedCar) throws IOException {
        //advanced window
        advancedPanel = new JScrollPane();
        JPanel advancedContent = new JPanel();
        ((JScrollPane) (advancedPanel)).getVerticalScrollBar().setUnitIncrement(10);
        ((JScrollPane) (advancedPanel)).setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        ((JScrollPane) (advancedPanel)).setViewportView(advancedContent);
        advancedContent.setLayout(new BoxLayout(advancedContent, BoxLayout.PAGE_AXIS));
        Map<File, ArrayList<String[]>> configs = null;
        configs = DataInterface.generateConfigs(loadedCar);
        for (Map.Entry<File, ArrayList<String[]>> item: configs.entrySet()) {
            JPanel file = new JPanel();
            file.setLayout(new BoxLayout(file, BoxLayout.PAGE_AXIS));
            file.setBorder(BorderFactory.createTitledBorder(item.getKey().getName()));
            for (String[] value: item.getValue()) {
                if (value.length == 1) {
                    JLabel l = new JLabel(value[0]);
                    l.setAlignmentX(Component.LEFT_ALIGNMENT);
                    l.setFont(new Font(null, Font.PLAIN, 18));
                    file.add(l);
                } else {
                    JPanel textChooser = new JPanel();
                    textChooser.setLayout(new GridLayout(0, 2));
                    textChooser.add(new JLabel(value[0]));
                    textChooser.add(new JTextField(value.length==3?value[2]:""));
                    textChooser.setToolTipText(value[1]);
                    textChooser.setAlignmentX(Component.LEFT_ALIGNMENT);
                    file.add(textChooser);
                }
            }
            advancedContent.add(file);
        }
        return advancedPanel;
    }

}
