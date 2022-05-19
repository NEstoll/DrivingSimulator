package application;

import javax.swing.*;
import java.awt.*;

public class FileImport extends JPanel {
    public FileImport(String text) {
        super(new GridBagLayout());
        // Put text on the left
        GridBagConstraints lableConstraints = new GridBagConstraints();
        lableConstraints.gridx = 0;
        lableConstraints.gridy = 0;
        lableConstraints.gridwidth = 2;
        lableConstraints.gridheight = 3;
        lableConstraints.weightx = 1.0;
        lableConstraints.fill = GridBagConstraints.HORIZONTAL;
        this.add(new JLabel(text), lableConstraints);
        // Put button on the right
        GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.gridx = 2;
        buttonConstraints.gridy = 0;
        buttonConstraints.gridwidth = 1;
        buttonConstraints.gridheight = 3;
        buttonConstraints.weightx = 0;
        buttonConstraints.fill = GridBagConstraints.HORIZONTAL;
        this.add(new JButton("Upload File"), buttonConstraints);
    }
}
