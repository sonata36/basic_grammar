package com.sonata36.petshop;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/** 无需第三方测试框架的作业演示与功能检查。 */
public final class Test {
    private Test() {
    }

    public static void main(String[] args) {
        LocalDate today = LocalDate.of(2026, 10, 9);
        Clock clock = Clock.fixed(today.atStartOfDay(ZoneId.of("Asia/Shanghai")).toInstant(),
                ZoneId.of("Asia/Shanghai"));
        List<Animal> initialAnimals = new ArrayList<>();
        initialAnimals.add(new Cat("小橘", 2, "母"));
        List<Customer> initialCustomers = new ArrayList<>();
        initialCustomers.add(new Customer("老顾客", 1, today.minusDays(1)));

        MyAnimalShop shop = new MyAnimalShop(500, initialAnimals, initialCustomers, clock);
        shop.buyAnimal(new ChineseRuralDog("阿黄", 1, "公", true));
        Customer alice = new Customer("小林");
        check(shop.serveCustomer(alice) instanceof Cat, "应先出售最早入库的猫");
        check(shop.serveCustomer(alice) instanceof ChineseRuralDog, "应出售田园犬");
        check(alice.getVisitCount() == 2, "同一顾客的到店次数应累计");

        boolean animalMissing = false;
        try {
            shop.serveCustomer(new Customer("小王"));
        } catch (AnimalNotFoundException exception) {
            animalMissing = true;
            check(!exception.getMessage().isBlank(), "缺货异常应包含错误信息");
        }
        check(animalMissing, "库存售罄应抛出 AnimalNotFoundException");
        check(shop.getCustomers().size() == 4, "缺货顾客也应记入到店记录");
        check(shop.getAnimals().isEmpty(), "售出后应从库存移除动物");
        check(shop.getBalance() == 700.0, "余额应计入买入成本与售出收入");
        check(shop.getProfit(today) == 200.0, "当日利润应为销售额减买入成本");

        MyAnimalShop poorShop = new MyAnimalShop(50, List.of());
        boolean insufficientBalance = false;
        try {
            poorShop.buyAnimal(new Rabbit("团团", 1, "母"));
        } catch (InsufficientBalanceException exception) {
            insufficientBalance = true;
            check(!exception.getMessage().isBlank(), "余额不足异常应包含错误信息");
        }
        check(insufficientBalance, "余额不足应抛出 InsufficientBalanceException");
        check(poorShop.getAnimals().isEmpty(), "买入失败不应改变库存");

        MyAnimalShop choiceShop = new MyAnimalShop(500, List.of(
                new ChineseRuralDog("大黄", 3, "公", true),
                new Cat("小白", 2, "母"),
                new Rabbit("雪球", 1, "母")), new ArrayList<>(), clock);
        Customer bob = new Customer("小陈");
        check(choiceShop.serveCustomer(bob, 1) instanceof Cat, "应出售顾客选中的猫");
        check(choiceShop.getAnimals().size() == 2, "只应移除选中的动物");
        check("大黄".equals(choiceShop.getAnimals().get(0).getName()),
                "未选中的第一只动物应留在库存中");
        check("雪球".equals(choiceShop.getAnimals().get(1).getName()),
                "未选中的第三只动物应留在库存中");
        check(choiceShop.getBalance() == 700.0, "余额应按选中动物的价格入账");
        check(choiceShop.getProfit(today) == 200.0, "利润应按选中动物的价格计算");
        boolean invalidChoice = false;
        try {
            choiceShop.serveCustomer(bob, 9);
        } catch (IllegalArgumentException exception) {
            invalidChoice = true;
        }
        check(invalidChoice, "越界的宠物编号应被拒绝");
        check(bob.getVisitCount() == 1, "选择无效时不应增加到店次数");

        ByteArrayOutputStream reportBytes = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        try (PrintStream capturedOut = new PrintStream(reportBytes, true, StandardCharsets.UTF_8)) {
            System.setOut(capturedOut);
            shop.close();
        } finally {
            System.setOut(originalOut);
        }
        String report = reportBytes.toString(StandardCharsets.UTF_8);
        check(!report.contains("老顾客"), "歇业报表不应包含昨天到店的顾客");
        check(report.contains("小王"), "缺货顾客仍应出现在当日到店记录中");
        check(report.contains("今日利润：200.00 元"), "歇业报表应输出当日利润");
        System.out.print(report);
        check(!shop.isOpen(), "歇业后营业状态应为关闭");
        try {
            shop.buyAnimal(new Rabbit("团团", 1, "母"));
            throw new AssertionError("歇业后不应继续买入动物");
        } catch (IllegalStateException expected) {
            // 歇业后的交易应被拒绝。
        }
        System.out.println("所有测试通过");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
