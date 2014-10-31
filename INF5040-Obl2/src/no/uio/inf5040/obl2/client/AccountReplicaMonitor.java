package no.uio.inf5040.obl2.client;

public class AccountReplicaMonitor {

	public synchronized void doWait() throws InterruptedException {
		wait();
	}

	public synchronized void doNotify() {
		notify();
	}
}
