import java.nio.file.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

class Money {

	public static Map<String, Integer> convert_file_to_map(String filename) throws Exception {

		String data = new String(Files.readAllBytes(Paths.get(filename)));
		String[] parts = data.split("\n");
		Map<String, Integer> myMap = new HashMap<String, Integer>();

		for (String i : parts) {
			String name = i.replaceAll("[^a-zA-Z]", "");
			String amount = i.replaceAll("[^\\d]", "");
			int money = Integer.valueOf(amount);
			myMap.put(name, money);
		}
		return myMap;
	}

	public static void main(String[] args) throws Exception {

		Map<String, Integer> bankMap = new HashMap<String, Integer>();
		Map<String, Integer> customerMap = new HashMap<String, Integer>();
		List<String> listForCustomerPrint = new ArrayList<String>();
		bankMap = convert_file_to_map("banks.txt");
		customerMap = convert_file_to_map("customers.txt");
		int totalCustomercount = 0;

		List<Bank> bankThreadList = new ArrayList<>();
		List<Customer> customerThreadList = new ArrayList<>();
		LinkedBlockingQueue<String> sharedQueue = new LinkedBlockingQueue<String>();

		System.out.println("*************************************");
		System.out.println("*   Customers and Loan objectives   *");
		System.out.println("*************************************");
		customerMap.entrySet().forEach(entry -> {
			System.out.println(entry.getKey() + " : " + entry.getValue());
		});
		System.out.println("*************************************");
		System.out.println("*   Banks and Financial resources   *");
		System.out.println("*************************************");
		bankMap.entrySet().forEach(entry -> {
			System.out.println(entry.getKey() + " " + entry.getValue());
		});
		System.out.println();
		for (String bankName : bankMap.keySet()) {
			int moneyInBank = bankMap.get(bankName);
			Bank bank = new Bank(sharedQueue, moneyInBank, bankName);
			bankThreadList.add(bank);
		}

		for (String customerName : customerMap.keySet()) {
			int moneyRequiredByCustomer = customerMap.get(customerName);
			Customer customer = new Customer(sharedQueue, moneyRequiredByCustomer, customerName,
					new ArrayList<Bank>(bankThreadList));
			customerThreadList.add(customer);
			totalCustomercount++;
		}

		for (Bank bank : bankThreadList) {
			bank.customers = new ArrayList<Customer>(customerThreadList);
			bank.start();
		}

		for (Customer customer : customerThreadList) {
			customer.start();
		}
		while (totalCustomercount > 0) {
			String data = "";
			data = sharedQueue.take();

			if (data.startsWith("Later/")) {
				String[] myMessage = data.split("/", 2);
				listForCustomerPrint.add(myMessage[1]);
				totalCustomercount--;
			} else {
				System.out.println(data);
			}
		}
		System.out.println();
		System.out.println();
		for (String i : listForCustomerPrint) {
			System.out.println(i);
		}
		System.out.println();
		System.out.println();
		for (Bank bank : bankThreadList) {
				System.out.println(bank.bankName + " has only " + bank.moneyInBank + " dollar(s) remaining. ");
				Message msg = new Message("exit");
				bank.bankQueue.put(msg);
		}
	}
}