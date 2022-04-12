package fra.uas.User.model;

import org.springframework.hateoas.RepresentationModel;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "name", "age", "balance", })
public class UserRespnseDTO extends RepresentationModel {

	private String name;
	private double balance;
	private int age;

	private User user;

	public UserRespnseDTO() {
		user = new User();
	}

	public UserRespnseDTO(User user) {
		this.name = user.getUsername();
		this.balance = user.getBalance();
		this.age = user.getAge();
		this.user = user;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return " [  " + user + " ]";
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

}
