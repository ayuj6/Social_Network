package application;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Scanner;
/**
 * This class is responsible for dealing with file imports and exports.
 * Imports are processed via a parsing method
 * 
 * 
 * @author Team 70
 *
 */
public class FileManager {
  private SocialNetwork socialNetwork;

  /**
   * Constructor that  Initializes fields
   * @param socialNetwork
   */
  public FileManager(SocialNetwork socialNetwork) {
    this.socialNetwork = socialNetwork;
  }

  /**
   * Imports the file and reads it
   * @param fileName
   * @return Linked lisk of errors
   */
  public LinkedList<Object> importFile(String fileName) {
   // Makes variables to hold hold file for parsing and 
   File file = new File(fileName);
   LinkedList<Object> out = new LinkedList<>();
   int count=0; //counting for number of errors while importing a file

   	// If valid file was entered, then scan
    if (file.exists()) {
    	 count = 0;
      // Create scanner of the file
      try (Scanner readFile = new Scanner(file)) {

    	// Loop through each line of the file
        while (readFile.hasNextLine()) {
        	
          try {
        	// parse each line 
            String instruction = readFile.nextLine();
            instructionParser(instruction);

          } catch (IllegalArgumentException ie) {
        	
        	count++; //increment the number of errors
            continue;
          }

        }
      } catch (Exception e) {
    	  // Keep track of each error
    	  out.add(false);
    	  out.add(count);
        return out;
      }

    } else {
      // Keep track of number of errors
  	  out.add(false);
  	  out.add(count);
      return out;
    }
    //if the parser true 
	  out.add(true);
	  out.add(count);
	  return out;
  }


  /**
   * Exports a the log of commands to a new file
   * 
   * @param fileName
   * @return true if success
   */
  public boolean exportFile(String fileName) {
	// Checks if this is the log file
    if (!fileName.equals("log.txt")) {
      // Creates variables for parsing
      PrintWriter printWriter = null;
      Scanner scanner = null;
      File file = new File(fileName);

      
      try {
    	// Initializes variables for parsing
        file.createNewFile();
        printWriter = new PrintWriter(file);
        scanner = new Scanner(new File("log.txt"));

        // Loop each line and add to new file
        while (scanner.hasNextLine()) {
          printWriter.println(scanner.nextLine());
        }

      } catch (IOException e) {
        return false;

      } finally {
    	// Close out  variables used for parsing when finished
        if (printWriter != null) {
          printWriter.close();
        }
        if (scanner != null) {
          scanner.close();
        }
      }
    }
    return true;
  }

  /**
   * Parses the instructions
   * 
   * @param instruction
   */
  private void instructionParser(String instruction) {
	// Clean up string instruction
    instruction = instruction.trim();
    String[] instructionArray = instruction.split(" ");

    
    switch (instructionArray[0]) {
      // Check Instruction
      
      // add
      case "a":
    	  
        if (instructionArray.length == 3) { // a user1 user2
          String user1 = instructionArray[1].trim();
          String user2 = instructionArray[2].trim();

          socialNetwork.addFriend(user1, user2);

        } else if (instructionArray.length == 2) { // a user
          String user = instructionArray[1].trim();

          socialNetwork.addUser(user);

        } else { // invalid add
          throw new IllegalArgumentException("Invalid Add Command");
        }
        break;
      
      // remove
      case "r":

        if (instructionArray.length == 3) { // r user1 user2
          String user1 = instructionArray[1].trim();
          String user2 = instructionArray[2].trim();

          socialNetwork.removeFriend(user1, user2);

        } else if (instructionArray.length == 2) { // r user
          String user = instructionArray[1].trim();

          socialNetwork.removeUser(user);

        } else { // invalid remove
          throw new IllegalArgumentException("Invalid Remove Command");
        }

        break;
      
      // set central user
      case "s":
        String user = instructionArray[1].trim();

        if (instructionArray.length == 2) {
          socialNetwork.setCentralUser(user);
        }

        break;

      default:
    	// If not found
        throw new IllegalArgumentException("Invalid Set Command");

    }
  }

  /**
   * Updates the log
   * @param instruction the instruction
   * @param logFileWriter the File Writer
   */
  public void updateLog(String instruction, PrintWriter logFileWriter) {
    logFileWriter.println(instruction);
    logFileWriter.flush();
  }

}
