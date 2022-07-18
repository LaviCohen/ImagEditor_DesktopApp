package install.saveSystem;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

import gui.components.Board;
import install.DefaultSettings;
import main.Main;
import shapes.Shape;

public class Project {
	
	public String folder;
	public String name;
	public Board board;
	
	public Project() {
		this.folder = null;
		this.name = null;
		this.board = new Board(Color.WHITE, DefaultSettings.paperWidth, DefaultSettings.paperHeight);
	}
	private Project(File file){
		this.folder = file.getParentFile().getAbsolutePath();
		this.name = file.getAbsolutePath();
		System.out.println(file);
		this.name = name.substring(file.getAbsolutePath().lastIndexOf("\\"), name.length() - 4);
		try {
			Scanner scanner = new Scanner(file);
			String[] settings = scanner.nextLine().split(",");
			this.board = new Board(new Color(Integer.parseInt(settings[2])), 
					Integer.parseInt(settings[0]), Integer.parseInt(settings[1]));
			String line = null;
			while(scanner.hasNextLine()) {
				line = scanner.nextLine();
				this.board.addShape(Shape.parseShape(line));
			}
			scanner.close();
		} catch (IOException | NumberFormatException e) {
			e.printStackTrace();
		}
	}
	public Project(String data) {
		String[] lines = data.split("\n");
		String[] settings = lines[0].split(",");
		this.board = new Board(new Color(Integer.parseInt(settings[2])), 
				Integer.parseInt(settings[0]), Integer.parseInt(settings[1]));
		for (int i = 1; i < lines.length; i++){
			String line = lines[i];
			try {
				this.board.addShape(Shape.parseShape(line));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static Project loadProject(File file) {
		return new Project(file);
	}
	public void save() throws IOException {
		Main.install.writeToFile(new File(this.folder + "/" + name + ".iep"), getData());
	}
	public String getData() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(getSettings());
		
		LinkedList<Shape> shapes = board.getShapesList();
		for (Shape shape : shapes) {
			sb.append('\n');
			sb.append(shape.encodeShape());
		}
		return sb.toString();
	}
	private String getSettings() {
		return board.getPaperWidth() + "," + board.getPaperHeight()
		+ "," + board.backgroundColor.getRGB();
	}
	public boolean hasFile() {
		return this.folder != null && this.name != null;
	}
}