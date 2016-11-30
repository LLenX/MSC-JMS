# coding: utf-8

from database.JmsDAO import JmsDAO
import os
from EpredOption import *
from ExcelFormat import ExcelFormat

dao = JmsDAO(db_name='data', username='test', password='password')

option = AREA_ALL | DURATION_ALL_YEAR

dataHelper = dao.get_data_helper(option)

# 以下语句只需运行一次~ 将数据导入到数据库
dao.import_all_input_files('E:\\data\\')

dataHelper.prepare_input_files('D:\\data\\')

# 这里调用EpredCaller运行epred
# 另外Model和Rfile文件夹需要自行复制
os.system(
    r'java -jar xxx.jar -i D:\data\ -o D:\output\ -p-area all -p-duration annual -p-year 2016 -rr')

dataHelper.collect_output_files('D:\\output\\')

# 将数据库保存的输出文件导出到 D:\output_clone
dataHelper.export_output_files('D:\\output_clone\\')

dao.close()

#转换excel格式
"""
一个转换excel格式的类
对外接口：change_excel_for_all()：转换成总用电量形式的表格，对应需求文档第一个表格
         change_excel_for_all：转换成分镇街用电量形式的表格，对应需求文档第二和第三个表格,
"""
change=ExcelFormat('C:\\Users\\freedom\\Desktop\\test1.xls','C:\\Users\\freedom\\Desktop\\test2.xlsx')
change.change_excel_for_all()#按全社会用电量转

change2=ExcelFormat('C:\\Users\\freedom\\Desktop\\电量明细2009-2015.xls','C:\\Users\\freedom\\Desktop\\test.xlsx')
change2.change_excel_for_town()#按分镇街转

change2=ExcelFormat('C:\\Users\\freedom\\Desktop\\用电行业分类2009-2015.xls','C:\\Users\\freedom\\Desktop\\test3.xlsx')
change2.change_excel_for_town()#按分镇街转
