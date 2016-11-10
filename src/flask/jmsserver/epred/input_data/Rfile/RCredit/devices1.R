devi1<-function()
{
  
  print("请输入用户电气设备指标得分：配电间基本情况，电气设备基本情况，安全用具及规程，电气设备维护情况")
  
  edittor<-edit(rdev)
  rank_of_devices<-edittor$配电间基本情况*wei4[1,]+edittor$电气设备基本情况*wei4[2,]+edittor$安全用具及规程*wei4[3,]+edittor$电气设备维护情况*wei4[4,]
  
  devi1<-data.frame(
    distribution_room_cond=edittor$配电间基本情况,
    ele_devices_conditon=edittor$电气设备基本情况,
    safty_devices_regu=edittor$安全用具及规程,
    ele_devices_keep=edittor$电气设备维护情况,
    rank_ofdevices=rank_of_devices
  )
}