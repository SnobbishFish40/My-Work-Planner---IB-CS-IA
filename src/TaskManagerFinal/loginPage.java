package TaskManagerFinal;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class loginPage extends JFrame {
    private JLabel password1, label;
	private JTextField username;
	private JButton button;
	private JPasswordField Password;
	Color WHITE = new Color(255, 255, 255);
	Color BLACK = new Color(50, 50, 50);
	
	public static void main(String[] args) {
		loginPage loginPage = new loginPage();
		loginPage.setTitle("My Work Planner");
		ImageIcon img = new ImageIcon("myWorkPlanner.png");
		loginPage.setIconImage(img.getImage());
		loginPage.setLocation(new Point(500, 300));
        loginPage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginPage.setSize(400, 200);
        loginPage.setVisible(true);
	}
	
	public loginPage() {
        setTitle("Loading...");
        setSize(1000, 500);
        setLocation(new Point(250, 200));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel splashPanel = new JPanel(new BorderLayout());
        ImageIcon splashImage = new ImageIcon("splashScreen.png");
        JLabel splashLabel = new JLabel(splashImage);
        splashPanel.add(splashLabel);
        getContentPane().add(splashPanel);
        setVisible(true);

        // Give time for splash screen to show
        try {

            Thread.sleep(3000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        getContentPane().remove(splashPanel);
		
        JPanel panel = new JPanel();
		panel.setLayout(null);
		label = new JLabel("Username");
		label.setBounds(100, 8, 70, 20);
		panel.add(label);
		username = new JTextField();
		username.setBounds(100, 27, 193, 28);
		panel.add(username);
		password1 = new JLabel("Password");
		password1.setBounds(100, 55, 70, 20);
		panel.add(password1);
		Password = new JPasswordField();
		Password.setBounds(100, 75, 193, 28);
		panel.add(Password);
		button = new JButton("Login");
		button.setBounds(100, 110, 90, 25);
		button.setForeground(WHITE);
		button.setBackground(BLACK);
		panel.add(button);
		
		getContentPane().add(panel);
		
		pack();
		
		
		
		button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	String Username = username.getText();
            	String Password1 = Password.getText();

            	if (Username.equals("user") && Password1.equals("123")) {
            		JOptionPane.showMessageDialog(null, "Login Successful!");
            		taskManager taskManager = new taskManager();
            		taskManager.main(null);
            		dispose();
            	}
            	else {
            		JOptionPane.showMessageDialog(null, "Username or Password incorrect!");
            	}
            }
        });
	}
}
