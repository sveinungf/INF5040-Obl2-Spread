package no.uio.inf5040.obl2.client.dao.spread;

/**
 * An enumeration of the different service types available to messages in a
 * Spread network.
 */
public enum ServiceType {
	UNRELIABLE,
	RELIABLE,
	FIFO,
	CAUSAL,
	AGREED,
	SAFE
}
