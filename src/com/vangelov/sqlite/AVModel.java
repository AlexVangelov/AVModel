/**
 * Author: Alex Vangelov (email@data.bg)
 * License: Respect the work of the creator and have fun! 2012 Alex Vangelov
 */
package com.vangelov.sqlite;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AVModel {
	private SQLiteDatabase mDb;
	private String mTable;
	private String primary_key = "ID";
	private Map<String,String> aColumns = new HashMap<String,String>();
	private Map<String,Object> aReferencesInfo = new HashMap<String,Object>();
	
	public AVModel() {}
	public AVModel(SQLiteDatabase db) {
		this.mDb = db;
		this.mTable = getTableName();
		this.primary_key = getPrimaryKey().toUpperCase();
		initialize();
	}
	
	public void setDatabase(SQLiteDatabase db) {
		mDb = db;
	}
	public void setTableName(String tableName) {
		mTable = tableName;
	}
	
	public String getTableName() {
		if (mTable == null) {
			//Camel notation convertion
		}
		return mTable;
	}
	public void setPrimaryKey(String primary_key) {
		this.primary_key = primary_key;
	}
	public String getPrimaryKey() {
		return primary_key;
	}
	public Map<String,String> getColumns() {
		return aColumns;
	}
	public void initialize() {
		try {
	        Cursor cursor = mDb.rawQuery("PRAGMA table_info("+getTableName()+")", null);
	        if (cursor.moveToFirst()) {
		         do {
		        	 this.aColumns.put(cursor.getString(1).toUpperCase(), cursor.getString(2));
		        	 if (getPrimaryKey() == null) {	//primary key not specified, so get it
		        		 if (cursor.getInt(5) == 1) setPrimaryKey(cursor.getString(1));
		        	 }
		         } while (cursor.moveToNext());
		    }
		    if (cursor != null && !cursor.isClosed()) {
		         cursor.close();
		    }
	    } catch (Exception e) {
	        Log.v(mTable, e.getMessage(), e);
	        e.printStackTrace();
	    }
	    setRelations();
	}
	
	public void setRelations() {}
	
	public SQLRow newRow() {
		return new SQLRow();
	}
	
	public int update(ContentValues values,String whereClause,String[] whereArgs) {
		return mDb.update(getTableName(), values, whereClause, whereArgs);
	}
	
	public long replace(ContentValues initialValues) {
		return mDb.replace(getTableName(), getPrimaryKey(), initialValues);
	}
	
	public long replaceOrThrow(ContentValues initialValues) {
		return mDb.replaceOrThrow(getTableName(), getPrimaryKey(), initialValues);
	}
	
	public long insert(ContentValues values) {
		return mDb.insert(getTableName(), getPrimaryKey(), values);
	}
	
	public long insertOrThrow(ContentValues values) {
		return mDb.insertOrThrow(getTableName(), getPrimaryKey(), values);
	}
	
	public int delete(String whereClause,String[] whereArgs) {
		return mDb.delete(getTableName(), whereClause, whereArgs);
	}
	
	public SQLRow find(int id) {
		Cursor c = mDb.query(getTableName(), null, getPrimaryKey()+" = "+id, null, null, null, null);
		SQLCollection rows = new SQLCollection(c);
		return rows.get(0);
	}
	
	public SQLCollection find_with_limit(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
		Cursor c = mDb.query(getTableName(), columns, selection, selectionArgs, groupBy, having, orderBy, limit);
		return new SQLCollection(c);
	}
	
	public SQLCollection find_distinct(boolean distinct, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
		Cursor c = mDb.query(distinct,getTableName(), columns, selection, selectionArgs, groupBy, having, orderBy, limit);
		return new SQLCollection(c);
	}
	
	public SQLCollection find_with_factory(SQLiteDatabase.CursorFactory cursorFactory,boolean distinct, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
		Cursor c = mDb.queryWithFactory(cursorFactory,distinct,getTableName(), columns, selection, selectionArgs, groupBy, having, orderBy, limit);
		return new SQLCollection(c);
	}
	
	public SQLCollection find_all() {
		Cursor c = mDb.query(getTableName(), null, null, null, null, null, null);
		return new SQLCollection(c);
	}
	
	public SQLCollection find_all(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
		Cursor c = mDb.query(getTableName(), columns, selection, selectionArgs, groupBy, having, orderBy);
		return new SQLCollection(c);
	}
	public SQLRow find_one(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
		Cursor c = mDb.query(getTableName(), columns, selection, selectionArgs, groupBy, having, orderBy);
		SQLCollection rows = new SQLCollection(c);
		return (rows.size() > 0) ? rows.get(0) : null;
	}
	
	public class SQLRow extends HashMap<String, Object> {
		private static final long serialVersionUID = 1L;
		private Map<String,Object> aReferences = new HashMap<String,Object>();
		private Map<String,Object> aCashe = new HashMap<String,Object>();
		public int row_id = -1;
		public SQLRow() {}
		public SQLRow(Cursor cursor) {
			int num = cursor.getColumnCount();
			for (int i = 0; i < num; ++i) {
				String colName = cursor.getColumnName(i).toUpperCase();
				Object value = null;
				if (getColumns().get(colName).equals("INTEGER")) {
					value = cursor.getInt(i);
				} else if (getColumns().get(colName).equals("REAL")) {
					value = cursor.getFloat(i);
				} else if (getColumns().get(colName).equals("BLOB")) {
					value = cursor.getBlob(i);
				} else if (getColumns().get(colName).equals("NULL")) {
					value = null;
				} else {
					value = cursor.getString(i);
				}
				this.put(colName, value);
				aCashe.put(colName, this.get(colName));
			}
			this.row_id = (Integer) this.get(getPrimaryKey().toUpperCase());
		}
		
		@Override
		public Object get(Object key) {
			return super.get(((String)key).toUpperCase());
		}
		
		@Override
		public Object put(String key, Object value) {
			return super.put(((String)key).toUpperCase(),value);
		}
		
		public Object getRelation(String key) {
			return getRelation(key,false);
		}
		
		public Set<String> getRelationNames() {
			return aReferencesInfo.keySet();
		}
		
		public void putRelationHack(String key, Object value) {
			aReferences.put(key, value);
		}
		
		public Object getRelation(String key,boolean reload) {
			if (aReferences.get(key) == null || reload) {
				Object reference = aReferencesInfo.get(key);
				if (reference.getClass() == OneToManyByClass.class) {
					SQLCollection c = ((OneToManyByClass<?>)reference).get(this);
					//c.setParentInfo(this);
					aReferences.put(key, c);
				} else if (reference.getClass() == BelongsToByClass.class) {
					aReferences.put(key, ((BelongsToByClass<?>)reference).get(this));
				}
			}
			return aReferences.get(key);
		}
		public int remove() {
			aReferences.clear();
			return delete(getPrimaryKey()+" = "+row_id, null);
		}
		public void save() {
			if (this.row_id == -1) {
				ContentValues insertValues = BuildContentValues(this);
				this.row_id = (int) insert(insertValues);
			} else {
				if (!this.equals(aCashe)) {
					Map<String,Object> diffMap = new HashMap<String,Object>();
					for (Map.Entry<String,Object> entry : aCashe.entrySet()) {
						String key = entry.getKey();
						Object val = this.get(entry.getKey());
						if (!val.equals(entry.getValue())) {
							diffMap.put(key, val);
						}
					}
					ContentValues updateValues = BuildContentValues(diffMap);
					update(updateValues, getPrimaryKey()+" = "+this.row_id, null);
				}
			}
			for (Entry<String,Object> entry : aReferences.entrySet()) {
				Object rf = entry.getValue();
				if (rf.getClass() == SQLCollection.class) {
					String foreign_key = ((OneToManyByClass<?>)aReferencesInfo.get(entry.getKey())).foreign_key;
					for (SQLRow r : ((SQLCollection)rf)) r.put(foreign_key, this.row_id);
					((SQLCollection)rf).save();
				} else if (rf.getClass() == SQLRow.class) {
					((SQLRow)rf).save();
				}
			}		
			aCashe.putAll(this);
		}
	}	
	
	private ContentValues BuildContentValues(Map<String, Object> diffMap) {
		ContentValues cv = new ContentValues();
		for (Map.Entry<String,Object> entry : diffMap.entrySet()) {
			Object value = entry.getValue();
			if (value == null) {
				cv.putNull(entry.getKey());
			} else if (value.getClass() == Integer.class) {
				cv.put(entry.getKey(), (Integer) entry.getValue());
			} else if (value.getClass() == byte[].class) {
				cv.put(entry.getKey(), (byte[]) value);
			} else if (value.getClass() == float.class) {
				cv.put(entry.getKey(), (Float) value);
			} else {
				cv.put(entry.getKey(), (String) value);
			}
		}
		return cv;
	}
	
	public class SQLCollection extends ArrayList<SQLRow> {
		private static final long serialVersionUID = 1L;
		private List<SQLRow> forDeletion = new ArrayList<SQLRow>();
		private Map<String,Integer>  relationPairs = new HashMap<String,Integer>();
		private Map<String,SQLRow> parentInfo = null;
		public SQLCollection() {}
		public SQLCollection(Cursor cursor) {
			if (cursor.moveToFirst()) {
		         do {
		        	 SQLRow r = new SQLRow(cursor);
		        	 this.add(r);
		         } while (cursor.moveToNext());
		    }
		    if (cursor != null && !cursor.isClosed()) {
		         cursor.close();
		    } 
		}
		private void setRelationInfo(String relationKey, int relationValue) {
			relationPairs.put(relationKey, relationValue);
		}
		
		@SuppressWarnings("unused")
		private void setParentInfo(String key,SQLRow parent) {
			if (parentInfo == null) {
				parentInfo = new HashMap<String,SQLRow>();
			}
			parentInfo.put(key, parent);
		}
		@Override
		public boolean add(SQLRow object) {
			for (Entry<String, Integer> entry : relationPairs.entrySet()) object.put(entry.getKey(), entry.getValue());
			if (parentInfo != null) {
				for (Entry<String, SQLRow> entry : parentInfo.entrySet()) object.putRelationHack(entry.getKey(),entry.getValue());
			}
			return super.add(object);
		}
		
		public void save() {
			if (!forDeletion.isEmpty()) {
				for (SQLRow r : forDeletion) {
					r.save();
					r.remove();
				}
			}
			for (SQLRow r : this) r.save();
		}
		
		public void clear(boolean includeRelatedCollections) {
			if (includeRelatedCollections) {
				for (SQLRow r : this) {
					for (String key : ((SQLRow)r).getRelationNames()) {
						Object reference = ((SQLRow)r).getRelation(key);
						if (reference.getClass() == SQLCollection.class) {
							((SQLCollection)reference).clear(includeRelatedCollections);
						}
					}
					forDeletion.add(r);
				}
			}
			super.clear();
		}
		
		@Override
		public void clear() {
			for (SQLRow r : this) {
				forDeletion.add(r);
			}
			super.clear();
		}
		
		public boolean remove(Object object,boolean includeRelatedCollections) {
			if (includeRelatedCollections) {
				for (String key : ((SQLRow)object).getRelationNames()) {
					Object reference = ((SQLRow)object).getRelation(key);
					if (reference.getClass() == SQLCollection.class) {
						((SQLCollection)reference).clear(includeRelatedCollections);
					}
				}
			}
			return this.remove(object);
		}
		
		@Override
		public boolean remove(Object object) {
			forDeletion.add((SQLRow) object);
			return super.remove(object);
		}
		
		public List<CharSequence> arrayFromTextColumn(String column) {
			List<CharSequence> ret = new ArrayList<CharSequence>();
			for (SQLRow r : this) ret.add((CharSequence) r.get(column));
			return ret;
		}
	}
	
	public <T extends AVModel> void setHasMany(String key, Class<T> model, String foreign_key) {
		OneToManyByClass<T> ref = new OneToManyByClass<T>(model,foreign_key,null,null,null,null,null,null);
		aReferencesInfo.put(key, ref);
	}
	public <T extends AVModel> void setHasMany(String key, Class<T> model, String foreign_key, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
		OneToManyByClass<T> ref = new OneToManyByClass<T>(model,foreign_key,columns,selection,selectionArgs,groupBy,having,orderBy);
		aReferencesInfo.put(key, ref);
	}
	
	public <T extends AVModel> void setBelongsTo(String key, Class<T> model, String foreign_key) {
		BelongsToByClass<T> ref = new BelongsToByClass<T>(model,foreign_key,null,null,null,null,null,null);
		aReferencesInfo.put(key, ref);
	}
	public <T extends AVModel> void setBelongsTo(String key, Class<T> model, String foreign_key, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
		BelongsToByClass<T> ref = new BelongsToByClass<T>(model,foreign_key,columns,selection,selectionArgs,groupBy,having,orderBy);
		aReferencesInfo.put(key, ref);
	}
	
	private class OneToManyByClass<T extends AVModel> {
		private String foreign_key;
		public Class<T> model;
		private String[] columns = null;
		private String selection = null;
		private String[] selectionArgs = null;
		private String groupBy = null;
		private String having = null;
		private String orderBy = null;
		private T am;
		public OneToManyByClass(Class<T> model, String foreign_key, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
			this.foreign_key = foreign_key;
			this.model = model;
			this.columns = columns;
			this.selection = selection;
			this.selectionArgs = selectionArgs;
			this.groupBy = groupBy;
			this.having = having;
			this.orderBy = orderBy;
		}
		@SuppressWarnings({ "hiding" })
		public <T extends AVModel> SQLCollection get(SQLRow parent_row) {
				SQLCollection ret = null;
				am = null;
				try {
					am =  model.newInstance();
					am.setDatabase(mDb);
					am.setTableName(am.getTableName());
					am.setPrimaryKey(am.getPrimaryKey().toUpperCase());
					am.initialize();
					String query = foreign_key+" = "+parent_row.row_id;
					ret = am.find_all(columns, (selection == null) ? query : (selection+" and "+query), selectionArgs, groupBy, having, orderBy);
					ret.setRelationInfo(foreign_key, parent_row.row_id);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				}
				return ret;
		}
	}

	private class BelongsToByClass<T extends AVModel> {
		private String foreign_key;
		private Class<T> model;
		private String[] columns = null;
		private String selection = null;
		private String[] selectionArgs = null;
		private String groupBy = null;
		private String having = null;
		private String orderBy = null;
		private T am;
		public BelongsToByClass(Class<T> model, String foreign_key, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
			this.foreign_key = foreign_key;
			this.model = model;
			this.columns = columns;
			this.selection = selection;
			this.selectionArgs = selectionArgs;
			this.groupBy = groupBy;
			this.having = having;
			this.orderBy = orderBy;
		}
		@SuppressWarnings({ "hiding" })
		public <T extends AVModel> SQLRow get(SQLRow parent_row) {
				am = null;
				try {
					am =  model.newInstance();
					am.setDatabase(mDb);
					am.setTableName(am.getTableName());
					am.setPrimaryKey(am.getPrimaryKey().toUpperCase());
					am.initialize();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				}
				String query = am.getPrimaryKey()+" = "+parent_row.get(foreign_key);
				return am.find_one(columns, (selection == null) ? query : (selection+" and "+query), selectionArgs, groupBy, having, orderBy);
		}
	}
}
