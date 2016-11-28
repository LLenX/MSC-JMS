#coding: utf-8
from NPredMain import NPredMain
"""
为方便起见，所有时间维度都统一接口
NpredMain(inputpath,outputpath)
    inputpath:包含几个输入数据文件的文件夹
    outputpath：包含Data4r Pred report result 四个文件夹的那个文件夹

为方便起见，所有时间维度都统一接口
例：2016年
全社会：A_Y_pred(year,startmonth,endmonth)
    全年：A_Y_pred(2016,1,12）
    上半年：A_Y_pred(2016,1,6）
    下半年：A_Y_pred(2016,7,12)
    第一季度：A_Y_pred(2016,1,3）
    后面同理
    最后得到一个allpred.csv

分镇街:T_Y_pred(year,startmonth,endmonth)
    使用方法同全社会
    最后得到分阵街预测结果.csv

程序运行只会生成一个存放预测结果的csv文件，若生成报告还需要生成其他文件，请联系程序员

根据java程序的预测规则，会从需要预测的区间的第一个没有数据的月份开始预测
比如 全社会预测2016年全年，已知有201201 到201606的数据，但是中间没有201603的数据
则会从2016年3月份开始预测，得到的结果是2016年3月到12月都是预测值（即使这之间有已存在的数据）

"""
example = NPredMain("C:\\Users\\freedom\\Desktop\\坑锅\\数据","C:\\Users\\freedom\\Desktop")

example.A_Y_pred(2016,1,12)
example.T_Y_pred(2016,1,12)