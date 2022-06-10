package application;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;


public class TabMenu extends JPanel {
	public static JTextField name;
	public static JTextField build;

	public TabMenu() {
		super(new GridLayout(1, 1));

		JTabbedPane tabbedPane = new JTabbedPane()
		{
			@Override
			public Dimension getPreferredSize()
			{
				int tabsWidth = 0;

				for (int i = 0; i < getTabCount(); i++) {
					tabsWidth += getBoundsAt(i).width;
				}

				Dimension preferred = super.getPreferredSize();

				preferred.width = Math.max(preferred.width, tabsWidth);

				return preferred;
			}
		};

		JComponent configPanel = makeConfigPanel();
		JComponent ptPanel = makepowertrainPanel();
		JComponent suspensionPanel = makeSuspensionPanel();
		JComponent aeroPanel = makeAeroPanel();
	
		// Add corresponding components to tabs
		tabbedPane.addTab("Configuration", configPanel);
		tabbedPane.addTab("Power Train", ptPanel);
		tabbedPane.addTab("Suspension", suspensionPanel);
		tabbedPane.addTab("Aerodynamics", aeroPanel);

		//Add the tabbed pane to this panel.
		add(tabbedPane);

		//The following line enables to use scrolling tabs.
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
	}
	

   
	private JComponent makeConfigPanel() {
		JPanel header = new JPanel();
		header.setLayout(new BoxLayout(header, BoxLayout.PAGE_AXIS));
		header.add(new JLabel("Name:"));
		name = new JTextField();
		header.add(name);
		header.add(new JLabel("Build Version: "));
		build = new JTextField();
		header.add(build);
		JButton load = new JButton("Load");
		load.addActionListener((e) -> {
			JFileChooser choose = new JFileChooser();
			choose.setDialogTitle("Please select folder containing car");
			choose.setCurrentDirectory(DataInterface.getAssetto());
			choose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			choose.setAcceptAllFileFilterUsed(false);
			choose.setVisible(true);
			choose.showOpenDialog(null);
			try {
				DataInterface.loadDefaultFiles(choose.getSelectedFile());
			} catch (IOException io) {
				io.printStackTrace();
			}
		});
		
		//add "Submit" button
		JButton build =  new JButton("Build");
		build.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!name.getText().equals("")) {
					DataInterface.setName(name.getText());
					DataInterface.outputFiles(new File(DataInterface.getAssetto(), "content\\cars\\" + name.getText()));
				}
			}
		});
		header.add(load);
		header.add(build);
		return header;
	}

	private JComponent makepowertrainPanel() {
		JPanel panelP = new JPanel();
		panelP.setLayout(new BoxLayout(panelP, BoxLayout.PAGE_AXIS));
		panelP.add(new FileImport("Powertrain data", DataInterface.Type.TORQUE));
        panelP.add(new FileImport("Gear data", DataInterface.Type.GEARS));
		return panelP;
	}
	
	// Puts info on Suspension Panel
	private JComponent makeSuspensionPanel() {
		JPanel panelS = new JPanel();
		panelS.setLayout(new BoxLayout(panelS, BoxLayout.PAGE_AXIS));
		panelS.add(new FileImport("Suspension data", DataInterface.Type.SUSPENSION));
		panelS.add(new FileImport("Suspension data", DataInterface.Type.SUSPENSION));
		panelS.add(new FileImport("SUspension data", DataInterface.Type.SUSPENSION));
		return panelS;
	}  

	// 
	private JComponent makeAeroPanel() {
		JPanel panelA = new JPanel();
		panelA.add(new JLabel("Aero will not be done with this project!"));
		
		return panelA;
	} 
	public static void main(String[] args) {
		//Create and set up the window.
		JFrame frame = new JFrame("Mines Formula SAE");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Add content to the window.
		frame.add(new TabMenu(), BorderLayout.CENTER);

		//Display the window.
		//frame.pack();
		frame.setVisible(true);
	}


}
