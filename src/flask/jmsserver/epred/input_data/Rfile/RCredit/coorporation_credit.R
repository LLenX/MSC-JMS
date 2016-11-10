coorc<-function(u)
{
  
  n<-1#参与评估的小组个数
  lamda<-c(0.5)#参与评估的小组的权重
  rank_of_coorporation<-c(0)#初始化每个小组的总评分
  #初始化单项评分
  a1<-c(0)
  a2<-c(0)
  a3<-c(0)
  
  for (i in 1:n)
  {
    
    a1[i]<-u$调度合作记录
    a2[i]<-u$需求侧管理
    a3[i]<-u$电费退补记录
    
  }
  a1<-as.numeric(a1)
  a2<-as.numeric(a2)
  a3<-as.numeric(a3)
  
  rank_of_safty<-t(as.matrix(wei6,ncol=1))%*%t(cbind(a1,a2,a3))%*%as.matrix(lamda,ncol=1)
  
  a1=t(as.matrix(a1))%*%as.matrix(lamda)
  a2=t(as.matrix(a2))%*%as.matrix(lamda)
  a3=t(as.matrix(a3))%*%as.matrix(lamda)
  
  coorc<-data.frame(
    sche_coor=a1,#调度合作记录
    DSM=a2,#需求侧管理
    ele_fee_charge=a3,#电费退补记录
    rank_ofsafty=rank_of_safty
  )
  
}