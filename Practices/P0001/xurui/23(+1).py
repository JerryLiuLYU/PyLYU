a,b = input().split()
a,b = eval(a),eval(b)
if a>b:
    c = a
else:
    c = b
for i in range(c,a*b+1):
    if i%a==0 and i%b==0:
        d = i
        break
print(d)