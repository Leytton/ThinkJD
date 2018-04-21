[**中文文档**](https://blog.csdn.net/Leytton/article/details/80021702)

[**English Document**](https://blog.csdn.net/Leytton/article/details/80021029)

最新版本 V1.2.3

# 1 简介

`ThinkJD`，又名`ThinkJDBC`，一个简洁而强大的开源JDBC操作库。你可以使用Java像`ThinkPHP`框架的M方法一样，`一行代码搞定数据库操作`。

**先睹为快：**
```
//数据库配置(只需调用一次)
D.setDbConfig("jdbc:mysql://127.0.0.1:3306/DbName?characterEncoding=UTF-8","root","root");
//插入数据
long id=D.M("user").field("name,weight","Tom",60).add();
//更新数据
D.M("user").field("weight",100).where("id=?",id).save();
//查询数据
User user=D.M("user").find(User.class,id);
//删除数据
D.M("user").delete(id);
```

**项目主页** https://github.com/Leytton/ThinkJD (Github)   https://gitee.com/Leytton/ThinkJD (码云)

**测试项目** https://github.com/Leytton/ThinkJD_Demo

**博客主页** https://blog.csdn.net/Leytton

# 2 使用方法

## 0x01 添加依赖
将[ThinkJDBC-x.x.x.jar](https://github.com/Leytton/ThinkJD/releases)和下面的两个依赖库添加到项目编译路径里。

 - mysql-connector-java-5.1.39.jar
 - commons-dbutils-1.6.jar

 这两个Jar包在下面目录里：
 
 https://github.com/Leytton/ThinkJD/tree/master/lib
 
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
//D.M('user') 将会操作 `jd_user` 表
```

> 注:`D.M('user').prefix('jd_')`方法可单独指定表前缀【V1.2.3新增】

## 0x03 过滤方法
| 操作| 参数| 示例 |说明| 
| ------------- |------------- |------------- | -------------
|`table`|table(String table) | table("user") | 
|`join` |join(String join)| join("left join machine on user.id=user_id and machine_status=1")|
|`field`|①field(String filed)<br>②field(String filed, Object... dataParam)| ①field("id,username")<br>②field("id,username",1111,"Leytton")| ①用于查询操作<br>②用于更新操作
|`where`|①where(String where)<br>②where(String where, Object... whereParam)|①where("id=1111 and username='Leytton'")<br>②where("id=? and username=?",1111,"Leytton")
|`group`|group(String group)|group("type")
|`having`|having(String having)|having("id>1234")
|`order`|order(String order)|order("id desc")
|`page`|page(long page, long rows)|page(1,10)
|`limit`|①limit(long rows)<br>②limit(long offset, long rows)|①limit(10)<br>②limit(1,10)
|`union`|union(String union,Boolean isAll)|①union("select * from user_two where id>1234",false)<br>②union("select * from user_two where id>1234",true)

## 0x04 查询数据

| 操作| 参数| 说明|
| -------- |--------|--------
|select|<`T`> List<`T`> select(Class<`T`> type)
|find|①<`T`> T find(Class<`T`> type)<br>②<`T`> T find(Class<`T`> type, long id)<br>③<`T`> T find(Class<`T`> type, String key, Object value)
|count|①long count()<br>②long count(String field)
|max|double max(String field)
|min|double min(String field)
|avg|double avg(String field)
|sum|double sum(String field)

```
//find查询
//select id,name from jd_user where id>4 order by id asc limit 0,1
User res = D.M("user").field("id,name").where("id>?",4).order("id asc").find(User.class);

//find 根据id查询
//select * from user where id=3 limit 0,1
User user = D.M("user").find(User.class,3);

//find根据字段查询
//select * from jd_user where name='Tom' limit 0,1
User user=D.M("user").fetchSql(true).find(User.class,"name","Bob");

//where,field过滤
//select id,name,weight from jd_user where id>3
List<User> res = new M("user").field("id,name,weight").where("id>3").select(User.class);

//group分组查询
//select sex,sum(weight) as weight,avg(age) as age,count(id) as num from jd_user where id>5 group by sex order by sex desc limit 0,10
res = new M("user").field("sex,sum(weight) as weight,avg(age) as age,count(id) as num").where("id>?",5).group("sex").order("sex desc").page(1, 10).select(User.class);

//join联表查询
//select jd_user.id,name,weight,sum(gold) as num from jd_user left join jd_gold on user_id=jd_user.id where jd_user.id>3 group by jd_user.id
res = new M("user").field("jd_user.id,name,weight,sum(gold) as num").join("left join jd_gold on user_id=jd_user.id").where("jd_user.id>3").group("jd_user.id").select(User.class);

//union联表查询
//(select id,name from jd_user where id=4 ) union all (select id,name from jd_user where id<3) union (select id,name from jd_user where id=3)
res = new M("user").field("id,name").where("id=4").union("select id,name from jd_user where id<3",true)
					.union("select id,name from jd_user where id=3",false).select(User.class);

//统计查询
long num= new M("user").where("id>3").count();
num= D.M("user").fetchSql(true).where("id>3").count("id");
num= (long) D.M("user").fetchSql(false).where("id<0").max("id");
num= (long) D.M("user").where("id<3").max("id");
num= (long) D.M("user").min("id");
num= (long) D.M("user").where("id>3").min("id");
num= (long) D.M("user").fetchSql(false).where("id>3").avg("id");
double avg= D.M("user").fetchSql(false).where("id>3").avg("id");
num= (long) D.M("user").where("id>13441").sum("age");
```

> 通过调用`fetchSql(true)`方法，可以获取到 `ThinkJD`产生的SQL语句(Exception形式)并且不会执行数据库操作。
![fetchSql](https://img-blog.csdn.net/2018042023324417?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0xleXR0b24=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

user表结构：

|字段名 | 数据类型| 备注|
|--------|--------|--------
|id |int | 用户id,自增长主键
|name | varchar | 用户名
|age |tinyint|年龄
|weight | float | 体重
|sex|tinyint|性别 0女/1男
|time|int|时间

`select()`和 `find()`查询结果封装到JavaBean里返回：


```

public class User {
	
	private long id;
	private int age;
	private String name;
	private float weight;
	private int sex;
	private int num;
	private long time;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getWeight() {
		return weight;
	}
	public void setWeight(float weight) {
		this.weight = weight;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	
}

```

## 0x05 插入数据
| 操作| 参数| 说明|
| -------- | -------- | --------
|add|long add()|前提方法:field()<br>返回自动生成的主键值;
```
//指定插入字段
long id=D.M("user").field("name,weight","Tom",60).add();

/*不指定插入字段,第一个参数固定为""或null,第二个参数对应id为null
 */
id=D.M("user").field("",null,"Tom",60).add();
```

## 0x06 更新数据
| 操作| 参数|说明|
| -------- | -------- | -------- 
|save|long save()|前提方法:field(),where();<br>返回执行生效行数

```
long num=D.M("user").field("name,weight","Mike",100).where("id=?",1234).save();
num=D.M("user").field("weight",100).where("id>?",1234).save();

```

## 0x07 删除数据
| 操作| 参数|说明|
| -------- | -------- | -------- 
|delete|long delete()|前提方法:field()<br>返回执行生效行数

`注：为防止误删除，where条件不能为空。`

```
long num=D.M("user").delete(13424);
num=D.M("user").delete("time",1523681398);
num=D.M("user").where("id>=?",13421).delete();
```

## 0x08 执行SQL

| 操作| 参数|说明| 
| -------- | -------- | -------- 
|execute|void execute(String... sqls)|直接执行SQL语句

```
D.M().execute( sql1 [ sql2 , sql3 ... ] );
```

# 3 许可证

[Apache License 2.0](https://github.com/Leytton/ThinkJD/blob/master/LICENSE) 

# 4 关于
如果喜欢的话，请点个赞让我知道哦~在找到比它用得更顺手的JDBC库之前，这个项目会持续更新。