package no.uio.inf5040.obl2.client.dao.spread;

import java.net.InetAddress;
import java.net.UnknownHostException;

import no.uio.inf5040.obl2.client.dao.AccountDAO;
import no.uio.inf5040.obl2.client.dao.DAOException;
import no.uio.inf5040.obl2.client.model.Account;
import spread.AdvancedMessageListener;
import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadGroup;
import spread.SpreadMessage;

public class AccountDAOImpl implements AccountDAO,
		AdvancedMessageListener {

	private int numReplicas;
	private Account account;
	private SpreadConnection connection;
	private SpreadGroup group;

	public AccountDAOImpl(String host, int port, String accountName,
			int numReplicas) throws DAOException {

		String privateName = "privateName";

		try {
			InetAddress server = InetAddress.getByName(host);
			connection = new SpreadConnection();
			connection.connect(server, port, privateName, false, true);

			group = new SpreadGroup();
			group.join(connection, accountName);

			numReplicas = 0;
			account = new Account();
		} catch (SpreadException | UnknownHostException e) {
			throw new DAOException(e);
		}
	}

	@Override
	public void addInterest(double percent) throws DAOException {
		sendMessage("addInterest\t" + percent);
	}

	@Override
	public double getBalance() {
		return account.getBalance();
	}

	@Override
	public void deposit(double amount) throws DAOException {
		sendMessage("deposit\t" + amount);
	}

	@Override
	public void withdraw(double amount) throws DAOException {
		sendMessage("withdraw\t" + amount);
	}

	private void sendMessage(String text) throws DAOException {
		SpreadMessage message = new SpreadMessage();

		message.setData(text.getBytes());
		message.addGroup(group);
		message.setAgreed(); // TODO check this

		try {
			connection.multicast(message);
		} catch (SpreadException e) {
			throw new DAOException(e);
		}
	}

	@Override
	public void membershipMessageReceived(SpreadMessage message) {
		System.out.println("New membership message - number of members:"
				+ message.getMembershipInfo().getMembers().length);

		numReplicas = message.getMembershipInfo().getMembers().length;
	}

	@Override
	public void regularMessageReceived(SpreadMessage message) {
		String[] fields = new String(message.getData()).split("\\s+");
		String command = fields[0];
		double argument = fields.length > 1 ? Double.parseDouble(fields[1])
				: 0.0;

		switch (command) {
		case "deposit":
			account.deposit(argument);
			break;

		case "withdraw":
			account.withdraw(argument);
			break;

		case "addInterest":
			account.addInterest(argument);
			break;
		}
	}

	/**
	 * @return
	 */
	public int getNumReplicas() {
		return numReplicas;
	}
}
