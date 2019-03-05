for i in range(1,10):
    for j in range(1,i+1):
        print('{}*{}={}'.format(i,j,i*j),end=' ')#end作用：在末尾添加字符串，并使其不换行
        #print('%d*%d=%d'%(j,i,j*i),end='')
    print()
    #print(' ')
    #print('\n')