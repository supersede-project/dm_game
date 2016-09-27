package eu.supersede.dm.ahp.algorithm;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Matrix<T> {
	
	static class Index {
		Set<String> set = new HashSet<>();
		List<String> order = new ArrayList<>();
		
		public void add(String id) {
			if( set.contains( id ) ) {
				throw new RuntimeException( "Duplicate element: " + id );
			}
			set.add( id );
			order.add( id );
		}

		public boolean contain( String id ) {
			return set.contains( id );
		}

		public int size() {
			return order.size();
		}

		public String get(int index) {
			return order.get( index );
		}

		public void clear() {
			this.order.clear();
			this.set.clear();
		}
		
		public List<String> order() {
			return this.order;
		}
	}
	
	Matrix.Index rowHeaders = new Index();
	Matrix.Index colHeaders = new Index();
	Map<String, Map<String, Cell<T>>> table = new HashMap<String, Map<String, Cell<T>>>();
	
	public void addRow( String id ) {
		rowHeaders.add( id );
	}
	public void addColumn( String id ) {
		colHeaders.add( id );
	}
	public void setValue( String row, String col, T value ) {
		if( !rowHeaders.contain( row ) ) {
			throw new RuntimeException( "Row not existing: " + row );
		}
		if( !colHeaders.contain( col ) ) {
			throw new RuntimeException( "Column not existing: " + row );
		}
		Map<String, Cell<T>> tableRow = table.get( row );
		if( tableRow == null ) {
			tableRow = new HashMap<String, Cell<T>>();
			table.put( row, tableRow );
		}
		Cell<T> cell = tableRow.get( col );
		if( cell == null ) {
			cell = new Cell<T>();
			tableRow.put( col, cell );
		}
		cell.setValue( value );
	}
	public void setValue( int rowIndex, int colIndex, T value ) {
		if( rowIndex >= rowHeaders.size() ) {
			throw new RuntimeException( "Row out of bounds: " + rowIndex );
		}
		if( colIndex >= colHeaders.size() ) {
			throw new RuntimeException( "Column out of bounds: " + rowIndex );
		}
		setValue( rowHeaders.get( rowIndex ), colHeaders.get( colIndex ), value );
	}
	public T getValue( String row, String col, T def ) {
		if( !rowHeaders.contain( row ) ) {
			throw new RuntimeException( "Row not existing: " + row );
		}
		if( !colHeaders.contain( col ) ) {
			throw new RuntimeException( "Column not existing: " + row );
		}
		Map<String, Cell<T>> tableRow = table.get( row );
		if( tableRow == null ) {
			return def;
		}
		Cell<T> cell = tableRow.get( col );
		if( cell == null ) {
			return def;
		}
		return cell.getValue();
	}
	public void clear() {
		this.colHeaders.clear();
		this.rowHeaders.clear();
		this.table.clear();
	}
	public Matrix.Index getColumnHeaders() {
		return this.colHeaders;
	}
	public Matrix.Index getRowHeaders() {
		return this.rowHeaders;
	}
	public T getValue( int rowIndex, int colIndex, T def) {
		return getValue( getRowHeaders().get( rowIndex ), getColumnHeaders().get( colIndex ), def );
	}
	public void dump( PrintStream ps ) {
		for( int r = 0; r < getRowHeaders().size(); r++ ) {
			for( int c = 0; c < getColumnHeaders().size(); c++ ) {
				//ps.print( "\t" + getValue( r, c, -1 ) );
			}
			ps.println();
		}
	}
}