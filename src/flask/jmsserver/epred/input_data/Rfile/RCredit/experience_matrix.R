expr<-function(n,m)
{
  #n是专家的个数
  #m是变量的个数
  df<-matrix(0,n,m)
  
  print("Please enter whether you want to edit the experience matrix! 1 for yes, 0 for no.")
  K<-readline()
  
  if(K==1)
    df<-edit(df)#输入的是每个因子
  
  
  weight<-matrix(0,m,1)#初始化权重
  
  for (i in 1:m)
  {
    weight[i,1]<-mean(df[,i])
    
  }
  
  exp<-data.frame(weight=weight)
}