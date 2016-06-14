package com.yh.example;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

public class ZKConnectSample implements Watcher{
	
	public final static String path="192.168.145.128:2181";
	
	public final static CountDownLatch cd=new CountDownLatch(1);

	@Override
	public void process(WatchedEvent event) {
		// TODO Auto-generated method stub
		System.out.println("事件状态："+event.getState());
		if(KeeperState.SyncConnected==event.getState())
		cd.countDown();
		
	}
	
	public static void main(String[] args) {
		ZKConnectSample zks=new ZKConnectSample();
		try {
			ZooKeeper zk=new ZooKeeper(path, 1000, zks);
			System.out.println(zk.getState());
			cd.await();
			System.out.println(zk.getState());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}
