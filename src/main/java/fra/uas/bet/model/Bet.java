package fra.uas.bet.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "id"," eventName","username", "state", "quote","decision","amount","amountwon"})
public class Bet {

	private int id;//
	private String username;//
	private double amount;//
	private double quote;//
	private String eventName;//
	private String decision;//
	private State state;//
	private double amountWon;//

	public Bet(String username, double amount, double quote, String eventName, String decision) {

		this.username = username;
		this.amount = amount;
		this.quote = quote;
		this.eventName = eventName;
		this.decision = decision;
		this.state = State.OPEN;
		this.amountWon = 0;
	}

	public Bet() {
		this.state = State.OPEN;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getQuote() {
		return quote;
	}

	public void setQuote(double quote) {
		this.quote = quote;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getDecision() {
		return decision;
	}

	public void setDecision(String decision) {
		this.decision = decision;
	}

	@Override
	public String toString() {
		String r;
		r = "Username: " + username + ", Event: " + eventName + ", Amount: " + amount + ", Decision: " + decision
				+ ", State: " + state + ", ID: " + id;
		return r;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public double getAmountWon() {
		return amountWon;
	}

	public void setAmountWon(double amountWon) {
		this.amountWon = amountWon;
	}

	public int getID() {
		return id;
	}

	public void setID(int id2) {
		id = id2;
	}

}
