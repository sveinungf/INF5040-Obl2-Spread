package no.uio.inf5040.obl2.client.dao;

/**
 * A Data Access Object for a bank account.
 */
public interface AccountDAO {

	/**
	 * Multiplies the current balance with the given percent.
	 * 
	 * @param percent
	 *            - the percent to multiply with.
	 * @throws DAOException
	 */
	void addInterest(double percent) throws DAOException;

	/**
	 * Gets the balance from the account.
	 * 
	 * @return the balance.
	 * @throws DAOException
	 */
	double getBalance() throws DAOException;

	/**
	 * Adds {@code amount} to the current balance.
	 * 
	 * @param amount
	 *            - the amount to add.
	 * @throws DAOException
	 */
	void deposit(double amount) throws DAOException;

	/**
	 * Subtracts {@code amount} from the current balance.
	 * 
	 * @param amount
	 *            - the amount to subtract.
	 * @throws DAOException
	 */
	void withdraw(double amount) throws DAOException;
}
