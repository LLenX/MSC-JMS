# coding: utf-8

from JmsDAO import JmsDAO
import os
from EpredOption import *

dao = JmsDAO(db_name='data', username='test', password='password')

option = AREA_ALL | DURATION_ALL_YEAR | TIME_FIRST_HALF

dataHelper = dao.get_data_helper(option)

# 以下语句只需运行一次~ 将数据导入到数据库
dataHelper.import_input_files('E:\\data\\')

dataHelper.prepare_input_files('D:\\data\\')

# 这里调用EpredCaller运行epred
# 另外Model和Rfile文件夹需要自行复制
os.system(r'java -jar xxx.jar -i D:\data\ -o D:\output\ -p-area all -p-duration annual -p-year 2016 -rr')

dataHelper.collect_output_files('D:\\output\\')

# 将数据库保存的输出文件导出到 D:\output_clone
dataHelper.export_output_files('D:\\output_clone\\')

dao.close()
