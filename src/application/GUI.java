package application;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Main class for UI, will handle all subcomponents and displaying the application
 */
public class GUI extends JFrame {
    public static void main(String[] args) {
        //create frame
        JFrame frame = new GUI();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //create data interface
        try {
            DataInterface.verifyAssetto();
        } catch (IOException e) {
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
                    DataInterface.setAssetto(chooser.getSelectedFile());
                    break;
                case JFileChooser.CANCEL_OPTION:
                case JFileChooser.ERROR_OPTION:
                    //reprompt?
                    return;
            }
            DataInterface.setAssetto(chooser.getSelectedFile());
        }

        //advanced window
        JScrollPane scroll = new JScrollPane();
        JPanel advancedContent = new JPanel();
        scroll.createVerticalScrollBar();
        scroll.getVerticalScrollBar().setUnitIncrement(10);
        scroll.setViewportView(advancedContent);
        advancedContent.setLayout(new BoxLayout(advancedContent, BoxLayout.PAGE_AXIS));
        Map<File, ArrayList<String[]>> configs = null;
        try {
            configs = DataInterface.generateConfigs("test_car");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("Car not found");
        }
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
                    textChooser.setLayout(new GridLayout());
                    textChooser.add(new JLabel(value[0]));
                    textChooser.add(new JTextField());
                    textChooser.setToolTipText(value[1]);
                    textChooser.setAlignmentX(Component.LEFT_ALIGNMENT);
                    file.add(textChooser);
                }
            }
            advancedContent.add(file);
        }


        //add basic content
        JPanel basicContent = new JPanel();
        //upload buttons (added in a row top to bottom in the center of the window)
        basicContent.setLayout(new BoxLayout(basicContent, BoxLayout.PAGE_AXIS));
        basicContent.add(new FileImport("Aero"));
        basicContent.add(new FileImport("Drivetrain"));
        basicContent.add(new FileImport("Suspension"));

        //content layout
        JPanel layout = new JPanel(new BorderLayout());
        layout.add(scroll, BorderLayout.CENTER);

        //add name field
        layout.add(new TextField(), BorderLayout.PAGE_START);

        //add "Submit" button
        layout.add(new JButton("Build"), BorderLayout.PAGE_END);

        //add content, make visible
        frame.setContentPane(layout);
        frame.pack();
        frame.setVisible(true);
    }

}
