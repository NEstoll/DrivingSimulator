package src.application;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
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
		

		JTabbedPane tabbedPane = new JTabbedPane();

		JComponent configPanel = makeConfigPanel();
		JComponent ptPanel = makepowertrainPanel();
		JComponent suspensionPanel = makeSuspensionPanel();
		JComponent aeroPanel = makeAeroPanel();
	
		// Add corresponding components to tabs
		tabbedPane.addTab("Power Train", ptPanel);
		tabbedPane.addTab("Suspension", suspensionPanel);
		tabbedPane.addTab("Aero", aeroPanel);
		tabbedPane.addTab("Configuration", configPanel);
		
		
		//Add the tabbed pane to this panel.
		add(tabbedPane);

		//The following line enables to use scrolling tabs.
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
	}
	
  

// private static void basicPanelSetup() {
        //add basic content
        //JPanel basicContent = new JPanel();
        //upload buttons (added in a row top to bottom in the center of the window)
		/*
		 * basicContent.setLayout(new BoxLayout(basicContent, BoxLayout.PAGE_AXIS));
		 * basicContent.add(new FileImport("Aero data", DataInterface.Type.AERO));
		 * basicContent.add(new FileImport("Powertrain data",
		 * DataInterface.Type.TORQUE)); basicContent.add(new FileImport("Gear data",
		 * DataInterface.Type.GEARS)); basicContent.add(new
		 * FileImport("Suspension data", DataInterface.Type.SUSPENSION)); basicPanel =
		 * basicContent;
		 */
   
	private JComponent makeConfigPanel() {
		JPanel panelC = new JPanel();
		panelC.setLayout(new BoxLayout(panelC, BoxLayout.PAGE_AXIS));
		// Name of car
		name = new JTextField();
		panelC.add(new JLabel("Name: "));
		panelC.add(name);
		// Version of car
		build = new JTextField();
		panelC.add(new JLabel("Build Version: "));
		panelC.add(build);
		return panelC;

	}

	private JComponent makepowertrainPanel() {
		JPanel panelP = new JPanel();
		panelP.setLayout(new BoxLayout(panelP, BoxLayout.PAGE_AXIS));
		// Gearing
		// Big Label
		panelP.add(new JLabel("Gearing"));

		// Torque Curve
		panelP.add(new JLabel("Torque Curve"));
		return panelP;

	}
	
	private JComponent makeSuspensionPanel() {
		JPanel panelS = new JPanel();
		panelS.setLayout(new BoxLayout(panelS, BoxLayout.PAGE_AXIS));
		panelS.add(new JLabel("I don't know what data needs to be here"));
		
		return panelS;
	}  

	private JComponent makeAeroPanel() {
		JPanel panelA = new JPanel();
		panelA.add(new JLabel("Aero will not be done with this project!"));
		
		return panelA;
	} 
	public static void main(String[] args) {
		//Create and set up the window.
		JFrame frame = new JFrame("Mines Formula SAE");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(600,600));

		//Add content to the window.
		frame.add(new TabMenu(), BorderLayout.CENTER);

		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}


}
