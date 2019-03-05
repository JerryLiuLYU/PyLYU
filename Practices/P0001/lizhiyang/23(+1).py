def lcm(x,y):
    if x>y:
        greater = x
    else:
        greater = y
    while(True):
        if((greater%x==0)and(greater%y == 0)):
            lcm = greater
            break
        greater += 1

num1 = int(input("输入第一个数字："))
num2 = int(input("输入第二个数字："))
print(lcm(num1,num2))
