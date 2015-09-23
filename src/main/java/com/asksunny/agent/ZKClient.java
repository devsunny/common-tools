package com.asksunny.agent;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class ZKClient implements Watcher {

	public ZKClient() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void process(WatchedEvent event) {
		String path = event.getPath();
		System.out.println(event.getType());
		System.out.println(path);
		if (event.getType() == Event.EventType.None) {
			// We are are being told that the state of the
			// connection has changed
			switch (event.getState()) {
			case SyncConnected:
				System.out.println("SyncConnected");
				break;
			case Disconnected:
				System.out.println("Disconnected");
				break;
			case Expired:
				System.out.println("Expired");
				break;
			}
		} else if (event.getType() == Event.EventType.NodeDataChanged) {
			System.out.println(String.format("Data changed:%s", event.getPath()));			
		} else {
			
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
