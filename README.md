# 宠物店考核作业

使用 Java 17 编写的宠物店示例，包含动物抽象类、中华田园犬、猫、兔子、顾客、宠物店接口与实现，以及两种业务异常。

## 运行

在 PowerShell 中执行：

```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp out com.sonata36.petshop.Test
```

`Test` 会演示进货、销售、缺货、余额不足和歇业，并在检查通过后输出“所有测试通过”。

## 业务约定

- 动物按照入库顺序出售；中华田园犬售价 100 元，猫售价 200 元，兔子售价 80 元。
- 顾客每次到店都会增加到店次数并留下独立记录，即使当时没有库存。歇业时只输出当日到店记录。
- 当日利润 = 当日销售额 − 当日买入成本。构造时提供的初始库存和余额属于开店前状态，不计入当日利润。
- 歇业后不再接受进货或顾客。业务代码使用题目正文中的 `AnimalNotFoundException`；另提供 `AnimalNotFountException` 兼容第 8 条的拼写。
