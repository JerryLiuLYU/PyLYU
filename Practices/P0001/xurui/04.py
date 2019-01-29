def fac(n):
    a = 1
    for i in range(1,n+1):
        a=a*i
    return a
n = int(input())
print(fac(n))