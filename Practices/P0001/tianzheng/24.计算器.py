'''def suan(a):
    return eval(a)
a=input()
print(suan(a))'''

def jia(x,y):
    return x+y
def jian(x,y):
    return x-y
def cheng(x,y):
    return x*y
def chu(x,y):
    return x/y
print("选择计算\n")
print("1加\n")
print("2减\n")
print("3乘\n")
print("4除\n")
choice=input("输入选择：1/2/3/4\n")
num1=int(input("输入第一个数字\n"))
num2=int(input("输入第二个数字\n"))
if choice=='1':
    print(num1,'+',num2,'=',jia(num1,num2))
elif choice=='2':
    print(num1,'-',num2,'=',jian(num1,num2))
elif choice=='3':
    print(num1,'*',num2,'=',cheng(num1,num2))
elif choice=='4':
    if num2==0:#区分字符零
        print("分母不能为零")
    else:
        print(num1,'/',num2,'=',chu(num1,num2))
else:
    print("非法输出")