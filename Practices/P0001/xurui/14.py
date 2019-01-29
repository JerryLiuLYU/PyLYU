import random
list1 = [random.randint(1,10) for i in range(5)]
print(list1)
list2 = [random.randint(1,10) for i in range(5)]
print(list2)
list3 = list1 + list2
print(list3)
print(set(list3))
print(list(set(list3)))