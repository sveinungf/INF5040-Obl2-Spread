package no.uio.inf5040.obl2.client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import no.uio.inf5040.obl2.client.dao.AccountDAO;
import no.uio.inf5040.obl2.client.dao.DAOException;
import no.uio.inf5040.obl2.client.dao.spread.AccountDAOImpl;

public class AccountReplicaClient {

	private static final int ARG_SERVER_ADDRESS = 0;
	private static final int ARG_ACCOUNT = 1;
	private static final int ARG_REPLICAS = 2;
	private static final int ARG_FILENAME = 3;

	private static final String EXIT = "exit";
	private static final String SLEEP = "sleep";

	private AccountDAO accountDAO;

	public AccountReplicaClient(String host, int port, String accountName,
			int numReplicas) throws DAOException {

		accountDAO = new AccountDAOImpl(host, port, accountName,
				numReplicas);
	}

	private void readFrom(Reader in) throws IOException {
		BufferedReader br = new BufferedReader(in);

		String input;

		while ((input = br.readLine()) != null) {

			String[] fields = input.split("\\s+");
			String command = fields[0];
			double argument = fields.length > 1 ? Double.parseDouble(fields[1])
					: 0.0;

			if (EXIT.equals(command)) {
				break;
			}

			else if (SLEEP.equals(command)) {
				try {
					Thread.sleep((long) argument);
				} catch (InterruptedException e) {
					System.err.println("Exception e: " + e.getMessage());
					e.printStackTrace();
				}
			}

			else {
				handleInput(command, argument);
				// do stuff
			}
		}
	}

	public void handleInput(String command, double value) {
		try {
			switch (command) {
			case "balance":
				System.out.println("Current balance: "
						+ accountDAO.getBalance());
				break;

			case "deposit":
				accountDAO.deposit(value);
				System.out.println("Deposited " + value + " to account");
				break;

			case "withdraw":
				accountDAO.withdraw(value);
				System.out.println("Withdrew " + value + " from account");
				break;

			case "addInterest":
				accountDAO.addInterest(value);
				System.out.println("Added " + value + " percent interest to account");
				break;
			}
		} catch (Exception e) {
			System.err.println("Exception: " + e.getMessage());
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		if (args == null || args.length <= ARG_REPLICAS) {
			System.out.println("Usage: java -jar client.jar <server address> "
					+ "<account name> <number of replicas> [file name]");
			return;
		}

		String[] serverFields = args[ARG_SERVER_ADDRESS].split(":");

		if (serverFields.length != 2) {
			System.out.println("Error: Server address must be hostname:port");
			return;
		}

		String host = serverFields[0];
		int port = Integer.parseInt(serverFields[1]);
		String accountName = args[ARG_ACCOUNT];
		int numReplicas = Integer.parseInt(args[ARG_REPLICAS]);
		String filename = args.length > ARG_FILENAME ? args[ARG_FILENAME]
				: null;

		AccountReplicaClient client;
		try {
			client = new AccountReplicaClient(host, port, accountName,
					numReplicas);
		} catch (DAOException e) {
			e.printStackTrace();
			return;
		}

		Reader in;

		if (filename == null) {
			System.out.println("Enter a command, or \"exit\" to exit.");
			in = new InputStreamReader(System.in);
		} else {
			try {
				in = new FileReader(filename);
			} catch (FileNotFoundException e) {
				System.out.println("Error: File " + filename + " not found.");
				return;
			}
		}

		try {
			client.readFrom(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
