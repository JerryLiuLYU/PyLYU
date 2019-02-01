import os
def print_dir():
    filepath=input()
    if filepath == "":
        print("请输入正确的路径：")
    else:
        for i in os.listdir(filepath):
            print(os.path.join(filepath,i))
print(print_dir())