package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


public class LoginWindow extends JFrame implements ActionListener {
	JButton SUBMIT;
	JPanel panel;
	JLabel label1, label2,label3;
	JTextField userText, passText,uri;
	JCheckBox authenticationEnabled;
//	String serviceUrl =  "https://api.devpersistent.iot-accelerator.ericsson.net/occhub/proxy/appiot/odatav2/34bb4075-753b-4209-a79d-ca347b615c63";
	String serviceUrl = "http://services.odata.org/V4/OData/OData.svc/";
	String username = "AnalyticsApplication@AnalyticsUser";
	String password = "9AS+kRXd9K3oupdISlAoGAI4uY%asR-E";

	public LoginWindow() {
		
		label3 = new JLabel();
		label3.setText("Uri:");
		uri = new JTextField(30);
		uri.setText(serviceUrl);
		
		label1 = new JLabel();
		label1.setText("Username:");
		userText = new JTextField(15);
		userText.setText(username);

		label2 = new JLabel();
		label2.setText("Password:");
		passText = new JPasswordField(15);
		passText.setText(password);
		// this.setLayout(new BorderLayout());
		authenticationEnabled = new JCheckBox("Basic authentication" ,true);
		authenticationEnabled.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent event) {
		        JCheckBox cb = (JCheckBox) event.getSource();
		        if (cb.isSelected()) {
		            userText.setEnabled(true);
		            passText.setEnabled(true);
		        } else {
		            userText.setEnabled(false);
		            passText.setEnabled(false);
		        }
		    }
		});
		
		

		SUBMIT = new JButton("SUBMIT");

		panel = new JPanel(new GridLayout(5, 1));
		panel.add(label3);
		panel.add(uri);		
		panel.add(authenticationEnabled);
		panel.add(new JLabel());
		panel.add(label1);
		panel.add(userText);
		panel.add(label2);
		panel.add(passText);
		panel.add(SUBMIT);
		add(panel, BorderLayout.CENTER);
		SUBMIT.addActionListener(this);
		setTitle("Odata Service Authentication");
	}

	public void actionPerformed(ActionEvent ae) {
		String user = userText.getText();
		String pass = passText.getText();
		String url = uri.getText();
		try {
			OdataClientHandler odataHandler = OdataClientHandler.getClientHandler(authenticationEnabled.isSelected(),url, user, pass);
			setVisible(false);
			GraphHandler.displayXchema(odataHandler);

		} 
		catch (Exception e) {
			System.out.printf("%s :enter the valid username and password",e.getMessage());
			JOptionPane.showMessageDialog(this, "Incorrect login or password", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}


