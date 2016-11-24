townCheck<-function(ipadd,opadd,timelength){
library(forecast)
## 修改时间 7-27
## 根据timelength自动的分割历史数据与预测数据范围

#读取数据
##1.分镇街用电量历史数据
# towndata = read.csv(choose.files(),header = T)
# data = read.csv(paste(ipadd,"town/town.csv",sep=""),header = T)
# timelength=4#测试用
# ipadd = "C:/Users/Nelson/Desktop/Rdata/"
# opadd = "D:/test/R4java/"
towndata = read.csv(paste(ipadd,"Check/town/town.csv",sep=""),header = T)
# data=towndata[1:24,]#现在只有1415的数据，所以只用两年
# realdata=towndata[25:(24+timelength),]
data = towndata[1:(length(towndata[,1])-timelength),]
realdata = towndata[(length(towndata[,1])-timelength+1):length(towndata[,1]),]
# data = towndata[1:36,]
# realdata = towndata[37:(36+timelength),]
##4.历史数据开始时间,预测开始时间（开始时间由java提供）
# starttime = read.csv(choose.files(),header = F)
starttime = read.csv(paste(ipadd,"Check/town/starttime.csv",sep=""),header = F)
# predata = c()
sumerr = c()
townratio = c()
for(id in 1:length(data[1,])){
  total=data[,id]
  totalts = ts(total,frequency = 12,start = c(starttime[1,1],starttime[1,2]))
  hotmodel = HoltWinters(totalts)
  fore = forecast.HoltWinters(hotmodel,h=timelength)
  # png(file=paste('D:\\test\\R4java\\分镇街预测\\',colnames(data)[id],'.png',sep=""))
  png(file=paste(opadd,"PCheck/town/",colnames(data)[id],'.png',sep=""))
  plot(ts(towndata[,id],frequency = 12,start = c(starttime[1,1],starttime[1,2])),
       main=paste(colnames(data)[id],"预测结果"),xlab="时间",ylab="用电量")
  lines(ts(fore$mean,frequency = 12,start = c(starttime[2,1],starttime[2,2])),col=2)
  dev.off()
  # predata = rbind(predata,as.vector(fore$mean))
  sumerr = rbind(sumerr,abs(sum(fore$mean-realdata[,id])/sum(realdata[,id])))
  townratio = rbind(townratio,sum(data[,id])/sum(data))
}

err.frame = data.frame(sumerr,townratio)
colnames(err.frame) = c("总误差","占比")
rownames(err.frame) = colnames(towndata)


#计算
#1.各个镇街用电量占比
#2.用电量预测误差在各个区间内的镇街数目，占比，用电量占比
# num.na = length(which(is.na(sumerr)))
# numratio.na = num.na/length(data[1,])
# powratio.na = sum(townratio[which(is.na(sumerr))])

num.good = length(which(sumerr<=0.02))
numratio.good = num.good/length(data[1,])
powratio.good = sum(townratio[which(sumerr<=0.02)])
vec.good = c(num.good,numratio.good,powratio.good)

num.soso = length(which(sumerr>0.02 & sumerr<=0.05))
numratio.soso = num.soso/length(data[1,])
powratio.soso = sum(townratio[which(sumerr>0.02 & sumerr<=0.05)])
vec.soso = c(num.soso,numratio.soso,powratio.soso)

num.bad = length(which(sumerr>0.05))
numratio.bad = num.bad/length(data[1,])
powratio.bad = sum(townratio[which(sumerr>0.05)])
vec.bad = c(num.bad,numratio.bad,powratio.bad)

ratio.frame = data.frame(vec.good,vec.soso,vec.bad)
colnames(ratio.frame) = c("预测准确","预测较准确","不准确")
row.names(ratio.frame) = c("镇街数","镇街数占比","用电量占比")

write.csv(err.frame,paste(opadd,"PCheck/town/towncheck.csv",sep=""),fileEncoding = "GB18030")
write.csv(ratio.frame,paste(opadd,"PCheck/town/towncheckratio.csv",sep=""),fileEncoding = "GB18030")
}