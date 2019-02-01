with open("test.txt","wt") as out_file:
    out_file.write("hello world")
with open("test.txt","rt") as in_file:
    test=in_file.read()
print(test)