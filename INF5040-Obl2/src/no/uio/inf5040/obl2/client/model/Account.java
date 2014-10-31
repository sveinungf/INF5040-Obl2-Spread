package no.uio.inf5040.obl2.client.model;

public class Account {

	private double balance;

	public Account() {
		balance = 0.0;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public void deposit(double amount) {
		balance += amount;
	}

	public void withdraw(double amount) {
		balance -= amount;
	}

	public void addInterest(double percent) {
		balance *= 1 + percent / 100;
	}
}
