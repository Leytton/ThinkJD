package com.llqqww.thinkjdbc;

public class DbConfig {
	private String DbUrl = "";
	private String DbUser = "";
	private String DbPassword="";
	private String DriverName = "com.mysql.jdbc.Driver";
	
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
