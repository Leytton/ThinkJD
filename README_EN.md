![ThinkJDBC](https://gitee.com/uploads/images/2018/0428/174620_372c5f0f_890966.png)

[![Latest Version](https://img.shields.io/badge/Latest%20Version-V1.4.2__10-green.svg?longCache=true&style=flat-square)](https://gitee.com/Leytton/ThinkJD) [![中文文档](https://img.shields.io/badge/%E4%B8%AD%E6%96%87%E6%96%87%E6%A1%A3-V1.4.2__10-green.svg?longCache=true&style=flat-square)](https://blog.csdn.net/Leytton/article/details/80021702) [![English Document](https://img.shields.io/badge/English%20Document-V1.4.2__10-green.svg?longCache=true&style=flat-square)](https://blog.csdn.net/Leytton/article/details/80021029) [![CSDN Blog](https://img.shields.io/badge/CSDN%20Bolg-Leytton-red.svg?longCache=true&style=flat-square)](https://blog.csdn.net/Leytton)


# 1 Introduction

`ThinkJD`, also known as `ThinkJDBC`, an easy and powerful open source JDBC library. You can operate the database with one line  code of Java,just like the M method of `ThinkPHP` framework. ThinkJD will automatically manage database connection. After use or exception  occurs, it will close the connection to avoid memory leaks.



**Quick Start:**

```
//Configurat the database(only once)
D.setDbConfig("jdbc:mysql://127.0.0.1:3306/DbName?characterEncoding=UTF-8","root","root");

/*JavaBean Mode,Automatically get the table name,
*primary key,auto-increment attribute,field name and value
*/
User user = new User();
user.setAge(10);
user.setName("Hello");
user.setSex(true);

//insert data
long id=D.M(user).add();

//query data
user=D.M(User.class).find(id);

//update data
user.setSex(false);
D.M(user).field("sex").save();
//update the all nonnull fields of JavaBean by default,if there is no specified field

//delete data
D.M(user).delete();
//D.M(User.class).delete(id);

//Table Mode，specify the table name, primary key,auto-increment attribute,field name and value
//insert data
long id=D.M("user").field("name,weight").data("Tom",60).add();
//update data
D.M("user").field("name,weight").data("Tom",100).where("id=?",id).save();
//query data
user=D.M(User.class).find(id);
//delete data
D.M("user").delete(id);
```

**Project Page** https://github.com/Leytton/ThinkJD https://gitee.com/Leytton/ThinkJD

**Test Demo** https://github.com/Leytton/ThinkJD_Demo

**Blog Page** https://blog.csdn.net/Leytton

# 2 Getting Started

## 0x01 Add the Dependencies
### Jar library

Add the ThinkJDBC-x.x.x-core.jar core library and the  following two dependencies:

 - mysql-connector-java-5.1.39.jar
 - commons-dbutils-1.6.jar

or maybe you prefer to work with maven.

### Maven
```
<dependency>
    <groupId>com.llqqww</groupId>
    <artifactId>thinkjdbc</artifactId>
    <version>1.4.2_10</version>
</dependency>
```

 
## 0x02 Config the Database
There are three ways to connect database by using ThinkJD.You can config username and password (file way or code way) or using the JDBC DataSources/Resource Pools  such as Hikari,C3P0,etc.

Firstly，you should define the way toconnect database:
### (1)File way
Add file in the project root directory (like Hikari configuration file format).ThinkJD will automatically load the configuration file when the first time you start . It will ignore if the file doesn‘t exist.
`thinkjdbc.properties`

```
jdbcUrl = jdbc:mysql://127.0.0.1:3306/thinkjdbc?useUnicode=true&characterEncoding=UTF-8
dataSource.user = root
dataSource.password = root
```

### (2)Using Username and Password
```
D.setDbConfig("jdbc:mysql://127.0.0.1:3306/database?useUnicode=true&characterEncoding=UTF-8","root","root");
```

### (3)Using JDBC Pool
Example for Hikari：
```
HikariConfig config = new HikariConfig("/hikari.properties");
HikariDataSource dataSource = new HikariDataSource(config);
D.setDataSource(dataSource);
```

`Note that if you defined the JDBC pool,it will be preferred to use.`

### (3)Config Table Prefix
You can config the prefix of table(Not necessary).
```
D.setTablePrefix("jd_");
//D.M('user') D.M(User.class) will operate the `jd_user` table
```
> Note: `D.M('user').prefix('jd_')`to set the table prefix temporarily

## 0x03 Chain Method
| Operation| Param| Eg. |Note| 
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

## 0x04 Select Method

| Operation| Param| Note
| -------- |--------|--------
|select|①<`T`> List<`T`> select()<br>②<`T`> List<`T`> select(String key, Object value)
|find|①<`T`> T find()<br>②<`T`> T find(Object value)<br>③<`T`> T find(String key, Object value)
|count|①long count()<br>②long count(String field)
|max|double max(String field)
|min|double min(String field)
|avg|double avg(String field)
|sum|double sum(String field)

```
//find
//select id,name from jd_user where id>4 order by id asc limit 0,1
User res = D.M(User.class).field("id,name").where("id>?",4).order("id asc").find();

//find by id
//select * from jd_user where id=3 limit 0,1
User user = D.M(User.class).find(3);

//find by field
//select * from jd_user where name='Tom' limit 0,1
User user=D.M(User.class).fetchSql(true).find("name","Bob");

//query with where,field,etc.
//select id,name,weight from jd_user where id>3
List<User> res = D.M(User.class).field("id,name,weight").where("id>3").select();

//query with group
//select sex,sum(weight) as weight,avg(age) as age,count(id) as num from jd_user where id>5 group by sex order by sex desc limit 0,10
res = D.M(User.class).field("sex,sum(weight) as weight,avg(age) as age,count(id) as num")
	.where("id>?",5).group("sex").order("sex desc").page(1, 10).select();

//query with join
//select jd_user.id,name,weight,sum(gold) as num from jd_user left join jd_gold on user_id=jd_user.id where jd_user.id>3 group by jd_user.id
res = D.M(User.class).field("jd_user.id,name,weight,sum(gold) as num")
	.join("left join jd_gold on user_id=jd_user.id").where("jd_user.id>3").group("jd_user.id").select();

//query with union
//(select id,name from jd_user where id=4 ) union all (select id,name from jd_user where id<3) union (select id,name from jd_user where id=3)
res = D.M(User.class).field("id,name").where("id=4").union("select id,name from jd_user where id<3",true)
	.union("select id,name from jd_user where id=3",false).select();

//statistical query
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

>  By calling the method of `fetchSql(true)`,you can get the SQL statement  `ThinkJD` produced(exception way) and there will be no operation for the database.
>  
![fetchSql](https://img-blog.csdn.net/2018042023324417)

user table fields：

|Field Name| Data Type | note
|--------|--------|--------
|id |int | primary key auto_increment
|name | varchar | 
|age |tinyint|
|weight | float | 
|sex|tinyint|0:women/1:man
|time|int|


The return value of `select()` and `find()` will be saved to a JavaBean such as:
```

//@Table(name="user") //tabble name=class name by default，you can annotate to redefine
public class User {
	//@Column(isKey=true) //key=id,isAutoInc=true by default，you can annotate to redefine
	private Long id;
	private Integer age;
	//@Column(name="user_name") //column name=attribute name by default，you can annotate to redefine
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

## 0x05 Add method
| Operation| Param| Note | 
| -------- | -------- | --------
|add|long add()|data() must be called in Table Mode;<br>return the id which is a auto_increment primary key;
```
//insert fields specified
long id=D.M(User.class).field("name,weight").data("Tom",60).add();

/*
 *insert fields unspecified.insert data by filed order
 */
id=D.M("user").data(null,"Tom",60,...).add();

/*Semi-automatic Mode.Data specified and Automatically get the table name,primary key,
 *auto-increment attribute,field name.Insert data by javaBean attributes order
 *insert into jd_user (age,name,weight,sex,time) values(?,?,?,...)
 */
 id=D.M(User.class).data("Tom",60,...).add();
 
/*JavaBean Mode,Automatically get the table name,
*primary key,auto-increment attribute,field name and value
*/
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

## 0x06 Update method
| Operation| Param|Note
| -------- | -------- | -------- 
|save|long save()|data(),where() must be called in Table Mode;<br>return the affected number of rows

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

## 0x07 Delete Method
| Operation| Param|Note
| -------- | -------- | -------- 
|delete|long delete()|where() must be called in Table Mode;<br>return the affected number of rows

`To avoid careless deletion, [where] conditions mustn't be null`
```
long num=D.M("user").delete(5);//default-> id=?
num=D.M("user").delete("time",1523681398);//time=?
num=D.M(User.class).where("id>=?",13421).delete();

//JavaBean Mode
User user=new User();
user.setId(10L);
long num=D.M(user).delete();
```

## 0x08 Execute Method

| Operation| Param|Note
| -------- | -------- | -------- 
|execute|void execute(String... sqls)|execute the statements directly

```
D.M().execute( sql1 [ sql2 , sql3 ... ] );
```

## 0x09 Transaction
The DB engine must be InnoDB to support Transaction。
`eg.`

```
Connection conn=null;
try {
	//get the connection which has started transaction
	conn = D.M().startTrans();
	//operate the DB by the transaction connection
	long id=new M("gold").trans(conn).field("user_id,gold,type,time").data(3,5,0,System.currentTimeMillis()/1000).add();
	System.out.println(id);
	if(id>0) {
		throw new SQLException("Transaction Rollback Test");
	}
	id=new M("gold").trans(conn).field("user_id,gold,type,time").data(3,5,0,System.currentTimeMillis()/1000).add();
	System.out.println(id);
	//commit
	D.M().commit(conn);
} catch (SQLException e) {
	e.printStackTrace();
	try {
		//rollback
		D.M().rollback(conn);
	} catch (SQLException e1) {
		e1.printStackTrace();
	}
}
```

# 3 License

[Apache License 2.0](https://github.com/Leytton/ThinkJD/blob/master/LICENSE)

# 4 About

if you like this project,star it to let me know :) .Before finding a more convenient JDBC lib,I'll update it continuously.