package com.sonata36.petshop;

/** 宠物店的基本营业行为。 */
public interface AnimalShop {
    void buyAnimal(Animal animal);

    Animal serveCustomer(Customer customer);

    /** 按库存列表中的下标（从 0 开始）出售指定动物。 */
    Animal serveCustomer(Customer customer, int animalIndex);

    void close();
}
