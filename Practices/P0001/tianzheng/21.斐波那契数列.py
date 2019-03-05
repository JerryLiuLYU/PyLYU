def a(n):
    if n<2:
        return n
    else:
         return (a(n-1)+a(n-2))
c=int(input())
print(a(c))