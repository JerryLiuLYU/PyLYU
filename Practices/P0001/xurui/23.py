a,b = input().split()
a,b = eval(a),eval(b)
if a>b:
    c = b
else:
    c = a
for i in range(1,c+1):
    if a%i==0 and b%i==0:
        d = i
print(d)