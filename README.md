# 宠物店考核作业

使用 Java 17 编写的宠物店示例，包含动物抽象类、中华田园犬、猫、兔子、顾客、宠物店接口与实现，以及两种业务异常。

## 自己动手测试

在项目根目录打开 PowerShell，编译后运行交互入口：

```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java '-Dfile.encoding=UTF-8' -cp out com.sonata36.petshop.PetShopConsole
```

启动时输入开店余额，然后按照菜单进货、招待顾客、查看库存与利润、歇业。选“重新开店”可以换一个余额再次尝试。想验证异常，可用 50 元开店买入 100 元的狗，或者在库存为空时招待顾客。重复输入同一顾客名字可检查到店次数是否累计。输入 `0` 退出；输入不是有效数字时会提示重新输入。

如果使用 IntelliJ IDEA，也可以将 `src` 标记为 Sources Root，直接运行 `PetShopConsole.main()`。

## 固定功能检查

完成上面的编译后，在 PowerShell 中执行：

```powershell
java '-Dfile.encoding=UTF-8' -cp out com.sonata36.petshop.Test
```

`Test` 会演示进货、销售、缺货、余额不足和歇业，并在检查通过后输出“所有测试通过”。

## 业务约定

- 动物按照入库顺序出售；中华田园犬售价 100 元，猫售价 200 元，兔子售价 80 元。
- 顾客每次到店都会增加到店次数并留下独立记录，即使当时没有库存。歇业时只输出当日到店记录。
- 当日利润 = 当日销售额 − 当日买入成本。构造时提供的初始库存和余额属于开店前状态，不计入当日利润。
- 歇业后不再接受进货或顾客。业务代码使用题目正文中的 `AnimalNotFoundException`；另提供 `AnimalNotFountException` 兼容第 8 条的拼写。
