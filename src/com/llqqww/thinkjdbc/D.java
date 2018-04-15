package com.llqqww.thinkjdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

public class D {

	private static DbConfig dbConfig;
	private static DataSource dataSource;
	
	public static M M() throws SQLException {
		return new M();
	}

	public static M M(String table) throws SQLException {
		return new M(table);
	}
	
	public static void setDbConfig(String DbUrl,String DbUser,String DbPassword) throws ClassNotFoundException{
		D.dbConfig = new DbConfig(DbUrl,DbUser,DbPassword);
		Class.forName(dbConfig.getDriverName());
	}
	
	public static void setDbConfig(DbConfig dbConfig) throws ClassNotFoundException{
		D.dbConfig = dbConfig;
		Class.forName(dbConfig.getDriverName());
	}
	
	public static void setDataSource(DataSource dataSource) {
		D.dataSource=dataSource;
	}
	
	public static Connection getConnection() throws SQLException {
		Connection conn=null;
		if(null!=dataSource) {
			conn = dataSource.getConnection();
		}else {
			if(null!=dbConfig) {
				conn = DriverManager.getConnection(dbConfig.getDbUrl(), dbConfig.getDbUser(), dbConfig.getDbPassword());
			}else {
				throw new SQLException("DbConfig haven't set , D.setDbConfig() should be call first !");
			}
		}
		return conn;
	}
	
	public static String version(){
		String version="ThinkJDBC V1.0 By Leytton(http://www.llqqww.com) 2018/04/14. Apache 2.0 Licence";
		System.out.println(version);
		return version;
	}
	
}
