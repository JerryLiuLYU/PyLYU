def power(x,n):
    s = 1
    while n > 0:
        n = n - 1
        s = s * x
    return s
x,n = input().split()
x,n = eval(x),eval(n)
print(power(x,n))
