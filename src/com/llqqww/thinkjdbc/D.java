package com.llqqww.thinkjdbc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import javax.sql.DataSource;

public class D {
	
	private static DbConfig dbConfig;
	private static DataSource dataSource;
	private static String version="V1.4.5_15";
	private static String TablePrefix="";
	private static String pk="id";
	private static boolean isPkAutoInc=true;
	private static boolean isAutoClose=true;
	private static boolean checkField=true;
	
	private static final ThreadLocal<Connection> threadConn = new ThreadLocal<Connection>(){
		//ThreadLocal没有被当前线程赋值时或当前线程刚调用remove方法后调用get方法，返回此方法值
        @Override
        protected Connection initialValue()
        {
            return null;
        }
    };
	
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
	
	public static M M() throws Exception {
		return new M();
	}

	public static M M(String table) throws Exception {
		return new M(table);
	}
	
	public static M M(Object bean) throws Exception {
		return new M(bean);
	}
	
	public static M M(Class<?> beanClass) throws Exception {
		return new M(beanClass);
	}
	
	public static Connection getConnection() throws Exception {
		Connection conn=threadConn.get();
//		System.out.println("Thread:"+Thread.currentThread().getName()+",conn==null:"+(conn==null));
		if(null!=conn) {
			return conn;
		}else if(null!=dataSource) {
			conn = dataSource.getConnection();
		}else {
			if(null!=dbConfig) {
				conn = DriverManager.getConnection(dbConfig.getDbUrl(), dbConfig.getDbUser(), dbConfig.getDbPassword());
			}else {
				throw new Exception("DbConfig/DataSource haven't set , D.setDbConfig()/D.setDataSource() should be called first !");
			}
		}
		threadConn.set(conn);
		return conn;
	}
	
	/**
	 * 获取事务连接
	 * @return Connection 返回事务连接
	 * @throws Exception 获取失败或设置为事务失败时关闭连接并抛异常
	 */
	public static Connection getTransConnection() throws Exception{
		Connection conn=null;
		try {
			conn= getConnection();
			conn.setAutoCommit(false);
		} catch (Exception e) {
			closeConn(conn);
			throw e;
		}
		return conn;
	}
	
	/**
	 * 事务提交
	 * 中途异常关闭conn连接
	 * @param conn 事务连接
	 * @throws Exception if has error
	 */
	public static void commit(Connection conn) throws Exception{
		Exception ex = null;
		try {
			conn.commit();
		} catch (Exception e) {
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
	 * @throws Exception if has error
	 */
	public static void rollback(Connection conn) throws Exception{
		Exception ex = null;
		try {
			conn.rollback();
		} catch (Exception e) {
			ex=e;
		}finally {
			autoCloseTransConn(conn);
			if(null!=ex) {
				throw ex;
			}
		}
	}
	
	/**
	 * 关闭连接
	 */
	public static void closeConn(){
		Connection conn=threadConn.get();
		try {
			if(conn!=null && !conn.isClosed()) {
				conn.close();
				threadConn.remove();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 关闭连接,有事务不关闭
	 * @param conn 连接
	 * @throws Exception if has error
	 */
	protected static void closeConn(Connection conn) throws Exception{
		if(conn!=null && !conn.isClosed()) {
			conn.close();
			threadConn.remove();
		}
	}
	
	/**
	 * 关闭连接,有事务不关闭
	 * @param conn 连接
	 * @throws Exception if has error
	 */
	protected static void autoCloseConn(Connection conn) throws Exception{
		if(D.isAutoClose() && null!=conn && conn.getAutoCommit()) {
			D.closeConn(conn);
		}
	}
	
	/**
	 * 关闭事务连接
	 * @param conn 连接
	 * @throws Exception if has error
	 */
	protected static void autoCloseTransConn(Connection conn) throws Exception{
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
