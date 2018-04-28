package com.llqqww.thinkjdbc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

public class D {

	private static DbConfig dbConfig;
	private static DataSource dataSource;
	private static String version="V1.4.2_10";
	private static String TablePrefix="";
	private static String pk="id";
	private static boolean isPkAutoInc=true;
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
	
	public static void setDbConfig(String DbUrl,String DbUser,String DbPassword) throws ClassNotFoundException{
		D.dbConfig = new DbConfig(DbUrl,DbUser,DbPassword);
		Class.forName(dbConfig.getDriverName());
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
