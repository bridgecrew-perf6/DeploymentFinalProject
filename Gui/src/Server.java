

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class Server extends Application {
  @Override // Override the start method in the Application class
  public void start(Stage primaryStage) {
    // Text area for displaying contents
    TextArea ta = new TextArea();

    // Create a scene and place it in the stage
    Scene scene = new Scene(new ScrollPane(ta), 450, 200);
    primaryStage.setTitle("Server"); // Set the stage title
    primaryStage.setScene(scene); // Place the scene in the stage
    primaryStage.show(); // Display the stage
    
    new Thread( () -> {
      try {
        // Create a server socket
        ServerSocket serverSocket = new ServerSocket(8000);
        Platform.runLater(() ->
          ta.appendText("Server started at " + new Date() + '\n'));
  
        // Listen for a connection request
        Socket socket = serverSocket.accept();
  
        // Create data input and output streams
        DataInputStream inputFromClient = new DataInputStream(
          socket.getInputStream());
       // DataOutputStream outputToClient = new DataOutputStream(
        //  socket.getOutputStream());
        
        PrintWriter outputToClient =
                new PrintWriter(socket.getOutputStream(), true);
  
        while (true) {
          // Receive number from the client
         // int number = inputFromClient.readInt();

//          boolean check = false;
//          
//          for(int i = 2; i <= number / 2; i++) {
//        	  if(number % i == 0) {
//        		  check = true;
//        		  break;
//        	  }
//          }
//          
//          if (!check) {
//        	//  System.out.println(number + "is prime.");
//        	  // Send area back to the client
//              outputToClient.println("yes");
//          }
//          else {
//        	 // System.out.println(number + "is not prime.");
//        	  // Send area back to the client
//              outputToClient.println("no");
//          }
  
          String path = "//Users//hellenfernandes//Documents//GUI//poem.txt";
          
          wordsCounter(path, outputToClient);
  
          Platform.runLater(() -> {
            ta.appendText("Sending to client\n");
           // ta.appendText("Area is: " + area + '\n'); 
          });
        }
      }
      catch(IOException ex) {
        ex.printStackTrace();
      }
    }).start();
  }

  public void wordsCounter(String path, PrintWriter outputToClient) throws IOException {

		try {

			Class.forName("com.mysql.cj.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/wordoccurrences";
			String user = "root";
			String password = "Programmer2@";

			try (Connection connection = DriverManager.getConnection(url, user, password)) {
				try (Statement statement = connection.createStatement()) {
					String sql = "truncate table wordoccurrences.word";
					statement.executeUpdate(sql);
				}

				FileInputStream fileInp = new FileInputStream(path);
				Scanner s = new Scanner(fileInp);


				while (s.hasNext()) {
					String nextWord = s.next().toLowerCase();

					nextWord = nextWord.replaceAll("[^a-zA-Z]", "");

					String sql = "Select count(word) as c from wordoccurrences.word where word = '" + nextWord + "';";

					Statement statement = connection.createStatement();

					ResultSet r = statement.executeQuery(sql);
					r.next();
					int count = r.getInt("c");
					statement.close();

					if (count >= 1) {

						sql = "select frequency from wordoccurrences.word where word = '" + nextWord + "';";
						statement = connection.createStatement();

						r = statement.executeQuery(sql);
						r.next();
						int rc = r.getInt("frequency");

						sql = "update wordoccurrences.word set frequency = " + (rc + 1) + " where word = '" + nextWord
								+ "';";

						statement = connection.createStatement();
						statement.executeUpdate(sql);

					} else {
						sql = "insert into wordoccurrences.word (word, frequency) values ('" + nextWord + "', 1);";
						statement = connection.createStatement();
						statement.executeUpdate(sql);
						statement.close();
					}
				}
				s.close();

				String all = "";
				
				System.out.println("\n****SORTED BY MOST FREQUENTLY USED WORD****\n");
				try (Statement statement = connection.createStatement()) {
					String sql = "select * from word order by frequency desc limit 20";
					try (ResultSet result = statement.executeQuery(sql)) {
						while (result.next()) {
							String name = result.getString("word");
							int frequency = result.getInt("frequency");
							System.out.println(name + " " + frequency);
							//outputToClient.println(name + " " + frequency);
							all += name + " " + frequency + "-";
						
						}
					}
				}
//				
				outputToClient.println(all);
				
//		map.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).limit(20)
//				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new))
//				.forEach((a, integer) -> System.out
//						.println(String.format("Word: %s\t\t| Frequency: %s times", a, integer)));

				connection.close();

			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				System.out.println(e);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

  /**
   * The main method is only needed for the IDE with limited
   * JavaFX support. Not needed for running from the command line.
   */
  public static void main(String[] args) {
    launch(args);
  }
}
