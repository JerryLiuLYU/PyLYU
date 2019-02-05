import random
a=[random.randint(0,100) for i in range(10)]
def sort(a):
    for i in range(len(a)-1):
        for j in range(len(a)-1-i):
            if a[j]>a[j+1]:
                a[j],a[j+1]=a[j+1],a[j]
sort(a)
print(a)