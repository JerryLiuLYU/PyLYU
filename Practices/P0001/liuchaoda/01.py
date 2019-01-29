#冒泡排序
lis = [56,12,1,8,354,10,34,56,7,23,456,234,-58]

for i in range(len(lis)-1):
    for j in range(len(lis)-i-1):
        if lis[j]>lis[j+1]:
            lis[j],lis[j+1] = lis[j+1],lis[j]
print(lis,end='')