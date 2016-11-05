import xlrd

data = xlrd.open_workbook('test.xls')
table = data.sheets()[0]
nrows = table.nrows
for i in range(nrows):
	if i == 0:
		continue
	print table.row_values(i)[:13]