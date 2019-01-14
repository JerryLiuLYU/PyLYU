def Calu(*args):
    '''计算a*a + b*b + c*c + ……'''
    total = 0
    for item in args:
        total = total + item * item
    return total
lst = [1, 2, 3, 4]
print(Calu(*lst))