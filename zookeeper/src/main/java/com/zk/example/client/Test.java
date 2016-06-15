package com.zk.example.client;

public class Test {
	
	
	public static void main(String[] args) {
		int READ = 1 << 0;

        int WRITE = 1 << 1;

        int CREATE = 1 << 2;

        int DELETE = 1 << 3;

        int ADMIN = 1 << 4;

        int ALL = READ | WRITE | CREATE | DELETE | ADMIN;
        System.out.println("read:"+READ);
        System.out.println("wirte:"+WRITE);
        System.out.println("create:"+CREATE);
        System.out.println("delete:"+DELETE);
        System.out.println("admin:"+ADMIN);
        System.out.println("all:"+ALL);
	}

}
