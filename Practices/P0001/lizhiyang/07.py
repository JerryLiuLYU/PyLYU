import os
def print_dir():
    forder = input("请输入一个路径：")
    if forder == "":
        print("请输入正确的路径")
    else:
        for i in os.listdir(forder):
            print(os.path.join(forder,i))

print(print_dir())