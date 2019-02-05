def beishu(x,n):
    if x>n:
        x,n=n,x
    for i in range(x,x*n+1):
        if i%x==0 and i%n==0:
            return i
x,n=input().split()
x,n=int(x),int(n)
print(beishu(x,n))