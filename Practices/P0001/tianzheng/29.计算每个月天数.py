import calendar,datetime
today1=datetime.date.today()
monthRange=calendar.monthrange(today1.year,today1.month)
print(monthRange)
