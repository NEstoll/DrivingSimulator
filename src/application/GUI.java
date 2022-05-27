package application;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
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
        try {
            DataInterface.verifyAssetto();
        } catch (IOException e) {
            //prompt user for assetto location
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setDialogTitle("Please select assettocorsa folder");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.setVisible(true);
            chooser.showOpenDialog(frame);

            DataInterface.setAssetto(chooser.getSelectedFile());


//            FileDialog findAssetto = new FileDialog(new JFrame());
//            findAssetto.setTitle("Please provide the assettocorsa folder");
//            findAssetto.setFilenameFilter((dir, name) -> dir.isDirectory());
//            findAssetto.setVisible(true);
//            String a = findAssetto.getFile();
//            if (a!=null) {
//                DataInterface.setAssetto(new File(a));
//            }

//            JPanel findAssetto = new JPanel(new BorderLayout());
//            findAssetto.add(new JLabel("assettocorsa folder not found, please provide:"), BorderLayout.PAGE_START);
//            findAssetto.add(new TextField(), BorderLayout.CENTER);
//            findAssetto.add(new JButton("Browse"), BorderLayout.LINE_END)
        }

        //add basic content
        JPanel basicContent = new JPanel(new BorderLayout());
        //upload buttons (added in a row top to bottom in the center of the window)
        JPanel uploads = new JPanel();
        uploads.setLayout(new BoxLayout(uploads, BoxLayout.PAGE_AXIS));
        uploads.add(new FileImport("Aero"));
        uploads.add(new FileImport("Drivetrain"));
        uploads.add(new FileImport("Suspension"));
        basicContent.add(uploads, BorderLayout.CENTER);

        //add info section
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.PAGE_AXIS));
        info.add(new JLabel("Click for More Info"));
        Icon icon = new ImageIcon("icon.png");
        JButton iconButton = new JButton(icon);
        Box box = Box.createVerticalBox();
        box.add(iconButton);
        info.add(iconButton);

        info.add(new JLabel("Click for More Info"));
        Icon icon2 = new ImageIcon("icon.png");
        JButton iconButton2 = new JButton(icon2);
        Box box2 = Box.createVerticalBox();
        box2.add(iconButton2);
        info.add(iconButton2);

        info.add(new JLabel("Click for More Info"));
        Icon icon3 = new ImageIcon("icon.png");
        JButton iconButton3 = new JButton(icon3);
        Box box3 = Box.createVerticalBox();
        box3.add(iconButton3);
        info.add(iconButton3);

        //add name field
        basicContent.add(new TextField(), BorderLayout.PAGE_START);

        //add "Submit" button
        basicContent.add(new JButton("Build"), BorderLayout.PAGE_END);

        //add content, make visible
        frame.setContentPane(basicContent);
        frame.pack();
        frame.setVisible(true);
    }

}
