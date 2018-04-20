package com.llqqww.thinkjdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

	private String sql;
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

	public M() throws SQLException {
	}

	public M(String table) throws SQLException {
		this.table = table;
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

	public M join(String join) {
		this.join = join;
		return this;
	}

	public M field(String filed) {
		this.field = filed;
		return this;
	}

	public M field(String filed, Object... dataParam) {
		this.field = filed;
		this.param_data = dataParam;
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

	public <T> List<T> select(Class<T> type) throws SQLException {
		if (buildSql_Select()) {
			List<T> beanList = new QueryRunner().query(conn, sql, new BeanListHandler<T>(type), param_where);
			this.close();
			return beanList;
		} else {
			return null;
		}
	}

	/**
	 * 查询一条数据,默认参考字段为id.可搭配page,limit,order,group,having使用
	 * 
	 * @param type
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public <T> T find(Class<T> type, long id) throws SQLException {
		return find(type, "id", id);
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
	public <T> T find(Class<T> type, String key, Object value) throws SQLException {
		this.where(key + "=?", value);
		return find(type);
	}

	/**
	 * 查询一条数据,可搭配page,limit,order,group,having使用
	 * 
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	public <T> T find(Class<T> type) throws SQLException {
		this.limit(1);
		if (buildSql_Select()) {
			T bean = new QueryRunner().query(conn, sql, new BeanHandler<T>(type), param_where);
			this.close();
			return bean;
		} else {
			return null;
		}
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

	public long add() throws SQLException {
		if (buildSql_Insert()) {
			Map<String, Object> result_insert = new QueryRunner().insert(conn, sql, new MapHandler(), param_data);
			if (result_insert != null && result_insert.size() > 0) {
				long id = (long) result_insert.get("GENERATED_KEY");
				this.close();
				return id;
			} else {
				this.close();
				throw new SQLException();
			}
		} else {
			return 0;
		}
	}

	public long save() throws SQLException {
		Object[] params = new Object[param_data.length + param_where.length];
		int obj_index = 0;
		for (Object object : param_data) {
			params[obj_index++] = object;
		}
		for (Object object : param_where) {
			params[obj_index++] = object;
		}
		if (buildSql_Update()) {
			long num = new QueryRunner().update(conn, sql, params);
			this.close();
			return num;
		} else {
			return 0;
		}
	}

	/**
	 * 删除数据,默认参考字段为id.可搭配page,limit,order使用
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public long delete(long id) throws SQLException {
		return delete("id", id);
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
		if (buildSql_Delete()) {
			int result_delete = new QueryRunner().update(conn, sql, param_where);
			this.close();
			return result_delete;
		} else {
			return 0;
		}
	}

	public void execute(String... sqls) throws SQLException {
		if (sqls.length < 1) {
			return;
		}
		PreparedStatement stmt = null;
		for (String sql : sqls) {
			stmt = conn.prepareStatement(sql);
			stmt.execute();
		}
		if (null != stmt && !stmt.isClosed()) {
			stmt.close();
		}
		this.close();
	}

	public String getField(String field) throws SQLException {
		this.field(field);
		if (buildSql_Select()) {
			Object res = new QueryRunner().query(conn, sql, new ScalarHandler<Object>(), param_where);
			this.close();
			if (null == res) {
				return null;
			} else {
				return res.toString();
			}
		} else {
			return null;
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
		initSql();
		if ("" == this.field) {
			this.field = "*";
		}
		if ("" == this.table) {
			throw new SQLException("Undefined table");
		}
		if ("" != this.having && "" == this.group) {
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
		initSql();
		if ("" == this.table) {
			throw new SQLException("Undefined table");
		}
		if ("" == where) {
			throw new SQLException("Undefined where sql");
		}
		sql = "delete from " + this.table + " " + this.where + " " + this.order + " " + this.limit;
		return doFetchSql();
	}

	private boolean buildSql_Insert() throws SQLException {
		initSql();
		if ("" == this.table) {
			throw new SQLException("Undefined table");
		}
		this.field = this.field.replaceAll(" ", "");
		if ("" != this.field) {
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
		initSql();
		if ("" == this.table) {
			throw new SQLException("Undefined table");
		}
		if ("" == where) {
			throw new SQLException("Undefined where sql");
		}
		if ("" == this.field) {
			throw new SQLException("Undefined fields to update");
		}
		this.field = this.field.replaceAll(" ", "");
		String[] fileds = field.split(",");
		String setSql = "";
		int filed_index = 0;
		for (; filed_index < fileds.length - 1; filed_index++) {
			setSql += fileds[filed_index] + "=?,";
		}
		setSql += fileds[filed_index] + "=?";
		if (null == param_data || param_data.length < 1) {
			throw new SQLException("Undefined data to update");
		}
		sql = "update " + this.table + " set " + setSql + " " + this.where + " " + this.order + " " + this.limit;
		return doFetchSql();
	}

	private boolean doFetchSql() throws SQLException {
		sql = sql.replaceAll(" +", " ").trim();
		if (fetchSql) {
			this.close();
			String msg ="╔══════════════════════════════════════════════════════════════\r\n"
					+	"║SQL debuging and you'll get a invalid return value !!!\r\n" + "║" + sql + "\r\n"
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
		this.conn = D.getConnection();
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

	private void close() throws SQLException {
		if (null != conn && !conn.isClosed()) {
			conn.close();
		}
	}
}
