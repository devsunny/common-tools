package com.asksunny.db;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SetOperation;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.util.TablesNamesFinder;

public class DataAnalyzer {

	
	
	public void analyze(Connection conn, String query) throws Exception
	{
		
		CCJSqlParserManager pm = new CCJSqlParserManager();
		net.sf.jsqlparser.statement.Statement statement = pm.parse(new StringReader(query));		
		if (statement instanceof Select) {
			Select selectStatement = (Select) statement;
			selectStatement.getSelectBody().accept(new QueryVisitor());
			
			TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
			List<String> tableList = tablesNamesFinder.getTableList(selectStatement);
			String tableName = tableList.get(0);
			System.out.println(tableName);			
			String selectAll = String.format("select * from %s where 1>2", tableName);
			EntityMetaData emd =   getEntityMetaData(conn, selectAll);
			
			FieldMetadata[] fmetas = emd.getFieldMetadatas();
			for (int i = 0; i < fmetas.length; i++) {
				
			}
			
			
			
		}else{
			throw new RuntimeException("Not  supported operation");
		}
		
		

		
	}
	
	
	protected EntityMetaData getEntityMetaData(Connection conn, String query) throws SQLException
	{
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		ResultSetMetaData rsmd = rs.getMetaData();
		int colcount = rsmd.getColumnCount();
		FieldMetadata[] fmetas = new FieldMetadata[colcount];		
		for(int i=0; i<=colcount; i++)
		{
			FieldMetadata fmeta = new FieldMetadata();
			fmeta.setScale(rsmd.getScale(i));
			fmeta.setPrecision(rsmd.getPrecision(i));
			fmeta.setIndex(i);
			fmeta.setEntityName(rsmd.getTableName(i));
			fmeta.setJdbcType(rsmd.getColumnType(i));
			fmeta.setDisplaySize(rsmd.getColumnDisplaySize(i));			
			fmetas[i-1] = fmeta;			
		}
		EntityMetaData emd = new EntityMetaData();
		emd.setFieldMetadatas(fmetas);
		return emd;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String sql = "SELECT c1, c2, c3,c4 FROM MY_TABLE1 where c5=12 and c2='A'" ;
		DataAnalyzer analyzer = new DataAnalyzer();
		analyzer.analyze(null,  sql);

	}
	
	private static class QueryVisitor implements SelectVisitor
	{

		@Override
		public void visit(PlainSelect plainSelect) {
			Expression expr = plainSelect.getWhere();			
			System.out.println(String.format("selectBody %s", plainSelect.getSelectItems()));
			System.out.println(String.format("plainSelect %s", expr.getClass().toString()));
		}

		@Override
		public void visit(SetOperationList setOpList) {
			List<PlainSelect> selects = setOpList.getPlainSelects();
			for (PlainSelect plainSelect : selects) {
				System.out.println(String.format("PlainSelect %s", plainSelect.toString()));
			}
			List<SetOperation> setOpts = setOpList.getOperations();			
			for (SetOperation setOperation : setOpts) {	
				System.out.println(String.format("setOpList %s", setOperation.toString()));	
			}
			
					
		}

		@Override
		public void visit(WithItem withItem) {
			System.out.println(String.format("withItem %s", withItem.toString()));
			
		}
		
	}

}
