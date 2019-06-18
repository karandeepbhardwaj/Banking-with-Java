class Message {

	private String name;
	private int money;
	private Bank bank;
	private String status;
	private Customer customer;

	public Message(String name, Integer money, Bank bank, String status, Customer customer) {
		this.name = name;
		this.money = money;
		this.bank = bank;
		this.status = status;
		this.customer = customer;
	}
	public Message(Bank bank, String status2) {
		this.bank = bank;
		this.status = status2;
	}
	public String getString() {
		return name;
	}
	public int getInteger() {
		return money;
	}
	public Thread getBank() {
		return this.bank;
	}
	public String getStatus() {
		return status;
	}
	public Customer getCustomer() {
		return this.customer;
	}
}