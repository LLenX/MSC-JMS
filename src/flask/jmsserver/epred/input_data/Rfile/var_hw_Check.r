var_hw_Check<-function(ipadd,opadd){
#读取数据
# var_data = read.csv(choose.files(),header = T)
# ipadd = "C:/Users/Nelson/Desktop/Rdata/"
# opadd = "D:/test/R4java/"
var_data = read.csv(paste(ipadd,"Check/var/var.csv",sep=""),header = T) 
#需要春节平滑的数据
# spring = read.csv(choose.files(),header = F)
spring = read.csv(paste(ipadd,"Check/var/spring.csv",sep=""),header = F)

needsmooth=1

if(needsmooth==1){
  #需要逆平滑的数据
  # resmo = read.csv(choose.files(),header = F)
  resmo = read.csv(paste(ipadd,"Check/var/resmo.csv",sep=""),header = F)
}

#Granger test
#library(MSBVAR)
#lag3 = granger.test(var_data[1:24,1:5],3)
#fix(lag3)
# starttime = read.csv(choose.files(),header = F)
starttime = read.csv(paste(ipadd,"Check/var/starttime.csv",sep=""),header = F)
#平滑数据 全社会用电量
var_data.copy=var_data
sj = exp(seq(-0.03,-0.012,length.out = 14))
spr.smooth=c(sj[order(-sj[2:8])],sj)
for(i in 1:length(spring[,1])){
  var_data[spring[i,1],1]=var_data[spring[i,1],1]/(mean(spr.smooth[spring[i,2]:spring[i,3]])^(spring[i,3]-spring[i,2]+1))
}

#用dse包拟合数据
library(dse)
#ip 时序化的驱动因素
#op 时序化的用电量
#ip<-ts(var_data[1:24,2:5],frequency = 12,start = c(starttime[1,1],starttime[1,2]))
#op<-ts(var_data[1:24,1],frequency = 12,start=c(starttime,1))
ip<-ts(var_data[,2:5],frequency = 12,start = c(starttime[1,1],starttime[1,2]))
op<-ts(var_data[,1],frequency = 12,start=c(starttime[1,1],starttime[1,2]))
modeldata  = TSdata(input=ip,output = op)
var_model = estVARXls(modeldata,max.lag = 3)
# var_model
# var_resid = checkResiduals(var_model)
# bias_fitting = abs(var_resid$residuals)/var_data[1:22,1]
# bias_fitting
# mean(bias_fitting[4:22])
#进行预测
var_fore1 = dse::forecast(var_model,conditioning.inputs = ip)
# bias_fore1 = (var_fore1$forecast[[1]]-var_data[23:24,1])/var_data[23:24,1]
# bias_fore1

# 随机扰动测试稳定性
# num_outliners_0.05 = rep(0,30)
# num_outliners_0.1 = rep(0,30)
# for (ii in 1:30)
# {random_bias = rep(0,200)
# for (jj in 1:100)
# {ip<-ts(var_data[1:24,2:6],frequency = 12,start = c(2014,1))
# random_error = runif(10,-0.05,0.05)
# print(mean(abs(random_error)))
# ip[23:24,] =ip[23:24,]*(rep(1,10)+random_error)
# var_fore1 = dse::forecast(var_model,conditioning.inputs = window(ip,end = c(2015,12)))
# bias_fore1 = (var_fore1$forecast[[1]]-var_data[23:24,1])/var_data[23:24,1]
# print(bias_fore1)
# random_bias[(2*jj-1):(2*jj)] =as.numeric(bias_fore1) }
# num_outliners_0.05[ii] = length(which(abs(random_bias)>0.05))
# num_outliners_0.1[ii] = length(which(abs(random_bias)>0.1))
# }

#作图
#plot(op)
#lines(var_fore1$pred,col=2)
#lines(var_fore1$forecast[[1]],col=2)

#先利用Holt-winter预测经济数据
library(forecast)
# new_ip = var_data[,2:5]
# new_ip=matrix(0,nrow=length(var_data[,1])+6,ncol=length(var_data[,2:5])) 半年度
new_ip=matrix(0,nrow=length(var_data[,1])+12,ncol=length(var_data[,2:5]))
for (j in 1:ncol(ip))
{
  holtmodel = HoltWinters(ip[,j])
  pred = forecast.HoltWinters(holtmodel,h=12)
  new_ip[,j] = c(ip[,j],pred$mean)
}
new_ip = ts(new_ip,frequency = 12,start = c(starttime[1,1],starttime[1,2]))
var_fore2 = dse::forecast(var_model,conditioning.inputs = new_ip)
var_fore2 = as.numeric(var_fore2$forecast[[1]])

if(needsmooth==1){
  for(i in 1:length(resmo[,1])){
    var_fore2[resmo[i,1]]=var_fore2[resmo[i,1]]*(mean(spr.smooth[resmo[i,2]:resmo[i,3]])^(resmo[i,3]-resmo[i,2]+1))
  }
}


#将预测结果var_fore2与holt-winter模型预测结果比对
##Holt-Winter模型预测
# sj = exp(seq(-0.03,-0.012,length.out = 18))#参数仍可以继续调
# spr.smooth=c(sj[order(-sj[2:11])],sj)
total=var_data.copy[,1]
for(i in 1:length(spring[,1])){
  # total[spring[i,1]]=total[spring[i,1]]/cumprod(spr.smooth[spring[i,2]:spring[i,3]])[spring[i,3]-spring[i,2]+1]
  total[spring[i,1]]=total[spring[i,1]]/(mean(spr.smooth[spring[i,2]:spring[i,3]])^(spring[i,3]-spring[i,2]+1))
}
totalts = ts(total,frequency = 12,start = c(starttime[1,1],starttime[1,2]))
hotmodel = HoltWinters(totalts)
fore.hw = forecast.HoltWinters(hotmodel,h=12)

if(needsmooth==1){
  for(i in 1:length(resmo[,1])){
    # fore$mean[resmo[i,1]]=fore$mean[resmo[i,1]]*
    #   cumprod(spr.smooth[resmo[i,2]:resmo[i,3]])[resmo[i,3]-resmo[i,2]+1]
    fore.hw$mean[resmo[i,1]]=fore.hw$mean[resmo[i,1]]*
      (mean(spr.smooth[spring[i,2]:spring[i,3]])^(spring[i,3]-spring[i,2]+1))
  }
}


##对var_fore2与fore.hw结果进行比对，输出月偏差和总偏差
bias = abs(var_fore2-fore.hw$mean)/fore.hw$mean
error = abs(sum(var_fore2-fore.hw$mean))/sum(fore.hw$mean)
mbias = mean(bias)
arbit.frame = t(data.frame(c(error,mbias,bias)))
write.csv(arbit.frame,paste(opadd,"PCheck/var/var模型与Holt-Winter模型预测结果比较.csv",sep=""),fileEncoding = "GB18030")
# colnames(arbit.frame) = c("总偏差","平均月偏差","第一月偏差","第二月偏差","第三月偏差"
#                           ,"第四月偏差","第五月偏差","第六月偏差")
file=paste(opadd,"全社会预测/allPred.png",sep="")
png(file=paste(opadd,"PCheck/var/var模型与Holt-Winter模型预测结果比较.png",sep=""))
# png(file='D:\\test\\R4java\\VAR分析\\var预测结果.png')
# plot(ts(fore.hw$mean,frequency = 12,start = c(starttime[2,1],starttime[2,2])),main='var模型与Holt-Winter模型预测结果比较',xlab="时间",ylab="用电量")
# lines(ts(var_fore2,frequency = 12,start = c(starttime[2,1],starttime[2,2])),col=2)
ts1<-ts(fore.hw$mean,frequency = 12,start = c(starttime[2,1],starttime[2,2]))
ts2<-ts(var_fore2,frequency = 12,start = c(starttime[2,1],starttime[2,2]))
ymax<-max(c(fore.hw$mean,var_fore2))
ymin<-min(c(fore.hw$mean,var_fore2))
plot(as.Date(ts1),fore.hw$mean,type="l",ylim=c(ymin,ymax),
     main="全社会用电量预测值与真实值",xlab="时间",ylab="用电量")
lines(as.Date(ts2),var_fore2,type="l",col=2)

dev.off()
##输出结果
#1.六个月预测值
#2.方程参数
# varpred = var_fore2
# varY=as.vector(var_model$model$A)
# coefX=as.vector(var_model$model$C)
# varX1=coefX[1:3]
# varX2=coefX[4:6]
# varX3=coefX[7:9]
# varX4=coefX[10:12]
# var.frame=data.frame(varpred)
# colnames(var.frame)=c("var预测结果")
# coef.frame=data.frame(varY,c(0,varX1),c(0,varX2),c(0,varX3),c(0,varX4))
# write.csv(var.frame,"D:\\test\\R4java\\VAR分析\\varpred.csv")
# write.csv(coef.frame,"D:\\test\\R4java\\VAR分析\\varcoef.csv")
# write.csv(var.frame,paste(opadd,"VAR分析/varpred.csv",sep=""))
# write.csv(coef.frame,paste(opadd,"VAR分析/varcoef.csv",sep=""))

#error_fore2 = var_fore2-var_data[23:28,1]
#bias_fore2 = abs(error_fore2)/var_data[23:28,1]
#bias_fore2
#mean(bias_fore2)
#sum(error_fore2)/sum(var_data[23:28,1])
#作图
#plot(var_data[23:28,1],type = "l")
#lines(var_fore2,col=2)
# file=paste(opadd,"全社会预测/allPred.png",sep="")
# png(file=paste(opadd,"VAR分析/var预测结果.png",sep=""))
# # png(file='D:\\test\\R4java\\VAR分析\\var预测结果.png')
# plot(ts(c(var_data.copy[,1],var_fore2),frequency = 12,start = c(starttime[1,1],starttime[1,2])),main='VAR模型预测结果',xlab="时间",ylab="用电量")
# lines(ts(var_fore2,frequency = 12,start = c(starttime[2,1],starttime[2,2])),col=2)
# dev.off()
}