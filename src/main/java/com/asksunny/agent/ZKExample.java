package com.asksunny.agent;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.AsyncCallback.StringCallback;

public class ZKExample {

	public ZKExample() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		 
		ZKClient client = new ZKClient();
		ZooKeeper zk  = new ZooKeeper("localhost:2181", 1000, client);
		boolean created = false ;
		 for(;;){
			// ..zk.create(path, data, acl, createMode, cb, ctx)
			 if(created==false){
				 zk.create("/mydata", "".getBytes(),  Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				 created = true;
			 }			 
			 try {
				Thread.sleep(1000);
			} catch (Exception e) {
				;
			}
		 }
	}

}
