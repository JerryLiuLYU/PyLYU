import random,string
str1 = "0123456789"
str2 = string.ascii_letters
str3 = str1 + str2
ma = random.sample(str3,6)
print(ma)
ma = ''.join(ma)
print(ma)