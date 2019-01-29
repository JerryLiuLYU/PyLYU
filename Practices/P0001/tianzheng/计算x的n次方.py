x,n=input().split() 
a=1
s=1
while a<=int(n):
    s*=eval(x)
    a+=1
print(s)
'''x,n=map(eval,input().split()) 
a=1
s=1
while a<=n:
    s*=x
    a+=1
print(s)'''
