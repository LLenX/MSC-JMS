接收参数"aa"可用request.form.get("aa")。
因为request.form["aa"]只能接受form中的数据，
如果是别的方式添加的数据就会返回错误400。
接收文件用request.files["file"]。


00对应第0行第0列的数据
request.form.get("11")得到精度校验下的分类下的字符串内容，即:
"全社会用电量"或者"分镇街用电量"或者"副模型"


