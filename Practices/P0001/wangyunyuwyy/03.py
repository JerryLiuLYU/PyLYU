arr=input()
num=[int(n) for n in arr.split()]
def calc(*num):
    sum=0
    for i in num:
        sum=sum+i*i
    return sum
a=calc(*num)
print(a)