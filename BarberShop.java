/*
 * @author BEATREE
 * @date 2018/12/23
 * @website www.teenshare.club
 * */

package xzy.os.program;

import java.util.concurrent.Semaphore;

import javax.swing.JOptionPane;

public class BarberShop {
	
	Semaphore CHAIR = new Semaphore(1);	//理发椅信号量，只能有一个理发
	Semaphore PAY = new Semaphore(0);	//支付信号量
	Semaphore RECEIVE = new Semaphore(0);	//支付收款信号量
	Semaphore SLEEP = new Semaphore(0);	//睡觉信号量，0为睡觉 1为睡醒
	Semaphore SOFA = new Semaphore(0);	//沙发信号量；用户自定义
	Semaphore HAIRCUT = new Semaphore(0);	//沙发信号量；用户自定义
	int CUSTOMER;						//顾客数量；用户自定义
	
	public void init(int customer, int sofa){
		CUSTOMER = 0;	//初始化顾客数量
		SOFA = new Semaphore(sofa);//初始化沙发信号量
		System.out.println("================================");
		System.out.println("该轮将有"+customer+"个顾客参与模拟");
		System.out.println("该轮理发店将有"+sofa+"个沙发");
		System.out.println("================================");
		for (int i = 1; i <= customer; i++) {
			new Thread(new Customer(i)).start();
			try {
				Thread.sleep((int)(Math.random()*1500));	//线程随机休眠
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] arg0) {
		int customer = Integer.parseInt(JOptionPane.showInputDialog("请输入顾客数量："));
		int sofa = Integer.parseInt(JOptionPane.showInputDialog("请输入沙发数量："));
		BarberShop barberShop = new BarberShop();
		barberShop.init(customer, sofa);
	}
	
	//定义顾客进程
	class Customer implements Runnable{
		
		int i;	//当前顾客编号
		public Customer(int i) {
			this.i = i;
		}
		
		@Override
		public void run() {
			try {
				
				CUSTOMER ++; 	//有顾客进入理发店，顾客数目++
				System.out.println("顾客"+i+"：进入了理发店");
				if(isFull()) {
					System.out.println("顾客"+i+"：还挺忙的，沙发也没了，那我先走了~");
					leave(i);
				}else {
					SOFA.acquire();	//获得沙发信号量，如果有空位则进行下一步
					if(isFirst()) {	//判断是不是空，如果是，则该用户是第一个进入的顾客，需要唤醒理发师
						System.out.println("顾客"+i+"：Tony老师，醒醒，我要理发~");
						new Thread(new Barber()).start();		//启动理发师线程
						SLEEP.release(); //唤醒理发师
						CHAIR.acquire();
						SOFA.release();
						HAIRCUT.acquire();	//确认理发
						CutandPay(i);
						CHAIR.release();
						leave(i);
					}else {			//不是第一个进入的
						CHAIR.acquire();//申请理发椅信号量
						System.out.println("顾客"+i+"坐上了理发椅");
						SOFA.release();//申请到理发椅后释放掉CHAIR,让出沙发
						HAIRCUT.acquire();
						CutandPay(i);
						CHAIR.release();
						leave(i);
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//判断顾客是否是第一个
		public synchronized boolean isFirst(){
			return CUSTOMER==1;
		}
		//判断理发店是否已满
		public synchronized boolean isFull(){
			//System.out.println(SOFA.availablePermits());
			return 0==SOFA.availablePermits();
		}
		public void leave(int i) {
			try {
				CUSTOMER--;
				RECEIVE.acquire();
				System.out.println("顾客"+i+"离开了理发店");
				System.out.println("目前还剩下："+CUSTOMER+"位顾客");
				System.out.println("----------------------------------------");
				/*if(CUSTOMER==0) {
					System.out.println("Tony老师：没有顾客了，终于可以又睡觉了 /伸懒腰");
				}*/
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		public void CutandPay(int i) {
			
			try {
				System.out.println("顾客"+i+"：理发中...");
				System.out.println("（Tony老师正在设计最潮最帅光头发型）");
				Thread.sleep(1200);
				System.out.println("------------顾客"+i+"理发完成-----------------");
				System.out.println("顾客"+i+"支付中：Tony老师，我给您转支付宝了");
				PAY.release();//通知理发师自己付过钱了
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	//定义顾客进程
	class Barber implements Runnable{

		@Override
		public void run() {
				try {
					SLEEP.acquire();	//获取信号量，睡醒
					while(true) {
						while(CUSTOMER!=0) {
							System.out.println("Tony老师：我这就给您理发");
							HAIRCUT.release();
							PAY.acquire();		//判断是否收到钱了
							System.out.println("Tony老师：欸，好嘞。我收到您的付款了。");
							RECEIVE.release();
						}
						Thread.sleep(1000);//等待最后一个顾客离店
						System.out.println("Tony老师：可算忙完了，给我瞌睡的啊了~  不说了，睡觉去了/哈欠");
						break;
					}
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			
		}
		
	}
}

