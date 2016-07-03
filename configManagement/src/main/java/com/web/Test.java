package com.web;

import java.util.Calendar;
import java.util.Date;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.web.service.OrderRepository;
import com.web.service.OrderService;
import com.web.service.ProductRepository;
import com.web.service.pojo.Order;
import com.web.service.pojo.Product;
import com.web.util.LockUtil;

public class Test {
    private static ApplicationContext ctx;  
    
    static 
    {  
        ctx = new ClassPathXmlApplicationContext("applicationContext.xml");  
    }        
    
    public static void testpurchase(){
    	OrderService os = (OrderService)ctx.getBean("orderService"); 
    	
        Order order = new Order();
        order.setProductId(1L);
        order.setCreateTime(new Date());
        order.setPnum(1);
        os.doOrder(order);
    }
    
    public static void testMapper(){
    	ProductRepository mapper = (ProductRepository)ctx.getBean("productMapper"); 
        //测试id=1的用户查询，根据数据库中的情况，可以改成你自己的.
        System.out.println("得到用户id=1的用户信息");
        Product product = mapper.selectProductById(1L);
        System.out.println(product.getName()); 
        
        OrderRepository omapper = (OrderRepository)ctx.getBean("orderMapper"); 
        Order order = new Order();
        order.setProductId(1L);
        order.setCreateTime(new Date());
        order.setPnum(1);
        omapper.saveOrder(order);   	
    }
    
    public static void testShardLog(int type,String identity){
    	System.out.println("---------------开始获取锁"+identity);
    	LockUtil.init("192.168.3.71:2181");
    	LockUtil.getShardLock(type, identity);
    	System.out.println("---------------获取锁结束"+identity);
    }
    public static void main(String[] args)  
    {  
    	//共享锁
    	int type = Integer.parseInt(args[0]);
    	System.out.println("type="+type);
    	testShardLog(type,args[1]);
//    	testShardLog(0,"f6");
//    	try {
//    		LockUtil.init("192.168.3.71:2181");
//			//LockUtil.addChildWatcher("/LockService");
//    		LockUtil.getExclusiveLock();
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
    	//排他锁
    	/*long nowtime = Calendar.getInstance().getTimeInMillis();
    	System.out.println("begin with "+nowtime);
    	testpurchase();
    	System.out.println("end with "+nowtime);*/
    //	String []names = ctx.getBeanDefinitionNames();
//    	for(String s: names){
//    		System.out.println(s);
//    	}
    	
    	//共享锁
    	try {
    		Thread.sleep(Integer.MAX_VALUE);
    		System.out.println("-------------开始--释放锁");
    		LockUtil.unlockForShardLock();
    		System.out.println("-------------成功--释放锁");
			Thread.sleep(3000);
			System.out.println("---------------结束退出");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    } 
}
