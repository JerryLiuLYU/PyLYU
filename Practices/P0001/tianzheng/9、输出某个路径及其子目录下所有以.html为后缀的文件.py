import os
def print_dir(filepath):
    for i in os.listdir(filepath):
        path=os.path.join(filepath,i)
        if os.path.isdir(path):
            print_dir(path)
        if path.endswith(".html"):
            print(path)
filepath="C:\Users\TianZheng\Documents\Tencent Files\1098744966\FileRecv"
print_dir(filepath)