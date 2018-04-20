package com.llqqww.thinkjdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

public class D {

	private static DbConfig dbConfig;
	private static DataSource dataSource;
	private static String version="V1.1.2";
	private static String TablePrefix="";
	
	public static M M() throws SQLException {
		return new M();
	}

	public static M M(String table) throws SQLException {
		return new M(table);
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

	public static String getVersion(){
		return version;
	}
	
	public static void about() {
		String about="ThinkJDBC "+version+" By Leytton 2018/04/14. Apache 2.0 Licence";
		System.out.println(about);
	}
	
}
