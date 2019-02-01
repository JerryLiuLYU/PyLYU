arr=input()
num=[int(n) for n in arr.split()]
for i in range(num.count(3)):
    e_index=num.index(3)
    num[e_index]="3a"
print(num)