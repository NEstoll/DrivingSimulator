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
        labelConstraints.gridwidth = 1;
        labelConstraints.gridheight = 3;
        labelConstraints.weightx = 1.0;
        labelConstraints.fill = GridBagConstraints.HORIZONTAL;
        label = new JLabel(DataInterface.getInputFiles().containsKey(type)?DataInterface.getInputFiles().get(type).getName():"No file selected");
        this.add(label, labelConstraints);
        DataInterface.addfileListener(type, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                label.setText(DataInterface.getInputFiles().containsKey(type)?DataInterface.getInputFiles().get(type).getName():"No file selected");
            }
        });

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
                JFileChooser choose = new JFileChooser();
                choose.setDialogTitle("Please select file");
                choose.setCurrentDirectory(new File("."));
                choose.setVisible(true);
                choose.showOpenDialog(null);
                handleFile(choose.getSelectedFile());
            }
        });
        this.add(fileOpen, buttonConstraints);

        // add info button
        GridBagConstraints infoIconConstraints = new GridBagConstraints();
        infoIconConstraints.gridx = 2;
        infoIconConstraints.gridy = 0;
        infoIconConstraints.gridwidth = 1;
        infoIconConstraints.gridheight = 3;
        infoIconConstraints.weightx = 0;
        infoIconConstraints.fill = GridBagConstraints.HORIZONTAL;

        ImageIcon icon = new ImageIcon("icon.png");
        icon.setImage(icon.getImage().getScaledInstance(16, 16 , Image.SCALE_SMOOTH));
        JButton iconButton = new JButton(icon);
        iconButton.setToolTipText("Click for more Info");
        this.add(iconButton, infoIconConstraints);

        iconButton.addActionListener(e -> JOptionPane.showMessageDialog(iconButton, DataInterface.formatString(type)));

        this.setBorder(BorderFactory.createTitledBorder(text));
    }

    public void handleFile(File file) {
        if (file == null) {
            label.setText("No File Selected");
            DataInterface.inputFile(null, type);
            return;
        }
        label.setText(file.getName());
        DataInterface.inputFile(file, type);
    }

    public JLabel getLabel() {
        return label;
    }

    public DataInterface.Type getType() {
        return type;
    }
}
