![ThinkJDBC](https://gitee.com/uploads/images/2018/0428/174620_372c5f0f_890966.png)

[![最新版本](https://img.shields.io/badge/%E6%9C%80%E6%96%B0%E7%89%88%E6%9C%AC-V1.4.2__10-green.svg?longCache=true&style=flat-square)](https://gitee.com/Leytton/ThinkJD) [![中文文档](https://img.shields.io/badge/%E4%B8%AD%E6%96%87%E6%96%87%E6%A1%A3-V1.4.2__10-green.svg?longCache=true&style=flat-square)](https://blog.csdn.net/Leytton/article/details/80021702) [![English Document](https://img.shields.io/badge/English%20Document-V1.3.1__8-yellowgreen.svg?longCache=true&style=flat-square)](https://blog.csdn.net/Leytton/article/details/80021029) [![CSDN Blog](https://img.shields.io/badge/CSDN%20Bolg-Leytton-red.svg?longCache=true&style=flat-square)](https://blog.csdn.net/Leytton)

# 1 简介

`ThinkJD`，又名`ThinkJDBC`，一个简洁而强大的开源JDBC操作库。你可以使用Java像`ThinkPHP`框架的M方法一样，`一行代码搞定数据库操作`。ThinkJD会自动管理数据库连接，使用完毕或程序异常都会关闭连接以免造成内存溢出。

**先睹为快：**
```
//数据库配置(只需调用一次)
D.setDbConfig("jdbc:mysql://127.0.0.1:3306/DbName?characterEncoding=UTF-8","root","root");

//JavaBean模式，自动获取表名、主键、自增属性、字段名和数据
User user = new User();
user.setAge(10);
user.setName("Hello");
user.setSex(true);
//插入数据
long id=D.M(user).add();
//查询数据
user=D.M(User.class).find(id);
//更新数据
user.setSex(false);
D.M(user).field("sex").save();//不指定字段名默认更新JavaBean的所有非空属性
//删除数据
D.M(user).delete();
//D.M(User.class).delete(id);

//Table模式，手动指定表名、主键、自增属性、字段名和数据
//插入数据
long id=D.M("user").field("name,weight").data("Tom",60).add();
//更新数据
D.M("user").field("name,weight").data("Tom",100).where("id=?",id).save();
//查询数据
user=D.M(User.class).find(id);
//删除数据
D.M("user").delete(id);
```

**项目主页** https://gitee.com/Leytton/ThinkJD (码云) https://github.com/Leytton/ThinkJD (Github)

**测试项目** https://github.com/Leytton/ThinkJD_Demo

# 2 使用方法

## 0x01 添加依赖
### 导入Jar包
[ThinkJDBC-x.x.x-full.jar](https://github.com/Leytton/ThinkJD/releases) 包含了ThinkJDBC-x.x.x-core.jar核心库和两个依赖库，只需要添加这一个jar包就行了

 - mysql-connector-java-5.1.39.jar
 - commons-dbutils-1.6.jar
 
或者
###Maven
```
<dependency>
    <groupId>com.llqqww</groupId>
    <artifactId>thinkjdbc</artifactId>
    <version>1.4.2_10</version>
</dependency>
```

 
## 0x02 定义数据库
ThinkJD支持直接定义用户名密码访问数据库，也支持使用Hikari、C3P0等数据库连接池。

**数据库连接方式有三种：**

### (1)配置文件方式
在项目根目录下添加文件(跟Hikari配置文件格式一样) 

程序第一次启动时会自动加载读取配置文件，如果文件不存在则忽略。【V1.2.4_5 增加功能】

`thinkjdbc.properties`
```
jdbcUrl = jdbc:mysql://127.0.0.1:3306/thinkjdbc?useUnicode=true&characterEncoding=UTF-8
dataSource.user = root
dataSource.password = root
```

### (2)帐号密码方式
```
D.setDbConfig("jdbc:mysql://127.0.0.1:3306/database?useUnicode=true&characterEncoding=UTF-8","root","root");
```

### (3)使用数据库连接池
例如使用Hikari连接池： 
```
HikariConfig config = new HikariConfig("/hikari.properties");
HikariDataSource dataSource = new HikariDataSource(config);
D.setDataSource(dataSource);
```
`注：如果定义了数据库连接池，ThinkJD会优先使用。`


### (3)配置表前缀
只需调用一次，配置表前缀不是必需的
```
D.setTablePrefix("jd_");
//D.M('user') D.M(User.class)将会操作 `jd_user` 表
```

> 注:`D.M('user').prefix('jd_')`方法可单独指定表前缀【V1.2.3新增】

## 0x03 连贯操作
| 操作| 参数| 示例 |说明| 
| ------------- |------------- |------------- | -------------
|`table`|table(String table) | table("user") | 
|`pk`|pk(String key) | pk("id") | 
|`autoInc`|autoInc(boolean isPkAutoInc) | autoInc(false) | 
|`join` |join(String join)| join("left join machine on user.id=user_id and machine_status=1")|
|`field`|field(String filed)| field("id,username")| 
|`data`|data(Object... dataParam)| data(11,"Leytton")| 
|`setInc`|setInc(String key,long num)| setInc("gold",5) //gold=gold+5|
|`setDec`|setDec(String key,long num)| setDec("gold",5) //gold=gold-5| 
|`where`|①where(String where)<br>②where(String where, Object... whereParam)|①where("id=1111 and username='Leytton'")<br> ②where("id=? and username=?",1111,"Leytton")
|`group`|group(String group)|group("type")
|`having`|having(String having)|having("id>1234")
|`order`|order(String order)|order("id desc")
|`asc`|asc(String key)|asc("id")
|`desc`|desc(String key)|desc("id")
|`page`|page(long page, long rows)|page(1,10)
|`limit`|①limit(long rows)<br>②limit(long offset, long rows)|①limit(10)<br>②limit(1,10)
|`union`|union(String union,Boolean isAll)|①union("select * from user_two where id>1234",false)<br>②union("select * from user_two where id>1234",true)

## 0x04 查询数据

| 操作| 参数| 说明|
| -------- |--------|--------
|select|①<`T`> List<`T`> select()<br>②<`T`> List<`T`> select(String key, Object value)
|find|①<`T`> T find()<br>②<`T`> T find(Object value)<br>③<`T`> T find(String key, Object value)
|count|①long count()<br>②long count(String field)
|max|double max(String field)
|min|double min(String field)
|avg|double avg(String field)
|sum|double sum(String field)

```
//find查询
//select id,name from jd_user where id>4 order by id asc limit 0,1
User res = D.M(User.class).field("id,name").where("id>?",4).order("id asc").find();

//find 根据id查询
//select * from jd_user where id=3 limit 0,1
User user = D.M(User.class).find(3);

//find根据字段查询
//select * from jd_user where name='Tom' limit 0,1
User user=D.M(User.class).fetchSql(true).find("name","Bob");

//where,field过滤
//select id,name,weight from jd_user where id>3
List<User> res = D.M(User.class).field("id,name,weight").where("id>3").select();

//group分组查询
//select sex,sum(weight) as weight,avg(age) as age,count(id) as num from jd_user where id>5 group by sex order by sex desc limit 0,10
res = D.M(User.class).field("sex,sum(weight) as weight,avg(age) as age,count(id) as num").where("id>?",5).group("sex").order("sex desc").page(1, 10).select();

//join联表查询
//select jd_user.id,name,weight,sum(gold) as num from jd_user left join jd_gold on user_id=jd_user.id where jd_user.id>3 group by jd_user.id
res = D.M(User.class).field("jd_user.id,name,weight,sum(gold) as num").join("left join jd_gold on user_id=jd_user.id").where("jd_user.id>3").group("jd_user.id").select();

//union联表查询
//(select id,name from jd_user where id=4 ) union all (select id,name from jd_user where id<3) union (select id,name from jd_user where id=3)
res = D.M(User.class).field("id,name").where("id=4").union("select id,name from jd_user where id<3",true)
	.union("select id,name from jd_user where id=3",false).select();

//统计查询
long num= new M(User.class).where("id>3").count();
num= D.M(User.class).fetchSql(true).where("id>3").count("id");
num= (long) D.M(User.class).fetchSql(false).where("id<0").max("id");
num= (long) D.M(User.class).where("id<3").max("id");
num= (long) D.M(User.class).min("id");
num= (long) D.M(User.class).where("id>3").min("id");
num= (long) D.M(User.class).fetchSql(false).where("id>3").avg("id");
double avg= D.M(User.class).fetchSql(false).where("id>3").avg("id");
num= (long) D.M(User.class).where("id>13441").sum("age");
```

> 通过调用`fetchSql(true)`方法，可以获取到 `ThinkJD`产生的SQL语句(Exception形式)并且不会执行数据库操作。
![fetchSql](https://img-blog.csdn.net/2018042023324417)

user表结构：

|字段名 | 数据类型| 备注|
|--------|--------|--------
|id |int | 用户id,自增长主键
|name | varchar | 用户名
|age |tinyint|年龄
|weight | float | 体重
|sex|tinyint|性别 0女/1男
|time|int|时间

`select()`和 `find()`查询结果封装到JavaBean里返回，JavaBean可使用注解映射数据库字段。

> `注意:墙裂建议JavaBean字段基础数据类型使用【Integer、Long、Boolean、Float、Double、Byte、Short、Char】不要使用【integer、long、boolean、float、double、byte、short、char】，因为前者可以赋值为null而后者不行(null时为0)，所以获取到的值是不准确的。ThinkJD的save更新等操作通过判断属性值不为null则加入数据库更新字段队列。ThinkJD会自动检测以上不符合的数据类型并发出警告。如需关闭调用D.setCheckField(false);`

```
//@Table(name="user")默认类名为表名,可注解重定义
public class User {
	//@Column(isKey=true)默认id为主键、isAutoInc=true自增,可注解重定义
	private Long id;
	private Integer age;
	//@Column(name="user_name")默认属性名为表字段,可注解重定义
	private String name;
	private Float weight;
	private Boolean sex;
	
	@Column(isColumn=false)
	private Integer num;
	
	private Long time;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Float getWeight() {
		return weight;
	}
	public void setWeight(Float weight) {
		this.weight = weight;
	}
	public Boolean getSex() {
		return sex;
	}
	public void setSex(Boolean sex) {
		this.sex = sex;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
}

```

## 0x05 插入数据
| 操作| 参数| 说明|
| -------- | -------- | --------
|add|long add()|Table模式前提方法:data()<br>返回自动生成的主键值;

```
/*指定插入字段*/
long id=D.M(User.class).field("name,weight").data("Tom",60).add();

/*不指定插入字段，按表字段顺序插入*/
id=D.M("user").data(null,"Tom",60,...).add();

/*使用javaBean半自动模式，自动获取表名、主键、字段名，给定data按javaBean属性顺序插入,生成的sql语句如下
 *insert into jd_user (age,name,weight,sex,time) values(?,?,?,...)
 */
id=D.M(User.class).data("Tom",60,...).add();

//使用javaBean全自动模式，自动获取表名、主键、字段名和数据
User user = new User();
user.setId(5);
user.setAge(10);
user.setName("Hello");

//insert into jd_user (age,name) values(?,?) Params[10,Hello]
num=D.M(user).add();

//insert into jd_user (name) values(?) Params[Hello]
num=D.M(user).field("name").add();

//insert into jd_user (id,age,name) values(?,?,?) Params[5,10,Hello]
num=D.M(user).autoInc(false).add();
```

## 0x06 更新数据
| 操作| 参数|说明|
| -------- | -------- | -------- 
|save|long save()|Table模式前提方法:data(),where();<br>返回执行生效行数

```
long num=D.M("user").field("name,weight").data("Mike",100).where("id=?",1234).save();
User user = new User();
user.setId(5);
user.setAge(10);
user.setName("Hello");

//update jd_user set age=?,name=? where id=?; Params[10,Hello,5]
num=D.M(user).save();

//update jd_user set name=? where id=?; Params[Hello,5]
num=D.M(user).field("name").save();

//update jd_user set id=?,age=?,name=? where id=?; Params[5,10,Hello,4]
id=D.M(user).autoInc(false).fetchSql(true).where("id=?",user.getId()-1).save();
```

## 0x07 删除数据
| 操作| 参数|说明|
| -------- | -------- | -------- 
|delete|long delete()|Table模式前提方法:where()<br>返回执行生效行数

`注：为防止误删除，where条件不能为空。`

```
long num=D.M("user").delete(5);//默认为id=?
num=D.M("user").delete("time",1523681398);//time=?
num=D.M(User.class).where("id>=?",13421).delete();

//JavaBean模式
User user=new User();
user.setId(10L);
long num=D.M(user).delete();
```

## 0x08 执行SQL

| 操作| 参数|说明| 
| -------- | -------- | -------- 
|execute|void execute(String... sqls)|直接执行SQL语句

```
D.M().execute( sql1 [ sql2 , sql3 ... ] );
```

## 0x09 事务支持
数据库表引擎应该为InnoDB以支持事务操作。
`代码示例：`

```
Connection conn=null;
try {
	//获取已开启事务的数据库连接
	conn = D.M().startTrans();
	//使用事务连接操作数据库
	long id=new M("gold").trans(conn).field("user_id,gold,type,time").data(3,5,0,System.currentTimeMillis()/1000).add();
	System.out.println(id);
	if(id>0) {
		throw new SQLException("Transaction Rollback Test");
	}
	id=new M("gold").trans(conn).field("user_id,gold,type,time").data(3,5,0,System.currentTimeMillis()/1000).add();
	System.out.println(id);
	//提交事务
	D.M().commit(conn);
} catch (SQLException e) {
	e.printStackTrace();
	try {
		//事务回滚
		D.M().rollback(conn);
	} catch (SQLException e1) {
		e1.printStackTrace();
	}
}
```

# 3 许可证

[Apache License 2.0](https://github.com/Leytton/ThinkJD/blob/master/LICENSE) 

# 4 关于
如果喜欢的话，请点个赞让我知道哦~在找到比它用得更顺手的JDBC库之前，这个项目会持续更新。