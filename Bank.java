import java.util.concurrent.LinkedBlockingQueue;
import java.util.*;

class Bank extends Thread {

	public LinkedBlockingQueue<String> sharedQueue = new LinkedBlockingQueue<>();
	public LinkedBlockingQueue<Message> bankQueue = new LinkedBlockingQueue<>();
	public List<Customer> customers;
	public Integer moneyInBank;
	public String bankName;

	public Bank(LinkedBlockingQueue<String> sharedQueue, Integer moneyInBank, String bankName) {
		super();
		this.sharedQueue = sharedQueue;
		this.moneyInBank = moneyInBank;
		this.bankName = bankName;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Message dataFromCustomer;
				dataFromCustomer = bankQueue.take();
				String customerName = dataFromCustomer.getString();
				int moneyRequired = dataFromCustomer.getInteger();
				String status = dataFromCustomer.getStatus();

				if (status == "requesting" && moneyRequired <= moneyInBank) {
					status = "accepted";
					this.moneyInBank -= moneyRequired;
					Message msg = new Message(this, status);

					String forSharedQueue = bankName+" approves a loan of "+moneyRequired+ " dollars from "+ customerName;
					sharedQueue.put(forSharedQueue);
					dataFromCustomer.getCustomer().customerQueue.put(msg);
				} else {
					status = "rejected";
					customers.remove(dataFromCustomer.getCustomer());
					Message msg = new Message(this, status);
					String forSharedQueue = bankName+" denies a loan of "+moneyRequired+ " dollars from "+ customerName;
					sharedQueue.put(forSharedQueue);
					dataFromCustomer.getCustomer().customerQueue.put(msg);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (customers.size() == 0) {
				try {
					String forSharedQueue = bankName+" has "+ moneyInBank+" dollar(s) remaining.";
					sharedQueue.put(forSharedQueue);
					break;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}