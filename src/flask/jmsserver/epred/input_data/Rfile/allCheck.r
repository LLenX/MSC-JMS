allCheck<-function(ipadd,opadd,timelength,needsmooth){

## 修改时间 7-27
## 根据timelength自动判断需要预测的月份
## 修改时间 7-15 凌晨
## 添加needsmooth判断是否需要平滑和逆平滑。若不需要，则平滑逆平滑操作，以及部分的数据读取都省略
##
  
###使用Holt-Winters模型并对春节期间用电量进行修正的模型
library(forecast)
#读取数据
##1.历史数据
# timelength = 12
# ipadd = 'C:/Users/Nelson/Desktop/Rdata/'
# opadd = 'D:/7-14check/'
# needsmooth=1
# alldata = read.csv(choose.files(),header = T)
alldata = read.csv(paste(ipadd,"Check/all/all.csv",sep=""),header = T, fileEncoding = "GB18030")
# data = alldata[1:36,]
data = alldata[1:(length(alldata[,1])-timelength),]
# realdata = alldata[37:(36+timelength),]
realdata = alldata[(length(alldata[,1])-timelength+1):length(alldata[,1]),]


  ##2.春节影响期（下标，影响期首尾下标。由java程序提供）
  ##
  ##小心spring和resmo出现空文件的错误，后期要添加trycatch
  ##
  # spring = read.csv(choose.files(),header = F)
  spring = read.csv(paste(ipadd,"Check/all/spring.csv",sep=""),header = F,fileEncoding = "GB18030")

if(needsmooth==1){
  ##3.预测月份中需要逆平滑的月份及天数（下标，影响期首尾下标）
  # resmo = read.csv(choose.files(),header = F)
  resmo = read.csv(paste(ipadd,"Check/all/resmo.csv",sep=""),header = F,fileEncoding = "GB18030")
}

##4.历史数据开始时间,预测开始时间（开始时间由java提供）
# starttime = read.csv(choose.files(),header = F)
starttime = read.csv(paste(ipadd,"Check/all/starttime.csv",sep=""),header = F,fileEncoding = "GB18030")

total=data
#用16上半年真实值检验预测结果，修改参数
#realdata=read.csv(choose.files(),header = T) 检验，确定参数
#realdata=realdata[,1]
##春节用电量修正
sj = exp(seq(-0.03,-0.012,length.out = 14))#参数仍可以继续调
# sj = exp(seq(-0.03,-0.008,length.out = 18))
spr.smooth=c(sj[order(-sj[2:8])],sj)
  
for(i in 1:length(spring[,1])){
  # total[spring[i,1]]=total[spring[i,1]]/cumprod(spr.smooth[spring[i,2]:spring[i,3]])[spring[i,3]-spring[i,2]+1]
  total[spring[i,1]]=total[spring[i,1]]/(mean(spr.smooth[spring[i,2]:spring[i,3]])^(spring[i,3]-spring[i,2]+1))
}



##利用历史数据预测未来12月用电量
totalts = ts(total,frequency = 12,start = c(starttime[1,1],starttime[1,2]))
hotmodel = HoltWinters(totalts)
fore = forecast.HoltWinters(hotmodel,h=timelength)

if(needsmooth==1){
  for(i in 1:length(resmo[,1])){
    # fore$mean[resmo[i,1]]=fore$mean[resmo[i,1]]*
    #   cumprod(spr.smooth[resmo[i,2]:resmo[i,3]])[resmo[i,3]-resmo[i,2]+1]
    fore$mean[resmo[i,1]]=fore$mean[resmo[i,1]]*
      (mean(spr.smooth[resmo[i,2]:resmo[i,3]])^(resmo[i,3]-resmo[i,2]+1))
  }
}

#plot.ts(total)
bias=abs(fore$mean[1:timelength]-realdata)/realdata
mb=mean(bias)
sumerr=abs(sum(fore$mean[1:timelength])-sum(realdata))/sum(realdata)

#write.csv(fore$mean,'D:\\test\\R4java\\pred.csv',row.names = F)
#png(file='D:\\test\\R4java\\allPred.png')
#plot.ts(total)
#rect(1,5,3,7,col="white")
#dev.off()
#plot(ts(total,frequency = 12,start = c(2013,1),end = c(2015,12)),)
#lines(fore$fitted,col=2)

# write.csv(fore$mean,'D:\\test\\R4java\\allpred.csv',row.names = F)
monthcheck.frame = data.frame(starttime[2,2]:(starttime[2,2]+timelength-1),fore$mean,realdata,bias)
colnames(monthcheck.frame)=c("月份","预测值","真实值","月误差")
all.frame = data.frame(mb,sumerr)
colnames(all.frame) = c("月平均误差","总误差")

##输出
write.csv(monthcheck.frame,paste(opadd,"PCheck/all/monthcheck.csv",sep=""),row.names = F,fileEncoding = "GB18030")
write.csv(all.frame,paste(opadd,"PCheck/all/allcheck.csv",sep=""),row.names = F,fileEncoding = "GB18030")
png(file=paste(opadd,"PCheck/all/monthbias.png",sep=""))

# plot(ts(realdata,frequency = 12,start = c(starttime[2,1],starttime[2,2])),
      # main="全社会用电量预测值与真实值",xlab="时间",ylab="用电量")
#lines(ts(fore$mean,frequency = 12,start = c(starttime[2,1],starttime[2,2])),col=2)
ts1<-ts(realdata,frequency = 12,start = c(starttime[2,1],starttime[2,2]))
ts2<-ts(fore$mean,frequency = 12,start = c(starttime[2,1],starttime[2,2]))
ymax<-max(c(realdata,fore$mean))
ymin<-min(c(realdata,fore$mean))
plot(as.Date(ts1),realdata,type="l",ylim=c(ymin,ymax),xaxt = "n",
     main="全社会用电量预测值与真实值",xlab="时间",ylab="用电量")
axis(1,at=as.Date(ts1),labels = format(as.Date(ts1),"%Y-%m"))
lines(as.Date(ts2),fore$mean,type="l",col=2)

dev.off()
}