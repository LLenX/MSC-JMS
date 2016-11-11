credit_ass<-function(souadd,ipadd,opadd){
  #创建一个用户并对其进行评估
  # souadd = 'D:/7-14check/RCredit/'
  # ipadd = 'C:/Users/Nelson/Desktop/Rdata/'
  # opadd = 'C:/Users/Nelson/Desktop/Rdata/'
  # source(paste(ipadd,'experience_matrix.R', encoding = 'GB18030'
  source(paste(souadd,'experience_matrix.R',sep=''), encoding = 'GB18030')

  source(paste(souadd,'AHP_weight.R',sep=''), encoding = 'GB18030')
  source(paste(souadd,'hara_weight.R',sep=''), encoding = 'GB18030')
  source(paste(souadd,'evalue_3.R',sep=''), encoding = 'GB18030')
  # source(paste(souadd,'AHP_weight.R',sep=''), encoding = 'GB18030')
  source(paste(souadd,'experience_matrix.R',sep=''), encoding = 'GB18030')
  # source(paste(souadd,'rank_of_operation.R',sep=''), encoding = 'GB18030')
  source(paste(souadd,'law_credit1.R',sep=''), encoding = 'GB18030')
  #source(paste(souadd,'devices1.R',sep=''), encoding = 'GB18030')  
  # source(paste(souadd,'safety1.R',sep=''), encoding = 'GB18030')  
  source(paste(souadd,'coorporation_credit.R',sep=''), encoding = 'GB18030')
  source(paste(souadd,'reactivecompen.R',sep=''), encoding = 'GB18030')
  #source(paste(souadd,'operation.R',sep=''), encoding = 'GB18030')
  source(paste(souadd,'experience_matrix.R',sep=''), encoding = 'GB18030')
  
  u<-read.csv(paste(ipadd,'user.csv',sep=''))
  l<-length(u[,1])
  
  users<-data.frame(
    用户名=c('0'),
    用户等级=c('0'),
    无功补偿=c('0'),
    法律信用=c('0'),
    用户电气设备情况=c('0'),
    安全评价=c('0'),
    合作信用=c('0'),
    经营能力=c('0')
    
    
  )
  
  
  eval3<-evalue3(u)
  
  for (i in 1:l)
  {
    users[i,]=users
    
  }
  
  
  users$用户等级=eval3$weight
  users$无功补偿=eval3$rank_of_reactivecompensation
  users$法律信用=eval3$rank_of_credit
  users$用户电气设备情况=eval3$rank_of_devices
  users$安全评价=eval3$rank_of_safty
  users$合作信用=eval3$rank_ofcoorperation_credit
  users$经营能力=eval3$rank_of_operation
  users$用户名=u$用户名
  
  
  write.csv(users,file=paste(opadd,'userassessment.csv',sep=''))
  
}
