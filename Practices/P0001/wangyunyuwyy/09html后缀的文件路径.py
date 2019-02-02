import os
filepath=input()
def print_dir(filepath):
    for i in os.listdir(filepath):
        path=os.path.join(filepath,i)
        if os.path.isdir(path):
            print_dir(path)
        if path.endswith(".html"):
            print(path)
print_dir(filepath)