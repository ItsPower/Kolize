package fr.itspower.kolize.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import fr.itspower.kolize.Kolize;
import fr.itspower.kolize.Utils.Utils;

public class SQL {

	private Kolize plugin;
	private SQLdriver driver;
	
	private Connection con;
	private Statement st;
	private String conName;
	private boolean connected;
	
	//TODO https://www.spigotmc.org/resources/mysql-api.16454/

	public SQL(Kolize plugin, String name) {
		this.plugin = plugin;
		this.conName = name;
		this.connected = false;
	}

	public Boolean Connect(String host, String db, String user, String pass) {
		Utils.HOST = host;
		Utils.DB = db;
		Utils.USER = user;
		Utils.PASS = pass;
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
