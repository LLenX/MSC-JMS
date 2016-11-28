#coding: utf-8
import os
import xlrd
import math
import csv
import xlsxwriter
"""
基于自然增长率的全社会预测的类
"""
class AllPred:
    def __init__(self, initpath, outputpath):
        """
        :param initpath:输入数据所在文件夹
        :param outputpath: 输出数据所在文件夹
        """
        self.initpath = initpath
        self.outputpath = outputpath

    def pred(self, year, startmonth, endmonth):
        """
        对指定时间区间进行预测
        :param year:预测年份
        :param startmonth:预测起始月
        :param endmonth:预测终止月
        :return:
        """
        flag = 1
        flag = self.initialized()
        if flag == 0:
            print("没有输入文件")
            return

        flag = self.getdata()
        if flag == 0:
            print("输入数据不合法,请检查输入文件")
            return

        flag = self.calc(year, startmonth, endmonth)
        if flag == 0:
            print("输入数据不足，无法完成预测")
            return
        if flag == -1:
            print("该需要预测的时间区间的数据已全部存在，无需预测")
            return

        self.csvwrite()
        print("计算完成")

    def initialized(self):
        """
        初始化输入输出地址
        :return:
        """

        self.initpath = '\\'.join([self.initpath,"总用电量.xlsx"])
        if  ( os.path.exists(self.initpath) and os.path.isfile(self.initpath) ) == False:
            return 0
        self.read_workbook = xlrd.open_workbook(self.initpath, encoding_override="utf-8")
        self.read_sheet = self.read_workbook.sheets()[0]
        self.read_nrows = self.read_sheet.nrows
        self.read_ncols = self.read_sheet.ncols

        self.outputpath = '\\'.join([self.outputpath, "result"])
        if ( (os.path.exists(self.outputpath) == False) or (os.path.isfile(self.outputpath)) ):
            os.makedirs(self.outputpath)
        self.outputpath = '\\'.join([self.outputpath, "Pred"])
        if ((os.path.exists(self.outputpath) == False) or (os.path.isfile(self.outputpath))):
            os.makedirs(self.outputpath)
        self.outputpath = '\\'.join([self.outputpath, "all"])
        if ((os.path.exists(self.outputpath) == False) or (os.path.isfile(self.outputpath))):
            os.makedirs(self.outputpath)


        print("输入输出地址初始化成功")
        return 1

    def getdata(self):
        """
        读取数据，判断是否有非法数据
        :return:
        """
        self.monlist = []
        self.datalist = []
        for row in range(1,self.read_nrows):
            if (self.read_sheet.cell(row,0).ctype != xlrd.XL_CELL_NUMBER):
                return 0;
            if (self.read_sheet.cell(row,1).ctype != xlrd.XL_CELL_NUMBER):
                return 0;
            month = AllPred.cell_to_str(self.read_sheet.cell(row,0))
            data =  AllPred.cell_to_str(self.read_sheet.cell(row,1))
            self.monlist.append(int(AllPred.monthform(month)))
            self.datalist.append(float(data))

    def clacmonth(self,month):
        """
        计算某个月的预测结果
        :param month:要计算的月，如201607
        :return:
        """
        data1 = data2 = 0.0
        lastmonth = self.findlastmonth(month)
        if lastmonth in self.monlist:
            data1 = self.datalist[self.monlist.index(lastmonth)]
        else:
            return 0
        lastmonth = self.findlastmonth(lastmonth)
        lastmonth = self.findlastmonth(lastmonth)
        if lastmonth in self.monlist:
            data2 = self.datalist[self.monlist.index(lastmonth)]
        else:
            return 0
        x = math.sqrt(data1 / data2)
        self.result.append( [str(data1*x)] )
        return 1

    def calc(self,year,startmonth,endmonth):
        """
        对某个时间区间内进行预测计算,结果储存在self.result中
        :param year:预测年份
        :param startmonth:预测起始月
        :param endmonth:预测终止月
        :return:
        """
        while True:
            month=year*100+startmonth
            if (month in self.monlist):
                startmonth += 1
            else:
                break
        self.result = []
        for i in range(startmonth,endmonth+1):
            month = year*100+i
            flag = self.clacmonth(month)
            if flag==0:
                return 0
        if len(self.result) == 0:
            return -1
        return 1

    def csvwrite(self):
        """
        将self.result中的结果写进csv中
        :return:
        """
        csv_writer = csv.writer(open("".join([self.outputpath,"/allpred.csv"]), mode="w", newline="\n", encoding="utf-8"))
        self.result.insert(0,["x"])
        csv_writer.writerows(self.result)

    def findlastmonth(self,month):
        year = month //100
        mon = month %100
        return (year-1)*100+mon

    @staticmethod
    def monthform(month):
        month_list = list(month)
        if "." in month_list:
            month_list = month_list[0:month_list.index(".")]
        return "".join(month_list)

    @staticmethod
    def cell_to_str(cell):
        if cell.ctype == xlrd.XL_CELL_TEXT:
            return cell.value
        else:
            return str(cell.value)


