import calendar
year,month = input().split()
year,month = eval(year),eval(month)
out = calendar.monthcalendar(year,month)
print(out)