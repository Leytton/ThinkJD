package com.llqqww.thinkjdbc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

public class D {
	
	private static DbConfig dbConfig;
	private static DataSource dataSource;
	private static String version="V1.4.4_12";
	private static String TablePrefix="";
	private static String pk="id";
	private static boolean isPkAutoInc=true;
	private static boolean isAutoClose=true;
	private static boolean checkField=true;
	
	static{
		File cfgFile=new File("thinkjdbc.properties");
		if(cfgFile.exists()) {
			try {
				Properties cfg = new Properties();
				cfg.load(new FileInputStream(cfgFile));
				String DbUrl = cfg.getProperty("jdbcUrl");
				String DbUser = cfg.getProperty("dataSource.user");
				String DbPassword = cfg.getProperty("dataSource.password");
				setDbConfig(DbUrl, DbUser, DbPassword);
				//System.out.println("File Config");
			} catch (IOException |ClassNotFoundException e) {
				e.printStackTrace();
			}
		}else {
			cfgFile=null;
		}
		
	}
	
	public static M M() throws SQLException {
		return new M();
	}

	public static M M(String table) throws SQLException {
		return new M(table);
	}
	
	public static M M(Object bean) throws SQLException {
		return new M(bean);
	}
	
	public static M M(Class<?> beanClass) throws SQLException {
		return new M(beanClass);
	}
	
	public static Connection getConnection() throws SQLException {
		Connection conn=null;
		if(null!=dataSource) {
			conn = dataSource.getConnection();
		}else {
			if(null!=dbConfig) {
				conn = DriverManager.getConnection(dbConfig.getDbUrl(), dbConfig.getDbUser(), dbConfig.getDbPassword());
			}else {
				throw new SQLException("DbConfig/DataSource haven't set , D.setDbConfig()/D.setDataSource() should be called first !");
			}
		}
		return conn;
	}
	
	/**
	 * 获取事务连接
	 * @return Connection 返回事务连接
	 * @throws SQLException 获取失败或设置为事务失败时关闭连接并抛异常
	 */
	public static Connection getTransConnection() throws SQLException{
		Connection conn=null;
		try {
			conn= getConnection();
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			closeConn(conn);
			throw e;
		}
		return conn;
	}
	
	/**
	 * 事务提交
	 * 中途异常关闭conn连接
	 * @param conn 事务连接
	 * @throws SQLException if has error
	 */
	public static void commit(Connection conn) throws SQLException{
		SQLException ex = null;
		try {
			conn.commit();
		} catch (SQLException e) {
			ex=e;
		}finally {
			autoCloseTransConn(conn);
			if(null!=ex) {
				throw ex;
			}
		}
	}
	
	/**
	 * 事务回滚，中途异常关闭conn连接
	 * @param conn 事务连接
	 * @throws SQLException if has error
	 */
	public static void rollback(Connection conn) throws SQLException{
		SQLException ex = null;
		try {
			conn.rollback();
		} catch (SQLException e) {
			ex=e;
		}finally {
			autoCloseTransConn(conn);
			if(null!=ex) {
				throw ex;
			}
		}
	}
	
	/**
	 * 关闭连接,有事务不关闭
	 * @param conn
	 * @throws SQLException
	 */
	public static void closeConn(Connection conn) throws SQLException{
		if(conn!=null && !conn.isClosed()) {
			conn.close();
		}
	}
	
	/**
	 * 关闭连接,有事务不关闭
	 * @param conn
	 * @throws SQLException
	 */
	protected static void autoCloseConn(Connection conn) throws SQLException{
		if(D.isAutoClose() && conn.getAutoCommit()) {
			D.closeConn(conn);
		}
	}
	
	/**
	 * 关闭事务连接
	 * @param conn
	 * @throws SQLException
	 */
	protected static void autoCloseTransConn(Connection conn) throws SQLException{
		if(D.isAutoClose()) {
			D.closeConn(conn);
		}
	}
	
	public static void setDbConfig(String DbUrl,String DbUser,String DbPassword) throws ClassNotFoundException{
		setDbConfig(new DbConfig(DbUrl,DbUser,DbPassword));
	}
	
	public static void setDbConfig(DbConfig dbConfig) throws ClassNotFoundException{
		D.dbConfig = dbConfig;
		Class.forName(dbConfig.getDriverName());
	}
	
	public static DbConfig getDbConfig() {
		return dbConfig;
	}
	
	public static void setDataSource(DataSource dataSource) {
		D.dataSource=dataSource;
	}
	
	public static DataSource getDataSource() {
		return dataSource;
	}
	
	public static String getTablePrefix() {
		return TablePrefix;
	}

	public static void setTablePrefix(String tablePrefix) {
		TablePrefix = tablePrefix;
	}
	
	public static boolean isAutoClose() {
		return isAutoClose;
	}

	public static void setAutoClose(boolean isAutoClose) {
		D.isAutoClose = isAutoClose;
	}

	public static boolean isCheckField() {
		return checkField;
	}

	public static void setCheckField(boolean checkField) {
		D.checkField = checkField;
	}

	public static String getPk() {
		return pk;
	}

	public static void setPk(String pk) {
		D.pk = pk;
	}

	public static boolean isPkAutoInc() {
		return isPkAutoInc;
	}

	public static void setPkAutoInc(boolean isPkAutoInc) {
		D.isPkAutoInc = isPkAutoInc;
	}

	public static String getVersion(){
		return version;
	}
	
	public static void about() {
		String about="ThinkJDBC "+version+" By Leytton 2018/04/14-. Apache 2.0 Licence";
		System.out.println(about);
	}
	
}
