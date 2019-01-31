num = int(input("请输入一个数字："))
factoral = 1


if num<0:
    print("抱歉，负数没有阶乘")
elif num == 0:
    print("0的阶乘为1")

else:
    for i in range(1,num + 1):
        factoral =factoral *i
    print(factoral)
