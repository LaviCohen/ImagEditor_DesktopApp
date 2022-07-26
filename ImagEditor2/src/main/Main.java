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
import le.gui.dialogs.LDialogs;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import gui.Theme;
import gui.components.Board;
import gui.components.ShapeList;
import install.Decoder;
import install.DefaultSettings;
import install.Install;
import install.Resources;
import install.saveSystem.Project;
import languages.Translator;
import le.gui.components.LMenu;
import le.gui.components.LSlider;
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
	public static final double version = 2.5;
	/**
	 * The frame of the program.
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
	public static Install install;
	/**
	 * The program's translator, used for supporting multiple languages.
	 */
	public static Translator translator;
	/**
	 * The program's logger, used for logging the program's activity.
	 */
	public static Logger logger;
	/**
	 * The program's Main.theme.
	 * */
	public static Theme theme;
	/**
	 * Represents the website of the product for using its services (as accounts,
	 * for example).
	 * 
	 * @see le.web.AbstractWebsite
	 */
	public static Website website;
	/**
	 * Current logged-in account. As default, LOCAL_ACCOUNT.
	 */
	public static Account myAccount;
	/**
	 * The time took the program to initialize itself.
	 * */
	public static long initTime;
	/**
	 * The time the program started at.
	 * */
	public static long startUpMillis;
	/**
	 * The bottom panel, which holds zoom and paper size.
	 * */
	public static JPanel controlBar;
	/**
	 * The side bar which holds the shapeList and some action buttons.
	 */
	public static JPanel sideBarPanel;
	/**
	 * GUI list of all the shapes are currently exist.
	 */
	public static ShapeList shapeList;
	/**
	 * The label which holds the size of the paper (width x height).
	 */
	public static JLabel sizeLabel;
	/**
	 * The "Layers" label.
	 * */
	public static JLabel layersLabel;
	/**
	 * The slider which uses to set the zoom of the paper.
	 */
	public static LSlider zoomSlider;
	/**
	 * The scrollable wrapper of the paper.
	 */
	public static JScrollPane boardScrollPane;
	/**
	 * The program's menu bar.
	 * */
	public static LMenu lMenu;
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
	public static void main(String[] args) {
		startup();
		createProgramVars();
		checkIfPreviousRunFailed();
		initializeLogger();
		applyExceptionsHandling();
		checkIfInstalled();
		initNormalSetting();
		initResources();
		initProgramFrame();
		initMenuBar();
		initControlBar();
		initProject();
		initBoardScrollPane();
		updateSizeLabel();
		initSideBarPanel();
		updateShapeList();
		applyThemeColors();
		displayFrame();
		finishStartup();
		websiteChecks();
	}
	private static void initProject() {
		Main.currentProject = new Project();
	}
	private static void initResources() {
		Resources.init();
	}
	private static void initNormalSetting() {
		Main.install.initNormalSetting();
	}
	private static void initializeLogger() {
		Main.logger.initializeLogger();
	}
	private static void displayFrame() {
		f.setVisible(true);
	}
	private static void finishStartup() {
		initTime = (System.currentTimeMillis() - startUpMillis);
		System.out.println("Init took " + initTime + " milli-seconds");
	}
	private static void startup() {
		startUpMillis = System.currentTimeMillis();
		System.out.println("Start-Up");
	}
	private static void createProgramVars() {
		Main.install = new Install("C:\\ImagEditor" + Main.version);
		Main.translator = new Translator(install);
		Main.logger = new Logger(install);
		Main.website = new Website("http://localhost/ImagEditorWebsite/");
		Main.myAccount = Account.LOCAL_ACCOUNT;
	}
	private static void websiteChecks() {
		Main.website.checkUpdate();
		if (Main.website.checkWebsite() && DefaultSettings.keepMeLoggedIn) {
			tryToLogIn();
		}
	}
	private static void initBoardScrollPane() {
		boardScrollPane = new JScrollPane(getBoard(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		boardScrollPane.applyComponentOrientation(ComponentOrientation.UNKNOWN);
		f.add(boardScrollPane, BorderLayout.CENTER);
	}
	private static void initProgramFrame() {
		f = new JFrame(Main.translator.get("ImagEditor v") + version);
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.setIconImage(Resources.logo.getImage());
		f.setLayout(new BorderLayout());
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
		f.applyComponentOrientation(Main.translator.getComponentOrientation());
	}
	private static void checkIfInstalled() {
		if (!Main.install.isInstalled()) {
			int answer = LDialogs.showConfirmDialog(f, "Do you want to install ImageEditor v" + version + "?");
			switch (answer) {
			case LDialogs.YES_OPTION:
				if (Main.install.install()) {
					Main.logger.initializeLiveLogger();
					LDialogs.showMessageDialog(f, "Install has been completed successfully!");
				} else {
					LDialogs.showMessageDialog(f, "Error: install failed", "Install Error",
							LDialogs.ERROR_MESSAGE);
					return;
				}
				break;
			default:
				System.exit(0);
				return;
			}
		}
	}
	private static void applyExceptionsHandling() {
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
					int ans = LDialogs.showOptionDialog(null, "An error has occurred", "Warning", 0,
							LDialogs.WARNING_MESSAGE, new String[] { "Open Log", "Cancel" }, 0);
					if (ans == 0) {
						System.out.println("Opening error log from error message");
						Actions.action("Log");
					}
				}
				if (criticality > 2) {
					int ans = LDialogs.showOptionDialog(null, "An error has occurred", "ERROR", 0,
							LDialogs.ERROR_MESSAGE, new String[] { "Open Log", "Cancel" }, 0);
					if (ans == 0) {
						System.out.println("Opening error log from error message");
						Actions.action("Log");
					}
				}
			}
		});
	}
	private static void checkIfPreviousRunFailed() {
		if (Main.install.getFile("Data\\Logs\\live log.txt").exists()) {
			long pause = System.currentTimeMillis();
			if (LDialogs.YES_OPTION == LDialogs.showConfirmDialog(null, "<html>Last time, the app crashed.<br/>"
					+ "would you like to send us auto report about it?</html>")) {
				try {
					website.sendReport("Auto Reporter", "Crash Report",
							Main.install.getText("Data\\Logs\\live log.txt"));
				} catch (Exception e) {
					LDialogs.showMessageDialog(null, "Can't report your crash.", "Connection Erorr",
							LDialogs.ERROR_MESSAGE);
				}
			}
			startUpMillis += System.currentTimeMillis() - pause;
		}
	}
	public static void applyThemeColors() {
		System.out.println("Appling " + (Main.theme.isLightMode()?"Light":"Dark") + " Mode");
		//Control panel
		Main.theme.affect(controlBar);
		Main.theme.affect(sizeLabel);
		Main.theme.affect(zoomSlider);
		//Menu bar
		Main.theme.affect(lMenu);
		//Side bar
		Main.theme.affect(sideBarPanel);
		Main.theme.affect(shapeList);
		Main.theme.affect(layersLabel);
		//Board
		Main.theme.affect(getBoard());
	}
	public static void tryToLogIn() {
		String data = Main.install.getText("Data/Settings/user.txt");
		if (data != null && !data.equals("")) {
			data = Decoder.decode(data);
			String[] loginData = data.split("\n");
			try {
				Account.login(loginData[0], loginData[1]);
			} catch (AccountUndefindException e) {
				LDialogs.showMessageDialog(Main.f,
						"<html>We couldn't login to your saved account (" + loginData[0]
								+ "),<br/>please check the username and the password.</html>",
						"Failed to Login", LDialogs.WARNING_MESSAGE);
			}
		}
	}
	public static void initControlBar() {
		zoomSlider = new LSlider(Main.translator.get("Zoom") + ":",
				10, 200, DefaultSettings.paperZoom);
		controlBar = new JPanel(new BorderLayout());
		sizeLabel = new JLabel("");
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
	public static void initSideBarPanel() {
		sideBarPanel = new JPanel(new BorderLayout());
		layersLabel = new JLabel("<html><font size=30>" + 
						Main.translator.get("Layers") + "</font></html>");
		sideBarPanel.add(layersLabel, BorderLayout.NORTH);
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
		sideBarPanel.add(actionsPanel, BorderLayout.SOUTH);
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
						LDialogs.showMessageDialog(Main.f, Main.translator.get("This is the top layer!"),
								Main.translator.get("Warning"), LDialogs.WARNING_MESSAGE);
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
		sideBarPanel.add(actionsPanel, BorderLayout.SOUTH);
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
						LDialogs.showMessageDialog(Main.f, Main.translator.get("This is the down layer!"),
								Main.translator.get("Warning"), LDialogs.WARNING_MESSAGE);
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
		sideBarPanel.add(actionsPanel, BorderLayout.SOUTH);
		f.add(sideBarPanel, Main.translator.getBeforeTextBorder());
	}
	public static void updateShapeList() {
		System.out.println("Update shapeList");
		Shape s = null;
		if (getShapeList() != null) {
			s = getShapeList().getSelectedShape();
			sideBarPanel.remove(getShapeList());
		}
		shapeList = new ShapeList(getBoard().getShapesList().toArray(new Shape[0]));
		shapeList.setBackground(Main.theme.getBackgroundColor());
		sideBarPanel.add(getShapeList(), BorderLayout.CENTER);
		if (s != null) {
			getShapeList().setSelection(s);
		}
		f.revalidate();
		f.repaint();
	}
	public static void initMenuBar() {
		lMenu = new LMenu(new String[][] {
				{ Main.translator.get("File"), Main.translator.get("Open Project from this Computer") + "#o",
						Main.translator.get("Open Project from Web"), Main.translator.get("Save Project") + "#s",
						Main.translator.get("Save Project As...") + "#@s", Main.translator.get("Upload Project"),
						Main.translator.get("Export Image") + "#@e", Main.translator.get("Set Paper Size"),
						Main.translator.get("Preferences"), Main.translator.get("Set Language"), 
						Main.translator.get("Log"), Main.translator.get("Send Report"),
						Main.translator.get("Visit Website") },
				{ Main.translator.get("Actions"), Main.translator.get("Edit") + "#e",
						Main.translator.get("Set Paper Size"), Main.translator.get("Refresh") + "#r" },
				{ Main.translator.get("Add"), Main.translator.get("Rectangle") + "@r",
						Main.translator.get("Text") + "@t", Main.translator.get("Picture") + "@p"
						, Main.translator.get("Code") + "@c"},
				{ Main.translator.get("Account"), Main.translator.get("Profile") } }, menuListener);
		f.setJMenuBar(lMenu);
	}
	public static JPopupMenu getPopupMenuForShape(Shape s) {
		JPopupMenu popup = new JPopupMenu("Options");
		Main.theme.affect(popup);
		JMenuItem setName = new JMenuItem(Main.translator.get("Set Name"));
		Main.theme.affect(setName);
		setName.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				s.setName(LDialogs
						.showInputDialog(null, Main.translator.get("Enter the new name for") + " \"" + s.getName() + "\"", null));
				Main.updateShapeList();
			}
		});
		popup.add(setName);
		JMenuItem edit = new JMenuItem(Main.translator.get("Edit"));
		Main.theme.affect(edit);
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
		Main.theme.affect(editEffects);
		editEffects.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				((Picture) s).editEffects();
			}
		});
		popup.add(editEffects);
		JMenuItem copy = new JMenuItem("Copy as Image");
		Main.theme.affect(copy);
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
		Main.sizeLabel.setText(getBoard().getPaperWidth() + "px X " + getBoard().getPaperHeight() + "px");
	}
}