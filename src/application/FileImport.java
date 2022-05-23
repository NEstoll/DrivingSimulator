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
    private JButton fileOpen;
    private JLabel label;


    /**
     * @param text The label for the file import
     */
    public FileImport(String text) {
        //super constructor
        super(new GridBagLayout());

        // Put text on the left
        GridBagConstraints lableConstraints = new GridBagConstraints();
        lableConstraints.gridx = 0;
        lableConstraints.gridy = 0;
        lableConstraints.gridwidth = 2;
        lableConstraints.gridheight = 3;
        lableConstraints.weightx = 1.0;
        lableConstraints.fill = GridBagConstraints.HORIZONTAL;
        label = new JLabel("No File Uploaded");
        this.add(label, lableConstraints);

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
                FileDialog files = new FileDialog(new JFrame());
                files.setVisible(true);
                handleFile(files.getFile());
            }
        });
        this.add(fileOpen, buttonConstraints);
        this.setBorder(BorderFactory.createTitledBorder(text));
    }

    public void handleFile(String filename) {
        label.setText(filename);
        DataInterface.getInstance().inputFile(new File(filename), ((TitledBorder)getBorder()).getTitle());
    }

    public JLabel getLabel() {
        return label;
    }
}
