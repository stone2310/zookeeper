package com.zk.dev.curator;

import java.util.List;
import java.util.concurrent.Executor;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

public class CuratorClientTestMe {
	private CuratorFramework client;
	
	public CuratorClientTestMe(){
		RetryPolicy retryPolicy=new ExponentialBackoffRetry(1000,3);
		client=CuratorFrameworkFactory.builder().connectString("192.168.3.71:2181")
				.sessionTimeoutMs(10000).retryPolicy(retryPolicy).connectionTimeoutMs(15000).namespace("base").build();
		client.start();
		
	}
	//创建节点
	public void creatNode(String path,String data) throws Exception{
		client.create().withMode(CreateMode.PERSISTENT).withACL(Ids.OPEN_ACL_UNSAFE).forPath(path, data.getBytes());
	}
	
	//修改节点
	public void setData(String path,int version,String data)throws Exception{
		client.setData().withVersion(version).forPath(path, data.getBytes());
	}
	
	//删除节点
	public void deleteNode(String path,int version)throws Exception{
		Executor arg1;
		client.delete().guaranteed().withVersion(version).inBackground(new BackgroundCallback() {
			
			@Override
			public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
				try {
					System.out.println("event.getStat():"+event.getStat()+" thread:"+Thread.currentThread().getName());
					System.out.println("event.getType():"+event.getType());
					System.out.println("event.getData():"+(event.getData()!=null?new String(event.getData()):""));
					System.out.println("event.getResultCode():"+event.getResultCode());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).forPath(path);
		System.out.println("删除成功");
		System.out.println("aaa");
	}
	
	//读取数据
	public void getData(String path)throws Exception{
		Stat stat=new Stat();
		byte[] forPath = client.getData().storingStatIn(stat).forPath(path);
		System.out.println(forPath!=null?new String(forPath):"");
		
	}
	
	//读取子节点,异步方式
	public void getChildren(String path)throws Exception{
		
		Stat stat=new Stat();
		List<String> forPath = client.getChildren().storingStatIn(stat).usingWatcher(new Watcher(){

			@Override
			public void process(WatchedEvent event) {
				System.out.println("path:"+event.getPath());
				System.out.println("type:"+event.getType());
				System.out.println("state:"+event.getState());
			}
		}).forPath(path);
		
		for(String str:forPath){
			System.out.println("str:"+str);
		}
		System.out.println("stat:"+stat);
	}
	
	public void closeClient(){
		if(client!=null){
			client.close();
		}
	}
	
	
	
	
	
	public static void main(String[] args) {
		CuratorClientTestMe cctm=new CuratorClientTestMe();
		try {
//			cctm.creatNode("/curator/test3", "curator");
			
//			cctm.deleteNode("/curator", 0);
//			cctm.setData("/curator",0,"newdata");
			cctm.getChildren("/curator");
			Thread.sleep(30000);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			cctm.closeClient();
		}
	}

}
