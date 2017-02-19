package com.github.unchama.sql;

import java.sql.SQLException;

import com.github.unchama.player.GiganticPlayer;

public abstract class PlayerTableManager extends TableManager{

	public PlayerTableManager(Sql sql) {
		super(sql);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	/**ex)
	 * command = "add column if not exists name varchar(30) default null,"
	 *
	 * @return command
	 */
	abstract String addOriginalColumn();
	/**set new player data
	 *
	 * @param gp
	 * @return command
	 */
	abstract void insertNewPlayer(GiganticPlayer gp);
	/**ex)
		for(BlockType bt : BlockType.values()){
			double n = rs.getDouble(bt.getColumnName());
			datamap.put(bt, new MineBlock(n));
		}
	 *
	 * @param gp
	 * @throws SQLException
	 */
	abstract void loadPlayer(GiganticPlayer gp)throws SQLException;
	/**ex)
		for(BlockType bt : datamap.keySet()){
			i++;
			command += bt.getColumnName() + " = '" + datamap.get(bt).getNum() + "',";
		}
	 *
	 * @param gp
	 * @return
	 */
	abstract String savePlayer(GiganticPlayer gp);






	@Override
	Boolean createTable() {
		String command;
		//create Table
		command =
				"CREATE TABLE IF NOT EXISTS "
				+ db + "." + table;
		//Unique Column add
		command += "(uuid varchar(128) unique)";
		//send
		if(!sendCommand(command)){
			plugin.getLogger().warning("Failed to Create " + table + " Table");
			return false;
		}

		//Column add
		command =
				"alter table " + db + "." + table + " ";
		//name add
		command += "add column if not exists name varchar(30) default null,";
		//original column
		command += this.addOriginalColumn();
		//index add
		command += "add index if not exists uuid_index(uuid)";

		//send
		if(!sendCommand(command)){
			plugin.getLogger().warning("Failed to add Column in " + table + " Table");
			return false;
		}
		return true;
	}

	@Override
	public Boolean load(GiganticPlayer gp) {
		String command = "";
		final String struuid = gp.uuid.toString().toLowerCase();
		int count = -1;

 		command = "select count(*) as count from " + db + "." + table
 				+ " where uuid = '" + struuid + "'";

 		this.checkStatement();
 		try{
			rs = stmt.executeQuery(command);
			while (rs.next()) {
				   count = rs.getInt("count");
				  }
			rs.close();
		} catch (SQLException e) {
			plugin.getLogger().warning("Failed to count player:" + gp.name);
			e.printStackTrace();
			return false;
		}

 		if(count == 0){
 			//uuid is not exist

 			//new uuid line create
 			command = "insert into " + db + "." + table
 	 				+ " (name,uuid) values('" + gp.name
 	 				+ "','" + struuid+ "')";
 			if(!sendCommand(command)){
 				plugin.getLogger().warning("Failed to create new row (player:" + gp.name + ")");
 				return false;
 			}

 			this.insertNewPlayer(gp);
 			return true;

 		}else if(count == 1){
 			//uuidが存在するときの処理
 			//update name
 			command = "update " + db + "." + table
 					+ " set name = '" + gp.name + "'"
 					+ " where uuid like '" + struuid + "'";
 			if(!sendCommand(command)){
 				plugin.getLogger().warning("Failed to update name (player:" + gp.name + ")");
 				return false;
 			}

 			command = "select * from " + db + "." + table
 					+ " where uuid = '" + struuid + "'";
			try {
				rs = stmt.executeQuery(command);
				while (rs.next()) {
					this.loadPlayer(gp);
				}
				rs.close();
			} catch (SQLException e) {
				plugin.getLogger().warning("Failed to read count (player:" + gp.name + ")");
				e.printStackTrace();
				return false;
			}
 			return true;
 		}else{
 			//mysqlに該当するplayerdataが2個以上ある時エラーを吐く
 			plugin.getLogger().warning("Failed to read count (player:" + gp.name + ")");
 			return false;
 		}
	}

	@Override
	public Boolean save(GiganticPlayer gp) {
		String command = "";
		final String struuid = gp.uuid.toString().toLowerCase();
		this.checkStatement();

		command = "update " + db + "." + table
				+ " set ";

		command += this.savePlayer(gp);

		command += " where uuid = '" + struuid + "'";

		command.replace(", where uuid", " where uuid");

		try {
			stmt.executeUpdate(command);
		}catch (SQLException e) {
			plugin.getLogger().warning("Failed to update " + table + "Data of Player:" + gp.name);
			e.printStackTrace();
			return false;
		}

		return true;
	}

}
