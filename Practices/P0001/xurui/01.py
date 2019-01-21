list = [56,12,1,8,354,10,100,34,56,7,23,456,234,-58]
def sortport():
    for i in range(len(list)-1):
        for j in range(len(list)-1-i):
            if list[j] > list[j+1]:
                list[j],list[j+1] = list[j+1],list[j]
    return list