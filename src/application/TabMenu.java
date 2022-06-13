package application;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.swing.*;


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
		aeroPanel.setPreferredSize(new Dimension(aeroPanel.getPreferredSize().width, Math.max(configPanel.getPreferredSize().height, Math.max(ptPanel.getPreferredSize().height, suspensionPanel.getPreferredSize().height))));
	
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
					DataInterface.setVersion(TabMenu.build.getText());
					DataInterface.outputFiles(new File(DataInterface.getAssetto(), "content\\cars\\" + name.getText() + (!TabMenu.build.getText().equals("")? " -" + TabMenu.build.getText():"")));
					GUI.gui.close();
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
		panelS.add(new FileImport("Suspension vehicle data", DataInterface.Type.VEHICLESETUP));
		panelS.add(new FileImport("Front suspension data", DataInterface.Type.FRONT));
		panelS.add(new FileImport("Rear suspension data", DataInterface.Type.REAR));
		return panelS;
	}  

	// 
	private JComponent makeAeroPanel() {
		JPanel aeroPanel = new JPanel();
		aeroPanel.setLayout(new BoxLayout(aeroPanel, BoxLayout.PAGE_AXIS));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setViewportView(aeroPanel);

		//advanced panel stuff
		String[] files = new String[] {"aero.ini", "wing_body_AOA_CL.lut", "wing_body_AOA_CD.lut", "wing_front_AOA_CL.lut", "height_frontwing_CL.lut", "wing_front_AOA_CD.lut", "height_frontwing_CD.lut", "wing_rear_AOA_CL.lut", "wing_rear_AOA_CD.lut"};
		for (String s: files) {
			FileInterface f = DataInterface.getOutput(s);
			JPanel file = new JPanel();
			file.setLayout(new BoxLayout(file, BoxLayout.PAGE_AXIS));
			file.setBorder(BorderFactory.createTitledBorder(s));
			if (f instanceof INIFile) {
				Map<String, Map<String, String>> m = ((INIFile) f).getValues();
				for (String section: m.keySet()) {
					JPanel sectionPanel = new JPanel();
					sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.PAGE_AXIS));
					sectionPanel.setBorder(BorderFactory.createTitledBorder(section));
					for (Map.Entry<String, String> e: m.get(section).entrySet()) {
							JPanel line = new JPanel();
							line.setLayout(new GridLayout(0, 2));
							line.add(new JLabel(e.getKey()));
							JTextField value = new JTextField(e.getValue());
							value.addActionListener(i -> ((INIFile) f).setValue(section, e.getKey(), value.getText()));
							line.add(value);
							line.setAlignmentX(Component.LEFT_ALIGNMENT);
							sectionPanel.add(line);
					}
					file.add(sectionPanel);
				}
			} else if (f instanceof LUTFile) {
				for (Pair<String, String> item: ((LUTFile) f).getInputData()) {
						JPanel line = new JPanel();
						line.setLayout(new GridLayout(0, 2));
						line.add(new JLabel(item.getKey()));
						JTextField value = new JTextField(item.getValue());
						value.addActionListener(e -> ((LUTFile) f).addValue(item.getKey(), value.getText()));
						line.add(value);
						line.setAlignmentX(Component.LEFT_ALIGNMENT);
					file.add(line);
				}
			}
			aeroPanel.add(file);
		}
		
		return scrollPane;
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
