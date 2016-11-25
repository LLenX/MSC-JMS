# JMS电量网页预测软件详细需求文档

## 用户部分

用户权限暂时只有一种，创建用户需要在一个单独的用户管理软件，连接数据库进行创建，指定创建用户的用户名与密码。

### 用户管理程序

用户管理程序是一个符合用户使用直觉的，只提供给佳木斯系统管理员的电脑使用的程序。提供用户的增加和删除功能。并且可以导入对应各种预测方法输入数据，符合规范的表格，导入到数据库。（表格具体需求的格式在下文中详细阐述）

打开软件之后，上面是一个用户列表，可以增加和删除用户。增加用户时，需要输入要增加的用户的用户名和密码，下方是一个导入各种预测方法输入数据的表格的按钮（本团队不负责不规范格式的表格的处理），选择对应的预测方法后，弹出一个文件选择框，以供管理员选择对应的表格文件。（后导入的表格文件数据会覆盖之前导入的表格文件的数据）。

## 网页部分

进入网页后首先会进入登录界面，需要输入用户名与密码，之后点击登录按钮进行登录。如果失败，出于安全考虑，会提示用户名密码错误。如果成功，将跳转到预测方法选择的界面，选择预测电量的方法。

预测电量有三种方法

### 基于自然增长率的预测方法

- 全社会电量预测
    + 全年度预测
    + 半年度预测
    + 季度预测
- 分镇街预测
    + 全年度预测
    + 半年度预测

### 剔除行业特殊组合的预测方法

- 全社会电量预测
    + 全年度预测
    + 半年度预测
    + 季度预测

需要剔除行业特殊组合的预测方法不提供分镇街预测

### 结合专家修改的预测方法

- 全社会电量预测
    + 全年度预测
    + 半年度预测
    + 季度预测
- 分镇街预测
    + 全年度预测
    + 半年度预测

对于每一种预测方法，选择对应选项（选择全社会或分镇街预测，选择预测的时间，对于剔除行业特殊组合，还需要选择剔除的行业）之后，点击提交选择按钮，后台将进行短暂计算

如果计算失败（例如输入数据不足，或预测时间是过去时间点等），则会在错误信息一栏用红色字体输出错误的调试信息，需要重新选择输入选项；

如果计算成功，会在原网页下方呈现预测报告。对于结合专家修改的预测方法，这时有两个按钮可以选择：其一，确认修改按钮，用户在输入框中修改对应时间的预测结果，点击此按钮，后台将重新生成报告内容，并重新呈现在网页下方；其二，保存最终数据按钮，用户点击后将当前数据结果（或修改结果）保存至数据库，之后相同时间的数据将不再提供修改功能。对于所有预测结果，将提供下载报告按钮，点击之后可以下载对应预测方式所生成的.docx格式的报告。

注：

- 对于任何的分镇街预测，所有镇街预测生成的曲线将全部呈现在网页下方。
- 对于基于自然增长率的预测方法和剔除行业特殊组合的预测方法，生成结果不可被修改。

## 用电数据表格格式

### 全社会电量预测

**除了剔除行业特殊组合预测方法以外**的全社会电量预测的输入数据表格格式必须按照原需求文档的格式给出
（表格格式为`佳木斯供电公司售电量预测与分析软件开发需求V3.0`第二页中4(1)1)中1) `2016年9月前五年28个供电单位每月售电量数据`下方表）

### 分镇街预测

输入数据表格式必须按照打包jms文件夹下，2015数据子文件夹中的`电量明细2012-2015.xls`的格式给出

### 剔除行业特殊组合的预测方法

输入数据表格式必须按照打包jms文件夹下，2015数据子文件夹中的`用电行业分类2012-2015.xls`的格式给出

**注意，表格字段必须严格按照该表给出，不得增删任何行业，也不得改动任何大行业下的小行业，也即不得改变表中任何行业的次序。**如果需要有此类必要改动，则需要另外拟定一份用于剔除行业特殊组合预测方法的固定、可行、有规律的表格格式规范。否则如果表格格式没有规律，就无法编程自动抽取。

## 备注

任何在上面没有被注明或提到的功能，实现细节都由本团队自行确定。
