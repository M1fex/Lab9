public class Bank {
    public void transfer(Account from, Account to, int amount){
        Account first;
        Account second;
        if(from.getId() < to.getId()){
            first = from;
            second = to;
        } else {
            first = to;
            second = from;
        }

        synchronized (first) {
            synchronized (second) {
                from.withdraw(amount);
                to.deposit(amount);
            }
        }
    }
}

class Account {
    private final int id;
    private int balance;

    public Account(int id, int balance){
        this.id = id;
        if(balance < 0)
            throw new IllegalArgumentException("Balance cannot be negative");
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public int getBalance() {
        return balance;
    }

    public void withdraw(int amount){
        if(amount > balance || amount < 0)
            throw new IllegalArgumentException("Insufficient funds");
        balance -= amount;
    }

    public void deposit(int amount){
        if(amount < 0)
            throw new IllegalArgumentException("Insufficient funds");
        balance += amount;
    }
}
