package no.uio.inf5040.obl2.client.dao.spread;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

import no.uio.inf5040.obl2.client.AccountReplicaMonitor;
import no.uio.inf5040.obl2.client.dao.AccountDAO;
import no.uio.inf5040.obl2.client.dao.DAOException;
import no.uio.inf5040.obl2.client.model.Account;
import spread.AdvancedMessageListener;
import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadGroup;
import spread.SpreadMessage;

/**
 * An implementation for handling a bank account using replicas in a Spread
 * network.
 */
public class AccountDAOImpl implements AccountDAO, AdvancedMessageListener {

	private static final String SEPARATOR = ":";
	private static final String SETBALANCE = "setBalance";
	private static final String ADDINTEREST = "addInterest";
	private static final String DEPOSIT = "deposit";
	private static final String WITHDRAW = "withdraw";

	private boolean started;
	private AccountReplicaMonitor monitor;

	private int requiredReplicas, currentReplicas;
	private Account account;
	private SpreadConnection connection;
	private SpreadGroup group;

	/**
	 * Connects to a Spread group, and blocks until the number of replicas in
	 * the group has reached the required amount of replicas.
	 * 
	 * @param host
	 *            - Spread daemon host.
	 * @param port
	 *            - Spread daemon port.
	 * @param accountName
	 *            - The name of the Spread group.
	 * @param requiredReplicas
	 *            - The amount of replicas required.
	 * @throws DAOException
	 */
	public AccountDAOImpl(String host, int port, String accountName,
			int requiredReplicas) throws DAOException {

		Random r = new Random();
		String privateName = "c-" + r.nextInt(1000);

		started = false;
		monitor = new AccountReplicaMonitor();

		this.requiredReplicas = requiredReplicas;
		currentReplicas = 0;
		account = new Account();

		try {
			InetAddress server = InetAddress.getByName(host);
			connection = new SpreadConnection();
			connection.connect(server, port, privateName, false, true);
			connection.add(this);

			group = new SpreadGroup();
			group.join(connection, accountName);

			monitor.doWait();
		} catch (SpreadException | UnknownHostException | InterruptedException e) {
			throw new DAOException(e);
		}
	}

	@Override
	public void addInterest(double percent) throws DAOException {
		sendMessage(ADDINTEREST + SEPARATOR + percent, ServiceType.AGREED);
	}

	@Override
	public double getBalance() {
		return account.getBalance();
	}

	@Override
	public void deposit(double amount) throws DAOException {
		sendMessage(DEPOSIT + SEPARATOR + amount, ServiceType.AGREED);
	}

	@Override
	public void withdraw(double amount) throws DAOException {
		sendMessage(WITHDRAW + SEPARATOR + amount, ServiceType.AGREED);
	}

	/**
	 * Multicasts a message to all clients in the group.
	 * 
	 * @param text
	 *            - The message text.
	 * @param serviceType
	 *            - The message service type.
	 * @throws DAOException
	 */
	private void sendMessage(String text, ServiceType serviceType)
			throws DAOException {
		SpreadMessage message = new SpreadMessage();

		message.setData(text.getBytes());
		message.addGroup(group);

		switch (serviceType) {
		case AGREED:
			message.setAgreed();
			break;
		case CAUSAL:
			message.setCausal();
			break;
		case FIFO:
			message.setFifo();
			break;
		case RELIABLE:
			message.setReliable();
			break;
		case SAFE:
			message.setSafe();
			break;
		case UNRELIABLE:
			message.setUnreliable();
			break;
		}

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

		int numMembers = message.getMembershipInfo().getMembers().length;

		if (numMembers > currentReplicas && started) {
			try {
				sendMessage(SETBALANCE + SEPARATOR + account.getBalance(),
						ServiceType.AGREED);
			} catch (DAOException e) {
				e.printStackTrace();
			}
		}

		if (numMembers >= requiredReplicas && !started) {
			monitor.doNotify();
			started = true;
		}

		currentReplicas = numMembers;
	}

	@Override
	public void regularMessageReceived(SpreadMessage message) {
		String[] fields = new String(message.getData()).split(SEPARATOR);
		String command = fields[0];
		double argument = fields.length > 1 ? Double.parseDouble(fields[1])
				: 0.0;

		switch (command) {
		case SETBALANCE:
			account.setBalance(argument);
			break;

		case DEPOSIT:
			account.deposit(argument);
			System.out.println(argument + " deposited to account");
			break;

		case WITHDRAW:
			account.withdraw(argument);
			System.out.println(argument + " withdrawn from account");
			break;

		case ADDINTEREST:
			account.addInterest(argument);
			System.out.println(argument + " percent interest added to account");
			break;
		}
	}
}
