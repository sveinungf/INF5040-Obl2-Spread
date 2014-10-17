package no.uio.inf5040.obl2.client.dao.spread;

import java.net.InetAddress;
import java.net.UnknownHostException;

import no.uio.inf5040.obl2.client.dao.AccountReplicaDAO;
import no.uio.inf5040.obl2.client.dao.DAOException;
import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadGroup;

public class AccountReplicaDAOImpl implements AccountReplicaDAO {

	private static final String GROUP_NAME = "accountReplica";

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
		} catch (SpreadException | UnknownHostException e) {
			throw new DAOException(e);
		}
	}
}
