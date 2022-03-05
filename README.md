# 定时输入投放工具

面向对象课程电梯作业的辅助工具，根据静态的含有时间信息的输入文件，向**实时交互程序**的标准输入中**定时投放**输入内容，免去手动向控制台输入的麻烦。

本工具也可用于测试其他控制台实时交互的程序。

## 输入格式

以行为单位，每行的格式为 `[<time>]<text>` ，表示在运行开始后的 `<time>` 时间（单位为秒）向被测试程序输入一行 `<text>`。

例：被测程序需要在运行开始后第 `1.0` 秒输入一行 `1-FROM-1-TO-2`，第 `2.0` 秒输入一行 `2-FROM-1-TO-3`，则本工具所需的输入内容应为（以下内容推荐存储在文件中）:

    [1.0]1-FROM-1-TO-2
    [2.0]2-FROM-1-TO-3

在运行开始第 `2.0` 秒后，即被测程序读出一行 `2-FROM-1-TO-3` 后将立即读到 `EOF`，从而不再继续等待输入。

## 使用方法

本工具既可导入到自己的工程中作为工具类使用，也可作为一个完整可执行的程序借助管道使用。

对于个人作业而言，可将该工具的 Jar 包导入到 IDEA 工程中：

- 在 IDEA 主界面上方菜单栏点击 `File` \-\> `Project Structure`
- 在弹出的窗口左侧选择 `Libraries`
- 点击上方 `+` 号，选择本工具的 Jar 包，将其添加入工程中。

本工具只含有一个 `TimeInput` 类，该类的部分源码:

```java
public class TimeInput {

    // 构造方法, 传入一个 InputStream 对象, 为静态的含有时间戳的输入, 格式见本文上一节
    public TimeInput(InputStream inputStream) { /* ... */ }

    // 返回一个 InputStream 对象，将上述静态的输入转化为实时的输入
    public InputStream getTimedInputStream() { /* ... */ }
}
```

使用示例 (以 `new TimeInput(System.in).getTimedInputStream()` 替换 `System.in`):

```java
import oo.util.TimeInput;

import java.util.Scanner;

public class Main {
    // Solution of A + B problem with multiple inputs.
    public static void main(String[] args) {
        // Scanner cin = new Scanner(System.in);
        Scanner cin = new Scanner(new TimeInput(System.in).getTimedInputStream());
        while (cin.hasNext()) {
            int a = cin.nextInt();
            int b = cin.nextInt();
            System.out.println(a + b);
        }
    }
}
/*
Sample content of `input.txt`:
[1.0]1 2
[2.0]2 3
*/
```

本工具也可直接执行，用来在互测中测试他人的程序。

示例命令 (从 `input.txt` 中读取静态的输入内容, 并通过管道发射给 `Elevator.jar`):

```shell
java -jar TimeInput.jar < input.txt | java -jar Elevator.jar 
```

## 注意事项

- 该工具仅限本地调试使用。提交的作业**不应依赖**本工具。
- 推荐采用**导入 Jar 包**方式使用该工具，不建议直接将本工具源代码添加到个人作业中。
- <font color="red">**严禁**</font>将本工具（包括 Jar 包以及源代码）提交到作业仓库中，如因此导致被查重系统判定为抄袭，后果自负。
