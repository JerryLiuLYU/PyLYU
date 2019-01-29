import os
def print_dir(filepath):
    for i in os.listdir(filepath):
        path = os.path.join(filepath,i)
        if os.path.isdir(path):
            print_dir(path)
        if path.endswith(".html"):
            print(path)
filepath = "d:"
print_dir(filepath)