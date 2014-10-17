package no.uio.inf5040.obl2.client;

public class MessageHandler {

	public MessageHandler() {
	}

	public double handleInput(String command, double value, Account account) {
		switch (command) {
		case "balance":
			System.out.println("");
			break;

		case "deposit":
			/*
			 * This command causes the balance to increase by <amount>. This
			 * increase should be performed on all the replicas in the group.
			 */
			account.deposit(value);
			// spread.sendMessage(update balance) - pseudo
			break;

		case "withdraw":
			/*
			 * This command causes the balance to decrease by <amount>. This
			 * decrease should be performed on all the replicas in the group.
			 */
			account.withdraw(value);
			// spread.sendMessage(update balance) - pseudo
			break;

		case "addInterest":
			/*
			 * This command causes the balance to increase by <percent> percent
			 * of the current value. In other words, the balance should be
			 * multiplied by (1 + <percent>/100). This update should be
			 * performed on all the replicas in the group.
			 */
			account.addInterest(value);
			// spread.sendMessage(update balance) - pseudo
			break;

		case "sleep":
			/*
			 * This command causes the client to do nothing for <duration>
			 * seconds. It is only useful in a batch file
			 */
			try {
				Thread.sleep((long) value);
			} catch (InterruptedException e) {
				System.err.println("Exception: " + e.getMessage());
				e.printStackTrace();
			}
			break;

		}

		return account.getBalance();
	}
}
