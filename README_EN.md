[**ÖÐÎÄÎÄµµ**](https://blog.csdn.net/Leytton/article/details/80021702)

[**English Document**](https://blog.csdn.net/Leytton/article/details/80021029)

# 1 Project

`ThinkJD`, also known as `ThinkJDBC`, an easy and powerful open source JDBC library. You can operate the database with one line of Java code,just like the M method of `ThinkPHP` framework.

**Quick Start:**

```
//Configurat the database(only once)
D.setDbConfig("jdbc:mysql://127.0.0.1:3306/DbName?characterEncoding=UTF-8","root","root");
//insert data
long id=D.M("user").field("name,weight","Tom",60).add();
//update data
D.M("user").field("weight",100).where("id=?",id).save();
//query data
User user=D.M("user").find(User.class,id);
//delete data
D.M("user").delete(id);
```

**Project Page** https://github.com/Leytton/ThinkJD https://gitee.com/Leytton/ThinkJD

**Test Demo** https://github.com/Leytton/ThinkJD_Demo

**Blog Page** https://blog.csdn.net/Leytton

# 2 Get Started

## 0x01 Add the Dependencies

Add [ThinkJDBC-x.x.x.jar](https://github.com/Leytton/ThinkJD/releases) and the  following dependencies to the build path.

 - mysql-connector-java-5.1.39.jar
 - commons-dbutils-1.6.jar

you can find them in the lib dir£º
 
 https://github.com/Leytton/ThinkJD/tree/master/lib
 
## 0x02 Config the Database
There are two ways to connect database by using ThinkJD.You can config username and password or using the JDBC DataSources/Resource Pools  such as Hikari,C3P0,etc.

Firstly£¬you should define the way toconnect database:
### (1)Using Username and Password
```
D.setDbConfig("jdbc:mysql://127.0.0.1:3306/database?useUnicode=true&characterEncoding=UTF-8","root","root");
```

### (2)Using JDBC Pool
Example for Hikari£º
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
//D.M('user') will operate the `jd_user` table
```

## 0x03 Filter Method
| Operation| Param| Eg. |Note| 
| ------------- |------------- |------------- | -------------
|`table`|table(String table) | table("user") | 
|`join` |join(String join)| join("left join machine on user.id=user_id and machine_status=1")|
|`field`|¢Ùfield(String filed)<br>¢Úfield(String filed, Object... dataParam)| ¢Ùfield("id,username")<br>¢Úfield("id,username",1111,"Leytton")| ¢Ùfor select sql<br>¢Úfor update sql
|`where`|¢Ùwhere(String where)<br>¢Úwhere(String where, Object... whereParam)|¢Ùwhere("id=1111 and username='Leytton'")<br>¢Úwhere("id=? and username=?",1111,"Leytton")
|`group`|group(String group)|group("type")
|`having`|having(String having)|having("id>1234")
|`order`|order(String order)|order("id desc")
|`page`|page(long page, long rows)|page(1,10)
|`limit`|¢Ùlimit(long rows)<br>¢Úlimit(long offset, long rows)|¢Ùlimit(10)<br>¢Úlimit(1,10)
|`union`|union(String union,Boolean isAll)|¢Ùunion("select from user_two where id>1234",false)<br>¢Úunion("select from user_two where id>1234",true)

## 0x04 Select Method

| Operation| Param| Note
| -------- |--------|--------
|select|<`T`> List<`T`> select(Class<`T`> type)
|find|¢Ù<`T`> T find(Class<`T`> type)<br>¢Ú<`T`> T find(Class<`T`> type, long id)<br>¢Û<`T`> T find(Class<`T`> type, String key, Object value)
|count|¢Ùlong count()<br>¢Úlong count(String field)
|max|double max(String field)
|min|double min(String field)
|avg|double avg(String field)
|sum|double sum(String field)

```
//select * from user where id=3 limit 0,1
User user = D.M("user").find(User.class,3);

//select id,name,weight from jd_user where id>3
List<User> res = new M("user").field("id,name,weight").where("id>3").select(User.class);

//select sex,sum(weight) as weight,avg(age) as age,count(id) as num from jd_user where id>5 group by sex order by sex desc limit 0,10
res = new M("user").field("sex,sum(weight) as weight,avg(age) as age,count(id) as num").where("id>?",5).group("sex").order("sex desc").page(1, 10).select(User.class);

long num= new M("user").where("id>3").count();
System.out.println("count:"+num);
num= D.M("user").fetchSql(true).where("id>3").count("id");
System.out.println("count:"+num);
num= (long) D.M("user").fetchSql(false).where("id<0").max("id");
System.out.println("max:"+num);
num= (long) D.M("user").where("id<3").max("id");
System.out.println("max:"+num);
num= (long) D.M("user").min("id");
System.out.println("min:"+num);
num= (long) D.M("user").where("id>3").min("id");
System.out.println("min:"+num);
num= (long) D.M("user").fetchSql(false).where("id>3").avg("id");
System.out.println("avg:"+num);
double avg= D.M("user").fetchSql(false).where("id>3").avg("id");
System.out.println("avg:"+avg);
num= (long) D.M("user").where("id>13441").sum("age");
System.out.println("sum:"+num);
```

user table fields£º

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

> by calling the method of `fetchSql(true)`,you can get the SQL statement  `ThinkJD` produced(exception way) and there will be no operation for the database.

## 0x05 Add method
| Operation| Param| Note | 
| -------- | -------- | --------
|add|long add()|field() must be called;<br>return the id which is a auto_increment primary key;
```
//insert fields specified
long id=D.M("user").field("name,weight","Tom",60).add();

/*
 *insert fields unspecified.The 1st parameter must be "" or null
 *and the 2nd parameter `null` point to `id`
 */
id=D.M("user").field("",null,"Tom",60).add();
```

## 0x06 Update method
| Operation| Param|Note
| -------- | -------- | -------- 
|save|long save()|field(),where() must be called;<br>return the affected number of rows

```
long num=D.M("user").field("name,weight","Mike",100).where("id=?",1234).save();
num=D.M("user").field("weight",100).where("id>?",1234).save();

```

## 0x07 Delete Method
| Operation| Param|Note
| -------- | -------- | -------- 
|delete|long delete()|field() must be called;;<br>return the affected number of rows

`To avoid careless deletion, [where] conditions mustn't be null`
```
long num=D.M("user").delete(13424);
num=D.M("user").delete("time",1523681398);
num=D.M("user").where("id>=?",13421).delete();
```

## 0x08 Execute Method

| Operation| Param|Note
| -------- | -------- | -------- 
|execute|void execute(String... sqls)|execute the statements directly

```
D.M().execute( sql1 [ sql2 , sql3 ... ] );
```

# 3 License

[Apache License 2.0](https://github.com/Leytton/ThinkJD/blob/master/LICENSE)

# 4 About

if you like this project,star it to let me know :) .Before finding a more convenient JDBC lib,I'll update it continuously.