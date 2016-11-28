#coding: utf-8
from AllPred import AllPred
from TownPred import TownPred
class  NPredMain:
    def __init__(self,initpath,outputpath):
        """
        :param initpath:输入数据所在文件夹
        :param outputpath: 输出数据所在文件夹，该文件夹就是包含Data4r report result 这三个文件夹那个根目录
        """
        self.initpath = initpath
        self.outputpath = outputpath

    def A_Y_pred(self,year,startmonth,endmonth):
        """
        基于自然增长率的全社会用电量预测
        :param year: 预测年份
        :param startmonth: 该年中需要预测的起始月
        :param endmonth: 该年中需要预测的终止月
        :return:
        """
        self.Apredictor = AllPred(self.initpath,self.outputpath)
        self.Apredictor.pred(year,startmonth,endmonth)

    def T_Y_pred(self,year,startmonth,endmonth):
        """
        基于自然增长率的分镇街用电量预测
        :param year: 预测年份
        :param startmonth: 该年中需要预测的起始月
        :param endmonth: 该年中需要预测的终止月
        :return:
        """
        self.Tpredictor = TownPred(self.initpath,self.outputpath)
        self.Tpredictor.pred(year,startmonth,endmonth)
