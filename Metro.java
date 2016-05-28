import java.util.*;
import java.util.Map.*;
import java.io.*;
import javax.imageio.*;


class Line {
	int id;
	String color;

	java.util.List<Station> stations = new ArrayList();  
	public Line(int iD, String col) {
		id = iD;
		color = col;
	}
}
class Connection {
	java.util.List<Station> connectedStations = new ArrayList();
	int length;
	boolean isTransfer = false;

	public Connection(Station s1, Station s2, int distance) {
		length = distance;
		connectedStations.add(s1);
		connectedStations.add(s2);
	}
	public Connection(Station s1, Station s2) {
		length = 1;
		connectedStations.add(s1);
		connectedStations.add(s2);
	}

}
class Transfer extends Connection {
	java.util.List<Line> linesTransfer = new ArrayList();
	boolean isTransfer = true;
	public Transfer(Station s1, Station s2, int distance, Line l1, Line l2) {
		super(s1, s2, distance);
		length = distance;
		connectedStations.add(s1);
		connectedStations.add(s2);
		linesTransfer.add(l1);
		linesTransfer.add(l2);
	}
	public Transfer(Station s1, Station s2, Line l1, Line l2) {
		super(s1, s2);
		connectedStations.add(s1);
		connectedStations.add(s2);
		linesTransfer.add(l1);
		linesTransfer.add(l2);
		length = 1;
	}

}

class Station {
	Line line;
	String name;
	int id;
	List<Connection> connections = new ArrayList();
	List<Transfer> transfers = new ArrayList();

	int xCoord;
	int yCoord;

	public Station(String n, int i, Line l, int x, int y) {
		name = n;
		id = i;
		line = l;
		xCoord = x;
		yCoord = y;
	}
	public Station(String n, int i, Line l) {
		name = n;
		id = i;
		line = l;
	}
	public Station(String n, int i) {
		name = n;
		id = i;
	}
}

public class Metro {
	public java.util.List<Line> lines = new ArrayList();
	public Map<Integer, Station> stations = new HashMap();
	String name;

	public Metro(String n) {
		name = n;
	}
	
	public ArrayList<Station> directions(Station or, Station dest) { // Finds the directions
		Station origin = or;
		Station destination = dest;
		Map<Station, Integer> shortDistances = new HashMap();
		java.util.List<Station> unvisited = new ArrayList();
		Map<Station, Station> previousStation = new HashMap();
		previousStation.put(findInStations(origin.id), null);

		shortDistances.put(origin, 0);
		
		for(Entry<Integer, Station> entry : stations.entrySet()) {               // Initializes everything
			if (entry.getValue().id != origin.id) {
				shortDistances.put(entry.getValue(), Integer.MAX_VALUE);  // Sets the distance to everything as infinity
				previousStation.put(entry.getValue(), null);  // The previous station is undefined
			}
			unvisited.add(entry.getValue());
		}
		shortDistances.replace(findInStations(origin.id), 0);



		while(unvisited.size() > 0) {  // While there are still stations to check
			int minDistPick = Integer.MAX_VALUE;

			for(Entry<Station, Integer> dist : shortDistances.entrySet()) {   
				if (dist.getValue() < minDistPick && unvisited.contains(findInStations(dist.getKey().id))) {
					minDistPick = dist.getValue();
					//System.out.println("Found minDistPick!" + dist.getKey().name + " " + dist.getValue()); //Testing
				}
			} // End for loop
			Station checkingThisStation = null;
			for(Entry<Station, Integer> entry : shortDistances.entrySet()) {
				if(minDistPick == shortDistances.get(entry.getKey()) && unvisited.contains(entry.getKey())) {
					checkingThisStation = entry.getKey();
					//System.out.println("Picked a station to check! " + checkingThisStation.name); //Testing
					break;
				}
			}// End for loop

			
			Station connectedStationToCTS = null;
			for(Connection conn : checkingThisStation.connections) {
				int alternatePath = shortDistances.get(findInStations(checkingThisStation.id)) + conn.length;
				//System.out.print("Found altPath " + alternatePath); //Testing
				///Transfer stuff here;
				for(Station connected : conn.connectedStations) {   // Finds the station being connected to
					if(!(connected.id == checkingThisStation.id && unvisited.contains(connected))) {
						connectedStationToCTS = connected;
						//System.out.println(" to " + connectedStationToCTS.name);
						break;

					}
				} // End for loop
				

				if(alternatePath < shortDistances.get(connectedStationToCTS)) { // If it's a better path, use it
					shortDistances.replace(findInStations(connectedStationToCTS.id), alternatePath);
					previousStation.replace(findInStations(connectedStationToCTS.id), findInStations(checkingThisStation.id));
					//System.out.println("New path found " + alternatePath + " to " + connectedStationToCTS.name);
				}
			}// End big for loop
			unvisited.remove(findInStations(checkingThisStation.id)); 
			//System.out.println("Removed station successfully!" + checkingThisStation.name);
			
			//System.out.println("-------------------------------------------");
		}// End the while loop
		ArrayList<Station> path = new ArrayList();
		java.util.List<Boolean> transferPath = new ArrayList();
		Station stationBefore = destination;
		Station stationChecked;
		while (stationBefore != null) {

			path.add(0, findInStations(stationBefore.id));
			stationChecked = stationBefore;
			stationBefore = previousStation.get(stationBefore);
		}
		//System.out.print("Origin: ");
		for (Station stationPassed : path) {
			System.out.print(stationPassed.name);
			//System.out.println("");
		}


		return path;
	}
	public HashMap<Station, Transfer> transferFinder(ArrayList<Station> path) {  //Finds the transfers in the path
		HashMap<Station, Transfer> transfersFrom = new HashMap();
		for(Station passedStation : path) {
			boolean nextStationExists = false;
			int indexOfNextStation = 1;
			for(Station stat : path) { //Finds index of next station, if exists;
				if(path.indexOf(stat) == path.indexOf(passedStation) + 1){
					indexOfNextStation = path.indexOf(stat);
					nextStationExists = true;
					break;
				}
			} // Ends the thingy.
			if(nextStationExists) { // If there's another station to check in the path
				if(path.get(indexOfNextStation).line.id != passedStation.line.id) { // This checks if they're on the same line; 
					Transfer theConnection = null;										//	if they are, no point doing stuff
				    for(Transfer falsConnections : passedStation.transfers) {   // Finds the connection between two stations 
				    	if(falsConnections.connectedStations.contains(path.get(indexOfNextStation))) {
				    		theConnection = falsConnections;
				    		break;
				    	}
				    }
				    if(theConnection != null)  {transfersFrom.put(passedStation, theConnection);}                           
				}
			}
		}
		return transfersFrom;
	}
	public void declarations() throws FileNotFoundException, IOException, UnsupportedEncodingException {
		File linesFile = new File(name + " Metro\\" +name + " Lines.txt");
		Scanner readLines = new Scanner(linesFile);

		readLines.nextLine(); //Skip the first line
		int tempID;
		String tempColor;

		while(readLines.hasNextLine()) {   // While there's another line to check
			tempID = readLines.nextInt();
			tempColor = readLines.next();

			//System.out.println(tempID + " " + tempColor);

			lines.add(new Line(tempID, tempColor));
		}
		readLines.close();
		// Finish extracting Lines


		// Extract stations and 
		File stationsFile = new File(name + " Metro\\" + name + " Stations.txt");
		/*Scanner readStations = new Scanner(stationsFile, "UTF-8");
		readStations.useDelimiter(System.getProperty("line.separator"));*/
		InputStream streamStations = new FileInputStream(stationsFile);
		BufferedReader readStations = new BufferedReader(new InputStreamReader(streamStations, "UTF-8"));
		//FileWriter testWriter = new FileWriter(name + " Metro\\" + name + " write.txt", true);

		File coordsFile = new File(name + " Metro\\MapCoords.txt");
		Scanner readCoords = new Scanner(coordsFile);

		//System.out.println(readStations.readLine()); // Skip the first line;

		int tempSID;
		String tempName;
		int tempLine;
		int x, y;
		StringBuilder nextLine;
	 	String lineRead = readStations.readLine();
	 	
		while((lineRead = readStations.readLine()) != null) { // While there's another station in the list
			StringBuilder[] things = new StringBuilder[3];
			things[0] = new StringBuilder("");
			things[1] = new StringBuilder("");
			things[2] = new StringBuilder("");
			int doingThisOne = 0;
			//boolean justSkipped = false;
			for(char c : lineRead.toCharArray()) { // todo: add all ranges of the possible alphabets (include spaces)
			
				System.out.println("testing: " + c + " " + (int) c);
				if(doingThisOne < 3) {
					if((c >= 0x410 && c <= 0x44F) || (c >= 0x20 && c <= 0x7E)) {
						things[doingThisOne].append(c);
						System.out.println("Added: " + c);
						//System.out.println(doingThisOne);
					}
					else if(c == 0x9) {
						System.out.println("Id is: " + doingThisOne);
						System.out.println(things[doingThisOne]); // todo: make doingthisone update only once when finishes thing
						doingThisOne++;
					}
				}
				
			}
			//things[0].delete(0, 2);
			//things[0].toString().replace(Character.toString((char) 0x20), "");
			//System.out.println(things[0]);
			tempSID = Integer.parseInt(things[0].toString());
			tempName = things[1].toString();
			tempLine = Integer.parseInt(things[2].toString());
			//System.out.println(things[1]);
			

			Line tempLineLine = null;
			
			for(Line l : lines) {        // Finds the line with the ID given
				if(l.id == tempLine) {
					tempLineLine = l;
				}
			}
			System.out.println(tempLineLine.color);
			x = readCoords.nextInt();
			y = readCoords.nextInt();

			stations.put(tempSID, new Station(tempName,tempSID,tempLineLine, x, y));
		}
		readStations.close();
		//testWriter.close();
		// Finish extracting stations

		/*for(Entry<Integer, Station> entry : stations.entrySet()) {
			System.out.println(entry.getValue().name);
		}*/

		File transfersFile = new File(name + " Metro\\" + name + " Transfers.txt");
		Scanner readTrans = new Scanner(transfersFile);
		//readTrans.useDelimiter("\\t|\\n");

		readTrans.nextLine(); //Skip the first line;
		int tempLength = 1;
		int s1, s2, l1, l2;
		while(readTrans.hasNextLine()) {   // While there is another transfer
			lineRead = readTrans.nextLine();
			StringBuilder[] things1 = new StringBuilder[5];
			for(int i = 0; i < 5; i++) {
				things1[i] = new StringBuilder("");
			}
			int doingThisOne1 = 0;
			boolean justSkipped1 = false;
			for(char c : lineRead.toCharArray()) { // todo: add all ranges of the possible alphabets (include spaces)
				if(doingThisOne1 < 5) {
					if((c >= 0x410 && c <= 0x44F) || (c >= 0x20 && c <= 0x7E)) {
						things1[doingThisOne1].append(c);
						//System.out.println(doingThisOne1);
					}
					else {
						System.out.println("Id is: " + doingThisOne1);
						if(doingThisOne1 > -1) {
							System.out.println(things1[doingThisOne1]); // todo: make doingthisone update only once when finishes thing
						}
						if(!justSkipped1) {
							doingThisOne1 += 1;
						}
						
					}
				}
			}
			tempLength = Integer.parseInt(things1[0].toString());
			System.out.println(tempLength);
			s1 = Integer.parseInt(things1[1].toString());
			System.out.println(s1);
			s2 = Integer.parseInt(things1[2].toString());
			System.out.println(s2);
			l1 = Integer.parseInt(things1[3].toString());
			System.out.println(l1);
			l2 = Integer.parseInt(things1[4].toString());
			System.out.println(l2 + "**");
			Line line1 = null;
			Line line2 = null;

			for(Line l : lines) {  // Finds the lines for the IDs give
				if(l.id == l1) {
					line1 = l;
				}
				if(l.id == l2) {
					line2 = l;
				}
			}

			Transfer tempTrans = new Transfer(findInStations(s1), findInStations(s2), tempLength, line1, line2);
			//System.out.println(tempTrans.length);
			findInStations(s1).connections.add(tempTrans);  // Adds the new transfer to the list for both connections
			findInStations(s1).transfers.add(tempTrans);    // and transfers for both stations
			findInStations(s2).connections.add(tempTrans);
			findInStations(s2).transfers.add(tempTrans);
		}
		readTrans.close();
		// End the transfers
		// ****ALL STATIONS ON A LINE MUST BE DECLARED SEQUENTIALLY!****
		for(Entry<Integer, Station> entry : stations.entrySet()) {   // Goes through and adds normal line connections;
			try {
				Station sBefore = findInStations(entry.getValue().id - 1);
				Station sAfter = findInStations(entry.getValue().id + 1);

				if(sBefore.line.id == entry.getValue().line.id) { // Checks if sBefore are on the same line (really before);
					// Same line, so adds a normal connection (not transfer)
					Connection tempCon = new Connection(entry.getValue(), sBefore); // Assumes all distances are 1;
					entry.getValue().connections.add(tempCon);
					sBefore.connections.add(tempCon);
				}
				if(sAfter.line.id == entry.getValue().line.id) { // Checks if sAfter are on the same line (really before);
					// Same line, so adds a normal connection (not transfer)
					Connection tempCon = new Connection(entry.getValue(), sAfter); // Assumes all distances are 1;
					entry.getValue().connections.add(tempCon);
					sAfter.connections.add(tempCon);
				}
			} catch (NullPointerException nullPoint) {}
		}
	}
	public Station findInStations(int iD) {      // Finds the station reference you are looking for in Map<> stations
		for(Entry<Integer, Station> stat : stations.entrySet()) {
			if(stat.getValue().id == iD) {
				return stat.getValue();
			}
		}
		return null;
	}
}

