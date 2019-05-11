/*# this is a .java file for barber problem

# 1个理发椅 - 确定理发的人数
# n张沙发的等候室 - 确定等候的人数
# 没有理发的，理发师去休息
# 顾客进入理发店 -> 有人在理发 -> 找个空沙发坐下
#                -> 沙发无空位 -> 等待人员已满 -> 离开理发店
#                -> 理发师在睡觉 -> 唤醒理发师 -> 理发 -> 付钱 -> 离开

# 涉及操作：1、理发师睡觉  2、理发  3、唤醒理发师 4、沙发区等待 5、付钱 6、离开理发店
# 期间的支撑： 1、判断状态：有无人员理发；有无沙发可等待
@author BEATREE
@date 2018/12/21
*/
package xzy.os.program;

import java.util.concurrent.Semaphore;

import javax.swing.JOptionPane;

public class BarberProblem {
	
	static int BUSY = 0;    		// 理发状态 /信号量     0：不忙 1：繁忙中
	static int SOFA = 0;       	// 沙发数目 
	static int CUSTOMER = 0;		//顾客数目
	static Semaphore mutex=new Semaphore(1);//信号量，只允许一个顾客占用理发师
	
	//初始化顾客和沙发数目
	public void init(int customer,int sofa) {	
		SOFA = sofa;
		System.out.println("----------------------------");
		System.out.println("该轮将有："+customer+"个顾客");
		System.out.println("该轮共有："+SOFA+"个沙发");
		System.out.println("----------------------------");
		for (int i =1;i<=customer;i++) {	//生成对应数目的对象，即顾客数目
			new Thread(new Customer(new BarberProblem(),i)).start();
			try {
				Thread.sleep((int)(Math.random()*100));	//线程随机休眠
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}	
	
	//判断是否为满, 因为SOFA位等待人数 1为理发椅上的人
	public synchronized boolean isFull() {
		if(CUSTOMER==SOFA+2) {	//如果顾客数目等于沙发数目加一刚好满，即第SOFA+1个人可以等待
			return true;
		}
		return false;
	}
	//	判断有无顾客
	public synchronized boolean isEmpty() {
		if(CUSTOMER==0) {
			return true;
		}
		return false;
	}
	
	//顾客进入
	public void CustomerIn(int i) {	
		CUSTOMER ++;	//顾客数目加一
		System.out.println("第"+CUSTOMER+"个顾客进入了理发店");
		if(!isFull()) {	//如果还有沙发的话
			ifWait(i);
		}else {
			//如果为满顾客离开
			System.out.println("第"+i+"位顾客：欸，还挺忙呢，那我就先走了(顾客离开）");
            leave(i);
		}
	}
	
	
	/*//唤醒理发师
	public void awakeBarber() {
		System.out.println("第1个顾客：嘿，醒醒啦，我要理发~");
	    System.out.println("Tony老师：（迷迷糊糊）好好好，我这就给您理。（说完摸了摸自己的光头）");
	    try {
			CutandPay(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  	//第一个顾客理发，因为只有第一个顾客能唤醒理发师
	}*/
	
	//顾客等待
	public void ifWait(int i) {
	    if (BUSY == 1){		//理发师忙碌中
	    	System.out.println("第"+i+"位顾客：我等等吧（开始等待理发师）");
        }
	    try {
			CutHair(i);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//理发操作
	public void CutHair(int i) throws InterruptedException {
		//信号量减，防止其他进程再进入
		mutex.acquire();
		
		synchronized (this) {
			//	同步
			while (BUSY==1) {
				wait();//	有人理发等待
			}
		}
		
		if(i==1) {//如果是第一位顾客
			System.out.println("第1个顾客：嘿，醒醒啦，我要理发~");
		    System.out.println("Tony老师：（迷迷糊糊）好好好，我这就给您理。（说完摸了摸自己的光头）");
		}
		if(CUSTOMER==1) {
			//只有一个顾客
		    System.out.println("目前只有一个顾客在理发店");
		}
		BUSY=1;
		System.out.println("第"+i+"个顾客理发中，即将产生最帅的光光头造型...");
		Thread.sleep(1000);
		//理发时间1000毫秒
		//付钱和离开
		pay(i);
		leave(i);
		mutex.release();
		//释放资源
		synchronized(this) {
			//同步并唤醒
			BUSY=0;
			notify();
			
		}
		if(isEmpty()) {
			//	没有顾客，理发师睡觉
			System.out.println("Tony老师：没人理发？那我睡觉去了啊~");
			
		}

	}
	//顾客付钱
	public void pay(int i) {
		System.out.println("----------------------------------------------");
		System.out.println("理发完成");
		System.out.println("顾客"+i+"：Tony老师，我给你转支付宝吧~（转账中）");
	}
	
	//顾客离开店铺
	public void leave(int i) {
		CUSTOMER -- ;  //顾客离开，少了一个顾客
		System.out.println("第"+i+"个顾客离开");
		System.out.println("还剩下"+CUSTOMER+"个顾客");;
		System.out.println("----------------------------------------------");
	}
	
	
	public static void main(String[] args) {
		BarberProblem baberProblem = new BarberProblem();
		int customerNum = Integer.parseInt(JOptionPane.showInputDialog("模拟的顾客数目："));
		int sofaNum = Integer.parseInt(JOptionPane.showInputDialog("请输入沙发数目："));
		baberProblem.init(customerNum, sofaNum);
		
	}

}
