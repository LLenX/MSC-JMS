townPred<-function(ipadd,opadd,timelength,numjump, modify_list = list()){
  
##
## 时间：7-14
##
## 时间：7-15
## 增加跳月预测的功能，在一年未完结的情况下进行下一年度的预测。
##
##
# ipadd = "C:/Users/Nelson/Desktop/Rdata/"
# opadd = "D:/7-14check/"
# timelength = 12
# numjump=2
  
library(forecast)
#读取数据
##1.分镇街用电量历史数据
# towndata = read.csv(choose.files(),header = T)
data = read.csv(paste(ipadd,"Predict/town/town.csv",sep=""),header = T)
#data=towndata[1:24,]
#realdata=towndata[25:28,]
##4.历史数据开始时间,预测开始时间（开始时间由java提供）
#starttime = read.csv(choose.files(),header = F)
starttime = read.csv(paste(ipadd,"Predict/town/starttime.csv",sep=""),header = F)
predata = c()

for(id in 1:length(data[1,])){
  total=data[,id]
  totalts = ts(total,frequency = 12,start = c(starttime[1,1],starttime[1,2]))
  hotmodel = HoltWinters(totalts,start.periods = 3,seasonal = "multiplicative")
  fore = forecast.HoltWinters(hotmodel,h=(timelength+numjump))
  
    #add by stardust
	if(id <= length(modify_list)){
		for(i in 1:length(fore$mean)){
			if(i > length(modify_list[[id]])){
				break;
			}
			if(modify_list[[id]][i] >= 0){
				fore$mean[i] = modify_list[[id]][i]
			}
		}
	}
	#end
  
  png(file=paste(opadd,"Pred/town/",colnames(data)[id],'.png',sep=""))
  plot(ts(c(data[,id],fore$mean),frequency = 12,start = c(starttime[1,1],starttime[1,2])),
       main=colnames(data)[id],xlab="时间",ylab="用电量")
  lines(ts(fore$mean[(numjump+1):length(fore$mean)],frequency = 12,start = c(starttime[2,1],starttime[2,2])),col=2)
  dev.off()
  predata = rbind(predata,sum(fore$mean[(numjump+1):length(fore$mean)]))
  rm(fore)
}
pred.frame = data.frame(predata)
rownames(pred.frame)= colnames(data)
colnames(pred.frame)= c(paste(timelength,"个月预测值",sep = ""))
write.csv(pred.frame,paste(opadd,"Pred/town/分镇街预测结果.csv",sep=""),fileEncoding = "GB18030")
}