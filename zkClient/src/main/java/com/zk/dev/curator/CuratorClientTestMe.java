package com.zk.dev.curator;

import java.util.List;
import java.util.concurrent.Executor;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type;
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
		System.out.println("创建"+path+"成功");
	}
	
	//修改节点
	public void setData(String path,int version,String data)throws Exception{
		client.setData().withVersion(version).forPath(path, data.getBytes());
		System.out.println("修改"+path+"成功");
	}
	
	//删除节点
	public void deleteNode(String path,int version)throws Exception{
		Executor arg1;
		client.delete().guaranteed().withVersion(version).inBackground(new BackgroundCallback() {
			
			@Override
			public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
				try {
					System.out.println("删除event.getStat():"+event.getStat()+" thread:"+Thread.currentThread().getName());
					System.out.println("删除event.getType():"+event.getType());
					System.out.println("删除event.getData():"+(event.getData()!=null?new String(event.getData()):""));
					System.out.println("删除event.getResultCode():"+event.getResultCode());
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
	
	//给节点添加watch
	//创建节点、删除节点、修改节点
	public void addWatch(String path)throws Exception{
		final NodeCache nc=new NodeCache(client, path);
		nc.start(true);
		nc.getListenable().addListener(new NodeCacheListener() {
			
			@Override
			public void nodeChanged() throws Exception {
				try {
					if(nc.getCurrentData()!=null){
						System.out.println("监听data:"+(nc.getCurrentData())!=null?(nc.getCurrentData().getData()):"");
						System.out.println("监听path:"+nc.getCurrentData().getPath());
						System.out.println("监听sata:"+nc.getCurrentData().getStat());
					}else{
						System.out.println("监听到删除操作");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	//子节点新增、变更、删除
	public void addChildWatch(String path)throws Exception{
		PathChildrenCache pcc=new PathChildrenCache(client, path, true);
		pcc.start(StartMode.POST_INITIALIZED_EVENT);
		System.out.println(pcc.getCurrentData().size());
		pcc.getListenable().addListener(new PathChildrenCacheListener() {

			@Override
			public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
				switch(event.getType()){
				case INITIALIZED:
					System.out.println("INITIALIZED"+Type.INITIALIZED);
					System.out.println("节点初始化完成");
					break;
				case CHILD_ADDED:
					System.out.println("CHILD_ADDED"+Type.CHILD_ADDED);
					System.out.println("新增子节点："+event.getData().getPath());
					break;
				case CHILD_UPDATED:
					System.out.println("CHILD_ADDED"+Type.CHILD_UPDATED);
					System.out.println("修改子节点："+event.getData().getPath());
					break;
				case CHILD_REMOVED:
					System.out.println("CHILD_ADDED"+Type.CHILD_REMOVED);
					System.out.println("删除子节点："+event.getData().getPath());
					break;
				default:
					break;
				}
			}
			
		});
	}
	
	public void closeClient(){
		if(client!=null){
			client.close();
		}
	}
	
	
	
	
	
	public static void main(String[] args) {
		CuratorClientTestMe cctm=new CuratorClientTestMe();
		try {
			cctm.addChildWatch("/curator");
			Thread.sleep(2000);
//			cctm.creatNode("/curator/test2", "test2");
			Thread.sleep(2000);
			cctm.setData("/curator/test2", 0, "test2new");
			Thread.sleep(2000);
			cctm.deleteNode("/curator/test2", 1);
			
			
			
			
//			cctm.deleteNode("/curator", 0);
//			cctm.setData("/curator",0,"newdata");
//			cctm.getChildren("/curator");
			Thread.sleep(30000);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			cctm.closeClient();
		}
	}

}
