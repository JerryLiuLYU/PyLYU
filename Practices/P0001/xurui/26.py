with open("test.tex","wt") as out_file:
    out_file.write("hello")
with open("test.txt","rt") as in_file:
    text = in_file.read()
print(text)