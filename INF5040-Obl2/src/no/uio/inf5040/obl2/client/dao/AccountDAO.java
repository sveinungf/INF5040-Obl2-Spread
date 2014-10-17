package no.uio.inf5040.obl2.client.dao;

public interface AccountDAO {

	void addInterest(double percent) throws DAOException;
	
	double getBalance() throws DAOException;
	
	void deposit(double amount) throws DAOException;
	
	void withdraw(double amount) throws DAOException;
}
