package model;

public class Admin extends User{

	public Admin(String id, String fullName, String email, String password, String phoneNumber, String address,
			String gender, String role) {
		super(id, fullName, email, password, phoneNumber, address, gender, role);
		// TODO Auto-generated constructor stub
		role = "ADMIN";
	}

}
