#写文件
with open("text.txt","wt") as out_file:
    out_file.write("改文件会写入到文件中\n看到我了吧！")
#Read a file
with open("text.txt","rt") as in_file:
    text=in_file.read()

print(text)