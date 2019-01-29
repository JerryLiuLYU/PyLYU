import random
def calc(*lis):
    a = 0
    for i in lis:
        a=a+i*i
    return a
lis = [random.randint(0,10) for j in range(10)]
print(lis)
print(calc(*lis))