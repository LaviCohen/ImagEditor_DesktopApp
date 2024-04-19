package install;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import le.gui.components.LSlider;
import le.gui.dialogs.LDialogs;
import le.gui.layouts.ListLayout;
import le.install.DataFile;
import main.Main;


public class Preferences {

	public static DataFile default_setting;
	
	//Paper
	public static int paperWidth;
	public static int paperHeight;
	public static int paperZoom;
	//Language
	public static String language;
	public static boolean autoSetDefLan;
	//Advanced
	public static boolean saveLogs;
	public static boolean keepMeLoggedIn;
	public static boolean useMoreRAM;
	public static boolean usePreviewPictures;
	public static boolean manualRefreshOnly;
	public static boolean showCutPartsOfPicture;
	public static boolean keepTrackOfTopLayers;
	public static int numOfBackOperations;
	//Appearance
	public static boolean darkMode;
	//Multi-Picture
	public static int mpPixelSize;
	public static int mpSourceWidth;
	public static double mpDivFactor;
	
	static {
		default_setting = new DataFile(Main.install.getFile("Data\\Settings\\default_setting.txt"));
	}
	
	public static void updateFromFile() {
		System.out.println("Reading Preferences");
		paperWidth = Integer.parseInt(default_setting.get("paper_width"));
		paperHeight = Integer.parseInt(default_setting.get("paper_height"));
		paperZoom = Integer.parseInt(default_setting.get("zoom"));
		language = default_setting.get("language");
		autoSetDefLan = Boolean.parseBoolean(default_setting.get("auto_set_def_lan"));
		saveLogs = Boolean.parseBoolean(default_setting.get("save_log_files"));
		keepMeLoggedIn = Boolean.parseBoolean(default_setting.get("keep_me_logged_in"));
		useMoreRAM = Boolean.parseBoolean(default_setting.get("use_more_ram"));
		usePreviewPictures = Boolean.parseBoolean(default_setting.get("use_preview_pictures"));
		manualRefreshOnly = Boolean.parseBoolean(default_setting.get("manual_refresh_only"));
		darkMode = Boolean.parseBoolean(default_setting.get("dark_mode"));
		showCutPartsOfPicture = Boolean.parseBoolean(default_setting.get("show_cut_parts_of_picture"));
		keepTrackOfTopLayers = Boolean.parseBoolean(default_setting.get("keep_track_of_layer"));
		numOfBackOperations = Integer.parseInt(default_setting.get("num_of_back_op"));
		mpPixelSize = Integer.parseInt(default_setting.get("multi_picture_pixel_size"));
		mpSourceWidth = Integer.parseInt(default_setting.get("multi_picture_src_width"));
		mpDivFactor = Double.parseDouble(default_setting.get("multi_picture_div_factor"));
	}
	public static void saveToFile() throws FileNotFoundException, IOException {
		default_setting.putWithoutSave("paper_width", paperWidth);
		default_setting.putWithoutSave("paper_height", paperHeight);
		default_setting.putWithoutSave("zoom", paperZoom);
		default_setting.putWithoutSave("language", language);
		default_setting.putWithoutSave("auto_set_def_lan", autoSetDefLan);
		default_setting.putWithoutSave("save_log_files", saveLogs);
		default_setting.putWithoutSave("keep_me_logged_in", keepMeLoggedIn);
		default_setting.putWithoutSave("use_more_ram", useMoreRAM);
		default_setting.putWithoutSave("use_preview_pictures", usePreviewPictures);
		default_setting.putWithoutSave("manual_refresh_only", manualRefreshOnly);
		default_setting.putWithoutSave("dark_mode", darkMode);
		default_setting.putWithoutSave("show_cut_parts_of_picture", showCutPartsOfPicture);
		default_setting.putWithoutSave("keep_track_of_layer", keepTrackOfTopLayers);
		default_setting.putWithoutSave("num_of_back_op", numOfBackOperations);
		default_setting.putWithoutSave("multi_picture_pixel_size", mpPixelSize);
		default_setting.putWithoutSave("multi_picture_src_width", mpSourceWidth);
		default_setting.putWithoutSave("multi_picture_div_factor", mpDivFactor);
		default_setting.save("Original Default Settings");
	}
	
	public static void showPreferencesdialog() {
		JDialog preferencesDialog = new JDialog(Main.f, "User Preferences");
		preferencesDialog.setLayout(new BorderLayout());
		preferencesDialog.add(new JLabel("Change the default settings:"),
				BorderLayout.NORTH);
		
		//Using tabbed pane to sort all preferences by categories
		JTabbedPane tabbedPane = new JTabbedPane();
		
		//Paper's preferences tab, including width, height and zoom
		JPanel paperPrefsPanel = new JPanel(new ListLayout(0, 3));
		//Width GUI
		JPanel widthPanel = new JPanel(new BorderLayout());
		widthPanel.add(new JLabel("Width:"), Main.translator.getBeforeTextBorder());
		JTextField widthField = new JTextField(Preferences.paperWidth + "");
		widthPanel.add(widthField);
		paperPrefsPanel.add(widthPanel);
		//Height GUI
		JPanel heightPanel = new JPanel(new BorderLayout());
		heightPanel.add(new JLabel("Height:"), Main.translator.getBeforeTextBorder());
		JTextField heightField = new JTextField(Preferences.paperHeight + "");
		heightPanel.add(heightField);
		paperPrefsPanel.add(heightPanel);
		//Zoom GUI
		LSlider zoomSlider = new LSlider("Zoom", Main.getZoomSlider().getSlider().getMinimum(),
				Main.getZoomSlider().getSlider().getMaximum(), Preferences.paperZoom);
		paperPrefsPanel.add(zoomSlider);
		
		tabbedPane.addTab("Paper", paperPrefsPanel);

		//Language's preferences
		JPanel languagePrefsPanel = new JPanel(new ListLayout(0, 3));
		//Default language GUI
		JPanel defLanPanel = new JPanel(new BorderLayout());
		defLanPanel.add(new JLabel("Defalut Language:"),
				Main.translator.getBeforeTextBorder());
		//The language picking JComboBox
		File f = Main.install.getFile("Languages");
		String[] allLanguages = f.list();
		String[] displayLanguages = new String[allLanguages.length + 1];
		displayLanguages[0] = Main.translator.DEFAULT_LANGUAGE;
		for (int i = 1; i < displayLanguages.length; i++) {
			displayLanguages[i] = 
					allLanguages[i - 1].substring(0, allLanguages[i - 1].indexOf('.'));
		}
		JComboBox<String> chooseLanguge = new JComboBox<>(displayLanguages);
		chooseLanguge.setSelectedItem(Preferences.language);
		defLanPanel.add(chooseLanguge);
		languagePrefsPanel.add(defLanPanel);
		//Auto-set default language
		JCheckBox autoSetDefLan = new JCheckBox("Auto-set Defalut Language When Changing Language", Preferences.autoSetDefLan);
		languagePrefsPanel.add(autoSetDefLan);
		
		tabbedPane.addTab("Language", languagePrefsPanel);
		
		preferencesDialog.add(tabbedPane);
		
		//Appearance tab
		JPanel appearancePrefsPanel = new JPanel(new ListLayout(0, 3));
		
		//Dark mode
		JCheckBox darkModeCheckBox = new JCheckBox("Dark Mode", Preferences.darkMode);
		appearancePrefsPanel.add(darkModeCheckBox);
		
		tabbedPane.addTab("Appearance", appearancePrefsPanel);
		
		
		//Multi-Picture Tab
		JPanel mpPanel = new JPanel(new ListLayout(0, 3));
		//Pixel Size
		JPanel mpPixelSizePanel = new JPanel(new BorderLayout());
		mpPixelSizePanel.add(new JLabel("Pixel Image Size:"), Main.translator.getBeforeTextBorder());
		JTextField mpPixelSizeField = new JTextField(mpPixelSize + "");
		mpPixelSizePanel.add(mpPixelSizeField);
		mpPanel.add(mpPixelSizePanel);
		
		//Source Width
		JPanel mpSourceWidthPanel = new JPanel(new BorderLayout());
		mpSourceWidthPanel.add(new JLabel("Output  Picture Width:"), Main.translator.getBeforeTextBorder());
		JTextField mpSourceWidthField = new JTextField(mpSourceWidth + "");
		mpSourceWidthPanel.add(mpSourceWidthField);
		mpPanel.add(mpSourceWidthPanel);
		
		//Diversity Factor
		LSlider divSlider = new LSlider("Diversity", 0, 10, 0.3, 0.05);
		mpPanel.add(divSlider);
		
		tabbedPane.addTab("Multi-Picture", mpPanel);
		
		
		//Advanced tab (save logs and CPU vs RAM priority)
		JPanel advancedPrefsPanel = new JPanel(new ListLayout(0, 4));
		
		//Save logs
		JCheckBox saveLogsCheckBox = new JCheckBox("Save the Logs Every time the Program is Being Used", Preferences.saveLogs);
		advancedPrefsPanel.add(saveLogsCheckBox);
		
		//Show cut parts of picture
		JCheckBox showCutPartsOfPictureCheckBox = new JCheckBox("Show cut parts of picture (transparent, while editing)", Preferences.showCutPartsOfPicture);
		advancedPrefsPanel.add(showCutPartsOfPictureCheckBox);
				
		
		//Keep track of top layer changes
		JCheckBox keepTrackCheckBox = new JCheckBox("Keep Track of Brushing (Enable Undoing Brushing & Erasing Operations)", Preferences.keepTrackOfTopLayers);
		advancedPrefsPanel.add(keepTrackCheckBox);
		
		//Limit number of operations
		JCheckBox limitOpNumCheckBox = new JCheckBox("Limit Number of Remembered Operations", Preferences.numOfBackOperations != -1);
		advancedPrefsPanel.add(limitOpNumCheckBox);
		JTextField numOfOpField = new JTextField();
		if (Preferences.numOfBackOperations != -1) {
			numOfOpField.setEditable(true);
			numOfOpField.setEnabled(true);
			numOfOpField.setText(numOfBackOperations + "");
		} else {
			numOfOpField.setEditable(false);
			numOfOpField.setEnabled(false);
			numOfOpField.setText("0");
		}
		advancedPrefsPanel.add(numOfOpField);
		limitOpNumCheckBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (limitOpNumCheckBox.isSelected()) {
					numOfOpField.setEditable(true);
					numOfOpField.setEnabled(true);
					numOfOpField.setText("15");
				} else {
					numOfOpField.setEditable(false);
					numOfOpField.setEnabled(false);
					numOfOpField.setText("0");
				}
			}
		});
		
		
		//CPU vs RAM priority
		JCheckBox useRAMCheckBox = new JCheckBox("Use More RAM to Reduce CPU & GPU Usage", Preferences.useMoreRAM);
		advancedPrefsPanel.add(useRAMCheckBox);
		
		//CPU vs RAM priority
		JCheckBox usePreviewCheckBox = new JCheckBox("Use Preview Pictures to Improve Preformence", Preferences.usePreviewPictures);
		advancedPrefsPanel.add(usePreviewCheckBox);
		
		//Manual Refresh
		JCheckBox manualRefreshOnlyCheckBox = new JCheckBox("Don't Auto-Refresh My Screen", Preferences.manualRefreshOnly);
		advancedPrefsPanel.add(manualRefreshOnlyCheckBox);
		
		
		tabbedPane.addTab("Advanced", advancedPrefsPanel);
		
		
		//Apply button
		JButton apply = new JButton("Apply Preferences");
		//The code inside the listeners update the defaults to the values in the dialog's input components
		apply.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//Updating paper dimension
				try {
					int width = Integer.parseInt(widthField.getText());
					int height = Integer.parseInt(heightField.getText());
					Preferences.paperWidth = width;
					Preferences.paperHeight = height;
				} catch (Exception e2) {
					if (e2 instanceof NumberFormatException) {
						LDialogs.showMessageDialog(Main.f, "Error while parsing paper dimensions",
								"Parsing Error", LDialogs.ERROR_MESSAGE);
					}else {
						e2.printStackTrace();
					}
				}
				
				//Updating zoom
				Preferences.paperZoom = (int) zoomSlider.getValue();
				//Updating Default Language
				Preferences.language = chooseLanguge.getSelectedItem().toString();
				//Updating auto-set default language
				Preferences.autoSetDefLan = autoSetDefLan.isSelected();
				//Updating dark mode
				Preferences.darkMode = darkModeCheckBox.isSelected();
				//Updating save logs
				Preferences.saveLogs = saveLogsCheckBox.isSelected();
				//Updating use more RAM
				Preferences.useMoreRAM = useRAMCheckBox.isSelected();
				//Updating use more preview
				Preferences.usePreviewPictures = usePreviewCheckBox.isSelected();
				//Updating manual refresh
				Preferences.manualRefreshOnly = manualRefreshOnlyCheckBox.isSelected();
				//Show cut parts of picture
				Preferences.showCutPartsOfPicture = showCutPartsOfPictureCheckBox.isSelected();
				//Updating keep track of layers
				Preferences.keepTrackOfTopLayers = keepTrackCheckBox.isSelected();
				//Updating number of back operations
				if (limitOpNumCheckBox.isSelected()) {
					Preferences.numOfBackOperations = Integer.parseInt(numOfOpField.getText());
				} else {
					Preferences.numOfBackOperations = -1;
				}
				//Updating Pixel Size
				Preferences.mpPixelSize = Integer.parseInt(mpPixelSizeField.getText());
				//Updating Source Width
				Preferences.mpSourceWidth = Integer.parseInt(mpSourceWidthField.getText());
				//Updating Pixel Size
				Preferences.mpDivFactor = divSlider.getValue();
				
				
				//Saving the settings to the default settings file
				try {
					Preferences.saveToFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				System.out.println("Preferences Has Been Applied Successfuly");
				
				preferencesDialog.dispose();
			}
		});
		preferencesDialog.add(apply, BorderLayout.SOUTH);
		Main.theme.affect(preferencesDialog);
		
		//Displaying the dialog
		preferencesDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		preferencesDialog.pack();
		if (Main.f != null) {
			Point p = new Point(Main.f.getWidth() / 2, Main.f.getHeight() / 2);
			SwingUtilities.convertPointToScreen(p, Main.f);
			preferencesDialog.setLocation(p.x - preferencesDialog.getWidth() / 2, p.y - preferencesDialog.getHeight() / 2);
		}
		preferencesDialog.setVisible(true);
	}
}