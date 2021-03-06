package eg.edu.alexu.csd.oop.jdbc.cs24;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MyResultSet implements ResultSet {
	private int cursor=0;
	private Object[][] result;
	private String[] columns;
	private MyResultSetMetaData rmd;
	private Boolean opened=false;
	private Statement statement;
	private String tableName;
	private static Logger logger = Logger.getLogger(String.valueOf(MyResultSet.class));
	public MyResultSet(Object[][] arr,String[] col,Statement s,String tn){
		this.result=arr;
		this.columns=col;
		opened=true;
		statement=s;
		tableName=tn;
		setMetaData();
		WriteInLog();

	}

	private void setMetaData(){
		rmd=new MyResultSetMetaData(tableName,columns,result);
	}

	public boolean absolute(int row) throws SQLException {
		if(!opened){ logger.severe("Couldn't change cursor's position to absolute");throw new SQLException();}
		logger.info("Changing cursor's position to absolute");
		if(Math.abs(row)<=result.length&&row!=0){
			if(row>0){
				this.cursor=row;
				return true;
			}
			else{
				this.cursor=result.length+row+1;
				return true;
			}
		}
		else {
			cursor=0;
			return false;}
	}
	
	public boolean next() throws SQLException {if(!opened) {logger.severe("Couldn't change the cursor's position ");throw new SQLException();}
		logger.info("Changing the cursor's position to its next position");
		if(cursor!=result.length){cursor++;return true;}
		else{cursor=result.length+1;return false;}
	}
	
	public boolean previous() throws SQLException {
		if(!opened) {logger.severe("Couldn't change the cursor's position");throw new SQLException();}
		logger.info("Changing the cursor's position to its previous position");
		if(cursor==1||cursor==0){
			cursor=0;
			return false;
		}
		else{cursor--; return true;}

	}
	
	public String getString(int columnIndex) throws SQLException {
		if(!opened||columnIndex<1||columnIndex>columns.length||result==null||cursor==0||cursor==result.length+1) {logger.severe("Couldn't get string of the column index:"+columnIndex);throw new SQLException();}
		else{logger.info("Getting string of the column index:"+columnIndex);return result[cursor-1][columnIndex-1].toString();}

	}
	private int getindex(String columnLabel){
		if(columns!=null){
			for(int i=0;i<columns.length;i++){
				if(columns[i].toLowerCase().equals(columnLabel.toLowerCase())){return i;}
			}
		}
		return -1;
	}
	
	public String getString(String columnLabel) throws SQLException {
		int i=getindex(columnLabel);
		if(!opened||i==-1) {logger.severe("Couldn't get string of the column label:"+columnLabel);throw new SQLException();}
		else{logger.info("Getting string of the column label:"+columnLabel);return getString(i+1);}

	}
	
	public int getInt(int columnIndex) throws SQLException {
		if(!opened||columnIndex<1||columnIndex>columns.length||result==null||cursor==0||cursor==result.length+1) {logger.severe("Couldn't get int of column index:"+columnIndex);throw new SQLException();}
		logger.info("Getting int of column index:"+columnIndex);
		if((result[cursor-1][columnIndex-1])instanceof Integer){return (int)result[cursor-1][columnIndex-1];}

		return 0;
	}
	
	public int getInt(String columnLabel) throws SQLException {
		int i=getindex(columnLabel);
		if(!opened||i==-1) {logger.severe("Couldn't get the in of column label:"+columnLabel);throw new SQLException();}
		else{logger.info("Getting int of column label:"+columnLabel);return getInt(i+1);}
	}
	
	public ResultSetMetaData getMetaData() throws SQLException {
		if(!opened) {logger.severe("Couldn't get metadata");throw new SQLException();}
		logger.info("Getting metadata");
		return this.rmd;
	}


	public Object getObject(int columnIndex) throws SQLException {
		if(!opened||columnIndex<1||columnIndex>columns.length||cursor==0||cursor==result.length+1) {logger.severe("Couldn't get object of column index:"+columnIndex);throw new SQLException();}
		else{logger.info("Getting object of column index:"+columnIndex);return result[cursor-1][columnIndex-1];}

	}
	
	public int findColumn(String columnLabel) throws SQLException {
		int i=getindex(columnLabel);
		if(!opened||i==-1) {logger.severe("Couldn't find column:"+columnLabel);throw new SQLException();}
		else{logger.info("Getting column with name:"+columnLabel);return i+1;}
	}
	
	public boolean isBeforeFirst() throws SQLException {
		if(!opened||result==null) {logger.severe("Couldn't check if index is before first");throw new SQLException();}
		logger.info("Checking if index is before first");
		return cursor==0;
	}


	public boolean isAfterLast() throws SQLException {
		if(!opened||result==null) {logger.severe("Couldn't check if index is after last");throw new SQLException();}
		logger.info("Checking if index is after last");
		return cursor==result.length+1;
	}


	public boolean isFirst() throws SQLException {
		if(!opened||result==null) {logger.severe("Couldn't check if index is first");throw new SQLException();}
		logger.info("Checking if index is first");
		return cursor==1;

	}


	public boolean isLast() throws SQLException {
		if(!opened||result==null) {logger.severe("Couldn't check if index is last");throw new SQLException();}
		logger.info("Checking if index is last");
		return cursor==result.length;
	}


	public void beforeFirst() throws SQLException {
		if(!opened||result==null) {logger.severe("Couldn't execute the before first method");throw new SQLException();}
		logger.info("Executing the before first method");
		cursor=0;

	}
	
	public void afterLast() throws SQLException {
		if(!opened||result==null) {logger.severe("Couldn't execute the after last method");throw new SQLException();}
		cursor=result.length+1;
		logger.info("Executing the after last method");

	}


	public boolean first() throws SQLException {
		if(!opened) {logger.severe("Couldn't check if cursor is on first");throw new SQLException();}
		logger.info("Checking if cursor is on first");
		if(result==null){return false;}
		else{cursor=1;return true;}
	}


	public boolean last() throws SQLException {
		if(!opened) {logger.severe("Couldn't check if cursor is on last");throw new SQLException();}
		logger.info("Checking if cursor is on last");
		if(result==null){return false;}
		else{cursor=result.length;return true;}
	}
	
	public Statement getStatement() throws SQLException {
		if(!opened) {logger.severe("Couldn't get statement");throw new SQLException();}
		logger.info("Getting statement");
		return statement;
	}
	
	public boolean isClosed() throws SQLException {
		logger.info("Checking if closed");
		return !opened;
	}

	public void close() throws SQLException {
		logger.info("Closing");
		this.statement=null;
		this.result=null;
		this.opened=false;
		this.columns=null;

	}
	private static void WriteInLog()
	{
		try
		{
			System.setProperty("java.util.logging.SimpleFormatter.format",
					"%1$tA %1$td %1$tB %1$tY %1$tH:%1$tM:%1$tS.%1$tL %tZ %4$s %2$s %5$s%6$s%n");
			FileHandler handler = new FileHandler("Logs"+System.getProperty("file.separator")+"MyLog%u.log", true);
			logger.addHandler(handler);
			SimpleFormatter formatter = new SimpleFormatter();
			handler.setFormatter(formatter);
		}catch (IOException e)
		{
			new File("Logs").mkdir();
			WriteInLog();
			e.printStackTrace();
		}
	}

//	================================ UNUSED METHODS ================================
	
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public boolean wasNull() throws SQLException {
		throw new UnsupportedOperationException();
	}



	public boolean getBoolean(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public byte getByte(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public short getShort(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}



	public long getLong(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public float getFloat(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public double getDouble(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public byte[] getBytes(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public Date getDate(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public Time getTime(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public boolean getBoolean(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public byte getByte(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public short getShort(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public long getLong(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public float getFloat(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public double getDouble(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public byte[] getBytes(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public Date getDate(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public Time getTime(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public Timestamp getTimestamp(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public InputStream getAsciiStream(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public InputStream getUnicodeStream(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public InputStream getBinaryStream(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public SQLWarning getWarnings() throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void clearWarnings() throws SQLException {
		throw new UnsupportedOperationException();
	}


	public String getCursorName() throws SQLException {
		throw new UnsupportedOperationException();
	}


	public Object getObject(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}

	
	public Reader getCharacterStream(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public Reader getCharacterStream(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public int getRow() throws SQLException {
		throw new UnsupportedOperationException();
	}


	public boolean relative(int rows) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void setFetchDirection(int direction) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public int getFetchDirection() throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void setFetchSize(int rows) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public int getFetchSize() throws SQLException {
		throw new UnsupportedOperationException();
	}


	public int getType() throws SQLException {
		throw new UnsupportedOperationException();
	}


	public int getConcurrency() throws SQLException {
		throw new UnsupportedOperationException();
	}


	public boolean rowUpdated() throws SQLException {
		throw new UnsupportedOperationException();
	}


	public boolean rowInserted() throws SQLException {
		throw new UnsupportedOperationException();
	}


	public boolean rowDeleted() throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateNull(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateByte(int columnIndex, byte x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateShort(int columnIndex, short x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateInt(int columnIndex, int x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateLong(int columnIndex, long x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateFloat(int columnIndex, float x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateDouble(int columnIndex, double x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateString(int columnIndex, String x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateDate(int columnIndex, Date x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateTime(int columnIndex, Time x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateObject(int columnIndex, Object x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateNull(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateBoolean(String columnLabel, boolean x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateByte(String columnLabel, byte x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateShort(String columnLabel, short x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateInt(String columnLabel, int x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateLong(String columnLabel, long x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateFloat(String columnLabel, float x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateDouble(String columnLabel, double x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateString(String columnLabel, String x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateBytes(String columnLabel, byte[] x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateDate(String columnLabel, Date x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateTime(String columnLabel, Time x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateObject(String columnLabel, Object x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void insertRow() throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateRow() throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void deleteRow() throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void refreshRow() throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void cancelRowUpdates() throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void moveToInsertRow() throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void moveToCurrentRow() throws SQLException {
		throw new UnsupportedOperationException();
	}


	public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public Ref getRef(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public Blob getBlob(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public Clob getClob(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public Array getArray(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public Ref getRef(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public Blob getBlob(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public Clob getClob(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public Array getArray(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public Date getDate(String columnLabel, Calendar cal) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public Time getTime(String columnLabel, Calendar cal) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public URL getURL(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public URL getURL(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateRef(int columnIndex, Ref x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateRef(String columnLabel, Ref x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateBlob(String columnLabel, Blob x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateClob(int columnIndex, Clob x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateClob(String columnLabel, Clob x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateArray(int columnIndex, Array x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateArray(String columnLabel, Array x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public RowId getRowId(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public RowId getRowId(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateRowId(int columnIndex, RowId x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateRowId(String columnLabel, RowId x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public int getHoldability() throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateNString(int columnIndex, String nString) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateNString(String columnLabel, String nString) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public NClob getNClob(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public NClob getNClob(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public SQLXML getSQLXML(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public SQLXML getSQLXML(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public String getNString(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public String getNString(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public Reader getNCharacterStream(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public Reader getNCharacterStream(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateClob(int columnIndex, Reader reader) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateClob(String columnLabel, Reader reader) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateNClob(int columnIndex, Reader reader) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public void updateNClob(String columnLabel, Reader reader) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
		throw new UnsupportedOperationException();
	}


	public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
		throw new UnsupportedOperationException();
	}

}
