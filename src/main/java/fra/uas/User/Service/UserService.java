package fra.uas.User.Service;

import java.util.ArrayList;

import fra.uas.User.model.User;

public interface UserService {

	void addUser(User user);

	void deleteUser(String UserID);

	public User getUserbyName(String name);

	ArrayList<User> getUserList();


	Boolean usernameExists(String name);

	public double depositMoney(User user, double amount);

	public User adjustUser(User user, User transferUser);

}
