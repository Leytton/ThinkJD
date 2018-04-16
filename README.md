# 1、简介(Project)

`ThinkJD`，又名`ThinkJDBC`，一个简洁而强大的开源JDBC操作库。你可以使用Java像`ThinkPHP`框架的M方法一样来操作数据库。

`ThinkJD`, also known as `ThinkJDBC`, an easy and powerful open source JDBC library. You can operate the database with JAVA just like the M method of `ThinkPHP` framework.

**项目主页(Home Page)** https://github.com/Leytton/ThinkJD https://gitee.com/Leytton/ThinkJD

**博客主页(Blog Page)** https://blog.csdn.net/Leytton

**`警告!!! 忙里偷闲一天时间搞出来的东西，来不及做全面测试，后面补上，欢迎大家提issue`**

**`Warning!!! This is just a one-day work as well as this document what you read.Too busy to do many`** **`tests,more functions are expected to be found and welcome to have a overall test for it :)`**

# 2、使用方法(Get Started)

## 0x01 定义数据库(Config the Database)
ThinkJD支持直接定义用户名密码访问数据库，也支持使用Hikari、C3P0等数据库连接池。

There are two ways to connect database by using ThinkJD.You can config username and password or using the JDBC DataSources/Resource Pools  such as Hikari,C3P0,etc.

**首先定义数据库连接方式：**

Firstly，you should define the way toconnect database:
### (1)帐号密码访问数据库(Using Username and Password)
```
D.setDbConfig("jdbc:mysql://127.0.0.1:3306/database?useUnicode=true&characterEncoding=UTF-8","root","root");
```

### (2)使用数据库连接池(Using JDBC Pool)
例如使用Hikari连接池(Example for Hikari)： 
```
HikariConfig config = new HikariConfig("/hikari.properties");
HikariDataSource dataSource = new HikariDataSource(config);
D.setDataSource(dataSource);
```
`注：如果定义了数据库连接池，ThinkJD会优先使用。`

`Note that if you defined the JDBC pool,it will be preferred to use.`

### (3)配置表前缀

```
D.setTablePrefix("jd_");
```

## 0x02 过滤方法(Filter Method)
| 操作(Operation)| 参数(Param)| 示例(Eg.) |说明(Note) | 
| ------------- |------------- |------------- | -------------
|`table`|table(String table) | table("user") | 
|`join` |join(String join)| join("left join machine on user.id=user_id and machine_status=1")|
|`field`|①field(String filed)<br>②field(String filed, Object... dataParam)| ①field("id,username")<br>②field("id,username",1111,"Leytton")| ①用于查询操作(for select sql)<br>②用于更新操作(for update sql)
|`where`|①where(String where)<br>②where(String where, Object... whereParam)|①where("id=1111 and username='Leytton'")<br>②where("id=? and username=?",1111,"Leytton")
|`group`|group(String group)|group("type")
|`having`|having(String having)|having("id>1234")
|`order`|order(String order)|order("id desc")
|`page`|page(long page, long rows)|page(1,10)
|`limit`|①limit(long rows)<br>②limit(long offset, long rows)|①limit(10)<br>②limit(1,10)
|`union`|union(String union,Boolean isAll)|①union("select from user_two where id>1234",false)<br>②union("select from user_two where id>1234",true)

## 0x03 查询数据(select method)
`select()`和 `find()`查询结果封装到JavaBean里返回，示例：

The return value of `select()` and `find()` will be saved to a JavaBean such as:

```
public class User {
	private long id;
	private String name;
	private float weight;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
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
}
```

user表结构(user table fields)：

|字段名(Field Name) | 数据类型(Data Type) | 备注
|--------|--------|--------
|id |int | 用户id,自增长主键(primary key auto_increment)
|name | varchar | 用户名
|weight | float | 体重

Eg.
```
List<User> res =D.M("user").field("id,name").where("id=?",1068).select(User.class);
```
| 操作(Operation)| 参数(Param)| 说明(Note) 
| -------- |--------|--------
|select|<`T`> List<`T`> select(Class<`T`> type)
|find|①<`T`> T find(Class<`T`> type)<br>②<`T`> T find(Class<`T`> type, long id)<br>③<`T`> T find(Class<`T`> type, String key, Object value)
|count|①long count()<br>②long count(String field)
|max|double max(String field)
|min|double min(String field)
|avg|double avg(String field)
|sum|double sum(String field)

```
long num= new M("user").where("id>13441").count();
System.out.println("count:"+num);
num= D.M("user").fetchSql(true).where("id>13441").count("type");
System.out.println("count:"+num);
num= (long) D.M("user").fetchSql(false).where("id<0").max("id");
System.out.println("max:"+num);
num= (long) D.M("user").where("id<13441").max("id");
System.out.println("max:"+num);
num= (long) D.M("user").min("id");
System.out.println("min:"+num);
num= (long) D.M("user").where("id>13441").min("id");
System.out.println("min:"+num);
num= (long) D.M("user").fetchSql(false).where("id>13442").avg("id");
System.out.println("avg:"+num);
double avg= D.M("user").fetchSql(false).where("id>13442").avg("id");
System.out.println("avg:"+avg);
num= (long) D.M("user").where("id>13441").sum("type");
System.out.println("sum:"+num);
```
> 通过调用`fetchSql(true)`方法，可以获取到 `ThinkJD`产生的SQL语句(Exception形式)并且不会执行数据库操作。
> by calling the method of `fetchSql(true)`,you can get the SQL statement  `ThinkJD` produced(exception way) and there will be no operation for the database.

## 0x04 插入数据(insert method)
| 操作(Operation)| 参数(Param)| 说明(Note) | 
| -------- | -------- | --------
|add|long add()|前提方法:field() must be called;<br>返回自动生成的主键值(return the id which is a auto_increment primary key);
```
//指定插入字段insert fields specified
long id=D.M("user").field("name,weight","Tom",60).add();

/*不指定插入字段,第一个参数固定为""或null,第二个参数对应id为null
 *insert fields unspecified.The 1st parameter must be "" or null
 *and the 2nd parameter `null` point to `id`
 */
id=D.M("user").field("",null,"Tom",60).add();
```

## 0x05 更新数据(update method)
| 操作(Operation)| 参数(Param)|说明(Note)
| -------- | -------- | -------- 
|save|long save()|前提方法:field(),where() must be called;<br>返回执行生效行数(return the affected number of rows)

```
long num=D.M("user").field("name,weight","Mike",100).where("id=?",1234).save();
num=D.M("user").field("weight",100).where("id>?",1234).save();

```

## 0x06 删除数据(delete method)
| 操作(Operation)| 参数(Param)|说明(Note) | 
| -------- | -------- | -------- 
|save|long save()|前提方法:field();<br>返回执行生效行数(return the affected number of rows)

`注：为防止误删除，where条件不能为空。`

`To avoid careless deletion, [where] conditions mustn't be null`
```
long num=D.M("user").delete(13424);
num=D.M("user").delete("time",1523681398);
num=D.M("user").where("id>=?",13421).delete();
```

## 0x07 执行SQL(execute method)

| 操作(Operation)| 参数(Param)|说明(Note) | 
| -------- | -------- | -------- 
|execute|void execute(String... sqls)|直接执行SQL语句(execute the statements directly)

```
D.M().execute( sql1 [ sql2 , sql3 ... ] );
```

## 0x08 依赖(Dependencies)

 - mysql-connector-java-5.1.39.jar
 - commons-dbutils-1.6.jar

## 0x09 许可证(License)

[Apache License 2.0](https://github.com/Leytton/ThinkJD/blob/master/LICENSE)

## 0x0A 关于(About)
如果喜欢的话，请点个赞让我知道哦~在找到比它用得更顺手的JDBC库之前，这个项目会持续更新。

if you like this project,star it to let me know :) .Before finding a more convenient JDBC lib,I'll update it continuously.