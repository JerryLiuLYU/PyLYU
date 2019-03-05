a,b=map(eval,input().split())
if a>b:
    m=b
else:
    m=a
for i in range(1,m+1):
    if a%i==0 and b%i==0:
        t=i
print(t)