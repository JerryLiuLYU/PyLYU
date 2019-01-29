import random
num = [random.randint(1,100) for i in range(10)]
print(num)
a = num[0]
for j in range(1,10):
    if num[j]>a:
        a = num[j]
print(a)