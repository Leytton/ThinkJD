# V1.4.6_17更新日志
1、清除MySQL8的驱动名
2、修复execute函数没有初始化连接问题

# V1.4.5_16更新日志
清除MySQL的jar包，添加Oracle驱动名

# V1.4.5_15更新日志
1、添加setInc(String,BigDecimal)和setDec(String,BigDecimal)方法用于小数自增减
2、fetchSql调试模式直接抛异常不继续向下执行

# V1.4.4_13更新日志
修复某个bug

# V1.4.4_12更新日志
支持Connection的复用，可手动关闭并且确保多线程安全

# V1.4.4_11更新日志
1、转化为UTF-8编码
2、转化为maven项目并提交到中央仓库
3、重写find与select查询内部方法,使其支持基于DBUtil查询的字段注解

# V1.4.2_10更新日志
1、bean模式可指定更新字段
2、用户配置>注解>D全局设置

# V1.4.1_8更新日志
 1、增加class或bean自动获取表名、字段、数据和名称、主键、是否自增注解功能
 
 2、表名、主键和自增可通过table、pk、autoInc函数动态设置，函数设置优先级>注解配置
 
 3、增加setInc、setDec函数用于数据库字段自增或自减
 
 4、增加asc、desc排序函数
 
 5、添加fetchSql=true调试模式中输出占位参数功能
 
 6、拆分field()函数为field()和data()，前者参数为","拼接的字段名字符串，后者参数为数据
