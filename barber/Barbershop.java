package xzy.os.barber;
import java.util.concurrent.Semaphore;
public class Barbershop {
	static int customer=0;//当前顾客数
	static int MAX=5;//提供的让顾客等待的椅子数
	static int busy=0;//理发师是否忙碌标识
	static Semaphore mutex=new Semaphore(1);//信号量，只允许一个顾客占用理发师
	public synchronized boolean isFull() {
		//判断是否为满
		if(customer==MAX) {
			return true;
			
		}
		return false;
	}
	
	public void simulate(int index) throws InterruptedException {
		//模拟函数
		System.out.println("------------------------------");
		System.out.println("顾客 "+index+" 进入了理发店");
		customer++;
		if(isFull()) {
			//如果为满顾客离开
			System.out.println("顾客"+index+":"+"人挺多的啊，那我先离开了...");
			customer--;
			
		}
		else {
			if(busy==1) {
				//如果理发师忙碌，顾客等待
				System.out.println("顾客"+index+"：欸，有沙发，那我等会儿吧~");
			}
			mutex.acquire();
			//信号量减，防止其他进程再进入
			synchronized (this) {
				//同步
				while (busy==1) {
					wait();
					//有人理发等待
				}
			}
			if(index==1) {
				//第一个顾客
			    System.out.println("第1个顾客：嘿，醒醒啦，我要理发~");
			    System.out.println("Tony老师：（迷迷糊糊）好好好，我这就给您理。（说完摸了摸自己的光头）");
			}
			if(customer==1) {
				//只有一个顾客
			    System.out.println("目前只有一个顾客在理发店了");
			}
			busy=1;
			System.out.println("顾客"+index+"理发中，Tony老师正在设计新的光头造型...");
			Thread.sleep(1000);
			//理发时间1000毫秒
			System.out.println("顾客"+index+"：Tony老师，我给你转支付宝吧~（转账中）");
			System.out.println("顾客"+index+"离开了理发店");
			customer--;
			mutex.release();
			//释放资源
			synchronized(this) {
				//同步并唤醒
				busy=0;
				notify();
				
			}
			if(customer==0) {
//				没有顾客，理发师睡觉
				System.out.println("Tony老师：没人理发？那我睡觉去了啊~");
				
			}
 
		}
		
	}
	public static void main(String[] args) throws InterruptedException {
		Barbershop barbershop= new Barbershop();
		for (int i =1;i<=10;i++) {
//			假设一共有10个顾客
			new Thread(new Barber(barbershop,i)).start();
			Thread.sleep((int)(600-Math.random()*500));
			
		}
	}
 
}