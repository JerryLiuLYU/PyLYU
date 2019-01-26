n=eval(input())
def fac(n):
    f=1
    if n==0 or n==1:
        return 1
    elif n>1:
        while n>0:
            f=f*n
            n=n-1
        return f
jc=fac(n)
print(jc)
