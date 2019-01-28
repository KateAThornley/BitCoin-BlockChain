import java.sql.Timestamp;

public class Block {
	private int index;
	private Timestamp timestamp; 
	private Transaction transaction;
	private String nonce;
	private String previousHash;
	private String hash;
	
	public Block (int index, String nowTimestamp, String nonce, String hash, String sender, String receiver,int numberOfCoins) { //constructor
		this.index = index;
		this.timestamp = new Timestamp(Long.parseLong(nowTimestamp));
		this.nonce = nonce;
		this.hash = hash;
		this.transaction = new Transaction(sender,receiver,numberOfCoins);
		
	}
	
	//getters and setters for Block properties
	
	public int getIndex() {
		return this.index;
	}
	public void setIndex(int newIndex) {
		this.index=newIndex;
	}	
	public Timestamp getTimestamp() {
		return this.timestamp;
	}
	public void setTimestamp(String newTimestamp) {
		this.timestamp = new Timestamp(Long.parseLong(newTimestamp));
	}
	
	public String getNonce() {
		return this.nonce;
	}
	public void setNonce(String newNonce) {
		this.nonce=newNonce;
	}
	
	public String getHash() {
		return this.hash;
	}
	public void setHash(String newHash) {
		this.hash=newHash;
	}
	
	public Transaction getTransaction() {
		return this.transaction;
	}
	public void setTransaction(String newSender, String newReceiver, int newAmount) {
		this.transaction=new Transaction(newSender, newReceiver, newAmount);
	}

	public String getPreviousHash() {
		return this.previousHash;
	}
	public void setPreviousHash(String newPreviousHash) {
		this.previousHash=newPreviousHash;
	}
		
	public String toString() {
		return timestamp.toString() + ":" + transaction.toString() + "." + nonce + previousHash;
	}
	
	
}