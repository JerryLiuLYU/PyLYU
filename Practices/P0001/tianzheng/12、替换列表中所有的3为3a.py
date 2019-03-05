m=['ssass','vrtgbthby','43234543',323,3,5,43,3,3,'3']
print(m.count(3))#计算3的个数
print(m.index(3))#计算第一个三的位置
for i in range(m.count(3)):
    x=m.index(3)
    m[x]='3a'
    print(m)