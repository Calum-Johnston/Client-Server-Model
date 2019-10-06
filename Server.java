import java.io.*;
import java.net.*;
import java.time.*;
import java.util.*;

public class Server {

	public static Map<String, List<String>> artistList = new HashMap<String, List<String>>();
	
	public static void main(String[] args) throws IOException {
		
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		long clientConnectionTime = 0;
		long clientDisconnectionTime = 0;
		
		readData();
		
		//Create output stream for writing to the log file
		PrintWriter logOut = new PrintWriter(new FileWriter("server.log", true)); 
		
		//Creates and opens a server socket on port 4455
		try {
			//Creates and opens a server socket on port 4455
			serverSocket = new ServerSocket(4456);		
			System.out.println("Server start - listening on port 4455");
			logOut.println("Server Started: DATE " + LocalDate.now() + " TIME " + LocalTime.now());
			
			while(true){
				//Waits for the client request
				clientSocket = serverSocket.accept();
				clientConnectionTime = System.currentTimeMillis();
				System.out.println("Connection established - Client Connected on port: 4455");
				logOut.println("Client Attempting to Connect: DATE " + LocalDate.now() + " TIME " + LocalTime.now());
				logOut.println("Client Successfully Connected");
			
				//Creates input & output streams to communicate with client
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);   
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
				//Server receives an artist name, and finds all song(s) associated to that artist
				String artistName;
				List<String> artistSongs;
				String artistSongsText = "";
				while(true){
				
					//Wait for an artist request from client
					artistName = in.readLine();
				
					//User wishes to terminate connection is received "quit"
					if(artistName.equals("quit")){
						clientDisconnectionTime = System.currentTimeMillis();
						logOut.println("Length of Client Connection: " + (clientDisconnectionTime - clientConnectionTime) + "ms");
						clientSocket.close();
						break;
					}else{
						logOut.println("Client request for artist: '" + artistName + "' received");
						System.out.println("Received message: " + artistName + " from " + clientSocket.toString());
					}
				
					//Confirm to client that the request has been received
					out.println("Request for " + artistName + " successfully received");
				
					//Retrieves the required information about the artist & returns to client
					artistSongs =  artistList.get(artistName);
					if(artistSongs == null){
						artistSongsText = "None";
					}else{
						artistSongsText = "";
						for(String song : artistSongs) {
							artistSongsText += song + ",";
						}
					}
					out.println(artistSongsText);
				}
				logOut.close();
			}
		} catch (IOException e) {
			System.out.println("Failed to establish Server - Could not listen on port: 4455");
		}		
	}
	
	
	//Reads the data in from the text file
	public static void readData() throws IOException{
		
		String line = null;
		BufferedReader br = null;
		boolean nextLine = false;
		String artist = "";
		String song = "";
		
		try {
			br = new BufferedReader(new FileReader("100worst.txt"));
			while((line = br.readLine()) != null){
				if(!(line.isEmpty())){
					if(nextLine == false){
						if(line.substring(2, 3).equals("-") || line.substring(0, 3).equals("100")){
							if(line.length() < 64){
								song = line.substring(4);
								nextLine = true;
							}else{
								song = line.substring(4,35);
								artist = line.substring(35, 65);
								updateDatabase(artist, song);
							}
						}
					}else{
						artist = line.substring(35, 65);
						nextLine = false;
						updateDatabase(artist, song);
					}
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("Cannot read file");
		}
	}
	
	public static void updateDatabase(String artist, String song) {
		song = song.trim();
		artist = artist.trim();
		
		String[] artists = artist.split("/");
		for(String art : artists){
			addArtist(art, song);
		}
	}
	
	public static void addArtist(String artist, String song){
		List<String> artistSongs = null;
		artistSongs =  artistList.get(artist);
		if(artistSongs == null){
			artistSongs = new ArrayList<String>();
			artistSongs.add(song);
		}else{
			artistSongs.add(song);	
		}
		artistList.put(artist, artistSongs);
	}
}