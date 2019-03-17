package fr.itspower.kolize;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SQL {

	private Kolize plugin;
	private SQLdriver driver;
	
	private String HOST;
	private String DB;
	private String USER;
	private String PASS;
	
	private Connection con;
	private Statement st;
	private String conName;
	private boolean connected;

	public SQL(Kolize plugin, String name) {
		this.plugin = plugin;
		this.conName = name;
		this.connected = false;
	}

	public Boolean Connect(String host, String db, String user, String pass) {
		this.HOST = host;
		this.DB = db;
		this.USER = user;
		this.PASS = pass;
		this.driver = new SQLdriver(host, db, user, pass);
		this.con = this.driver.open();
		try {
			this.st = this.con.createStatement();
			this.connected = true;
			this.plugin.getLogger().info("[" + this.conName + "] Connected to the database.");
		}
		catch (SQLException e) {
			this.connected = false;
			this.plugin.getLogger().info("[" + this.conName + "] Could not connect to the database.");
		}
		this.driver.close(this.con);
		return Boolean.valueOf(this.connected);
	}
}
