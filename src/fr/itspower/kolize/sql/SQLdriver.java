package fr.itspower.kolize.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.Bukkit;

public class SQLdriver {
	
	  String HOST = null;
	  String DB = null;
	  String USER = null;
	  String PASS = null;
	  private Connection con = null;
	  
	  public SQLdriver(String host, String db, String user, String pass)
	  {
	    this.HOST = host;
	    this.DB = db;
	    this.USER = user;
	    this.PASS = pass;
	  }
	  
	  public Connection open()
	  {
	    try
	    {
	      Class.forName("com.mysql.jdbc.Driver");
	      this.con = DriverManager.getConnection("jdbc:mysql://" + this.HOST + ":3306/" + this.DB, this.USER, this.PASS);
	      return this.con;
	    }
	    catch (SQLException e)
	    {
	      Bukkit.getLogger().log(Level.SEVERE, "Could not connect to MySQL server, error code: " + e.getErrorCode());
	    }
	    catch (ClassNotFoundException e)
	    {
	      Bukkit.getLogger().log(Level.SEVERE, "JDBC driver was not found in this machine.");
	    }
	    return this.con;
	  }
	  
	  public boolean checkConnection()
	  {
	    if (this.con != null) {
	      return true;
	    }
	    return false;
	  }
	  
	  public void close(Connection c)
	  {
	    c = null;
	  }
	  
	  public Connection getCon()
	  {
	    return this.con;
	  }
	  
	  public void setCon(Connection con)
	  {
	    this.con = con;
	  }
	}
