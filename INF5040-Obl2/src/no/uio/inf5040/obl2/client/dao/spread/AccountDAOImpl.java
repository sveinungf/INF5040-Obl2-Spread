package no.uio.inf5040.obl2.client.dao.spread;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

import no.uio.inf5040.obl2.client.dao.AccountDAO;
import no.uio.inf5040.obl2.client.dao.DAOException;
import no.uio.inf5040.obl2.client.model.Account;
import spread.AdvancedMessageListener;
import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadGroup;
import spread.SpreadMessage;

public class AccountDAOImpl implements AccountDAO, AdvancedMessageListener {

	private static final String SEPARATOR = ":";
	private static final String ADDINTEREST = "addInterest";
	private static final String DEPOSIT = "deposit";
	private static final String WITHDRAW = "withdraw";

	private int numReplicas;
	private Account account;
	private SpreadConnection connection;
	private SpreadGroup group;

	public AccountDAOImpl(String host, int port, String accountName,
			int numReplicas) throws DAOException {

		Random r = new Random();
		String privateName = "c-" + r.nextInt(1000);

		try {
			InetAddress server = InetAddress.getByName(host);
			connection = new SpreadConnection();
			connection.connect(server, port, privateName, false, true);
			connection.add(this);

			group = new SpreadGroup();
			group.join(connection, accountName);

			account = new Account();
		} catch (SpreadException | UnknownHostException e) {
			throw new DAOException(e);
		}
	}

	@Override
	public void addInterest(double percent) throws DAOException {
		sendMessage(ADDINTEREST + SEPARATOR + percent);
	}

	@Override
	public double getBalance() {
		return account.getBalance();
	}

	@Override
	public void deposit(double amount) throws DAOException {
		sendMessage(DEPOSIT + SEPARATOR + amount);
	}

	@Override
	public void withdraw(double amount) throws DAOException {
		sendMessage(WITHDRAW + SEPARATOR + amount);
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
		String[] fields = new String(message.getData()).split(SEPARATOR);
		String command = fields[0];
		double argument = fields.length > 1 ? Double.parseDouble(fields[1])
				: 0.0;

		switch (command) {
		case DEPOSIT:
			account.deposit(argument);
			break;

		case WITHDRAW:
			account.withdraw(argument);
			break;

		case ADDINTEREST:
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
