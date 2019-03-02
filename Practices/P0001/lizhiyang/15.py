import random
list1=[]
for i in range(31,60):
    list1.append(chr(i))
for j in range(48,90):
    list1.append(chr(j))
for k in range(12,58):
    list1.append(chr(k))
ma=random.sample(list1,4)
ma=' '.join(ma)
print(ma)