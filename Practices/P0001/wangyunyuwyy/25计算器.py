def add(x,y):
    return x+y
def jian(x,y):
    return x-y
def cheng(x,y):
    return x*y
def chu(x,y):
    return x/y
x,a,y=input().split()
x,a,y=int(x),str(a),int(y)
if a=='+':
    print(add(x,y))
elif a=='-':
    print(jian(x,y))
elif a=='*':
    print(cheng(x,y))
else:
    print(chu(x,y))