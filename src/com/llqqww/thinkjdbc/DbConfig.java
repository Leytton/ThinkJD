package com.llqqww.thinkjdbc;

public class DbConfig {
	
	public static final String DriverName_MYSQL="com.mysql.jdbc.Driver";
	public static final String DriverName_SQLSEVER="com.microsoft.sqlserver.jdbc.SQLServerDriver";
	public static final String DriverName_ORACLE="oracle.jdbc.OracleDriver";
	
	private String DbUrl = "";
	private String DbUser = "";
	private String DbPassword="";
	private String DriverName = DriverName_MYSQL;
	
	public DbConfig(String dbUrl, String dbUser, String dbPassword) {
		DbUrl = dbUrl;
		DbUser = dbUser;
		DbPassword = dbPassword;
	}

	public DbConfig(String dbUrl, String dbUser, String dbPassword, String driverName) {
		DbUrl = dbUrl;
		DbUser = dbUser;
		DbPassword = dbPassword;
		DriverName = driverName;
	}

	public String getDbUrl() {
		return DbUrl;
	}

	public void setDbUrl(String dbUrl) {
		DbUrl = dbUrl;
	}

	public String getDbUser() {
		return DbUser;
	}

	public void setDbUser(String dbUser) {
		DbUser = dbUser;
	}

	public String getDbPassword() {
		return DbPassword;
	}

	public void setDbPassword(String dbPassword) {
		DbPassword = dbPassword;
	}

	public String getDriverName() {
		return DriverName;
	}

	public void setDriverName(String driverName) {
		DriverName = driverName;
	}
	
}
