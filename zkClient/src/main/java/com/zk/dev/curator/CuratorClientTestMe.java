package com.zk.dev.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;

public class CuratorClientTestMe {
	private CuratorFramework client;
	
	public CuratorClientTestMe(){
		RetryPolicy retryPolicy=new ExponentialBackoffRetry(1000,3);
		client=CuratorFrameworkFactory.builder().connectString("192.168.145.128:2181,192.168.145.128:2182,192.168.145.128:2183")
				.sessionTimeoutMs(10000).retryPolicy(retryPolicy).connectionTimeoutMs(15000).namespace("base").build();
		client.start();
		
	}
	//创建节点
	public void creatNode(String path,String data) throws Exception{
		client.create().withMode(CreateMode.PERSISTENT).withACL(Ids.OPEN_ACL_UNSAFE).forPath(path, data.getBytes());
	}
	
	//修改节点
	
	//删除节点
	public void deleteNode(String path,int version)throws Exception{
		client.delete().guaranteed().withVersion(version).inBackground(new BackgroundCallback() {
			
			@Override
			public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
				try {
					System.out.println("event.getStat():"+event.getStat());
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
	
	//读取子节点
	
	public void closeClient(){
		if(client!=null){
			client.close();
		}
	}
	
	
	
	
	
	public static void main(String[] args) {
		CuratorClientTestMe cctm=new CuratorClientTestMe();
		try {
//			cctm.creatNode("/curator1", "curator1");
			
			cctm.deleteNode("/curator", 1);
			Thread.sleep(30000);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			cctm.closeClient();
		}
	}

}
