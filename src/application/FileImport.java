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
        lableConstraints.gridwidth = 1;
        lableConstraints.gridheight = 3;
        lableConstraints.weightx = 1.0;
        lableConstraints.fill = GridBagConstraints.HORIZONTAL;
        label = new JLabel("No File Uploaded");
        this.add(label, lableConstraints);

        // Put button on the right
        GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.gridx = 1;
        buttonConstraints.gridy = 0;
        buttonConstraints.gridwidth = 1;
        buttonConstraints.gridheight = 3;
        buttonConstraints.weightx = .3;
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

        //moved the label to a tooltip -Nicholas

//        // add info label
//        GridBagConstraints infoLabelConstraints = new GridBagConstraints();
//        infoLabelConstraints.gridx = 2;
//        infoLabelConstraints.gridy = 0;
//        infoLabelConstraints.gridwidth = 1;
//        infoLabelConstraints.gridheight = 1;
//        infoLabelConstraints.weightx = 0;
//        infoLabelConstraints.fill = GridBagConstraints.HORIZONTAL;
//        this.add(new JLabel("More Info"), infoLabelConstraints);

        // add info button
        GridBagConstraints infoIconConstraints = new GridBagConstraints();
        infoIconConstraints.gridx = 2;
        infoIconConstraints.gridy = 0;
        infoIconConstraints.gridwidth = 1;
        infoIconConstraints.gridheight = 3;
        infoIconConstraints.weightx = 0;
        infoIconConstraints.fill = GridBagConstraints.HORIZONTAL;

        ImageIcon icon = new ImageIcon("icon.png");
        icon.setImage(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        JButton iconButton = new JButton(icon);
        iconButton.setToolTipText("Click for more Info");
        this.add(iconButton, infoIconConstraints);


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
