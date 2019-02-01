n=input().split()
def find(*n):
    sum=0
    for i in n:
        if i>'0' and i<'9':
            sum=sum
        else:
            sum=sum+1
    if sum!=0:
        a='yes'
        return a
print(find(*n))