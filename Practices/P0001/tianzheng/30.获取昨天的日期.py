import datetime
def getYesterday():
    today=datetime.date.today()
    oneday=datetime.timedelta(days=1)
    yesterday=today-oneday
    return yesterday
print(getYesterday(),'\n',datetime.date.today())
print("{}\n{}".format(getYesterday(),datetime.date.today()))
print("{}'\n'{}".format(getYesterday(),datetime.date.today()))

from datetime import*
def getYesterday():
    today=date.today()
    oneday=timedelta(days=1)
    yesterday=today-oneday
    return yesterday
print(getYesterday(),'\n',date.today())

