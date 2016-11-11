allPred<-function(ipadd,opadd,timelength,needsmooth,numjump){

##
## 时间 7-14
## 加上时间长度以及是否需要平滑，若不需要平滑，则无需读取resmo，也无需对数据进行逆平滑
##
## 时间 7-16
## 加上“跳月”的功能，在一年未完结的时候进行下一年的预测。
## 
##

# ipadd = "G:/Test/Data/"
# opadd = "D:/test/R4java/"
# timelength = 12
# numjump = 0
# needsmooth=1
###使用Holt-Winters模型并对春节期间用电量进行修正的模型
library(forecast)
#读取数据
##1.历史数据
#data = read.csv(choose.files(),header = T)
data = read.csv(paste(ipadd,"Predict/all/all.csv",sep=""),header = T)  


##2.春节影响期（下标，影响期首尾下标。由java程序提供）
#spring = read.csv(choose.files(),header = F)
  spring = read.csv(paste(ipadd,"Predict/all/spring.csv",sep=""),header = F)

if(needsmooth==1){
##3.预测月份中需要逆平滑的月份及天数（下标，影响期首尾下标）
#resmo = read.csv(choose.files(),header = F)
  resmo = read.csv(paste(ipadd,"Predict/all/resmo.csv",sep=""),header = F)
}
##4.历史数据开始时间,预测开始时间（开始时间由java提供）
#starttime = read.csv(choose.files(),header = F)
starttime = read.csv(paste(ipadd,"Predict/all/starttime.csv",sep=""),header = F)

total=data[,1]
#用16上半年真实值检验预测结果，修改参数
#realdata=read.csv(choose.files(),header = T) 检验，确定参数
#realdata=realdata[,1]
##春节用电量修正
# sj = exp(seq(-0.032,-0.012,length.out = 14))#参数仍可以继续调
# spr.smooth=c(sj[order(-sj[2:8])],sj)
  
#增加到28天
sj = exp(seq(-0.03,-0.012,length.out = 14))#参数仍可以继续调
spr.smooth=c(sj[order(-sj[2:8])],sj)
for(i in 1:length(spring[,1])){
  # total[spring[i,1]]=total[spring[i,1]]/cumprod(spr.smooth[spring[i,2]:spring[i,3]])[spring[i,3]-spring[i,2]+1]
  total[spring[i,1]]=total[spring[i,1]]/(mean(spr.smooth[spring[i,2]:spring[i,3]])^(spring[i,3]-spring[i,2]+1))
}



##利用历史数据预测未来12月用电量
totalts = ts(total,frequency = 12,start = c(starttime[1,1],starttime[1,2]))
hotmodel = HoltWinters(totalts)
fore = forecast.HoltWinters(hotmodel,h=(timelength+numjump))
if(needsmooth==1){
  for(i in 1:length(resmo[,1])){
    # fore$mean[resmo[i,1]]=fore$mean[resmo[i,1]]*
    #   cumprod(spr.smooth[resmo[i,2]:resmo[i,3]])[resmo[i,3]-resmo[i,2]+1]
    fore$mean[resmo[i,1]+numjump]=fore$mean[resmo[i,1]+numjump]*
      (mean(spr.smooth[resmo[i,2]:resmo[i,3]])^(resmo[i,3]-resmo[i,2]+1))
  }
}

#plot.ts(total)
#bias=abs(fore$mean[1:6]-realdata)/realdata
#mean(bias)
#abs(sum(fore$mean[1:6])-sum(realdata))/sum(realdata)

#write.csv(fore$mean,'D:\\test\\R4java\\pred.csv',row.names = F)
#png(file='D:\\test\\R4java\\allPred.png')
#plot.ts(total)
#rect(1,5,3,7,col="white")
#dev.off()
#plot(ts(total,frequency = 12,start = c(2013,1),end = c(2015,12)),)
#lines(fore$fitted,col=2)
# write.csv(fore$mean,'D:\\test\\R4java\\allpred.csv',row.names = F)

year = fore$mean[(numjump+1):length(fore$mean)]
#输出
write.csv(year,paste(opadd,"Pred/all/allpred.csv",sep=""),row.names = F,fileEncoding = "GB18030")
png(file=paste(opadd,"Pred/all/allPred.png",sep=""))
plot(ts(c(data[,1],fore$mean),frequency = 12,start = c(starttime[1,1],starttime[1,2])),
     main="全社会用电量历史值与预测值",xlab="时间",ylab="用电量")
lines(ts(fore$mean[(numjump+1):length(fore$mean)],frequency = 12,start = c(starttime[2,1],starttime[2,2])),col=2)
dev.off()
}