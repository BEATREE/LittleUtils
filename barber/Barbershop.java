package xzy.os.barber;
import java.util.concurrent.Semaphore;
public class Barbershop {
	static int customer=0;//��ǰ�˿���
	static int MAX=5;//�ṩ���ù˿͵ȴ���������
	static int busy=0;//��ʦ�Ƿ�æµ��ʶ
	static Semaphore mutex=new Semaphore(1);//�ź�����ֻ����һ���˿�ռ����ʦ
	public synchronized boolean isFull() {
		//�ж��Ƿ�Ϊ��
		if(customer==MAX) {
			return true;
			
		}
		return false;
	}
	
	public void simulate(int index) throws InterruptedException {
		//ģ�⺯��
		System.out.println("------------------------------");
		System.out.println("�˿� "+index+" ����������");
		customer++;
		if(isFull()) {
			//���Ϊ���˿��뿪
			System.out.println("�˿�"+index+":"+"��ͦ��İ����������뿪��...");
			customer--;
			
		}
		else {
			if(busy==1) {
				//�����ʦæµ���˿͵ȴ�
				System.out.println("�˿�"+index+"���G����ɳ�������ҵȻ����~");
			}
			mutex.acquire();
			//�ź���������ֹ���������ٽ���
			synchronized (this) {
				//ͬ��
				while (busy==1) {
					wait();
					//�������ȴ�
				}
			}
			if(index==1) {
				//��һ���˿�
			    System.out.println("��1���˿ͣ��٣�����������Ҫ��~");
			    System.out.println("Tony��ʦ�������Ժ������úúã�����͸�������˵���������Լ��Ĺ�ͷ��");
			}
			if(customer==1) {
				//ֻ��һ���˿�
			    System.out.println("Ŀǰֻ��һ���˿���������");
			}
			busy=1;
			System.out.println("�˿�"+index+"���У�Tony��ʦ��������µĹ�ͷ����...");
			Thread.sleep(1000);
			//��ʱ��1000����
			System.out.println("�˿�"+index+"��Tony��ʦ���Ҹ���ת֧������~��ת���У�");
			System.out.println("�˿�"+index+"�뿪������");
			customer--;
			mutex.release();
			//�ͷ���Դ
			synchronized(this) {
				//ͬ��������
				busy=0;
				notify();
				
			}
			if(customer==0) {
//				û�й˿ͣ���ʦ˯��
				System.out.println("Tony��ʦ��û����������˯��ȥ�˰�~");
				
			}
 
		}
		
	}
	public static void main(String[] args) throws InterruptedException {
		Barbershop barbershop= new Barbershop();
		for (int i =1;i<=10;i++) {
//			����һ����10���˿�
			new Thread(new Barber(barbershop,i)).start();
			Thread.sleep((int)(600-Math.random()*500));
			
		}
	}
 
}