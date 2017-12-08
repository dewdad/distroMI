package gui;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class DistributedAnalyticsSystemCanvas {
	
	
	

	public static void main(String[] args) {
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}

		try {
			LoginWindow frame = new LoginWindow();
			frame.setSize(300, 150);
			frame.setVisible(true);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}

	}

}
