package no.uio.inf5040.obl2.client;

/**
 * Simple class for making the main thread wait until
 * the required amount of replicas are present
 *
 */
public class AccountReplicaMonitor {

	/**
	 * Waits on this object
	 */
	public synchronized void doWait() throws InterruptedException {
		wait();
	}

	/**
	 * Notifies thread waiting on this object
	 */
	public synchronized void doNotify() {
		notify();
	}
}
