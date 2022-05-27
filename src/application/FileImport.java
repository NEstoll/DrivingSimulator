package application;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Simple file import UI. Opens a FileDialog and reads file from there.
 */
public class FileImport extends JPanel {
    private  DataInterface.Type type;
    private JButton fileOpen;
    private JLabel label;


    /**
     * @param text The label for the file import
     */
    public FileImport(String text, DataInterface.Type type) {
        //super constructor
        super(new GridBagLayout());

        this.type = type;

        // Put text on the left
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = 0;
        labelConstraints.gridwidth = 2;
        labelConstraints.gridheight = 3;
        labelConstraints.weightx = 1.0;
        labelConstraints.fill = GridBagConstraints.HORIZONTAL;
        File old = type!=DataInterface.Type.NONE?DataInterface.getInputFiles().get(type):null;
        label = new JLabel(old!=null?old.getName():"No file selected");
        this.add(label, labelConstraints);

        // Put button on the right
        GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.gridx = 2;
        buttonConstraints.gridy = 0;
        buttonConstraints.gridwidth = 1;
        buttonConstraints.gridheight = 3;
        buttonConstraints.weightx = 0;
        buttonConstraints.fill = GridBagConstraints.HORIZONTAL;

        //add button functionality
        fileOpen = new JButton("Upload File");
        fileOpen.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser choose = new JFileChooser();
                choose.setDialogTitle("Please select file");
                choose.setCurrentDirectory(new File("."));
//                choose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//                choose.setAcceptAllFileFilterUsed(false);
                choose.setVisible(true);
                choose.showOpenDialog(new JFrame());
                handleFile(choose.getSelectedFile());
            }
        });
        this.add(fileOpen, buttonConstraints);
        this.setBorder(BorderFactory.createTitledBorder(text));
    }

    public void handleFile(File file) {
        if (file == null) {
            return;
        }
        label.setText(file.getName());
        DataInterface.inputFile(file, type);
    }

    public JLabel getLabel() {
        return label;
    }
}
