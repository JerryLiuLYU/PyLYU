import random
num = [random.randint(1,11) for i in range(20)]
print(num)
for j in range(num.count(3)):
    ele_index = num.index(3)
    num[ele_index] = '3a'
print(num)