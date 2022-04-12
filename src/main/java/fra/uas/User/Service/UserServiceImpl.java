package fra.uas.User.Service;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import fra.uas.User.Repository.UserRepository;
import fra.uas.User.model.User;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository = new UserRepository();

	// Add a User 
	@Override
	public void addUser(User user) {
		userRepository.userList.add(user);
		System.out.println("new User " + user.getFirstName() + " created!");
	}

	// Delete a User
	@Override
	public void deleteUser(String username) {
		for (int i = 0; i < userRepository.userList.size(); i++) {
			if (userRepository.userList.get(i).getUsername().equals(username)) {
				userRepository.userList.remove(i);
			}
		}

	}

	// Return the userRepository
	@Override
	public ArrayList<User> getUserList() {

		return userRepository.userList;
	}

	public User getUserbyName(String name) {
		for (int i = 0; i < userRepository.userList.size(); i++) {
			if (name.equals(userRepository.userList.get(i).getUsername())) {
				// System.out.println("We found " + name + "!");
				return userRepository.userList.get(i);
			}
		}
		System.out.println("No user found with the name " + name + "!");
		return null;

	}

	// Deposit money on an account
	@Override
	public double depositMoney(User user, double amount) {

		double newBalance = user.getBalance() + amount;

		user.setBalance(newBalance);

		return user.getBalance();
	}

	// Check if an user exists
	@Override
	public Boolean usernameExists(String name) {
		for (int i = 0; i < userRepository.userList.size(); i++) {
			if (userRepository.userList.get(i).getUsername().toLowerCase().equals(name.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	// Update certain Informations for a User
	@Override
	public User adjustUser(User user, User transferUser) {
		if (transferUser.getBirthday() != null) {
			user.setBirthday(transferUser.getBirthday());
			user.setAge(user.getAge());
		}
		if (transferUser.getFirstName() != null) {
			user.setFirstName(transferUser.getFirstName());
		}
		if (transferUser.getLastName() != null) {
			user.setLastName(transferUser.getLastName());
		}
		if (transferUser.getUsername() != null) {
			user.setUsername(transferUser.getUsername());
		}

		return user;
	}

}
