package core.ipc.repeatClient.repeatPeerClient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import argo.jdom.JsonNode;

public class ResponseManager {

	private static final Logger LOGGER = Logger.getLogger(ResponseManager.class.getName());

	private Lock lockMaster;
	private Map<Long, Semaphore> locks;
	private Map<Long, Reply> receivedReply;

	ResponseManager() {
		lockMaster = new ReentrantLock();
		locks = new HashMap<>();
		receivedReply = new HashMap<>();
	}

	public Reply waitFor(long id, long timeoutMs) throws InterruptedException {
		Semaphore s = new Semaphore(0);
		try {
			lockMaster.lock();
			if (!locks.containsKey(id)) {
				locks.put(id, s);
			}
			s = locks.get(id);
		} finally {
			lockMaster.unlock();
		}

		if (!s.tryAcquire(timeoutMs, TimeUnit.MILLISECONDS)) {
			LOGGER.warning("Timed out waiting for response for message ID " + id + ".");
			return new Reply();
		}
		try {
			lockMaster.lock();
			Reply reply = receivedReply.remove(id);
			if (reply != null) {
				reply.timeout = false;
				return reply;
			}
			return new Reply();
		} finally {
			lockMaster.unlock();
		}
	}

	public void notifyFor(long id, Reply reply) {
		try {
			lockMaster.lock();
			receivedReply.put(id, reply);

			Semaphore s = locks.get(id);
			if (s == null) {
				s = new Semaphore(0);
				locks.put(id, s);
			}
			s.release();
		} finally {
			lockMaster.unlock();
		}
	}

	public static class Reply {
		private String status;
		private JsonNode message;
		private boolean timeout;

		private Reply() {
			timeout = true;
		}

		public static Reply of(String status, JsonNode message) {
			Reply reply = new Reply();
			reply.status = status;
			reply.message = message;
			return reply;
		}

		public String getStatus() {
			return status;
		}

		public JsonNode getMessage() {
			return message;
		}

		public boolean isTimeout() {
			return timeout;
		}
	}
}
