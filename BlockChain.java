import java.util.List; 
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Random;

public class BlockChain {
	
	private ArrayList<Block> list = new ArrayList<Block>(); //arrayList for blockChain
	static HashMap userProfiles = new HashMap(); //HashMap used to store each user as the key with their respective bitcoins as the value
	
	public BlockChain() { //blockChain constructor
		
		this.list = new ArrayList<Block>();
	}
	
	public static int randomNumberGen (int min, int max) { //random number generator (used in add method)
		Random rand = new Random();
		return rand.nextInt((max-min)+1)+min;
	}
	
	public void add(Block block) { //adds block to BlockChain
		
		//Updating hashmap
		
		if (userProfiles.containsKey(block.getTransaction().getSender())==true) { //update sender's coins
			userProfiles.put(block.getTransaction().getSender(),((int)userProfiles.get(block.getTransaction().getSender())-block.getTransaction().getAmount()));
			System.out.println(block.getTransaction().getSender()+": Sender was in the system, is now updated: "+userProfiles.get(block.getTransaction().getSender()));
		}
		if (userProfiles.containsKey(block.getTransaction().getSender())==false) { //add new senders to hashmap with value set to zero
			userProfiles.put(block.getTransaction().getSender(), 0);
			System.out.println(block.getTransaction().getSender()+": Sender wasnt in the system, is now added: "+userProfiles.get(block.getTransaction().getSender()));
		}
		if (userProfiles.containsKey(block.getTransaction().getReceiver())==true) { //update receiver's coins
			userProfiles.put(block.getTransaction().getReceiver(), ((int)userProfiles.get(block.getTransaction().getReceiver())+block.getTransaction().getAmount()));
			System.out.println(block.getTransaction().getReceiver()+": Receiver was in the system, is now updated: "+userProfiles.get(block.getTransaction().getReceiver()));
		}
		if (userProfiles.containsKey(block.getTransaction().getReceiver())==false) { //add new receivers to hashmap with value set to numberOfCoins
			userProfiles.put(block.getTransaction().getReceiver(), 0+block.getTransaction().getAmount());
			System.out.println(block.getTransaction().getReceiver()+": Receiver wasnt in the system, is now added: "+userProfiles.get(block.getTransaction().getReceiver()));
		}
		
		//create proof of work 
		
		String nonceTry="";
		boolean match = false;
		int attempts=0; //keeps track of nonce creation attempts
		int value=0; 
		Sha1 sha = new Sha1();
		String blockHash="";
		
		//set block's previous hash
		block.setPreviousHash(this.list.get(block.getIndex()-1).getHash()); //sets block's previous hash
		
		//generate random numbers for visible characters 
		while (match==false) {
			nonceTry="";
			for (int i=0; i<19;i++) {
				value = randomNumberGen(33,126);
				char val = (char) value;
				nonceTry+=val;	//append new character to attempted nonce
			}
			attempts++;
			try {
				block.setNonce(nonceTry); //sets block's nonce to the attempted nonce for hash evaluation
				if (sha.hash(block.toString()).startsWith("00000")) {	//checks if hash starts with required 00000 using nonce
					match=true; //breaks while loop
					block.setNonce(nonceTry);
					blockHash=sha.hash(block.toString());
				}
			} catch (UnsupportedEncodingException e) { e.printStackTrace();}
		}
		block.setHash(blockHash); //sets block's hash
		this.list.add(block.getIndex(), block);
		System.out.println("Generated hash after "+attempts+" attempts: "+block.getHash());
	}
	
	public static BlockChain fromFile(String fileName) throws FileNotFoundException { //reads data from file
		
		BlockChain ourBlockChain = new BlockChain();
		
		int index=0;
		String timestamp=null;
		String sender=null;
		String receiver=null;
		int numberOfCoins=0;
		String nonce=null;
		String hash=null;
		
		File fileToRead = new File(fileName);
		Scanner input = new Scanner(fileToRead);	
		
		try {	
	
			int lineNumber = 0;
			
			while (input.hasNext()||lineNumber==7) { //reads file and assigns correct values to variables
				if (lineNumber == 8) {
					lineNumber = 0;
				}
				lineNumber++;
				if (lineNumber == 1) {
					index = input.nextInt(); 
				}
				if (lineNumber == 2) {
					timestamp = input.next();
				}
				if (lineNumber == 3) {
					sender = input.next();
				}
				if (lineNumber == 4) {
					receiver = input.next();
				}
				if (lineNumber == 5) {
					numberOfCoins = input.nextInt();
				}
				if (lineNumber == 6) {
					nonce = input.next();
				}
				if (lineNumber == 7) {
					hash = input.next();
				}
				if (lineNumber == 8) { //creates block
					
					Block block = new Block(index,timestamp,nonce,hash,sender,receiver,numberOfCoins);
					
					//Updating hashmap
					
					if (userProfiles.containsKey(sender)==true) { //update sender's coins
						userProfiles.put(sender,((int)userProfiles.get(sender)-numberOfCoins));
						System.out.println(sender+": Sender was in the system, is now updated: "+userProfiles.get(sender));
					}
					if (userProfiles.containsKey(sender)==false) { //add new senders to hashMap with value set to zero
						userProfiles.put(sender, 0);
						System.out.println(sender+": Sender wasnt in the system, is now added: "+userProfiles.get(sender));
					}
					if (userProfiles.containsKey(receiver)==true) { //update receiver's coins
						userProfiles.put(receiver, ((int)userProfiles.get(receiver)+numberOfCoins));
						System.out.println(receiver+": Receiver was in the system, is now updated: "+userProfiles.get(receiver));
					}
					if (userProfiles.containsKey(receiver)==false) { //add new receivers to hashMap with value set to numberOfCoins
						userProfiles.put(receiver, 0+numberOfCoins);
						System.out.println(receiver+": Receiver wasnt in the system, is now added: "+userProfiles.get(receiver));
					}
					//Setting hash for block
					if (block.getIndex()==0) {
						block.setPreviousHash("00000");
					}
					if (block.getIndex()!=0) {
						block.setPreviousHash(ourBlockChain.list.get(block.getIndex()-1).getHash());
					}
					
					//adding block to blockChain at specified index
					ourBlockChain.list.add(index,block);
						
				}
			}
		} catch (Exception FileNotFoundException) {
			System.out.println("No file by the name "+fileName+" found!");
		}
		return ourBlockChain;
	}
	
	public void toFile(String fileName) { //writes block information to file
		try {
			PrintWriter outputStream = new PrintWriter(fileName);
			int numberOfBlocks = this.list.size();
			System.out.println("Number of blocks in chain "+numberOfBlocks);
			
			for (int i=0;i<numberOfBlocks;i++) {
				outputStream.println(this.list.get(i).getIndex());
				outputStream.println(this.list.get(i).getTimestamp().getTime());
				outputStream.println(this.list.get(i).getTransaction().getSender());
				outputStream.println(this.list.get(i).getTransaction().getReceiver());
				outputStream.println(this.list.get(i).getTransaction().getAmount());
				outputStream.println(this.list.get(i).getNonce());
				outputStream.println(this.list.get(i).getHash());
			}
			outputStream.close();
		}
		catch(Exception FileNotFoundException) {
			System.out.println("Whoops! No file!");
		}
	}

	public boolean validateBlockChain() {	//checks block has proper hashes and previous hashes
		boolean valid = true;
		int size = 0;
		Sha1 sha = new Sha1();
		while (valid==true && size<this.list.size()) {
			
			try {
				if (sha.hash(this.list.get(size).toString())==this.list.get(size).getHash()) {
					System.out.println("Hash generated isnt the same");
					valid=false;
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (size==0 && this.list.get(size).getPreviousHash()!="00000") {
				System.out.println("Block #"+this.list.get(size).getIndex()+" first block's generated hash isnt the same");
				valid = false;
			}
			if (size!=0 && this.list.get(size).getPreviousHash() != this.list.get(size-1).getHash()) {
				System.out.println("Block #"+this.list.get(size).getIndex()+"the previous hashes dont match");
				valid = false;
			}
			
			size++;
			
		}
		System.out.println("Valid? "+valid);
		return valid;	
	}
	
	public int getBalance (String username, int index) { //gets value stored at key given (i.e. value associated to user name) 
			
		if (username==this.list.get(index).getTransaction().getSender()) {
			if (userProfiles.containsKey(username)==true) { //update sender's coins
				userProfiles.put(username,((int)userProfiles.get(username)-this.list.get(index).getTransaction().getAmount()));
				System.out.println(username+": Sender was in the system, is now updated: "+userProfiles.get(username));
			}
			if (userProfiles.containsKey(username)==false) { //add new senders to hashmap with value set to zero
				userProfiles.put(username, 0);
				System.out.println(username+": Sender wasnt in the system, is now added: "+userProfiles.get(username));
			}
		}
		if (username==this.list.get(index).getTransaction().getReceiver()) {
			if (userProfiles.containsKey(username)==true) { //update receiver's coins
				userProfiles.put(username, ((int)userProfiles.get(username)+this.list.get(index).getTransaction().getAmount()));
				System.out.println(username+": Receiver was in the system, is now updated: "+userProfiles.get(username));
			}
			if (userProfiles.containsKey(username)==false) { //add new receivers to hashmap with value set to numberOfCoins
				userProfiles.put(username, this.list.get(index).getTransaction().getAmount());
				System.out.println(username+": Receiver wasnt in the system, is now added: "+userProfiles.get(username));
			}
		}
		
		return (int)userProfiles.get(username);
	}
	
	
	public static void main(String[] args) {
		System.out.println("Here we go!");
		BlockChain blocks = new BlockChain();
		
		System.out.println("What file do you want to read?");
		Scanner scan = new Scanner(System.in);
		String file = scan.next();
		try {
			blocks=blocks.fromFile(file); //reads file
			blocks.validateBlockChain();  //validates each block in blockChain
			
			System.out.println("What file do you want to write to?");
			Scanner scan2 = new Scanner(System.in);
			String fileOut = scan2.next();
			blocks.toFile(fileOut); //prints to file with inputed name
			
			if (blocks.validateBlockChain()==true) { //only allows more transactions if the blockChain is valid
				System.out.println("Do you want to make a transaction? (yes/no)");
				String response = scan2.next();
						
				while (response.equals("yes")) {
					
					System.out.println("Here is your blockchain thus far: "+userProfiles.toString()); //added this in to see who has what on console, not necessary to system
					System.out.println("What is the sender's name?");
					String senderName = scan2.next();
					
					while (userProfiles.containsKey(senderName)==false) { //assures user chooses a sender that already has a profile
						System.out.println("Oops! That is not a user stored in our system! Please pick a user already stored.");
						System.out.println("What is the sender's name?");
						senderName = scan2.next();
					}
					
					if (userProfiles.containsKey(senderName)==true) { //checks to make sure sender is in hashMap
						System.out.println("Great! How much do you want to send?");
						int coins = scan2.nextInt();
			
						while ((int)userProfiles.get(senderName)<coins) { //assures sender has enough coins
							System.out.println("Whoops! You only have "+userProfiles.get(senderName)+" coins!");
							System.out.println("Please input a value equal to or less than your total coins.");
							coins = scan2.nextInt();
						}
						
						if ((int)userProfiles.get(senderName)>=coins) { //checks hashMap and only allows senders to send <= coins they have in profile
							System.out.println("Alrighty, who do you want to send "+coins+" coins to?");
							String receiverName = scan2.next();
							
							//converting timeStamp to string since I pass a string in my block constructor
							java.util.Date today = new java.util.Date();
							java.sql.Timestamp time = new java.sql.Timestamp(today.getTime());
							long timeLong = time.getTime();
							String timeNow = Long.toString(timeLong);
							
							//set nonce and hash to null so I can call the constructor, they are set after the nonce is generated
							String nonce=null;
							String hash=null;
							
							Block block = new Block(blocks.list.size(),timeNow,nonce,hash,senderName,receiverName,coins); //creates block
							blocks.add(block); //adds block to chain with properly generated nonce, hash, and previous hash
							blocks.validateBlockChain(); //validates blockChain with new block
						
						}
					}
					System.out.println("Would you like to make another transaction? (yes/no)");
					response = scan2.next();
				}
				System.out.println("What is the name of the file you want to print your chain to?");
				fileOut = scan2.next();
				blocks.toFile(fileOut); //prints to file with inputed name
				}
			System.out.println("Goodbye!");
			
		} catch (Exception FileNotFoundException) {
			System.out.println("Hey there buddy! No file by such name found!:(");
		}
	}
	
} 