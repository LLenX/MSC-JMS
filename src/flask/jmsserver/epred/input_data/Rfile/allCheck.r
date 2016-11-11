allCheck<-function(ipadd,opadd,timelength,needsmooth){

## �޸�ʱ�� 7-27
## ����timelength�Զ��ж���ҪԤ����·�
## �޸�ʱ�� 7-15 �賿
## ���needsmooth�ж��Ƿ���Ҫƽ������ƽ����������Ҫ����ƽ����ƽ���������Լ����ֵ����ݶ�ȡ��ʡ��
##
  
###ʹ��Holt-Wintersģ�Ͳ��Դ����ڼ��õ�������������ģ��
library(forecast)
#��ȡ����
##1.��ʷ����
# timelength = 12
# ipadd = 'C:/Users/Nelson/Desktop/Rdata/'
# opadd = 'D:/7-14check/'
# needsmooth=1
# alldata = read.csv(choose.files(),header = T)
alldata = read.csv(paste(ipadd,"Check/all/all.csv",sep=""),header = T)
# data = alldata[1:36,]
data = alldata[1:(length(alldata[,1])-timelength),]
# realdata = alldata[37:(36+timelength),]
realdata = alldata[(length(alldata[,1])-timelength+1):length(alldata[,1]),]


  ##2.����Ӱ���ڣ��±꣬Ӱ������β�±ꡣ��java�����ṩ��
  ##
  ##С��spring��resmo���ֿ��ļ��Ĵ��󣬺���Ҫ���trycatch
  ##
  # spring = read.csv(choose.files(),header = F)
  spring = read.csv(paste(ipadd,"Check/all/spring.csv",sep=""),header = F)

if(needsmooth==1){
  ##3.Ԥ���·�����Ҫ��ƽ�����·ݼ��������±꣬Ӱ������β�±꣩
  # resmo = read.csv(choose.files(),header = F)
  resmo = read.csv(paste(ipadd,"Check/all/resmo.csv",sep=""),header = F)
}

##4.��ʷ���ݿ�ʼʱ��,Ԥ�⿪ʼʱ�䣨��ʼʱ����java�ṩ��
# starttime = read.csv(choose.files(),header = F)
starttime = read.csv(paste(ipadd,"Check/all/starttime.csv",sep=""),header = F)

total=data
#��16�ϰ�����ʵֵ����Ԥ�������޸Ĳ���
#realdata=read.csv(choose.files(),header = T) ���飬ȷ������
#realdata=realdata[,1]
##�����õ�������
sj = exp(seq(-0.03,-0.012,length.out = 14))#�����Կ��Լ�����
# sj = exp(seq(-0.03,-0.008,length.out = 18))
spr.smooth=c(sj[order(-sj[2:8])],sj)
  
for(i in 1:length(spring[,1])){
  # total[spring[i,1]]=total[spring[i,1]]/cumprod(spr.smooth[spring[i,2]:spring[i,3]])[spring[i,3]-spring[i,2]+1]
  total[spring[i,1]]=total[spring[i,1]]/(mean(spr.smooth[spring[i,2]:spring[i,3]])^(spring[i,3]-spring[i,2]+1))
}



##������ʷ����Ԥ��δ��12���õ���
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
colnames(monthcheck.frame)=c("�·�","Ԥ��ֵ","��ʵֵ","�����")
all.frame = data.frame(mb,sumerr)
colnames(all.frame) = c("��ƽ�����","�����")

##���
write.csv(monthcheck.frame,paste(opadd,"PCheck/all/monthcheck.csv",sep=""),row.names = F,fileEncoding = "GB18030")
write.csv(all.frame,paste(opadd,"PCheck/all/allcheck.csv",sep=""),row.names = F,fileEncoding = "GB18030")
png(file=paste(opadd,"PCheck/all/monthbias.png",sep=""))

# plot(ts(realdata,frequency = 12,start = c(starttime[2,1],starttime[2,2])),
      # main="ȫ����õ���Ԥ��ֵ����ʵֵ",xlab="ʱ��",ylab="�õ���")
#lines(ts(fore$mean,frequency = 12,start = c(starttime[2,1],starttime[2,2])),col=2)
ts1<-ts(realdata,frequency = 12,start = c(starttime[2,1],starttime[2,2]))
ts2<-ts(fore$mean,frequency = 12,start = c(starttime[2,1],starttime[2,2]))
ymax<-max(c(realdata,fore$mean))
ymin<-min(c(realdata,fore$mean))
plot(as.Date(ts1),realdata,type="l",ylim=c(ymin,ymax),xaxt = "n",
     main="ȫ����õ���Ԥ��ֵ����ʵֵ",xlab="ʱ��",ylab="�õ���")
axis(1,at=as.Date(ts1),labels = format(as.Date(ts1),"%Y-%m"))
lines(as.Date(ts2),fore$mean,type="l",col=2)

dev.off()
}