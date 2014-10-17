package no.uio.inf5040.obl2.client.dao.spread;

import java.net.InetAddress;
import java.net.UnknownHostException;

import no.uio.inf5040.obl2.client.Account;
import no.uio.inf5040.obl2.client.dao.AccountReplicaDAO;
import no.uio.inf5040.obl2.client.dao.DAOException;
import spread.AdvancedMessageListener;
import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadGroup;
import spread.SpreadMessage;

public class AccountReplicaDAOImpl implements AccountReplicaDAO,
		AdvancedMessageListener {

	private static final String GROUP_NAME = "accountReplica";
	private int numReplicas;
	
	private Account account;

	private SpreadConnection connection;
	private SpreadGroup group;

	public AccountReplicaDAOImpl(String host, int port, String accountName,
			int numReplicas) throws DAOException {
		try {
			InetAddress server = InetAddress.getByName(host);
			connection = new SpreadConnection();
			connection.connect(server, port, accountName, false, true);

			group = new SpreadGroup();
			group.join(connection, GROUP_NAME);

			numReplicas = 0;
			account = new Account();
		} catch (SpreadException | UnknownHostException e) {
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
		
		switch(command) {
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
