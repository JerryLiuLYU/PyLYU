num = int(input())
if num % 400 == 0 or num % 4 == 0 and num % 100 != 0:
    print("是闰年")
else:
    print("不是闰年")