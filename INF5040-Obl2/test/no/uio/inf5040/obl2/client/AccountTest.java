package no.uio.inf5040.obl2.client;

import static org.junit.Assert.*;
import no.uio.inf5040.obl2.client.model.Account;

import org.junit.Test;

public class AccountTest {

	@Test
	public void addInterest() {
		Account account = new Account();
		account.setBalance(100.0);
		account.addInterest(7);
		
		assertEquals(107.0, account.getBalance(), 0.001);
	}
}
