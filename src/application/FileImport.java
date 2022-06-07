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
        File old = type!=DataInterface.Type.NONE?DataInterface.getInputFiles().get(type):null;
        label = new JLabel(old!=null?old.getName():"No file selected");
        this.add(label, labelConstraints);

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
//                choose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//                choose.setAcceptAllFileFilterUsed(false);
                choose.setVisible(true);
                choose.showOpenDialog(new JFrame());
                handleFile(choose.getSelectedFile());
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

        iconButton.addActionListener(e -> JOptionPane.showMessageDialog(iconButton, "Message"));

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
}
