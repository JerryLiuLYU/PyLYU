def yueshu(x,n):
    if x>n:
        x,n=n,x
    for i in range(1,n+1):
        if x%i==0 and n%i==0:
            max=i
    return max
num1,num2=input().split()
num1,num2=int(num1),int(num2)
print(yueshu(num1,num2))
