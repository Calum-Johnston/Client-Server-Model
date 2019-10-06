
import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class Client {

	public static void main(String[] args) throws IOException {
			
		Socket client = null;
		double serverResponseStartTime = 0;
		double serverResponseTime = 0;
				
		//Create the output to write to the log file		
		PrintWriter logOut = new PrintWriter(new FileWriter("client.log"), true);
		
		try {
			//Create and open a client socket
			client = new Socket("127.0.0.1", 4456);	
			System.out.println("Connection established - Successfully connected to Server: 127.0.0.1, Port: 4455");
			
			//Create the input stream and output to the socket for communicating with the server and writing to the log file
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
				
			//Allows the user to send and receive messages from the server
			String artistName = "";
			String artistSongsText = "";
			boolean valid = false;
				
			while(true){
				//User sends artist name to server
				while(valid == false){
					System.out.println("Enter an artist name");
					artistName = userIn.readLine();
					if(artistName.trim().isEmpty()){
						System.out.println("Enter a valid artist name");
					}else{
						valid = true;
					}
				}
				serverResponseStartTime = System.currentTimeMillis();
				System.out.println(serverResponseStartTime);
				out.println(artistName); valid = false;
					
				//Wait to see if request received successfully
				System.out.println(in.readLine());
					
				artistSongsText =  in.readLine();
				serverResponseTime = System.currentTimeMillis() - serverResponseStartTime;

				logOut.println("Response time from Server: " + serverResponseTime + "ms");
				logOut.println("The response length was: " + artistSongsText.getBytes().length + " bytes");
				logOut.println("The response was received at: " + LocalDate.now() + " , " + LocalTime.now());
					
				if(artistSongsText.equals("None")){
					System.out.println("No known artist exists");
				}else{
					String[] artistSongs = artistSongsText.split(",");
					for(String artistSong : artistSongs){
						System.out.println(artistName + " is associated with " + artistSong);
					}
				}
					
				//Prompts user to disconnect from the server 
				System.out.println("Query complete - disconnect? (quit)");
				if(userIn.readLine().equals("quit")){
					out.println("quit");
					System.out.println("Successfully disconnected from server"); break;
				}
					
			}
		} catch (SocketException e){
			System.out.println("Failed to establish Connection - Server not avaliable");
		} catch (IOException e) {
			System.out.println("Failed to establish Connection - Could not listen on port: 4455");
		} 	
		logOut.close();
	}
}
