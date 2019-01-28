
public class Transaction {
	private String sender;
	private String receiver;
	private int amount;
	
	public Transaction(String sender, String receiver, int numberOfCoins) { //constructor 
		this.sender = sender;
		this.receiver = receiver;
		this.amount = numberOfCoins;
	}
	
	//getters and setters for Transaction properties
	
	public String getSender() { 
		return this.sender;
	}
	public void setSender(String newSender) {
		this.sender=newSender;
	}
	
	public String getReceiver() {
		return this.receiver;
	}
	public void setReceiver(String newReceiver) {
		this.receiver=newReceiver;
	}
	
	public int getAmount() {
		return this.amount;
	}
	public void setAmount(int newAmount) {
		this.amount=newAmount;
	}
	
	public String toString() {
		return sender + ":" + receiver + "=" + amount;
	}
}

