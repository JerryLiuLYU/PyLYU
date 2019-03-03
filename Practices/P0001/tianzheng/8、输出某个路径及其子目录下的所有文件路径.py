def show_dir(filepa):
    for i in os.listdir(filepa):
        path= (os.path.join(filepa,i))
        print(path)
        if os.path.isdir(path):
            show_dir(path)
filepat = "C:\Users\TianZheng\Documents\Tencent Files\1098744966\FileRecv"
show_dir(filepat)