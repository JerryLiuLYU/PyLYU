def power(x, n):
    return x ** n
x = eval(input("输入x"))
n = eval(input("输入n"))
print("{}的{}次方为{}".format(x,n,power(x,n)))