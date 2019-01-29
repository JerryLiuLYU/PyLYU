import random
list1 = []
for i in range(65,91):   #大写字母
    list1.append(chr(i))
for j in range(97,123):   #小写字母
    list1.append(chr(j))
for k in range(48,58):   #数字
    list1.append(chr(k))
ma = random.sample(list1,6)
print(ma)
ma = ''.join(ma)
print(ma)