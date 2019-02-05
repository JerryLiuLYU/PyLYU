n=int(input("您需要对比的数字的个数是："))
num=[int(input("请输入您要对比的数字：")) for i in range(1,n+1)]
print("您要对比的数字是：{}".format(num))
print("最大数字为：{}".format(max(num)))