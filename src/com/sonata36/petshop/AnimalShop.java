package com.sonata36.petshop;

/** 宠物店的基本营业行为。 */
public interface AnimalShop {
    void buyAnimal(Animal animal);

    Animal serveCustomer(Customer customer);

    void close();
}
