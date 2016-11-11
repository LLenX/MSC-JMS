reacom<-function(u)
{
  
  nn<-u$一年力率奖励次数
  if(nn>=8)
  {score=100}else if(nn==7)
  {score=90}else if(nn==6)
  {score=80}else if(nn==5)
  {score=70}else if(nn==4)
  {score=60}else
  {score=0}
  reacom<-data.frame(reward_time=nn,rank_ofcoorperation_credit=score)
  
}