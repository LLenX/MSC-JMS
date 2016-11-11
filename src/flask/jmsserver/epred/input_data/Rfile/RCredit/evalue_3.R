evalue3<-function(u)
{
  
  
  
  operval<-u$经营能力
  
  lawval<-lawc1(u)
  
  devval<-u$用户电气设备
  
  safval<-u$安全评价
  
  cooval<-coorc(u)
  
  recval<-reacom(u)
  
  rank_of_user=as.numeric(operval)*wei[1,]+recval$rank_ofcoorperation_credit*wei[2,]+
    lawval$rank_ofcredit*wei[3,]+as.numeric(devval)*wei[4,]+
    as.numeric(safval)*wei[5,]+lawval$rank_ofcredit*wei[6,]
  
  evalue3<-data.frame(
    
    rank_of_operation=operval,
    
    sche_coor=cooval$sche_coor,#调度合作记录
    DSM=cooval$DSM,#需求侧管理
    ele_fee_charge=cooval$ele_fee_charge,#电费退补记录
    rank_ofcoorperation_credit= cooval$rank_ofsafty,
    
    illigal_usage=lawval$illigal_usage,#违章用电情况·
    ele_fee_payment=lawval$ele_fee_payment,#电费缴纳情况
    rank_of_credit=lawval$rank_ofcredit,
    
    rank_of_devices=devval,
    
    rank_of_safty= safval,
    
    sche_coor=cooval$sche_coor,#调度合作记录
    DSM=cooval$DSM,#需求侧管理
    ele_fee_charge=cooval$ele_fee_charge,#电费退补记录
    rank_of_safty= cooval$rank_ofsafty,
    
    reward_time=recval$reward_time,#奖励次数
    rank_of_reactivecompensation=recval$rank_ofcoorperation_credit,
    
    weight=rank_of_user
  )
  
}

