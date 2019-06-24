import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

class Customer extends Thread {

	public LinkedBlockingQueue<String> sharedQueue = new LinkedBlockingQueue<>();
	public LinkedBlockingQueue<Message> customerQueue = new LinkedBlockingQueue<>();
	public Integer moneyRequired;
	public String customerName;
	public List<Bank> bankThreadList;
	public String status;
	public Integer originalAmount;

	public Customer(LinkedBlockingQueue<String> sharedQueue, Integer moneyRequired, String customerName, List<Bank> bankThreadList) {
		super();
		this.sharedQueue = sharedQueue;
		this.moneyRequired = moneyRequired;
		this.originalAmount= moneyRequired;
		this.customerName = customerName;
		this.bankThreadList = bankThreadList;
	}

	@Override
	public void run() {
		while(true) {
			Random random = new Random();
			int number = random.nextInt(bankThreadList.size());
			Bank bank = bankThreadList.get(number);
			status = "requesting";
			int moneyRequested = 0;

			if(moneyRequired >= 50){
				moneyRequested = random.nextInt(50) +1;
			}else{
				moneyRequested = moneyRequired;
			}
			Message dataToBank = new Message(customerName, moneyRequested, bank, status, this);
			try {
				int Random_wait_time = random.nextInt(90) + 10;
				Thread.sleep(Random_wait_time);
				String forSharedQueue = customerName+" requests a loan of "+moneyRequested+" dollar(s) from "+bank.bankName;
				sharedQueue.put(forSharedQueue);
				bank.bankQueue.put(dataToBank);
				Message dataFromBank;
				dataFromBank = customerQueue.take();
				String status = dataFromBank.getStatus();
				
				if(status == "accepted") {
					this.moneyRequired -= moneyRequested;
				}else {
					bankThreadList.remove(number);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (moneyRequired == 0 || bankThreadList.size() == 0) {
				try {
					if(moneyRequired!= 0 && bankThreadList.size() == 0){
						String forSharedQueue ="Later/"+ customerName +" was only able to borrow "+(originalAmount - moneyRequired)+" dollar(s). Boo Hoo!";
						sharedQueue.put(forSharedQueue);
						// bank.customers.remove(this);
						break;
					}
					String forSharedQueue ="Later/"+ customerName +" has reached the objective of "+originalAmount+ " dollar(s). Woo Hoo!";
					sharedQueue.put(forSharedQueue);
					break;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}
}