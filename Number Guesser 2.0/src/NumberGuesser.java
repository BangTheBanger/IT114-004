import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class NumberGuesser{
	
	
	
	
	public static int randnum(int lvl) {
		int range = 9 + (lvl * 5);
		int num = new Random().nextInt(range) + 1;
		return num;
	}
	
	
	public static ArrayList<Integer> gamerun(ArrayList<Integer> data, Scanner input) {
		//System.out.println("Initial data array" + data);
		
		int lvl = 0, strikes = 0, num = randnum(lvl), guess = -1;
		boolean is_right = false, keep_going = true;
		ArrayList<Integer> alreadyguessed = new ArrayList<Integer>();
		
		try { //If the player quit mid-game last time.
			if (data.get(3) != null) {
				lvl = data.get(0);
				strikes = data.get(1);
				num = data.get(2);
				guess = -1;
				for (int x = 4; x < 9; x++) {
					alreadyguessed.add(data.get(x));
				}
			}
			else { 
				lvl = 0;
				strikes = 0;
				num = randnum(lvl);
				guess = -1;
			}
		} catch (Exception g) {}
		//System.out.println("after g data: " + lvl + " " + strikes + " " + num + " " + guess);
		
		
		while (keep_going) {
			System.out.println("I'm thinking of a number between 1 and " + (10 + (lvl * 5)) + ", let's see if you can guess it right.");
			String in = input.nextLine();
			try {
				guess = Integer.parseInt(in);
			}
			//if the user doesn't enter a number
			catch (Exception e) {
				//If the user didn't enter a number but entered 'quit'. Save the data and return the array with the information.
				if (in.equalsIgnoreCase("quit")) {
					keep_going = false;
					System.out.println("Tired of playing? No problem.");
					ArrayList<Integer> data_2 = new ArrayList<Integer>();
					data_2.add(lvl);
					data_2.add(strikes);
					data_2.add(num);
					data_2.add(2);
					try {
						for (int x = 0; x < 5; x++) {
							data_2.add(alreadyguessed.get(x));
						}
					} catch (Exception f) {}
					//System.out.println("Test for data output, through quit:\n" + data_2);
					return data_2;
				}
				
				//If the user didn't enter a number or 'quit'.
				else{ System.out.println("You didn't enter a number, please try again"); }
			}
			
			
			//if the input is as expected, the game will progress.
			//if the player guessed right.
			if (guess == num) {
				System.out.println("That's right!");
				is_right = true; keep_going = false;
				lvl++;
				System.out.println("You are now in level " + (lvl+1) + ".\n");
				
			} else {
				//When player gets it wrong. First add a strike, tell the player how many strikes he has left,
				//then tell them if their guess was higher or lower. 
				strikes++;
				System.out.println("That's wrong" + "\n" + "You have " + (5-strikes) + " strikes left");
				
				if (guess > num) { System.out.println("Your guess was higher than the number"); } 
				else { System.out.println("Your guess was lower than the number"); }
				
				alreadyguessed.add(guess);
				System.out.println("\nYou have already guessed the numbers: " + alreadyguessed);
				
				if (strikes >= 5) {
					System.out.println("Uh oh, looks like you need to get some more practice.");
					lvl--;
					System.out.println("You are now in level " + (lvl+1) + ".\n");
					is_right = false; keep_going = false;
				}
			}
		}
		ArrayList<Integer> data_2 = new ArrayList<Integer>();
		data_2.add(lvl);
		//System.out.println(data_2.get(0));
		data_2.add(0);
		data_2.add(randnum(lvl));
		//System.out.println(data_2.get(2));
		if (is_right) {
			data_2.add(1);
		} else {
			data_2.add(0);
		}
		//System.out.println("Test for data output, through normal means:\n" + data_2);
		return data_2;
	}
	
	
	public static void savegame(ArrayList<Integer> data, Scanner input) {
		boolean keep_going = true;
		
		try {
			while (keep_going) {
				System.out.println("Name your savefile:    (Note: Don't enter the file extension, it is by default '.txt')");
				String savename = input.nextLine() + ".txt";
				File fileReference = new File(savename);
				
				if (fileReference.createNewFile()) {
					System.out.println("Savefile '" + savename + "' successfully created.");
					
					try (FileWriter fw = new FileWriter(savename)) {
						for (int x = 0; x < data.size(); x++) {
							//System.out.println("Writing: " + data.get(x));
							String s = "";
							try {  fw.write(s = String.valueOf(data.get(x)) + "\n");  }catch(Exception p) {}
						}
							
					} catch (IOException e) {
					}
					keep_going = false;
					
					
				} else {
					System.out.println("File already exists");
					System.out.println("Do you wish to overwrite that save? (Reminder: 0 is no, 1 is yes)");
					String msg = input.nextLine();
					if (msg.equals("1")) {
						try (FileWriter fw = new FileWriter(savename)) {
							for (int x = 0; x < data.size(); x++) {
								//System.out.println("Writing: " + data.get(x));
								String s = "";
								try {  fw.write(s = String.valueOf(data.get(x)) + "\n");  }catch(Exception p) {}
							}
								
						} catch (IOException e) {
						}
						keep_going = false;
					} else if (msg.equals("0")) {
						System.out.println("Would you like to save your game?");
						msg = input.nextLine();
						if (msg.equals("0")) {  keep_going = false;  } 
						else if (msg.equals("1")) {}
						else if (msg.equalsIgnoreCase("quit")) {  keep_going = false;  } 
						else {  System.out.println("\nSorry, that wasn't a number.");  }
						
					} else { 
						if (msg.equalsIgnoreCase("quit")) {
							keep_going = false;
						} else {  System.out.println("\nSorry, that wasn't a number. To quit type 'quit'."); }
					}
					
				}
			}
			
		} catch (IOException ie) {
			ie.printStackTrace();

		}
	}
	
	
	public static ArrayList<Integer> loadgame(Scanner input) {
		ArrayList<Integer> data = new ArrayList<Integer>();
		boolean keep_going = true;
		
		
		while (keep_going) {
			System.out.println("What's the name of the save file?    (Note: Don't enter the file extension, it is by default '.txt') ");
			String fileName = input.nextLine() + ".txt";
			File file = new File(fileName);
			try (Scanner reader = new Scanner(file)) {
				while (reader.hasNextLine()) {
					String nl = reader.nextLine();
					int v = Integer. parseInt(nl);
					data.add(v);
				}
				System.out.println("Loaded save: " + file + "\n\n");
				return data;
			} catch (FileNotFoundException e) {
				//If file no exist
				System.out.println("Reader error: Save file not found.");
				System.out.println("Would you like to load a save file?");
				String msg = input.nextLine();
				if (msg.equals("0")) {  keep_going = false;  } 
				else if (msg.equals("1")) {}
				else {  System.out.println("\nSorry, that wasn't a number.");  }
			}
		}
		
		
		return null;
	}
	
	
	public static void main(String[] args) {
		try (Scanner input = new Scanner(System.in);) {
			System.out.println("Welcome to the Number Guesser game!\nUse 0 for no, 1 for yes | Enter 'quit' at anytime to quit.\n");
			boolean keep_going = true, keep_going_game = true, keep_going_save = true, keep_going_load = true;

			//Data Structure: 	[(0) level, (1) strikes, (2) number to guess,
			//					(3) return status (used to handle the 3 return cases: Player lose == 0, player win == 1, player quit == 2.),
			//					(4) guesses already made, (5) guesses already made, (6) guesses already made, (7) guesses already made, (8) guesses already made]
			ArrayList<Integer> data = new ArrayList<Integer>();
			//Data creation ^
			
			///TESTING STATEMENTS 
			/*
			data.add(0);
			data.add(1);
			data.add(5);
			data.add(2);
			data.add(4);
			*/
			///TESTING STATEMENTS  
			
			String msg = "";
			
			
			while (keep_going) {
				keep_going_load = true;
				while (keep_going_load) {
					System.out.println("Would you like to load a save file?");
					msg = input.nextLine();
					System.out.println();
					
					if(msg.equals("1")) {
						keep_going_load = false;
						data = loadgame(input);
					} else if (msg.equals("0")) {
						keep_going_load = false;
					} else {
						System.out.println("\nSorry, that wasn't a number.");
						continue;
					}
					System.out.println("Let's start.");
					
					while (keep_going_game) {
						data = gamerun(data, input);
						//System.out.println("LEVEL AFTER RUN" + data.get(0));
						
						try {
							ArrayList<Integer> data_2 = new ArrayList<Integer>();
							data_2.add(data.get(0));
							data_2.add(data.get(1));
							data_2.add(data.get(2));
							data_2.add(data.get(3));
							for (int x = 4; x < 9; x++) {
								data_2.add(data.get(x));
							}
							data = data_2;
						} catch (Exception a) {}
						
						if (data.get(3) == 2) {
							break;
						}
						//Asks user if they want to play again.
						System.out.println("Would you like to play another one?");
						msg = input.nextLine();
						//If user doesn't want to play again.
						if (msg.equals("0")) {
							keep_going = false; keep_going_game = false;
							System.out.println("Good game!\n\n");
							keep_going = false;
						}
						//If user does want to play again.
						else if(msg.equals("1")) { System.out.println("\nAlright, here we go again!"); }
						//If user doesn't input a number.
						else { System.out.println("\nSorry, that wasn't a number. I will begin a new one for you. Enter quit if you wish to leave."); }
						
					}
					keep_going_save = true;
					while (keep_going_save) {
						System.out.println("Would you like to save your game?");
						msg = input.nextLine();
						if (msg.equals("0")) { System.out.println("Alright then, I'll see you next time!"); keep_going_save = false; keep_going = false; } 
						else if (msg.equals("1")) { savegame(data, input); System.out.println("I'll see you next time!"); keep_going_save = false; keep_going = false; }
						else {  System.out.println("\nSorry, that wasn't a number.");  }
					}
				}
				
				
				
				/////////////Ask if they want to restart
				
				
			}
		
		
		
		
		
		
		} catch (Exception e) {
			System.out.println("Oh no! What are you doing? That's not a number, I can't handle this.");
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
}