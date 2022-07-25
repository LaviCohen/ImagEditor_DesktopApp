package webServices;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import install.DefaultSettings;
import install.Resources;
import le.gui.dialogs.LDialogs;
import main.Main;

public class Account {
	
	/**
	 * The default account, which uses in the case of none account logged-in.
	 */
	public static final Account LOCAL_ACCOUNT = new Account("local account", "", "none", false);
	
	//Gender Constants
	public static final String MALE = "male";
	public static final String FEMALE = "female";
	public static final String NONE_GENDER = "none";
	
	
	public String userName;
	public String password;
	public boolean isPremium;
	
	public String gender;
	
	public static void login(String userName, String password) throws AccountUndefindException{
		String userCode = Main.website.getResponse("getAccount.php", "userName=" + userName + "&" + "password=" + password + "&app=yes", "POST");
		if (userCode.equals("Account Undefined") || userCode == null || userCode.equals("") || userCode.equals("&&&&&&")) {
			throw new AccountUndefindException("Account undefind, incorrect userName or password");
		}else {
			userCode = userName + "&&" + password + "&&" + userCode;
			Main.myAccount = decode(userCode);
		}
		if (Main.myAccount.isPremium) {
			Main.install.initPremiumSetting();
		}
	}
	
	public static Account decode(String s) {
		String [] properties = s.split("&&");
		return new Account(properties[0], properties[1], properties[2], Boolean.valueOf(properties[3]));
	}
	public static String encode(Account a) {
		return a.userName + "&&" + a.password + "&&" + a.gender + "&&" + a.isPremium;
	}
	public Account(String userName, String password, String gender, boolean isPremium) {
		super();
		this.userName = userName;
		this.password = password;
		this.gender = gender;
		this.isPremium = isPremium;
	}
	@Override
	public String toString() {
		return "Account [userName=" + userName + ", password=" + password + ", gender=" + gender + ", isPremium="
				+ isPremium + "]";
	}
	public static void GUILogin() {
		JDialog d = new JDialog();
		d.setBackground(Main.theme.getBackgroundColor());
		d.setTitle("login");
		d.setLayout(new BorderLayout());
		
		JPanel dataPanel = new JPanel(new GridLayout(3, 1));
		
		Main.theme.affect(dataPanel);
		
		JPanel userNamePanel = new JPanel(new BorderLayout());
		Main.theme.affect(userNamePanel);
		userNamePanel.add(Main.theme.affect(new JLabel("User Name:")), BorderLayout.WEST);
		JTextField userNameField = new JTextField();
		Main.theme.affect(userNameField);
		userNamePanel.add(userNameField);
		
		dataPanel.add(userNamePanel);

		JPanel passwordPanel = new JPanel(new BorderLayout());
		Main.theme.affect(passwordPanel);
		passwordPanel.add(Main.theme.affect(new JLabel("Password:")), BorderLayout.WEST);
		JPasswordField passwordField = new JPasswordField();
		Main.theme.affect(passwordField);
		passwordPanel.add(passwordField);
		
		dataPanel.add(passwordPanel);
		
		JCheckBox rememberMe = new JCheckBox("Remember Me", DefaultSettings.keepMeLoggedIn);
		Main.theme.affect(rememberMe);
		
		dataPanel.add(rememberMe);
		
		JButton login = new JButton("Login");
		Main.theme.affect(login);
		login.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				d.setVisible(false);
				try {
					login(userNameField.getText(), new String(passwordField.getPassword()));
					if (rememberMe.isSelected()) {
						Main.install.writeToFile(Main.install.getFile("Data/Settings/user.txt"), Main.myAccount.userName + "\n"
								 + Main.myAccount.password);
					}
				} catch (AccountUndefindException e2) {
					LDialogs.showMessageDialog(Main.f, "Failed to Login");
					d.setVisible(true);
					d.toFront();
					return;
				}
				LDialogs.showMessageDialog(Main.f, "Hello " + Main.myAccount.userName + ", let\'s create some magic!");
				Main.myAccount.showAccount();
				d.dispose();
			}
		});
		
		d.add(Main.theme.affect(new JLabel("<html><font size=5>Login to Your Account</font></html>")), BorderLayout.NORTH);
		d.add(dataPanel);
		d.add(login, BorderLayout.SOUTH);
		d.pack();
		d.setVisible(true);
	}
	public void showAccount() {
		JDialog d = new JDialog();
		d.setBackground(Main.theme.getBackgroundColor());
		d.setLayout(new BorderLayout());
		ImageIcon icon = null;
		if (gender.equals(NONE_GENDER)) {
			icon = Resources.noneShadow;
		}else if (gender.equals(MALE)) {
			icon = Resources.maleShadow;
		}else if (gender.equals(FEMALE)) {
			icon = Resources.femaleShadow;
		}
		d.add(new JLabel(icon), BorderLayout.NORTH);
		JPanel personalDataPanel = new JPanel(new GridLayout((this.isPremium?3:2), 1));
		Main.theme.affect(personalDataPanel);
		if (this.isPremium) {
			personalDataPanel.add(Main.theme.affect(new JLabel("<html><i>Premium Account</i></html>")));
		}
		personalDataPanel.add(Main.theme.affect(new JLabel("UserName: " + this.userName)));
		personalDataPanel.add(Main.theme.affect(new JLabel("Gender: " + this.gender)));
		
		d.add(personalDataPanel);
		
		JButton login = new JButton((this == Account.LOCAL_ACCOUNT?"login":"logout"));
		Main.theme.affect(login);
		login.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (Main.myAccount == Account.LOCAL_ACCOUNT) {
					d.dispose();
					Account.GUILogin();
				}else {	
					d.dispose();
					logout();
					Main.myAccount.showAccount();
				}
			}
		});
		d.add(login, BorderLayout.SOUTH);
		d.pack();
		d.setVisible(true);
	}
	public static void logout() {
		Main.myAccount = Account.LOCAL_ACCOUNT;
		Main.install.initNormalSetting();
	}
}