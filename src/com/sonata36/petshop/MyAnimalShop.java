package com.sonata36.petshop;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/** 采用先进先出方式出售动物的宠物店。 */
public final class MyAnimalShop implements AnimalShop {
    private double balance;
    private final List<Animal> animals;
    private final List<Customer> customers;
    private final Map<LocalDate, Double> dailyProfits;
    private final Clock clock;
    private boolean open;

    public MyAnimalShop(double balance, List<Animal> initialAnimals) {
        this(balance, initialAnimals, new ArrayList<>(), Clock.systemDefaultZone());
    }

    public MyAnimalShop(double balance, List<Animal> initialAnimals,
            List<Customer> initialCustomers, Clock clock) {
        if (!Double.isFinite(balance) || balance < 0) {
            throw new IllegalArgumentException("初始余额必须是非负的有限数字");
        }
        Objects.requireNonNull(initialAnimals, "动物列表不能为空");
        Objects.requireNonNull(initialCustomers, "顾客列表不能为空");
        this.clock = Objects.requireNonNull(clock, "时钟不能为空");
        this.animals = new ArrayList<>(initialAnimals);
        this.customers = new ArrayList<>();
        for (Customer customer : initialCustomers) {
            this.customers.add(Objects.requireNonNull(customer, "顾客不能为 null").snapshot());
        }
        for (Animal animal : this.animals) {
            Objects.requireNonNull(animal, "动物不能为 null");
        }
        this.dailyProfits = new HashMap<>();
        this.balance = balance;
        this.open = true;
    }

    @Override
    public void buyAnimal(Animal animal) {
        ensureOpen();
        Objects.requireNonNull(animal, "动物不能为 null");
        if (balance < animal.getPrice()) {
            throw new InsufficientBalanceException(String.format(Locale.ROOT,
                    "余额不足：现有 %.2f 元，买入 %s 需要 %.2f 元",
                    balance, animal.getName(), animal.getPrice()));
        }
        balance -= animal.getPrice();
        animals.add(animal);
        dailyProfits.merge(LocalDate.now(clock), -animal.getPrice(), Double::sum);
    }

    @Override
    public Animal serveCustomer(Customer customer) {
        ensureOpen();
        Objects.requireNonNull(customer, "顾客不能为 null");
        LocalDate today = LocalDate.now(clock);
        customer.recordVisit(today);
        customers.add(customer.snapshot());
        if (animals.isEmpty()) {
            throw new AnimalNotFoundException("店内没有动物可买");
        }
        Animal soldAnimal = animals.remove(0);
        balance += soldAnimal.getPrice();
        dailyProfits.merge(today, soldAnimal.getPrice(), Double::sum);
        System.out.println("售出动物：" + soldAnimal);
        return soldAnimal;
    }

    @Override
    public void close() {
        ensureOpen();
        open = false;
        LocalDate today = LocalDate.now(clock);
        System.out.println("歇业日期：" + today);
        System.out.println("今日到店顾客：");
        boolean hasVisitors = false;
        for (Customer customer : customers) {
            if (today.equals(customer.getLastVisitDate())) {
                System.out.println(customer);
                hasVisitors = true;
            }
        }
        if (!hasVisitors) {
            System.out.println("无");
        }
        System.out.printf(Locale.ROOT, "今日利润：%.2f 元%n", getProfit(today));
    }

    public double getBalance() {
        return balance;
    }

    public List<Animal> getAnimals() {
        return List.copyOf(animals);
    }

    public List<Customer> getCustomers() {
        List<Customer> snapshots = new ArrayList<>();
        for (Customer customer : customers) {
            snapshots.add(customer.snapshot());
        }
        return List.copyOf(snapshots);
    }

    public double getProfit(LocalDate date) {
        return dailyProfits.getOrDefault(Objects.requireNonNull(date), 0.0);
    }

    public boolean isOpen() {
        return open;
    }

    private void ensureOpen() {
        if (!open) {
            throw new IllegalStateException("宠物店已经歇业");
        }
    }
}
