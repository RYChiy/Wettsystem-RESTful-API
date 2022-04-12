package fra.uas.User.model;

import java.time.LocalDate;
import java.time.Period;
import com.fasterxml.jackson.annotation.JsonFormat;


public class User {
	private String username;
	private String firstName;
	private String lastName;
	private double balance;
	private Role role;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate birthday;

	private int age;

	// Full Constructor
	public User(String username, String firstName, String lastName, double balance, LocalDate birthday) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthday = birthday;
		this.balance = balance;

		this.age = getAge();
		this.role = Role.USER;

	}

	public User(String username, String firstName, String lastName, double balance, int age) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.balance = balance;
		this.age = age;
		this.role = Role.USER;

	}

	// Constructor only including names
	public User(String username, String firstName, String lastName) {
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.role = Role.USER;

	}

	// Constructor only including the username
	public User(String username) {
		this.username = username;
		this.role = Role.USER;

	}


	public User() {

	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public LocalDate getBirthday() {
		return birthday;
	}

	public void setBirthday(LocalDate birthday) {
		this.birthday = birthday;
		LocalDate currentDate = LocalDate.now();
		this.age = Period.between(birthday, currentDate).getYears();
	}

	public int getAge() {

		LocalDate currentDate = LocalDate.now();
		return Period.between(birthday, currentDate).getYears();
	}

	public void setAge(int age) {

		this.age = age;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Role getRole() {
		return role;
	}


	public void setRole(Role role) {
		this.role = role;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	@Override
	public String toString() {
		String userFormat;
		userFormat = "Username: " + username + ", Age: "
				+ age + ", Balance: " + balance + ", Role: " + role;
		return userFormat;
	}

}
