a=input()
def s(c):
    try:
        float(c)#用int对小数出现错误
        return True
    except:
        return False
print(s(a))