package com.yh.example;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.zookeeper.AsyncCallback.Children2Callback;
import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.AsyncCallback.VoidCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Perms;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;

public class ZKOpSample {
	
	private ZooKeeper zk=null;
	
	public ZKOpSample(String path){
		try {
			zk=new ZooKeeper(path,1000,null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public ZooKeeper getZk() {
		return zk;
	}


	public void setZk(ZooKeeper zk) {
		this.zk = zk;
	}


	/**
	 * 同步创建
	 * @param path
	 * @param data
	 * @param acl
	 * @param createMode
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void testCreateNode(String path,byte[] data,List<ACL> acl,
            CreateMode createMode) throws KeeperException, InterruptedException{
		
		String create = zk.create(path, data, acl, createMode);
		System.out.println("create:"+create);
		
	}
	/**
	 * 异步创建
	 * @param path
	 * @param data
	 * @param acl
	 * @param createMode
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void testCreateNodeSync(String path,byte[] data,List<ACL> acl,
            CreateMode createMode) throws KeeperException, InterruptedException{
		
		zk.create(path,data,acl,createMode,new StringCallback() {
			
			@Override
			public void processResult(int rc, String path, Object ctx, String name) {
				System.out.println("rc:"+rc);
				System.out.println("path:"+path);
				System.out.println("ctx:"+ctx);
				System.out.println("name:"+name);
				
			}
		},"abc");
		
	}
	
	public void testDeleteNode(String path, int version) throws InterruptedException, KeeperException{
		zk.delete(path, version);
	}
	
	public void testDeleteNodeSync(String path, int version){
		zk.delete(path, version,new  VoidCallback() {
			
			@Override
			public void processResult(int rc, String path, Object ctx) {
				// TODO Auto-generated method stub
				System.out.println("rc："+rc);
				System.out.println("path:"+path);
				System.out.println("ctx:"+ctx);
				
			}
		},"上下文信息");
	}
	
	public List<ACL> getDigestAcl() {
		Id id=new Id("digest","zyh1:l0KBPQRLG1d/1LmmghuNB57dws8=");
		ACL acl=new ACL(Perms.ALL, id);
		return Collections.singletonList(acl);
	}
	public List<ACL> getAuthAcl() {
		Id id=new Id("auth","");
		ACL acl=new ACL(Perms.ALL, id);
		return Collections.singletonList(acl);
	}
	//同步方式
	public List<String> getChildren(String path, Watcher watcher) throws KeeperException, InterruptedException{
		List<String> children = zk.getChildren(path, watcher);
		return children;
		
	}
	
	//异步方式
	public void getChildrenSync(String path, Watcher watcher) throws KeeperException, InterruptedException{
		zk.getChildren(path, watcher, new Children2Callback() {
			
			@Override
			public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
				System.out.println("rc:"+rc);
				System.out.println("path:"+path);
				System.out.println("ctx"+ctx);
				for(String str:children){
					System.out.println("str:"+str);
				}
				System.out.println("stat:"+stat);
				
			}
		}, "上下文信息");
		
		
	}
	
	//异步方式获取数据
	public void getData(String path, Watcher watcher){
		zk.getData(path,watcher,
           new  DataCallback() {
			
			@Override
			public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
				System.out.println("rc:"+rc);
				System.out.println("path:"+path);
				System.out.println("ctx:"+ctx);
				System.out.println("data:"+new String(data));
				System.out.println("stat:"+stat);
			}
		}, "上下文信息");
	}
	
	//异步方式修改数据
	public void setData(String path, byte data[], int version){
		zk.setData(path, data, version,new StatCallback() {
			
			@Override
			public void processResult(int rc, String path, Object ctx, Stat stat) {
				System.out.println("rc:"+rc);
				System.out.println("path:"+path);
				System.out.println("ctx:"+ctx);
				System.out.println("stat:"+stat);
			}
		},"上下文信息");
	}
	
	//判断节点是否存在
	public void exists(String path, Watcher watcher){
		zk.exists(path, watcher, new StatCallback() {
			
			@Override
			public void processResult(int rc, String path, Object ctx, Stat stat) {
				System.out.println("rc:"+rc);
				System.out.println("path:"+path);
				System.out.println("ctx:"+ctx);
				System.out.println("stat:"+stat);
				
			}
		}, "上下文信息");
	}
	
	public static void main(String[] args)throws Exception {
		
		ZKOpSample zks=new ZKOpSample("192.168.145.128:2181");
		
//		zks.getZk().addAuthInfo("digest", "zyh1:111111".getBytes());
//		zks.testCreateNode("/javaclient/digestNode", "aacldata".getBytes(), zks.getAuthAcl(),CreateMode.PERSISTENT);
//		System.out.println("创建成功");
		
//		zks.testDeleteNodeSync("/javaclient/digestNode", 0);
//		System.out.println("删除成功");
		
//		List<String> children = zks.getChildren("/javaclient", null);
//		for(String str:children){
//			System.out.println("str:"+str);
//		}
		
//		zks.getChildrenSync("/javaclient", null);
		
//		zks.getData("/javaclient/syncNode", null);
		
//		zks.setData("/javaclient/syncNode", "syncNode1".getBytes(), 1);
		
		zks.exists("/javaclient/syncNode", null);
		
		
		Thread.sleep(3000);
		
	}

}
