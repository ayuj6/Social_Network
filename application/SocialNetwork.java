package application;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * This class creates a social network object that maintains users,
 * central users, files, and more.
 * 
 * @author Team 70
 *
 */
public class SocialNetwork implements SocialNetworkADT {
  // Fields
  private Graph graph;
  private String centralUser;
  private FileManager fileManager;
  private PrintWriter logFileWriter;

  /**
   * Initialize values to be used by this class
   */
  public SocialNetwork() {
    this.centralUser = null;
    this.graph = new Graph();
    this.fileManager = new FileManager(this);
    this.logFileWriter = createLogFileWriter();
  }

  @Override
  /**
   * Get friends of a user
   *
   * @param user the user whose friends we need to retrieve
   * @return the SocialNetwork
   */
  public List<String> displayNetwork(String user) {
    return graph.getAdjacentVerticesOf(user);
  }

  @Override
  /**
   * Set the central user of the network
   *
   * @param centralUser the central user who we need to set
   * @return true if the central user was set
   */
  public boolean setCentralUser(String centralUser) {
    this.centralUser = centralUser;
    fileManager.updateLog("s " + centralUser, logFileWriter);
    return true;
  }

  @Override
  /**
   * Get the central user of the network
   *
   * @return string central user's name
   */
  public String getCentralUser() {
    if (centralUser == null) {
      List<String> allUsers = graph.getAllVertices();
      centralUser = allUsers.get(0);
      return centralUser;
    }

    return centralUser;
  }

  @Override
  /**
   * Add user to the netwrok
   *
   * @param user the user to be added
   * @return true if user was added to the network
   */
  public boolean addUser(String user) {
	  // Check is user exists
    if (!graph.getAllVertices().contains(user) && isValidUser(user)) {
      graph.addVertex(user);
      fileManager.updateLog("a " + user, logFileWriter);
      return true;
    } else {
    	return false;
    }
  }

  @Override
  /**
   * Remove user from the network
   *
   * @param user the user to be removed
   * @return true if user was removed from the network
   */
  public boolean removeUser(String user) {
	// Check if exists
    if (getAllUsers().contains(user)) {
      graph.removeVertex(user);
      fileManager.updateLog("r " + user, logFileWriter);
      return true;
    } else {
      return false;
    }
  }

  @Override
  /**
   * Add friend for the central user
   *
   * @param friend the user to be added
   * @return true if friend was added
   */
  public boolean addFriend(String friend) {
	// Check if exists
    if (!centralUser.equals(friend) && isValidUser(friend)) {
      graph.addEdge(centralUser, friend);
      fileManager.updateLog("a " + centralUser + " " + friend, logFileWriter);
      return true;
    } else {
      return false;
    }
  }

  @Override
  /**
   * Remove friends from the central user
   *
   * @param friend the friend that needs to be removed
   * @return true if the friend was removed
   */
  public boolean removeFriend(String friend) {
	// True if added
    if (!centralUser.equals(friend)) {
      graph.removeEdge(centralUser, friend);
      fileManager.updateLog("r " + centralUser + " " + friend, logFileWriter);
      return true;
    } else {
      return false;
    }

  }

  @Override
  /**
   * Add Friendship between any two users
   *
   * @param friend1 the first user
   * @param friend2 the second user
   * @return true if the friedship was added to the network
   */
  public boolean addFriend(String friend1, String friend2) {
	// Check if users exist and should be added
    if (!friend1.equals(friend2) && isValidUser(friend1) && isValidUser(friend2)) {
      graph.addEdge(friend1, friend2);
      fileManager.updateLog("a " + friend1 + " " + friend2, logFileWriter);
      return true;
    } else {
    	return false;
    }
  }

  @Override
  /**
   * Removes the friendship between two users
   *
   * @param friend1 the first friend
   * @param friend2 the second friend
   * @return true if the friendship was removed
   */
  public boolean removeFriend(String friend1, String friend2) {
	// Check if friends are different
    if (!friend1.equals(friend2)) {
      graph.removeEdge(friend1, friend2);
      fileManager.updateLog("r " + friend1 + " " + friend2, logFileWriter);
      return true;
    } else {
    	return false;
    }
  }

  @Override
  /**
   * Reset SocialNetwork to remove all users
   */
  public void removeAllUsers() {
    graph = null;
  }

  @Override
  /**
   * Find mutual friends between two users
   *
   * @param user1 the first user
   * @param user2 the second user
   * @return list of mutual friends between two users
   */
  public List<String> getMutualFriends(String user1, String user2) {
	// Create two friend list of each user and merge them
    List<String> user1Friends = graph.getAdjacentVerticesOf(user1);
    List<String> user2Friends = graph.getAdjacentVerticesOf(user2);
    List<String> mutualFriends = new ArrayList<String>();
    
    // Loop through each friend and add if same as another users friend
    for (String friend : user1Friends) {
      if (user2Friends.contains(friend)) {
        mutualFriends.add(friend);
      }
    }
    return mutualFriends;
  }

  @Override
  /**
   * Get all users from the SocialNetwork
   *
   * @return list of all users in the Network
   */
  public List<String> getAllUsers() {
    return graph.getAllVertices();
  }

  /**
   * Get specific user that is requested
   * 
   * @param user the name of the user we wish to retreive
   * @return User the user object with the given name
   */
  public User getUser(String user) {
    return graph.getNode(user);
  }

  /**
   * Import a file and read it
   * 
   * @param fileName the name of the file to read from
   * @return true if the file was successfully imported
   */
  public boolean importFile(String fileName) {
	  // Keeps tally of how many line errors there were and reports that info
	  // to the user
	  int count = (int) fileManager.importFile(fileName).get(1);
	  if(count>0) {
		  Alert addUserAlert = new Alert(AlertType.ERROR, 
				  "Encountered "+ count + " Line(s) With Errors When Reading File");
	      addUserAlert.show();
	  }
	  // return true if successfully imported
	  return (boolean) fileManager.importFile(fileName).get(0);
  }

  /**
   * Export file to a given fileName
   *
   * @param fileName the file to which we wish to export to
   * @return true if export was successful
   */
  public boolean exportFile(String fileName) {
	// Close unused FileWriter and export to file name
    logFileWriter.close();
    return fileManager.exportFile(fileName);
  }

  /**
   * Find Shortest path between two users
   *
   * @param user1 the first user
   * @param user2 the second user
   * @return List of shortest path between the two users
   */
  public LinkedList<String> getShortestPath(String user1, String user2) {
    return graph.shortestPath(graph.getNode(user1), graph.getNode(user2));
  }

  /**
   * Get the number of friend groups in the SocialNetwork
   *
   * @return Number of Friend Groups that exist in the graph
   */
  public int getGroups() {
    return graph.getConnectedComponents();
  }

  /**
   * Helper method to check if a new user's username is valid
   * 
   * @param name name of user
   * @return true if valid; false otherwise
   */
  private boolean isValidUser(String name) {
	// Loop through all characters in name and check that each one is legal
    for (int i = 0; i < name.length(); i++) {
      Character letter = name.charAt(i);
      if (!Character.isLetter(letter) && !Character.isDigit(letter) && !letter.equals('\'')
          && !letter.equals('_')) {
        return false;
      }
    }
    return true;
  }

  /**
   * Create File Writer to write to log file
   *
   * @return the PrintWriter that will write to the log file
   */
  private PrintWriter createLogFileWriter() {
    File file = new File("log.txt");
    PrintWriter writer = null;

    
    try {
      // Try to create file, and return if successful
      // Otherwise print errors
      file.createNewFile();
      writer = new PrintWriter(file);
      writer.print("");

    } catch (IOException e) {
      e.printStackTrace();
    }
    return writer;
  }

}

