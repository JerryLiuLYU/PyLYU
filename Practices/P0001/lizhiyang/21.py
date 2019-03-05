n = int(input())
a,b = 0,1
num = [a,b]
for i in range(n):
    c = a+b
    num.append(c)
    a,b = b,a+b
print(num)