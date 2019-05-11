package xzy.os.barber;

public class Barber  implements Runnable {
	Barbershop barbershop;
	int index;
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			barbershop.simulate(index);
			
		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	public Barber (Barbershop bs,int i) {
		this.barbershop=bs;
		index=i;
	}
	
 
}
