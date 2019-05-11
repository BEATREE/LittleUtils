/*@author BEATREE
@date 2018/12/21*/
package xzy.os.program;

public class Customer implements Runnable{
	
	BarberProblem baberProblem;
	int index;
	
	public Customer (BarberProblem bs,int i) {
		this.baberProblem=bs;
		index=i;
	}
	@Override
	public void run() {
		baberProblem.CustomerIn(index);	//模拟顾客进入理发店
	}
}
