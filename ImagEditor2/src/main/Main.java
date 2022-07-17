package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import components.Board;
import components.ShapeList;
import install.Decoder;
import install.DefaultSettings;
import install.Install;
import install.Resources;
import install.saveSystem.Project;
import languages.Translator;
import le.gui.LMenu;
import le.gui.LSlider;
import le.log.ExceptionUtils;
import le.log.Logger;
import shapes.Picture;
import shapes.Rectangle;
import shapes.Shape;
import shapes.Text;
import webServices.Account;
import webServices.AccountUndefindException;
import webServices.Website;

/**
 * This class contains the main method and set up the whole program. In
 * addition, this class keeps all the static important variables, for example:
 * frame, board, website, account etc.
 */
public class Main {
	/**
	 * Holds the number of the version with minor version (after the decimal point)
	 */
	public static final double version = 2.3;
	/**
	 * The frame of the app.
	 */
	public static JFrame f;
	/**
	 * Represents the current project which is opened.
	 */
	public static Project currentProject;
	/**
	 * The program's install class instance, used for accessing and managing the
	 * program's files.
	 */
	public static Install install = new Install("C:\\ImagEditor" + Main.version);
	/**
	 * The program's translator, used for supporting multiple languages.
	 */
	public static Translator translator = new Translator(install);
	/**
	 * The program's logger, used for logging the program's activity.
	 */
	public static Logger logger = new Logger(install);
	/**
	 * Represents the website of the product for using its services (as accounts,
	 * for example).
	 * 
	 * @see le.web.AbstractWebsite
	 */
	public static Website website = new Website("http://localhost/ImagEditorWebsite/");
	/**
	 * The default account, which uses in the case of none account logged-in.
	 */
	public static final Account LOCAL_ACCOUNT = new Account("local account", "", "none", false);
	/**
	 * Current logged-in account. As default, LOCAL_ACCOUNT.
	 */
	public static Account myAccount = LOCAL_ACCOUNT;
	/**
	 * The side bar which holds the shapeList and some action buttons.
	 */
	private static JPanel shapeListPanel;
	/**
	 * GUI list of all the shapes are currently exist.
	 */
	private static ShapeList shapeList;
	/**
	 * The label which holds the size of the paper (width x height).
	 */
	private static JLabel sizeLabel;
	/**
	 * The slider which uses to set the zoom of the paper.
	 */
	private static LSlider zoomSlider;
	/**
	 * The scrollable wrapper of the paper.
	 */
	private static JScrollPane boardScrollPane;
	/**
	 * ActionListener for all of the menu actions.
	 */
	private static ActionListener menuListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			System.out.println("Menu Event [" + command + "]");
			Actions.action(Main.translator.get(command));
		}
	};
	private static long initTime;

	public static void main(String[] args) {
		System.out.println("Start-Up");
		long millis = System.currentTimeMillis();
		if (Main.install.getFile("Data\\Logs\\live log.txt").exists()) {
			long pause = System.currentTimeMillis();
			if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, "<html>Last time, the app crashed.<br/>"
					+ "would you like to send us auto report about it?</html>")) {
				try {
					website.sendReport("Auto Reporter", "Crash Report",
							Main.install.getText("Data\\Logs\\live log.txt"));
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Can't report your crash.", "Connection Erorr",
							JOptionPane.ERROR_MESSAGE);
				}
			}
			millis += System.currentTimeMillis() - pause;
		}
		Main.logger.initializeLogger();
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				System.out.println("Error " + e + " has been reported - ID " + ++Main.logger.errorCount);
				Main.logger.getErrorLogger().println(ExceptionUtils.exceptionToString((Exception) e, t, Main.logger.getErrorCount()));
				if (Main.logger.printInConsole) {
					e.printStackTrace(Main.logger.err);
				}
				int criticality = ExceptionUtils.getCriticality((Exception) e);
				if (criticality == 2) {
					int ans = JOptionPane.showOptionDialog(null, "An error has occurred", "Warning", 0,
							JOptionPane.WARNING_MESSAGE, null, new String[] { "Open Log", "Cancel" }, 0);
					if (ans == 0) {
						System.out.println("Opening error log from error message");
						Actions.action("Log");
					}
				}
				if (criticality > 2) {
					int ans = JOptionPane.showOptionDialog(null, "An error has occurred", "ERROR", 0,
							JOptionPane.ERROR_MESSAGE, null, new String[] { "Open Log", "Cancel" }, 0);
					if (ans == 0) {
						System.out.println("Opening error log from error message");
						Actions.action("Log");
					}
				}
			}
		});
		if (!Main.install.isInstalled()) {
			int answer = JOptionPane.showConfirmDialog(f, "Do you want to install ImageEditor v" + version + "?");
			switch (answer) {
			case JOptionPane.YES_OPTION:
				if (Main.install.install()) {
					Main.logger.initializeLiveLogger();
					JOptionPane.showMessageDialog(f, "Install done successfully!");
				} else {
					JOptionPane.showMessageDialog(f, "Error: install failed", "Install Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				break;
			default:
				System.exit(0);
				return;
			}
		}
		Main.install.initLanguage();
		f = new JFrame(Main.translator.get("ImagEditor v") + version);
		zoomSlider = new LSlider(Main.translator.get("Zoom") + ":", 10, 200, DefaultSettings.paperZoom);
		currentProject = new Project();
		shapeListPanel = new JPanel(new BorderLayout());
		Resources.init();
		initJMenuBar();
		getBoard().repaint();
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.setIconImage(Resources.logo.getImage());
		f.setLayout(new BorderLayout());
		boardScrollPane = new JScrollPane(getBoard(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		f.add(boardScrollPane, BorderLayout.CENTER);
		initControlBar();
		updateShapeList();
		initShapeListPanel();
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
			}
			@Override
			public void windowIconified(WindowEvent e) {
			}
			@Override
			public void windowDeiconified(WindowEvent e) {
			}
			@Override
			public void windowDeactivated(WindowEvent e) {
			}
			@Override
			public void windowClosing(WindowEvent e) {
			}
			@Override
			public void windowClosed(WindowEvent e) {
				System.out.println("Post-closing work started");
				if (DefaultSettings.saveLogs) {
					System.out.println("Saving log file");
					File f = Main.install.getFile("Data\\Logs\\Log saved at " + System.currentTimeMillis() + ".txt");
					try {
						f.createNewFile();
						Main.logger.disableTimeStamp();
						System.out.println("Run statistics:\nErrors: " + Main.logger.getErrorCount() + "\nInit Time: "
								+ initTime + " ms");
						Main.logger.exportTo(f);
						System.out.println("Log file saved successfully");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				Main.logger.stop();
				System.gc();
				System.exit(-1);
			}
			@Override
			public void windowActivated(WindowEvent e) {
			}
		});
		DefaultSettings.updateFromFile();
		f.applyComponentOrientation(Main.translator.getComponentOrientation());
		boardScrollPane.applyComponentOrientation(ComponentOrientation.UNKNOWN);
		f.setVisible(true);
		initTime = (System.currentTimeMillis() - millis);
		Main.website.checkUpdate();
		if (Main.website.checkWebsite() && DefaultSettings.keepMeLoggedIn) {
			tryToLogIn();
		}
		System.out.println("Init took " + initTime + " milli-seconds");
	}
	public static void tryToLogIn() {
		String data = Decoder.decode(Main.install.getText("Data/Settings/user.txt"));
		System.out.println(data);
		if (data != null && !data.equals("")) {
			String[] loginData = data.split("\n");
			try {
				Account.login(loginData[0], loginData[1]);
			} catch (AccountUndefindException e) {
				JOptionPane.showMessageDialog(Main.f,
						"<html>We couldn't login to your saved account (" + loginData[0]
								+ "),<br/>please check the username and the password.</html>",
						"Failed to Login", JOptionPane.WARNING_MESSAGE);
			}
		}
	}
	public static void initControlBar() {
		JPanel controlBar = new JPanel(new BorderLayout());
		sizeLabel = new JLabel(getBoard().paper.getWidth() + "x" + getBoard().paper.getHeight());
		controlBar.add(getSizeLabel(), Main.translator.getAfterTextBorder());
		zoomSlider.slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				getBoard().repaint();
			}
		});
		controlBar.add(zoomSlider, Main.translator.getBeforeTextBorder());
		f.add(controlBar, BorderLayout.SOUTH);
	}
	public static void initShapeListPanel() {
		shapeListPanel.add(new JLabel("<html><font size=30>" + Main.translator.get("Layers") + "</font></html>"),
				BorderLayout.NORTH);
		JPanel actionsPanel = new JPanel(new GridLayout(2, 2));
		JButton edit = new JButton(Resources.editIcon);
		edit.setToolTipText(Main.translator.get("Edit selected shape"));
		edit.setBackground(Color.WHITE);
		edit.setFocusPainted(false);
		actionsPanel.add(edit);
		edit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (getShapeList().getSelectedShape() != null) {
					getShapeList().getSelectedShape().edit();
				}
			}
		});
		JButton remove = new JButton(Resources.removeIcon);
		remove.setToolTipText(Main.translator.get("Remove selected shape"));
		remove.setBackground(Color.WHITE);
		remove.setFocusPainted(false);
		actionsPanel.add(remove);
		remove.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (getShapeList().getSelectedShape() != null) {
					getBoard().getShapesList().remove(getShapeList().getSelectedShape());
					getBoard().repaint();
					updateShapeList();
				}
			}
		});
		shapeListPanel.add(actionsPanel, BorderLayout.SOUTH);
		JButton uplayer = new JButton(Resources.up_layerIcon);
		uplayer.setToolTipText(Main.translator.get("Move selected shape 1 layer up"));
		uplayer.setBackground(Color.WHITE);
		uplayer.setFocusPainted(false);
		actionsPanel.add(uplayer);
		uplayer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (getShapeList().getSelectedShape() != null) {
					Shape s = getShapeList().getSelectedShape();
					if (getBoard().getShapesList().getLast() == s) {
						JOptionPane.showMessageDialog(Main.f, Main.translator.get("This is the top layer!"),
								Main.translator.get("Warning"), JOptionPane.WARNING_MESSAGE);
						return;
					}
					int sIndex = getBoard().getShapesList().indexOf(s);
					int upIndex = getBoard().getShapesList().indexOf(s) + 1;
					Shape up = getBoard().getShapesList().get(upIndex);
					getBoard().getShapesList().set(upIndex, s);
					getBoard().getShapesList().set(sIndex, up);
					getBoard().repaint();
					updateShapeList();
				}
			}
		});
		shapeListPanel.add(actionsPanel, BorderLayout.SOUTH);
		JButton downlayer = new JButton(Resources.down_layerIcon);
		downlayer.setToolTipText(Main.translator.get("Move selected shape 1 layer down"));
		downlayer.setBackground(Color.WHITE);
		downlayer.setFocusPainted(false);
		actionsPanel.add(downlayer);
		downlayer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (getShapeList().getSelectedShape() != null) {
					Shape s = getShapeList().getSelectedShape();
					if (getBoard().getShapesList().getFirst() == s) {
						JOptionPane.showMessageDialog(Main.f, Main.translator.get("This is the down layer!"),
								Main.translator.get("Warning"), JOptionPane.WARNING_MESSAGE);
						return;
					}
					int sIndex = getBoard().getShapesList().indexOf(s);
					int downIndex = getBoard().getShapesList().indexOf(s) - 1;
					Shape down = getBoard().getShapesList().get(downIndex);
					getBoard().getShapesList().set(downIndex, s);
					getBoard().getShapesList().set(sIndex, down);
					getBoard().repaint();
					updateShapeList();
				}
			}
		});
		shapeListPanel.add(actionsPanel, BorderLayout.SOUTH);
		f.add(shapeListPanel, Main.translator.getBeforeTextBorder());
	}
	public static void updateShapeList() {
		System.out.println("Update shapeList");
		Shape s = null;
		if (getShapeList() != null) {
			s = getShapeList().getSelectedShape();
			shapeListPanel.remove(getShapeList());
		}
		shapeList = new ShapeList(getBoard().getShapesList().toArray(new Shape[0]));
		shapeListPanel.add(getShapeList(), BorderLayout.CENTER);
		if (s != null) {
			getShapeList().setSelection(s);
		}
		f.revalidate();
		f.repaint();
	}
	public static void initJMenuBar() {
		LMenu lMenu = new LMenu(new String[][] {
				{ Main.translator.get("File"), Main.translator.get("Open Project from this Computer") + "#o",
						Main.translator.get("Open Project from Web"), Main.translator.get("Save Project") + "#s",
						Main.translator.get("Save Project As...") + "#@s", Main.translator.get("Upload Project"),
						Main.translator.get("Export Image") + "#e", Main.translator.get("Set Paper Size"),
						Main.translator.get("Preferences"), Main.translator.get("Log"),
						Main.translator.get("Send Report"), Main.translator.get("Visit Website") },
				{ Main.translator.get("Actions"), Main.translator.get("Edit") + "#e",
						Main.translator.get("Set Paper Size"), Main.translator.get("Refresh") + "#r" },
				{ Main.translator.get("Add"), Main.translator.get("Rectangle") + "@r",
						Main.translator.get("Text") + "@t", Main.translator.get("Picture") + "@p" },
				{ Main.translator.get("Account"), Main.translator.get("Profile") } }, menuListener);
		f.setJMenuBar(lMenu);
	}
	public static JPopupMenu getPopupMenuForShape(Shape s) {
		JPopupMenu popup = new JPopupMenu("Options");
		JMenuItem setName = new JMenuItem(Main.translator.get("Set Name"));
		setName.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				s.setName(JOptionPane
						.showInputDialog(Main.translator.get("Enter the new name for") + " \"" + s.getName() + "\""));
				Main.updateShapeList();
			}
		});
		popup.add(setName);
		JMenuItem edit = new JMenuItem(Main.translator.get("Edit"));
		edit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				s.edit();
			}
		});
		popup.add(edit);
		if (s instanceof Text || s instanceof Rectangle) {
			return popup;
		}
		popup.add(new JSeparator());
		JMenuItem editEffects = new JMenuItem("Edit Effects");
		editEffects.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				((Picture) s).editEffects();
			}
		});
		popup.add(editEffects);
		JMenuItem copy = new JMenuItem("Copy as image");
		copy.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Main.getBoard().addShape(((Picture) s).copy());
			}
		});
		popup.add(copy);
		return popup;
	}
	public static ShapeList getShapeList() {
		return shapeList;
	}
	public static LSlider getZoomSlider() {
		return zoomSlider;
	}
	public static JLabel getSizeLabel() {
		return sizeLabel;
	}
	public static Board getBoard() {
		return currentProject.board;
	}
	public static void switchProject(Project loadProject) {
		currentProject = loadProject;
		updateShapeList();
		getBoard().repaint();
		boardScrollPane.setViewportView(getBoard());
		Main.updateSizeLabel();
	}
	public static void updateSizeLabel() {
		Main.sizeLabel.setText(getBoard().getPaperWidth() + "*" + getBoard().getPaperHeight());
	}
}