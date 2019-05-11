/*
 * @author BEATREE
 * @date 2018/12/23
 * @website www.teenshare.club
 * */

package xzy.os.program;

import java.util.concurrent.Semaphore;

import javax.swing.JOptionPane;

public class BarberShop {
	
	Semaphore CHAIR = new Semaphore(1);	//�����ź�����ֻ����һ����
	Semaphore PAY = new Semaphore(0);	//֧���ź���
	Semaphore RECEIVE = new Semaphore(0);	//֧���տ��ź���
	Semaphore SLEEP = new Semaphore(0);	//˯���ź�����0Ϊ˯�� 1Ϊ˯��
	Semaphore SOFA = new Semaphore(0);	//ɳ���ź������û��Զ���
	Semaphore HAIRCUT = new Semaphore(0);	//ɳ���ź������û��Զ���
	int CUSTOMER;						//�˿��������û��Զ���
	
	public void init(int customer, int sofa){
		CUSTOMER = 0;	//��ʼ���˿�����
		SOFA = new Semaphore(sofa);//��ʼ��ɳ���ź���
		System.out.println("================================");
		System.out.println("���ֽ���"+customer+"���˿Ͳ���ģ��");
		System.out.println("�������꽫��"+sofa+"��ɳ��");
		System.out.println("================================");
		for (int i = 1; i <= customer; i++) {
			new Thread(new Customer(i)).start();
			try {
				Thread.sleep((int)(Math.random()*1500));	//�߳��������
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] arg0) {
		int customer = Integer.parseInt(JOptionPane.showInputDialog("������˿�������"));
		int sofa = Integer.parseInt(JOptionPane.showInputDialog("������ɳ��������"));
		BarberShop barberShop = new BarberShop();
		barberShop.init(customer, sofa);
	}
	
	//����˿ͽ���
	class Customer implements Runnable{
		
		int i;	//��ǰ�˿ͱ��
		public Customer(int i) {
			this.i = i;
		}
		
		@Override
		public void run() {
			try {
				
				CUSTOMER ++; 	//�й˿ͽ������꣬�˿���Ŀ++
				System.out.println("�˿�"+i+"������������");
				if(isFull()) {
					System.out.println("�˿�"+i+"����ͦæ�ģ�ɳ��Ҳû�ˣ�����������~");
					leave(i);
				}else {
					SOFA.acquire();	//���ɳ���ź���������п�λ�������һ��
					if(isFirst()) {	//�ж��ǲ��ǿգ�����ǣ�����û��ǵ�һ������Ĺ˿ͣ���Ҫ������ʦ
						System.out.println("�˿�"+i+"��Tony��ʦ�����ѣ���Ҫ��~");
						new Thread(new Barber()).start();		//������ʦ�߳�
						SLEEP.release(); //������ʦ
						CHAIR.acquire();
						SOFA.release();
						HAIRCUT.acquire();	//ȷ����
						CutandPay(i);
						CHAIR.release();
						leave(i);
					}else {			//���ǵ�һ�������
						CHAIR.acquire();//���������ź���
						System.out.println("�˿�"+i+"����������");
						SOFA.release();//���뵽���κ��ͷŵ�CHAIR,�ó�ɳ��
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
		
		//�жϹ˿��Ƿ��ǵ�һ��
		public synchronized boolean isFirst(){
			return CUSTOMER==1;
		}
		//�ж������Ƿ�����
		public synchronized boolean isFull(){
			//System.out.println(SOFA.availablePermits());
			return 0==SOFA.availablePermits();
		}
		public void leave(int i) {
			try {
				CUSTOMER--;
				RECEIVE.acquire();
				System.out.println("�˿�"+i+"�뿪������");
				System.out.println("Ŀǰ��ʣ�£�"+CUSTOMER+"λ�˿�");
				System.out.println("----------------------------------------");
				/*if(CUSTOMER==0) {
					System.out.println("Tony��ʦ��û�й˿��ˣ����ڿ�����˯���� /������");
				}*/
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		public void CutandPay(int i) {
			
			try {
				System.out.println("�˿�"+i+"������...");
				System.out.println("��Tony��ʦ����������˧��ͷ���ͣ�");
				Thread.sleep(1200);
				System.out.println("------------�˿�"+i+"�����-----------------");
				System.out.println("�˿�"+i+"֧���У�Tony��ʦ���Ҹ���ת֧������");
				PAY.release();//֪ͨ��ʦ�Լ�����Ǯ��
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	//����˿ͽ���
	class Barber implements Runnable{

		@Override
		public void run() {
				try {
					SLEEP.acquire();	//��ȡ�ź�����˯��
					while(true) {
						while(CUSTOMER!=0) {
							System.out.println("Tony��ʦ������͸�����");
							HAIRCUT.release();
							PAY.acquire();		//�ж��Ƿ��յ�Ǯ��
							System.out.println("Tony��ʦ���G�����ϡ����յ����ĸ����ˡ�");
							RECEIVE.release();
						}
						Thread.sleep(1000);//�ȴ����һ���˿����
						System.out.println("Tony��ʦ������æ���ˣ������˯�İ���~  ��˵�ˣ�˯��ȥ��/��Ƿ");
						break;
					}
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			
		}
		
	}
}

