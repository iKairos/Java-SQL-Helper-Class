import java.sql.*;

/**
 * A helper class that helps to interface to a MySQL database.
 * @author Kairos
 * @version 1.0 (Underdeveloped Build)
 *
 */
public class Interface {
	private Connection conn;
	
	public Interface()
	{
		try
		{
			this.conn = DriverManager.getConnection("mysqlServer", "user", "pass");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Closes the connection to this database interface.
	 * @return No return value
	 */
	public void close()
	{
		try {
			this.conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Interprets a <code>ResultSet</code> object and prints it into the console stream.
	 * @param	res a <code>ResultSet</code> object.
	 * @return No return value
	 */
	public void interpret_results(ResultSet res)
	{
		try{
			ResultSetMetaData data = res.getMetaData();
			int cols = data.getColumnCount();
			while(res.next()){
				for(int i=1; i<=cols; i++){
					String value = res.getString(i);
					System.out.println(value + " | " + data.getColumnName(i));
				}
				System.out.print("\n");
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns a <code>ResultSet</code> object that can be iterated to interpret its results.<br>
	 * Pass an empty list to <b>args</b> if you want to fetch everything in the table.
	 * @Note - Format of 2D <code>String</code> array: <code>{{"column_1","value_1"},...,{"column_n","name_n"}}</code>.<br>
	 * - Other logical comparisons such as &lt=, &gt= for queries are not yet present on this version of the code.
	 * @param	table	the table in which you want to fetch the row.
	 * @param	args	a two-dimensional list which contains the column of interest and its value. 
	 * If an empty list is passed, query will fetch everything.
	 * @return			a <code>ResultSet</code> object that can be interpreted. 
	 */
	public ResultSet fetch_row(String table, String[][] args)
	{
		String statement;
		
		if(args.length == 0)
		{
			statement = String.format("SELECT * FROM %s", table);
		}
		
		statement = String.format("SELECT * FROM %s WHERE ", table);
		
		for(int i=0;i<args.length;i++)
		{
			for(int j=0;j<args[i].length;j+=2)
			{
				statement += "%s=? AND ";
				statement = String.format(statement, args[i][j]);
			}
		}
		
		statement = statement.substring(0, statement.length()-5);
		
		try {
			PreparedStatement state = this.conn.prepareStatement(statement);
			int c = 0;
			for(int i=0;i<args.length;i++)
			{
				for(int j=0;j < args[i].length-1;j++)
				{
					state.setString(c+1, args[i][1]);
					c++;
				}
			}
			
			ResultSet res = state.executeQuery();
			
			return res;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Appends data to the database on a specific <b>table</b>.
	 * @Note Format of 2D <code>String</code> array:<br><code>{{"column_1","value_1"},...,{"column_n","name_n"}}</code>
	 * @param	table	the table in which you want to append the row.
	 * @param	args	a two-dimensional list of the column of interest and its value.
	 * @return No return value
	 */
	public void append_row(String table, String[][] args)
	{
		String statement;
		
		statement = String.format("INSERT INTO %s ", table);
		statement += "(";
		
		for(int i=0;i<args.length;i++)
		{
			for(int j=0;j<args[i].length;j+=2)
			{
				statement += "%s, ";
				statement = String.format(statement, args[i][j]);
			}
		}
		
		statement = statement.substring(0, statement.length()-2) + ") ";
		statement += "VALUES (";
		
		for(int i=0;i<args.length;i++)
		{
			statement += "?,";
		}
		
		statement = statement.substring(0, statement.length()-1) + ")";
		
		try {
			PreparedStatement state = this.conn.prepareStatement(statement);
			
			for(int i=0;i<args.length;i++)
			{
				for(int j=0;j<args[i].length-1;j++)
				{
					state.setString(i+1, args[i][j+1]);
				}
			}
		
			state.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Updates an existing data in a table.
	 * @Note Format of 2D <code>String</code> array:<br><code>{{"column_1","value_1"},...,{"column_n","name_n"}}</code>
	 * @param	table	the table in which you want to update.
	 * @param	column	the column of your data that you will update.
	 * @param	new_value	the new value that will replace the old row value in the database.
	 * @param	args	a two-dimensional array that contains the columns of interest and its value.
	 * @return	No return value
	 */
	public void update_data(String table, String column, String new_value, String[][] args)
	{
		String statement;
		
		statement = String.format("UPDATE %s SET %s='%s' WHERE ", table, column, new_value);
		
		for(int i=0;i<args.length;i++)
		{
			for(int j=0;j<args[i].length;j+=2)
			{
				statement += "%s=? AND ";
				statement = String.format(statement, args[i][j]);
			}
		}
		
		statement += "!@#";
		statement = statement.replace(" AND !@#", "");
		
		try {
			PreparedStatement state = this.conn.prepareStatement(statement);
			
			for(int i=0;i<args.length;i++)
			{
				for(int j=0;j<args[i].length-1;j++)
				{
					state.setString(i+1, args[i][j+1]);
				}
			}
			
			state.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Deletes data from the database.
	 * @Note Format of 2D <code>String</code> array:<br><code>{{"column_1","value_1"},...,{"column_n","name_n"}}</code>
	 * @param	table	the table to delete data from.
	 * @param	args	a two-dimensional array that contains the columns of interest and its value.
	 * @return No return value
	 */
	public void delete_data(String table, String[][] args)
	{
		String statement;
		
		statement = String.format("DELETE FROM %s WHERE ", table);
		
		for(int i=0;i<args.length;i++)
		{
			for(int j=0;j<args.length;j+=2)
			{
				statement += "%s=? AND ";
				statement = String.format(statement, args[i][j]);
			}
		}
		
		statement += "!@#";
		statement = statement.replace(" AND !@#", "");
		
		try {
			PreparedStatement state = this.conn.prepareStatement(statement);
			
			for(int i=0;i<args.length;i++)
			{
				for(int j=0;j<args[i].length-1;j++)
				{
					state.setString(i+1, args[i][j+1]);
				}
			}
			
			state.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
