# coding: utf-8

from JmsDAO import JmsDAO
from EpredOption import *

dao = JmsDAO('data')

option = AREA_ALL | DURATION_ALL_YEAR | TIME_FIRST_HALF

dataHelper = dao.get_data_helper(option)
dataHelper.prepare_input_files('D:\\data\\')

# 这里调用EpredCaller运行epred

# dataHelper.collect_output_files('D:\\result\\')

dao.close()
