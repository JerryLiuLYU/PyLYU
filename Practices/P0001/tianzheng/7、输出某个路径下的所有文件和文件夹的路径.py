import os
def print_dir():
    filepath = input("请输入一个路径：")
    if filepath =="":
        print("输入正确路径")
    else:
        for i in os.listdir(filepath):
            print(os.path.join(filepath,i))
print(print_dir())