package com.github.unchama.seichi.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.github.unchama.gigantic.Gigantic;
import com.github.unchama.yml.DebugManager;

public abstract class SeichiTableManager {
	protected DebugManager debug = Gigantic.yml.getManager(DebugManager.class);
	protected Gigantic plugin = Gigantic.plugin;
	private SeichiAssistSql sql;
	protected final String db;
	private Connection con;
	protected Statement stmt = null;
	protected ResultSet rs;
	protected final String table;

	public SeichiTableManager(SeichiAssistSql sql) {
		this.sql = sql;
		this.db = sql.getDataBaseName();
		this.con = sql.getConnection();
		this.table = SeichiAssistSql.SeichiTableManagerType
				.getTableNamebyClass(this.getClass());
		try {
			stmt = this.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * このテーブルに接続するステートメントを作成し，返り値とします．
	 *
	 * @return Statement
	 * @throws SQLException
	 */
	public Statement createStatement() throws SQLException {
		return con.createStatement();
	}

	/**
	 * このテーブルに接続するステートメントをチェックし，切断されていれば新しく生成します．
	 * Manager内でこのメソッドを実行する場合引数，返り値共に不要です．
	 *
	 * @param _stmt
	 * @return Statement
	 */
	public Statement checkStatement(Statement _stmt) {
		try {
			if (_stmt == null || _stmt.isClosed()) {
				plugin.getLogger()
						.warning(
								"("
										+ table
										+ ")Statement is Closed. Creating Statement...");
				_stmt = con.createStatement();
			}
		} catch (SQLException e) {
			plugin.getLogger()
					.warning(
							"Statement is Closed by Fatal Error. ReConnecting MySql...");
			sql.checkConnection();
		}
		return _stmt;
	}

	/**
	 * このテーブルに接続するステートメントをチェックし，切断されていれば新しく生成します．
	 * Manager内でこのメソッドを実行する場合引数，返り値共に不要です．
	 *
	 * @param _stmt
	 * @return Statement
	 */
	protected void checkStatement() {
		this.checkStatement(stmt);
	}

	/**
	 * コマンド出力関数 Manager以外でこのメソッドを実行する場合ステートメントを引数に追加してください
	 *
	 * @param command
	 * @return 可否
	 */
	public boolean sendCommand(String command, Statement _stmt) {
		_stmt = checkStatement(_stmt);
		try {
			_stmt.executeUpdate(command);
			return true;
		} catch (SQLException e) {
			plugin.getLogger().warning(
					"Failed to send Command in " + table + " Table");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * コマンド出力関数 Manager以外でこのメソッドを実行する場合ステートメントを引数に追加してください
	 *
	 * @param command
	 * @return 可否
	 */
	protected boolean sendCommand(String command) {
		return this.sendCommand(command, stmt);
	}
}
