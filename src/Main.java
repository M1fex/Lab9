import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Bank bank = new Bank();
        Account[] accounts = new Account[100];

        long initialTotal = 0;

        for (int i = 0; i < accounts.length; i++) {
            int balance = ThreadLocalRandom.current().nextInt(1000, 10001);
            accounts[i] = new Account(i, balance);
            initialTotal += balance;
        }

        System.out.println("Рахунки: " + accounts.length);
        System.out.println("Сума до переводів: " + initialTotal);

        Thread[] threads = new Thread[2000];

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                ThreadLocalRandom random = ThreadLocalRandom.current();

                int fromIndex = random.nextInt(accounts.length);
                int toIndex;

                do {
                    toIndex = random.nextInt(accounts.length);
                } while (toIndex == fromIndex);

                int amount = random.nextInt(1, 501);

                try {
                    bank.transfer(
                            accounts[fromIndex],
                            accounts[toIndex],
                            amount
                    );
                } catch (IllegalArgumentException e) {
                    // Недостаточно денег
                }
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        long finalTotal = 0;
        boolean allBalancesNonNegative = true;

        for (Account account : accounts) {
            finalTotal += account.getBalance();

            if (account.getBalance() < 0) {
                allBalancesNonNegative = false;
            }
        }

        System.out.println("Все потоки завершились");
        System.out.println("Сумма после переводов: " + finalTotal);
        System.out.println("Суммы совпадают: " + (initialTotal == finalTotal));
        System.out.println("Нет отрицательных балансов: " + allBalancesNonNegative);
    }
}