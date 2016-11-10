#this function is to calculate the weight by AHP algorithm
AHP<-function(a,n,ri)
  
{
  
  #normalization a
  cols<-colSums(a)
  b<-t(t(a)/cols)
  
  index<-rowSums(b)/sum(rowSums(b))
  
  #judge the largest eigvalue of a
  as.matrix(index)
  AW<-a%*%index
  lamda<-1/n*sum(AW/index)
  C<-(lamda-n)/(n-1)#lamda is the means of eigvalue of reciprocal matrix
  R<-ri
  CR<-C/R
  
  #show wether it pass consistency test
  judgeweight<-matrix(0,1,n)
  if(CR<0.1)
  {
    judgeweight<-index
    
  }else
  {
    judgeweight<-expr(5,n)$weight
  }
  wfun<-data.frame(weight=judgeweight)
}
