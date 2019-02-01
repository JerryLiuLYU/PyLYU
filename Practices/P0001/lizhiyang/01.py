import random
a=input()
a=int (a)
lis = [random.randint(0,100) for i in range(a)]
print(lis)
for i in range(len(lis)-1):
    for j in range(len(lis)-1-i):
        if lis[j] > lis[j+1]:
            lis[j],lis[j+1] = lis[j+1],lis[j]
print(lis)
