/*# this is a .java file for barber problem

# 1������ - ȷ����������
# n��ɳ���ĵȺ��� - ȷ���Ⱥ������
# û�����ģ���ʦȥ��Ϣ
# �˿ͽ������� -> �������� -> �Ҹ���ɳ������
#                -> ɳ���޿�λ -> �ȴ���Ա���� -> �뿪����
#                -> ��ʦ��˯�� -> ������ʦ -> �� -> ��Ǯ -> �뿪

# �漰������1����ʦ˯��  2����  3��������ʦ 4��ɳ�����ȴ� 5����Ǯ 6���뿪����
# �ڼ��֧�ţ� 1���ж�״̬��������Ա��������ɳ���ɵȴ�
@author BEATREE
@date 2018/12/21
*/
package xzy.os.program;

import java.util.concurrent.Semaphore;

import javax.swing.JOptionPane;

public class BarberProblem {
	
	static int BUSY = 0;    		// ��״̬ /�ź���     0����æ 1����æ��
	static int SOFA = 0;       	// ɳ����Ŀ 
	static int CUSTOMER = 0;		//�˿���Ŀ
	static Semaphore mutex=new Semaphore(1);//�ź�����ֻ����һ���˿�ռ����ʦ
	
	//��ʼ���˿ͺ�ɳ����Ŀ
	public void init(int customer,int sofa) {	
		SOFA = sofa;
		System.out.println("----------------------------");
		System.out.println("���ֽ��У�"+customer+"���˿�");
		System.out.println("���ֹ��У�"+SOFA+"��ɳ��");
		System.out.println("----------------------------");
		for (int i =1;i<=customer;i++) {	//���ɶ�Ӧ��Ŀ�Ķ��󣬼��˿���Ŀ
			new Thread(new Customer(new BarberProblem(),i)).start();
			try {
				Thread.sleep((int)(Math.random()*100));	//�߳��������
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}	
	
	//�ж��Ƿ�Ϊ��, ��ΪSOFAλ�ȴ����� 1Ϊ�����ϵ���
	public synchronized boolean isFull() {
		if(CUSTOMER==SOFA+2) {	//����˿���Ŀ����ɳ����Ŀ��һ�պ���������SOFA+1���˿��Եȴ�
			return true;
		}
		return false;
	}
	//	�ж����޹˿�
	public synchronized boolean isEmpty() {
		if(CUSTOMER==0) {
			return true;
		}
		return false;
	}
	
	//�˿ͽ���
	public void CustomerIn(int i) {	
		CUSTOMER ++;	//�˿���Ŀ��һ
		System.out.println("��"+CUSTOMER+"���˿ͽ���������");
		if(!isFull()) {	//�������ɳ���Ļ�
			ifWait(i);
		}else {
			//���Ϊ���˿��뿪
			System.out.println("��"+i+"λ�˿ͣ��G����ͦæ�أ����Ҿ�������(�˿��뿪��");
            leave(i);
		}
	}
	
	
	/*//������ʦ
	public void awakeBarber() {
		System.out.println("��1���˿ͣ��٣�����������Ҫ��~");
	    System.out.println("Tony��ʦ�������Ժ������úúã�����͸�������˵���������Լ��Ĺ�ͷ��");
	    try {
			CutandPay(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  	//��һ���˿�������Ϊֻ�е�һ���˿��ܻ�����ʦ
	}*/
	
	//�˿͵ȴ�
	public void ifWait(int i) {
	    if (BUSY == 1){		//��ʦæµ��
	    	System.out.println("��"+i+"λ�˿ͣ��ҵȵȰɣ���ʼ�ȴ���ʦ��");
        }
	    try {
			CutHair(i);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//������
	public void CutHair(int i) throws InterruptedException {
		//�ź���������ֹ���������ٽ���
		mutex.acquire();
		
		synchronized (this) {
			//	ͬ��
			while (BUSY==1) {
				wait();//	�������ȴ�
			}
		}
		
		if(i==1) {//����ǵ�һλ�˿�
			System.out.println("��1���˿ͣ��٣�����������Ҫ��~");
		    System.out.println("Tony��ʦ�������Ժ������úúã�����͸�������˵���������Լ��Ĺ�ͷ��");
		}
		if(CUSTOMER==1) {
			//ֻ��һ���˿�
		    System.out.println("Ŀǰֻ��һ���˿�������");
		}
		BUSY=1;
		System.out.println("��"+i+"���˿����У�����������˧�Ĺ��ͷ����...");
		Thread.sleep(1000);
		//��ʱ��1000����
		//��Ǯ���뿪
		pay(i);
		leave(i);
		mutex.release();
		//�ͷ���Դ
		synchronized(this) {
			//ͬ��������
			BUSY=0;
			notify();
			
		}
		if(isEmpty()) {
			//	û�й˿ͣ���ʦ˯��
			System.out.println("Tony��ʦ��û����������˯��ȥ�˰�~");
			
		}

	}
	//�˿͸�Ǯ
	public void pay(int i) {
		System.out.println("----------------------------------------------");
		System.out.println("�����");
		System.out.println("�˿�"+i+"��Tony��ʦ���Ҹ���ת֧������~��ת���У�");
	}
	
	//�˿��뿪����
	public void leave(int i) {
		CUSTOMER -- ;  //�˿��뿪������һ���˿�
		System.out.println("��"+i+"���˿��뿪");
		System.out.println("��ʣ��"+CUSTOMER+"���˿�");;
		System.out.println("----------------------------------------------");
	}
	
	
	public static void main(String[] args) {
		BarberProblem baberProblem = new BarberProblem();
		int customerNum = Integer.parseInt(JOptionPane.showInputDialog("ģ��Ĺ˿���Ŀ��"));
		int sofaNum = Integer.parseInt(JOptionPane.showInputDialog("������ɳ����Ŀ��"));
		baberProblem.init(customerNum, sofaNum);
		
	}

}
