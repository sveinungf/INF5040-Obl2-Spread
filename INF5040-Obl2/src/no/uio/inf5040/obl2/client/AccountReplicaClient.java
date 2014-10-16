package no.uio.inf5040.obl2.client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadGroup;
import spread.SpreadMessage;

public class AccountReplicaClient {

	public SpreadConnection connection;
	public String connName;

	public void init() {
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(100);
		connName = "CliNum-" + randomInt;

		connection = new SpreadConnection();
		try {
			connection.connect(InetAddress.getByName("127.0.0.1"), 4803,
					connName, false, false);

			SpreadGroup group = new SpreadGroup();
			group.join(connection, "testGroup");

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SpreadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void sendMessage(String text) {
		SpreadMessage message = new SpreadMessage();

		message.setData(text.getBytes());
		message.addGroup("testGroup");
		message.setReliable();

		try {
			connection.multicast(message);
		} catch (SpreadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		System.out.println("Starting Client...");
		AccountReplicaClient client = new AccountReplicaClient();
		client.init();
		System.out.println("Sending messages...");
		try {
			Thread.sleep(2000);
			client.sendMessage("Hello form client: " + client.connName);
			Thread.sleep(50000);
			client.sendMessage("Message 2.");
			Thread.sleep(6000);
			client.sendMessage("Message 3.");
			Thread.sleep(10000);
			client.sendMessage("Last Message.");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
