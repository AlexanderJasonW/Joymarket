package model;

import java.util.ArrayList;
import java.util.List;

public class Customer extends User{
	
	private double balance;
	private List<Product> cart;

	public Customer(String id, String fullName, String email, String password, String phoneNumber, String address,
			String gender, String role) {
		super(id, fullName, email, password, phoneNumber, address, gender, role);
		// TODO Auto-generated constructor stub
		this.balance = 0.0;
        this.cart = new ArrayList<>();
		role = "CUSTOMER";
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public List<Product> getCart() {
		return cart;
	}

	public void setCart(List<Product> cart) {
		this.cart = cart;
	}
	
	
	
}
