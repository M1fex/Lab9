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

        System.out.println("Всі потоки закінчились");
        System.out.println("Сума після переводів: " + finalTotal);
        System.out.println("Суми співпадають: " + (initialTotal == finalTotal));
        System.out.println("Нема від'ємних балансів: " + allBalancesNonNegative);

//======================================================================================================================

        RingBuffer firstBuffer = new RingBuffer(10);
        RingBuffer secondBuffer = new RingBuffer(10);

        for (int i = 0; i < 5; i++) {
            int producerId = i + 1;

            Thread producer = new Thread(() -> {
                int counter = 1;

                try {
                    while (true) {
                        firstBuffer.put("Потік № " + producerId + " згенерував повідомлення " + counter);
                        counter++;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            producer.setDaemon(true);
            producer.start();
        }


        for (int i = 0; i < 2; i++) {
            int translatorId = i + 6;

            Thread translator = new Thread(() -> {
                try {
                    while (true) {
                        String message = firstBuffer.take();
                        secondBuffer.put(
                                "Потік № " + translatorId
                                        + " переклав повідомлення: " + message
                        );
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            translator.setDaemon(true);
            translator.start();
        }


        for (int i = 0; i < 100; i++) {
            System.out.println(secondBuffer.take());
        }
    }
}