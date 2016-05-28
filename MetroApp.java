/*
	VERSION 2.2
	Program designed and creted by Mark Lavrentyev
	Last edited June 5, 2015
	MetroApp is designed to assist with metro maps in cities with such transit systems.
	MetroApp does not feature many cities.
	MetroApp is designed as a guide, not as a definite direction finder.
*/
import java.util.*;
import java.util.Map.*;
import java.io.*;
import javax.imageio.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.border.*;


public class MetroApp {
	
	public int[] idsToUse = {41, 89};
	

	JPanel fromStationPanel;
	ImageIcon lineFrom;
	JLabel labelpicFrom;
	JLabel fromStation;

	JPanel toStationPanel;
	ImageIcon lineTo;
	JLabel labelpicTo;
	JLabel toStation;

	JPanel mapMetro;
	JPanel directionsPanel;

	boolean originSelected = true;
	boolean destinationSelected = false;

	static MetroApp mApp = new MetroApp();   // Default metro is Moscow

	static Metro metro = new Metro("Athens");
	public static void main(String[] args) {
		metro.name = args[0];
		try{
			metro.declarations();
			mApp.setUpGUI();
		}catch (FileNotFoundException e) {e.printStackTrace();}
		catch (IOException e) {e.printStackTrace();}
		catch (Exception e) {e.printStackTrace();}
			
			//System.out.println(stations.get(0).xCoord + " " + stations.get(0).yCoord);
	}

	public void go(int originID, int destinationID) {        // Does the stuff to start everything
		Station origin = null;
		Station destination = null;
		directionsPanel.removeAll(); // Clears previous directions
		directionsPanel.updateUI();
		for (Entry<Integer, Station> entry: metro.stations.entrySet()) {         
			if(entry.getKey() == originID) {origin = entry.getValue();}
			else if(entry.getKey() == destinationID) {destination = entry.getValue();}
		}
		ArrayList<Station> path = metro.directions(origin, destination);
		for (Station s : path) {
			//directionsPanel.add(new JLabel());
			if(metro.transferFinder(path).containsKey(metro.findInStations(s.id))) {
				System.out.print("Transfer at: " + s.name);// In cmd Line version 1.1

				ImageIcon tempIcon = new ImageIcon(metro.name + " Metro\\" + s.line.color + " line small.png");
				directionsPanel.add(new JLabel("Transfer at " + s.name,SwingConstants.LEFT));
				System.out.print(" from " + s.line.color); // In cmd line version 1.1
				directionsPanel.add(new JLabel(" from " + s.line.color, tempIcon, SwingConstants.RIGHT));
				if(metro.transferFinder(path).get(metro.findInStations(s.id)).linesTransfer.get(1).color.equals(metro.findInStations(s.id).line.color)) { 
					tempIcon = new ImageIcon(metro.name + " Metro\\" + metro.transferFinder(path).get(metro.findInStations(s.id)).linesTransfer.get(0).color + " line small.png");
					System.out.print(" to " + metro.transferFinder(path).get(metro.findInStations(s.id)).linesTransfer.get(0).color);
					directionsPanel.add(new JLabel(" to " + metro.transferFinder(path).get(metro.findInStations(s.id)).linesTransfer.get(0).color, tempIcon,SwingConstants.LEFT));
				}
				else {
					tempIcon = new ImageIcon(metro.name + " Metro\\" + metro.transferFinder(path).get(metro.findInStations(s.id)).linesTransfer.get(1).color + " line small.png");
					System.out.print(" to " + metro.transferFinder(path).get(metro.findInStations(s.id)).linesTransfer.get(1).color);
					directionsPanel.add(new JLabel(" to " + metro.transferFinder(path).get(metro.findInStations(s.id)).linesTransfer.get(1).color, tempIcon,SwingConstants.LEFT));
				}
				System.out.println("");
				}
			
		}
		System.out.println("");
	}

	public void setUpGUI() throws IOException{

		try {
    		UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
		} catch (Exception e) {
  		  //e.printStackTrace();
		}	
		
		
		String myString = "Путь по Метро 2.0";
		byte bytes[] = myString.getBytes("UTF-8"); 
		String value = new String(bytes, "UTF-8"); 

		JFrame frame = new JFrame(value);  // Version 2 after the cmd-line version 1.1 (w/ transfers)
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 1000);
		frame.setLayout(new GridBagLayout());
		frame.getContentPane().setBackground(Color.WHITE);
		GridBagConstraints c = new GridBagConstraints();
		
		mapMetro = new JPanel(); 
		mapMetro.setBackground(Color.WHITE);
		ImageIcon pic = new ImageIcon("C:\\Users\\Mark\\Desktop\\Java\\Metro\\Athens Metro\\" + metro.name  + " Map.png");
		mapMetro.add(new JLabel(pic));
		mapMetro.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		mapMetro.addMouseListener( new MouseMapEvent()); //Says when the map is clicked;

		fromStationPanel = new JPanel();
		fromStationPanel.setBackground(Color.WHITE);
		fromStationPanel.setLayout(new BoxLayout(fromStationPanel, BoxLayout.PAGE_AXIS));
		lineFrom = new ImageIcon("C:\\Users\\Mark\\Desktop\\Java\\Metro\\" + metro.name + " Metro\\" + metro.lines.get(0).color + " line.png");   
		labelpicFrom = new JLabel(lineFrom);
		labelpicFrom.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		fromStationPanel.add(labelpicFrom);
		fromStation = new JLabel(metro.stations.get(0).name);
		fromStation.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		fromStationPanel.add(fromStation, 0.5);
		fromStationPanel.addMouseListener(new ClickOnFromPanelEvent());
		fromStationPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		JPanel arrowPanel = new JPanel();
		arrowPanel.setBackground(Color.WHITE);
		arrowPanel.setLayout(new BoxLayout(arrowPanel, BoxLayout.PAGE_AXIS));
		ImageIcon arrow = new ImageIcon("C:\\Users\\Mark\\Desktop\\Java\\Metro\\General Pics\\arrow.png");
		JLabel arrowPicLabel = new JLabel(arrow);
		arrowPicLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		arrowPanel.add(arrowPicLabel);

		arrowPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		toStationPanel = new JPanel();// Where the station travelling to is displayed;
		toStationPanel.setBackground(Color.WHITE);
		toStationPanel.setLayout(new BoxLayout(toStationPanel, BoxLayout.PAGE_AXIS));
		lineTo = new ImageIcon("C:\\Users\\Mark\\Desktop\\Java\\Metro\\" + metro.name + " Metro\\" + metro.lines.get(0).color + " line.png"); // Sets default pic for to Station
		labelpicTo = new JLabel(lineTo);
		labelpicTo.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		toStationPanel.add(labelpicTo);
		toStation = new JLabel(metro.stations.get(1).name);
		toStation.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		toStationPanel.add(toStation);
		toStationPanel.addMouseListener(new ClickOnToPanelEvent());
		toStationPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		directionsPanel = new JPanel();
		directionsPanel.setLayout(new BoxLayout(directionsPanel, BoxLayout.PAGE_AXIS));
		directionsPanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
		directionsPanel.setBackground(Color.WHITE);
		directionsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		directionsPanel.setPreferredSize(new Dimension(300, 500));
		JScrollPane scrollPane = new JScrollPane(directionsPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		//directionsPanel.setAlignmentY(java.awt.Component.NORTH);

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		c.gridheight = 1;
		frame.add(mapMetro, c);
		c.gridwidth = 1;
		c.gridheight = 1;

		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0.33;
		frame.add(fromStationPanel, c);

		c.gridx = 1;
		c.gridy = 1;
		frame.add(arrowPanel, c);

		c.gridx = 2;
		c.gridy = 1;
		frame.add(toStationPanel, c);


		c.gridx = 3;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 2;
		c.anchor = GridBagConstraints.NORTH;
		frame.add(directionsPanel, c);

		frame.pack();
		frame.setVisible(true);
	}

	class MouseMapEvent implements MouseListener {  // Listens to clicks on the map
		public void mouseClicked(MouseEvent e) {
			//directionsPanel.removeAll();
			int x = e.getX();
			int y = e.getY();
			System.out.println(x + " " + y);
			for(Entry<Integer,Station> entry : metro.stations.entrySet()) {
				int xStation = entry.getValue().xCoord;
				int yStation = entry.getValue().yCoord;
				if(x >= xStation - 5 && x <= xStation + 5 && y >= yStation - 5 && y <= yStation + 5) { //If both x and y are in range
					if(originSelected) {  // If choosing the origin
						fromStation.setText(entry.getValue().name);
						String fileName = "C:\\Users\\Mark\\Desktop\\Java\\Metro\\" + metro.name + " Metro\\" + entry.getValue().line.color + " line.png";
				
						lineFrom = new ImageIcon(fileName);
						labelpicFrom.setIcon(lineFrom);
						originSelected = false;
						destinationSelected = true;
						idsToUse[0] = entry.getValue().id;
					}
					else if(destinationSelected) { // If choosing destination
						toStation.setText(entry.getValue().name);
						String fileName = "C:\\Users\\Mark\\Desktop\\Java\\Metro\\" + metro.name + " Metro\\" + entry.getValue().line.color + " line.png";
				
						lineTo = new ImageIcon(fileName);
						labelpicTo.setIcon(lineTo);
						originSelected = true;
						destinationSelected = false;
						idsToUse[1] = entry.getValue().id;
						// Add stuff here to get directions, etc.
						go(idsToUse[0],idsToUse[1]);
						idsToUse[0] = 0;
						idsToUse[1] = 0;
					}
					break; // Don't check the others: no point 
				}
			}
		}
		public void mouseEntered(MouseEvent e) {     // Here to override Mouselistener stuff, do nothing
			//System.out.println("Entered!");     
		}
		public void mouseExited(MouseEvent e) {
			//System.out.println("Exited!");
		}
		public void mousePressed(MouseEvent e) {
		}
		public void mouseReleased(MouseEvent e) {
		} 
	}	

	class ClickOnFromPanelEvent implements MouseListener { // Listens to click on the from panel
		public void mouseReleased(MouseEvent e) {
		}
		public void mousePressed(MouseEvent e) {
		}
		public void mouseExited(MouseEvent e) {
		}
		public void mouseEntered(MouseEvent e) {
		}
		public void mouseClicked(MouseEvent e) {
			destinationSelected = false;
			originSelected = true;
		}
	} 
	class ClickOnToPanelEvent implements MouseListener { // Listens to click on the to panel
		public void mouseReleased(MouseEvent e) {
		}
		public void mousePressed(MouseEvent e) {
		}
		public void mouseExited(MouseEvent e) {
		}
		public void mouseEntered(MouseEvent e) {
		}
		public void mouseClicked(MouseEvent e) {
			destinationSelected = true;
			originSelected = false;
		}
	}
}