package com.llqqww.thinkjdbc;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

public class M {
	
	private Connection conn = null;
	private boolean fetchSql = false;
	private Boolean isPkAutoInc = null;//必须为Boolean
	
	private String sql;
	private String pk;
	private String table;
	private String prefix;
	private String join;
	private String field;
	private String where;
	private String group;
	private String having;
	private String order;
	private String limit;
	private String union;
	private Object[] param_where;
	private Object[] param_data;
	private Object bean;
	private Class<?> beanClass;
	LinkedHashMap<String, FieldInfo> fieldInfoMap;
	private boolean isInitedBeanClass;
	private boolean isNeedDataParam=true;

	public M(){
	}
	
	public M(String table){
//		System.out.println("table mode");
		table(table);
	}
	
	public M(Object bean){
		bean(bean);
	}
	
	public M(Class<?> beanClass){
		bean(beanClass);
	}
	
	public M bean(Object bean) {
//		System.out.println("bean mode");
		this.bean=bean;
		initTableDataByBeanClass();
		return this;
	}
	
	public M bean(Class<?> beanClass) {
//		System.out.println("class mode");
		this.beanClass=beanClass;
		initTableDataByBeanClass();
		return this;
	}
	
	public M trans(Connection conn) {
		this.conn = conn;
		return this;
	}
	
	public M fetchSql(boolean fetchSql) {
		this.fetchSql = fetchSql;
		return this;
	}
	
	public M table(String table) {
		this.table = table;
		return this;
	}

	public M prefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	public M pk(String pk) {
		this.pk = pk;
		return this;
	}

	public M autoInc(boolean isPkAutoInc) {
		this.isPkAutoInc = isPkAutoInc;
		return this;
	}

	public M join(String join) {
		this.join = join;
		return this;
	}

	public M field(String filed) {
		this.field = filed;
		return this;
	}

	public M data(Object... dataParam) {
		this.param_data = dataParam;
		return this;
	}
	
	public M setInc(String key,long num) throws SQLException {
		this.isNeedDataParam=false;
		if(null==this.field) {
			this.field=key+"="+key+"+"+num;
		}else {
			this.field+=(","+key+"="+key+"+"+num);
		}
		return this;
	}
	
	public M setDec(String key,long num) throws SQLException {
		this.isNeedDataParam=false;
		if(null==this.field) {
			this.field=key+"="+key+"-"+num;
		}else {
			this.field+=(","+key+"="+key+"-"+num);
		}
		return this;
	}
	
	public M where(String where) {
		this.where = "where " + where;
		return this;
	}
	
	public M where(String where, Object... whereParam) {
		this.where = "where " + where;
		this.param_where = whereParam;
		return this;
	}

	public M group(String group) {
		this.group = "group by " + group;
		return this;
	}

	public M having(String having) {
		this.having = "having " + having;
		return this;
	}

	public M order(String order) {
		this.order = "order by " + order;
		return this;
	}
	
	public M asc(String key) {
		if(null==this.order) {
			this.order(key+" asc");
		}else {
			this.order += ("," + key+" asc");
		}
		return this;
	}
	
	public M desc(String key) {
		if(null==this.order) {
			this.order(key+" desc");
		}else {
			this.order += ("," + key+" desc");
		}
		return this;
	}

	public M page(long page, long rows) {
		return limit(page - 1, rows);
	}

	public M limit(long rows) {
		limit(0, rows);
		return this;
	}

	public M limit(long offset, long rows) {
		offset = offset >= 0 ? offset : 0;
		this.limit = "limit " + offset + "," + rows;
		return this;
	}

	public M union(String union, Boolean isAll) {
		if(null==this.union) {
			this.union="";
		}
		if (isAll) {
			this.union += (" union all (" + union + ")");
		} else {
			this.union += (" union (" + union + ")");
		}
		return this;
	}
	
	public <T> List<T> select(String key, Object value) throws SQLException {
		this.where(key + "=?", value);
		return select();
	}
	
	@SuppressWarnings("unchecked") 
	public <T> List<T> select() throws SQLException{
		try {
			if (buildSql_Select()) {
				List<T> beanList = new QueryRunner().query(conn, sql, new BeanListHandler<T>((Class<T>) beanClass),param_where);
				return beanList;
			}
		} catch (SQLException e) {
			this.close();
			throw e;
		}
		return null;
	}

	/**
	 * 查询一条数据,默认参考字段为id.可搭配page,limit,order,group,having使用
	 * 
	 * @param type
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public <T> T find(Object value) throws SQLException {
		return find(this.pk, value);
	}

	/**
	 * 查询一条数据,自定义参考字段.可搭配page,limit,order,group,having使用
	 * 
	 * @param type
	 * @param key
	 * @param value
	 * @return
	 * @throws SQLException
	 */
	public <T> T find(String key, Object value) throws SQLException {
		this.where(key + "=?", value);
		return find();
	}

	/**
	 * 查询一条数据,可搭配page,limit,order,group,having使用
	 * 
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked") 
	public <T> T find() throws SQLException{
		this.limit(1);
		try {
			if (buildSql_Select()) {
				T bean = new QueryRunner().query(conn, sql, new BeanHandler<T>((Class<T>) this.beanClass), param_where);
				this.close();
				return bean;
			}
		} catch (SQLException e) {
			this.close();
			throw e;
		}
		return null;
	}

	public long count() throws SQLException {
		return this.count("*");
	}

	public long count(String field) throws SQLException {
		return (long) getTjNum("count(" + field + ") as tj_num");
	}

	public double max(String field) throws SQLException {
		return getTjNum("max(" + field + ") as tj_num");
	}

	public double min(String field) throws SQLException {
		return getTjNum("min(" + field + ") as tj_num");
	}

	public double avg(String field) throws SQLException {
		return getTjNum("avg(" + field + ") as tj_num");
	}

	public double sum(String field) throws SQLException {
		return getTjNum("sum(" + field + ") as tj_num");
	}

	public long add() throws SQLException{
		try {
			if (buildSql_Insert()) {
				Map<String, Object> result_insert = new QueryRunner().insert(conn, sql, new MapHandler(), param_data);
				long id=0;
				if(null!=result_insert && result_insert.containsKey("GENERATED_KEY")) {
					id = (long) result_insert.get("GENERATED_KEY");
				}
				close();
				return id;
			}
		} catch (SQLException e) {
			close();
			throw e;
		}
		return 0;
	}

	public long save() throws SQLException{
		try {
			if(buildSql_Update()) {
				Object[] params = new Object[param_data.length + param_where.length];
				int obj_index = 0;
				for (Object object : param_data) {
					params[obj_index++] = object;
				}
				for (Object object : param_where) {
					params[obj_index++] = object;
				}
				long num = new QueryRunner().update(conn, sql, params);
				close();
				return num;
			}
		} catch (SQLException e) {
			close();
			throw e;
		}
		return 0;
	}
	
	/**
	 * 删除数据,默认参考字段为id.可搭配page,limit,order使用
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public long delete(Object value) throws SQLException {
		return delete(this.pk, value);
	}

	/**
	 * 删除数据,自定义删除参考字段.可搭配page,limit,order使用
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @throws SQLException
	 */
	public long delete(String key, Object value) throws SQLException {
		this.where = "where " + key + "=?";
		Object[] params = new Object[] { value };
		this.param_where = params;
		return delete();
	}

	/**
	 * 删除数据,参考为where语句.可搭配page,limit,order使用
	 * 
	 * @return
	 * @throws SQLException
	 */
	public long delete() throws SQLException {
		try {
			if(buildSql_Delete()) {
				int result_delete = new QueryRunner().update(conn, sql, param_where);
				this.close();
				return result_delete;
			}else{
				return 0;
			}
		}catch (SQLException e) {
			close();
			throw e;
		}
	}

	public void execute(String... sqls) throws SQLException{
		if (sqls.length < 1) {
			return;
		}
		PreparedStatement stmt = null;
		try {
			for (String sql : sqls) {
				stmt = conn.prepareStatement(sql);
				stmt.execute();
			}
			if (null != stmt && !stmt.isClosed()) {
				stmt.close();
			}
			close();
		} catch (SQLException e) {
			close();
			throw e;
		}
	}
	
	/**
	 * 获取某个字段值
	 * @param field
	 * @return
	 * @throws SQLException
	 */
	public String getField(String field) throws SQLException{
		this.field(field);
		try {
			if (buildSql_Select()) {
				Object res = new QueryRunner().query(conn, sql, new ScalarHandler<Object>(), param_where);
				close();
				if (null != res) {
					return res.toString();
				}
			}
		} catch (SQLException e) {
			close();
			throw e;
		}
		return null;
	}
	
	/**
	 * 开启事务,返回conn,后续操作M("table").conn(conn)传入此conn事务才有效
	 * 中途异常关闭conn连接
	 * @return Connection
	 * @throws SQLException
	 */
	public Connection startTrans() throws SQLException{
		try {
			initDB();
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			close(true);
			throw e;
		}
		return conn;
	}
	
	/**
	 * 事务提交
	 * 中途异常关闭conn连接
	 * @throws SQLException
	 */
	public void commit(Connection conn) throws SQLException{
		this.conn=conn;
		try {
			conn.commit();
			close(true);
		} catch (SQLException e) {
			close(true);
			throw e;
		}
	}
	
	/**
	 * 事务回滚，中途异常关闭conn连接
	 * @throws SQLException
	 */
	public void rollback(Connection conn) throws SQLException{
		this.conn=conn;
		try {
			conn.rollback();
		} catch (SQLException e) {
			close(true);
			throw e;
		}
	}
	
	private double getTjNum(String field) throws SQLException {
		String res = this.getField(field);
		if (null != res) {
			double tj_num = Double.valueOf(res);
			return tj_num;
		} else {
			throw new SQLException("NULL return value of '" + field + "',check your 'where' sql");
		}
	}
	
	private boolean buildSql_Select() throws SQLException {
		if(null!=fieldInfoMap) {
			//where语句只提取字段信息
			if(null==this.field) {//没指定field
				for (String key : fieldInfoMap.keySet()) {
					if(null==this.field) {
						this.field=key;
					}else{
						this.field+=(","+key);
					}
				}
			}
			//没指定where则提取主键和值构造where语句
			if(null==this.where && null!=this.pk) {
				FieldInfo fieldInfo=fieldInfoMap.get(this.pk);
				if(null!=fieldInfo && null!=fieldInfo.getValueObj()) {
					this.where(this.pk+"=?",fieldInfo.getValueObj());
				}
			}
		}
		initSql();
		if (this.field.equals("")) {
			this.field = "*";
		}
		if (this.table.equals("")) {
			throw new SQLException("Undefined table");
		}
		if (!this.having.equals("") && this.group.equals("")) {
			throw new SQLException("Undefined 'group' before using 'having'");
		}
		sql = "select " + this.field + " from " + this.table + " " + this.join + " " + this.where + " " + this.group
				+ " " + this.having + " " + this.order + " " + this.limit;
		if ("" != union) {
			sql = "(" + sql + ") " + union;
		}
		return doFetchSql();
	}

	private boolean buildSql_Delete() throws SQLException {
		if(null!=fieldInfoMap) {
			//没指定where则提取主键和值构造where语句
			if(null==this.where && null!=this.pk) {
				FieldInfo fieldInfo=fieldInfoMap.get(this.pk);
				if(null!=fieldInfo && null!=fieldInfo.getValueObj()) {
					this.where(this.pk+"=?",fieldInfo.getValueObj());
				}
			}
		}
		initSql();
		if (this.table.equals("")) {
			throw new SQLException("Undefined table");
		}
		if (this.where.equals("")) {
			throw new SQLException("Undefined where sql");
		}
		sql = "delete from " + this.table + " " + this.where + " " + this.order + " " + this.limit;
		return doFetchSql();
	}

	private boolean buildSql_Insert() throws SQLException {
		if(null!=fieldInfoMap) {
			//insert语句提取字段和数据,以及判断主键是否自增
			
			//包含主键且自增
			if(null!=this.pk) {
				FieldInfo fieldInfo=fieldInfoMap.get(this.pk);
				if(null!=fieldInfo) {
					//A:用户设置>注释
					boolean autoInc= null!=this.isPkAutoInc?this.isPkAutoInc:fieldInfo.isAutoInc();
					if(autoInc) {
						fieldInfoMap.remove(this.pk);
					}
				}
			}
			initFieldData();
		}
		initSql();
		if (this.table.equals("")) {
			throw new SQLException("Undefined table");
		}
		this.field = this.field.replaceAll(" ", "");
		if (!this.field.equals("")) {
			this.field = "(" + this.field + ")";
		}
		if (null == param_data || param_data.length < 1) {
			throw new SQLException("Undefined data to insert");
		}
		String value = "values(";
		for (int value_index = 0; value_index < param_data.length - 1; value_index++) {
			value += "?,";
		}
		value += "?)";
		sql = "insert into " + this.table + " " + this.field + " " + value;
		return doFetchSql();
	}

	private boolean buildSql_Update() throws SQLException {
		if(null!=fieldInfoMap) {
			//update语句提取字段和数据,以及判断主键是否自增
			//包含主键且自增
			if(null!=this.pk) {
				FieldInfo fieldInfo=fieldInfoMap.get(this.pk);
				if(null!=fieldInfo) {
					//A:用户设置>注释
					boolean autoInc= null!=this.isPkAutoInc?this.isPkAutoInc:fieldInfo.isAutoInc();
					if(autoInc) {
						fieldInfoMap.remove(this.pk);
					}
					//没指定where则提取主键和值构造where语句
					if(null==this.where && null!=fieldInfo.getValueObj()) {
						this.where(this.pk+"=?",fieldInfo.getValueObj());
					}
				}
			}
			initFieldData();
		}
		initSql();
		if (this.table.equals("")) {
			throw new SQLException("Undefined table");
		}
		if (this.where.equals("")) {
			throw new SQLException("Undefined where sql or key field");
		}
		if (this.field.equals("")) {
			throw new SQLException("Undefined fields to update");
		}
		String[] fileds = field.split(",");
		String setSql = "";
		int filed_index = 0;
		//包含"="号的字段表示内部有表达式,无需构造key=?
		for (; filed_index < fileds.length - 1; filed_index++) {
			setSql += fileds[filed_index] + (fileds[filed_index].contains("=")?",":"=?,");
		}
		setSql += fileds[filed_index] + (fileds[filed_index].contains("=")?"":"=?");
		
		if (isNeedDataParam && (null == param_data || param_data.length < 1)) {
			throw new SQLException("Undefined data to update");
		}
		this.sql = "update " + this.table + " set " + setSql + " " + this.where + " " + this.order + " " + this.limit;
		return doFetchSql();
	}

	private boolean doFetchSql() throws SQLException {
		sql = sql.replaceAll(" +", " ").trim().toLowerCase();
		if (fetchSql) {
			this.close();
			String params="Params[";
			if(null!=param_data && (sql.contains("insert") || sql.contains("update")) ) {
				for (Object object : param_data) {
					if(null==object) {
						object="NULL";
					}
					if(params.equals("Params[")) {
						params+=object.toString();
					}else {
						params+=(","+object.toString());
					}
				}
			}
			if(null!=param_where && (sql.contains("delete") || sql.contains("update") || sql.contains("select")) ) {
				for (Object object : param_where) {
					if(null==object) {
						object="NULL";
					}
					if(params.equals("Params[")) {
						params+=object.toString();
					}else {
						params+=(","+object.toString());
					}
				}
			}
			params+="]";
			String msg ="╔══════════════════════════════════════════════════════════════\r\n"
					+	"║SQL debuging and you'll get a invalid return value !!!\r\n" 
					+	"║" + sql + "\r\n"
					+	"║" +params+ "\r\n"
					+	"║By ThinkJDBC " + D.getVersion() + "\r\n"
					+	"╚══════════════════════════════════════════════════════════════";
			try {
				throw new Exception("\r\n" + msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		} else {
			initDB();
			return true;
		}
	}

	private void initDB() throws SQLException {
		if(null==this.conn) {
			this.conn = D.getConnection();
		}
	}

	private void initSql() {
		table = table == null ? "" : (prefix == null ? D.getTablePrefix() : prefix) + table;
		join = join == null ? "" : join;
		field = field == null ? "" : field;
		where = where == null ? "" : where;
		group = group == null ? "" : group;
		having = having == null ? "" : having;
		limit = limit == null ? "" : limit;
		order = order == null ? "" : order;
		union = union == null ? "" : union;
	}
	
	private void initTableDataByBeanClass() {
		if(isInitedBeanClass) {
			return;
		}else {
			isInitedBeanClass=true;
		}
		if(null!=bean) {
			beanClass=bean.getClass();//获取类信息
		}
		if(null==beanClass) {
			return;
		}
		if(null==table) {
			//获取表名
			if(beanClass.isAnnotationPresent(Table.class)){//判断userClass是否使用了Table注解
	            Table tableAnnotation=(Table)beanClass.getAnnotation(Table.class);//获取注解信息
	            table(tableAnnotation.name());
	        }else {
	        	table(beanClass.getSimpleName());
	        }
		}
		
		//获取字段[和bean数据]
		String annoKeyField=null;
		
		fieldInfoMap=new LinkedHashMap<>();
		Field[] fields = beanClass.getDeclaredFields();//获取update或add字段
		for(Field field:fields){//遍历属性
        	field.setAccessible(true);
        	FieldInfo fiedInfo= new FieldInfo();
        	fiedInfo.setName(field.getName());//默认字段名
        	boolean isCheckField=D.isCheckField();
        	if(field.isAnnotationPresent(Column.class)){//具备Column注解,则读取注解
        		Column column=(Column)field.getAnnotation(Column.class);//获取注解信息
        		if(!column.isColumn()) {//如果注解了isColumn=false则忽略此字段//System.out.println("注解忽略字段:"+field.getName());
        			fiedInfo.setColumn(false);
        			continue;
        		}
        		if(isCheckField) {
        			isCheckField=column.isCheckField();
        		}
        		
                if(!column.name().equals("")){ //如果没有注解字段名则用变量名
                	fiedInfo.setName(column.name());
                }
                if(column.isKey()) {
                	annoKeyField=fiedInfo.getName();
                	fiedInfo.setKey(column.isKey());//如果是关键字段,则记录下来用于update操作where过滤
                    fiedInfo.setAutoInc(column.isAutoInc());
                }
            }
        	if(isCheckField) {
				checkField(field);
			}
        	try {
        		if(fiedInfo.isColumn()) {//只要没备注不是Column且不为null的都加入值
        			if(null!=bean) {//如果bean为null,即class模式
        				Object obj=field.get(bean);
        				if(null==obj) {//System.out.println("空值忽略字段:"+field.getName());
        					continue;
        				}
        				fiedInfo.setValueObj(obj);
        			}
    				fieldInfoMap.put(fiedInfo.getName(), fiedInfo);
        		}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
        	
        }
		
		//获取主键
		if(null!=this.pk) {//用户自定义主键
			if(fieldInfoMap.containsKey(this.pk)) {//如果包含该主键
			}
		}else if(null!=annoKeyField) {//用户没定义主键，注解主键
			this.pk=annoKeyField;
		}else {//D.pk
			if(fieldInfoMap.containsKey(D.getPk())) {//如果包含该主键
				this.pk=D.getPk();
			}
		}
	}
	
	private void initFieldData() {
		Integer obj_index = null;
		//没自定义字段就使用bean里面的
		if(null==this.field) {
			//没定义数据就用bean里面的
			if(null==this.param_data && null!=bean) {
				obj_index=0;
				this.param_data = new Object[fieldInfoMap.size()];
			}
			for (String key : fieldInfoMap.keySet()) {
				if(null==this.field) {
					this.field=key;
				}else{
					this.field+=(","+key);
				}
				if(null!=obj_index) {
					this.param_data[obj_index++]=fieldInfoMap.get(key).getValueObj();
				}
			}
		}else {//如果指定了field,却没指定data,则提取出数据
			if(null==this.param_data && null!=bean) {
				obj_index=0;
				String[] fileds = field.split(",");
				this.param_data = new Object[fileds.length];
				//包含"="号的字段表示内部有表达式,无需构造key=?
				for (String field : fileds) {
					if(fieldInfoMap.containsKey(field)) {
						Object filedData=fieldInfoMap.get(field).getValueObj();
						if(null!=filedData) {
							this.param_data[obj_index++]=filedData;
						}
					}
				}
			}
		}
	}
	

	private void checkField(Field field) {
		String fieldType=field.getType().getSimpleName();
		String correctType=null;
		switch (fieldType) {
			case "int":
				correctType="Integer";
				break;
			case "long":
				correctType="Long";
				break;
			case "boolean":
				correctType="Boolean";
				break;
			case "float":
				correctType="Float";
				break;
			case "double":
				correctType="Double";
				break;
			case "byte":
				correctType="Byte";
				break;
			case "short":
				correctType="Short";
				break;
			case "char":
				correctType="Char";
				break;
			default:
				break;
		}
		if(null!=correctType) {
			String fieldName=field.getDeclaringClass().getName()+"."+field.getName();
//			System.err.printf("警告!!! 字段%s, 建议使用类型:%s->%s\r\n",fieldName,fieldType,correctType);
			System.err.printf("Warning!!! Field %s, recommended Type:%s->%s\r\n",fieldName,fieldType,correctType);
			System.err.println("详情请访问(More to see) https://github.com/Leytton/ThinkJD");
		}
	}

	private void close() throws SQLException {
		close(false);
	}
	
	/**
	 * 强行关闭(不管有没有事务)
	 * @param isForce
	 * @throws SQLException
	 */
	private void close(boolean isEndTrans) throws SQLException {
		if (null != conn && !conn.isClosed()) {
			if(isEndTrans) {
				conn.setAutoCommit(true);//关闭事务
				conn.close();
			}else if(conn.getAutoCommit()){
				conn.close();
			}
			//如果开启事务则暂时不关闭
		}
	}
}
