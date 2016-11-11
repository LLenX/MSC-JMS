allPred<-function(ipadd,opadd,timelength,needsmooth,numjump){

##
## ʱ�� 7-14
## ����ʱ�䳤���Լ��Ƿ���Ҫƽ����������Ҫƽ�����������ȡresmo��Ҳ��������ݽ�����ƽ��
##
## ʱ�� 7-16
## ���ϡ����¡��Ĺ��ܣ���һ��δ����ʱ�������һ���Ԥ�⡣
## 
##

# ipadd = "G:/Test/Data/"
# opadd = "D:/test/R4java/"
# timelength = 12
# numjump = 0
# needsmooth=1
###ʹ��Holt-Wintersģ�Ͳ��Դ����ڼ��õ�������������ģ��
library(forecast)
#��ȡ����
##1.��ʷ����
#data = read.csv(choose.files(),header = T)
data = read.csv(paste(ipadd,"Predict/all/all.csv",sep=""),header = T)  


##2.����Ӱ���ڣ��±꣬Ӱ������β�±ꡣ��java�����ṩ��
#spring = read.csv(choose.files(),header = F)
  spring = read.csv(paste(ipadd,"Predict/all/spring.csv",sep=""),header = F)

if(needsmooth==1){
##3.Ԥ���·�����Ҫ��ƽ�����·ݼ��������±꣬Ӱ������β�±꣩
#resmo = read.csv(choose.files(),header = F)
  resmo = read.csv(paste(ipadd,"Predict/all/resmo.csv",sep=""),header = F)
}
##4.��ʷ���ݿ�ʼʱ��,Ԥ�⿪ʼʱ�䣨��ʼʱ����java�ṩ��
#starttime = read.csv(choose.files(),header = F)
starttime = read.csv(paste(ipadd,"Predict/all/starttime.csv",sep=""),header = F)

total=data[,1]
#��16�ϰ�����ʵֵ����Ԥ�������޸Ĳ���
#realdata=read.csv(choose.files(),header = T) ���飬ȷ������
#realdata=realdata[,1]
##�����õ�������
# sj = exp(seq(-0.032,-0.012,length.out = 14))#�����Կ��Լ�����
# spr.smooth=c(sj[order(-sj[2:8])],sj)
  
#���ӵ�28��
sj = exp(seq(-0.03,-0.012,length.out = 14))#�����Կ��Լ�����
spr.smooth=c(sj[order(-sj[2:8])],sj)
for(i in 1:length(spring[,1])){
  # total[spring[i,1]]=total[spring[i,1]]/cumprod(spr.smooth[spring[i,2]:spring[i,3]])[spring[i,3]-spring[i,2]+1]
  total[spring[i,1]]=total[spring[i,1]]/(mean(spr.smooth[spring[i,2]:spring[i,3]])^(spring[i,3]-spring[i,2]+1))
}



##������ʷ����Ԥ��δ��12���õ���
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
#���
write.csv(year,paste(opadd,"Pred/all/allpred.csv",sep=""),row.names = F,fileEncoding = "GB18030")
png(file=paste(opadd,"Pred/all/allPred.png",sep=""))
plot(ts(c(data[,1],fore$mean),frequency = 12,start = c(starttime[1,1],starttime[1,2])),
     main="ȫ����õ�����ʷֵ��Ԥ��ֵ",xlab="ʱ��",ylab="�õ���")
lines(ts(fore$mean[(numjump+1):length(fore$mean)],frequency = 12,start = c(starttime[2,1],starttime[2,2])),col=2)
dev.off()
}