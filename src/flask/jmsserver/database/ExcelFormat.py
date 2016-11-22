#coding: utf-8
import xlrd
import xlsxwriter
"""
一个转换excel格式的类
对外接口：change_excel_for_all()：转换成总用电量形式的表格，对应需求文档第一个表格
         change_excel_for_all：转换成分镇街用电量形式的表格，对应需求文档第二和第三个表格
"""
class ExcelFormat:

    def __init__(self,read_file_path,write_file_path):
        self.read_workbook = xlrd.open_workbook(read_file_path,encoding_override="utf-8")
        self.read_sheet = self.read_workbook.sheets()[0]
        self.read_nrows = self.read_sheet.nrows
        self.read_ncols = self.read_sheet.ncols
        self.write_workbook = xlsxwriter.Workbook(write_file_path)
        self.write_sheet = self.write_workbook.add_worksheet()

    def get_data(self, year, month):
        year_list = list(year)
        if "年" in year_list:
            year_list = year_list[0:year_list.index("年")]
        if "." in year_list:
            year_list = year_list[0:year_list.index(".")]
        month_list = list(month)
        if "月" in month_list:
            month_list = month_list[0:month_list.index("月")]
        if "." in month_list:
            month_list = month_list[0:month_list.index(".")]
        if len(month_list) == 1:
            month_list.insert(0, "0")
        return "".join(year_list) + "".join(month_list)

    def change_town_time(self):
        rowcount=1
        startcol=1
        if self.read_sheet.cell(0,startcol).value == "":
            startcol += 1
        self.write_sheet.write(0, 0, "时间")
        for col in range(startcol,self.read_ncols):
            if self.read_sheet.cell(0,col).value != "":
                for col2 in range(col,self.read_ncols):
                    if col2 != col and self.read_sheet.cell(0,col2).value != "":
                        break
                    data = self.get_data(ExcelFormat.cell_to_str(self.read_sheet.cell(0,col)),
                                    ExcelFormat.cell_to_str(self.read_sheet.cell(1,col2))
                                   )
                    self.write_sheet.write(rowcount,0,data)
                    rowcount += 1

    def change_town_name(self):
        colcount=1
        for row in range(2,self.read_nrows):
            data = ExcelFormat.cell_to_str(self.read_sheet.cell(row,0))
            self.write_sheet.write(0, colcount, data)
            colcount += 1

    def change_town_data(self):
        startcol = 1
        if self.read_sheet.cell(0, startcol).value == "":
            startcol += 1
        for row in range(2, self.read_nrows):
            for col in range(startcol,self.read_ncols):
                data = ExcelFormat.cell_to_str(self.read_sheet.cell(row,col))
                self.write_sheet.write(col-startcol+1,row-1 , data)

    def change_all_time(self):
        rowcount = 1
        self.write_sheet.write(0, 0, "时间")
        for col in range(1, self.read_ncols):
                for row in range(1, self.read_nrows):
                    if self.read_sheet.cell(row, col).value != "":
                        data = self.get_data(ExcelFormat.cell_to_str(self.read_sheet.cell(0, col)),
                                             ExcelFormat.cell_to_str(self.read_sheet.cell(row, 0))
                                             )
                    self.write_sheet.write(rowcount, 0, data)
                    rowcount += 1

    def change_all_data(self):
        rowcount=1
        for col in range(1, self.read_ncols):
            for row in range(1, self.read_nrows):
                if self.read_sheet.cell(row, col).value != "":
                    data = ExcelFormat.cell_to_str(self.read_sheet.cell(row, col))
                    self.write_sheet.write(rowcount, 1, data)
                    rowcount+=1

    def change_excel_for_all(self):
        self.change_all_time()
        self.change_all_data()
        self.write_workbook.close()

    def change_excel_for_town(self):
        self.change_town_time()
        self.change_town_name()
        self.change_town_data()
        self.write_workbook.close()

    @staticmethod
    def cell_to_str(cell):
        if cell.ctype == xlrd.XL_CELL_TEXT:
            return cell.value
        else:
            return str(cell.value)

