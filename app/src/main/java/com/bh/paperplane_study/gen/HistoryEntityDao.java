package com.bh.paperplane_study.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.bh.paperplane_study.bean.BeanType;
import com.bh.paperplane_study.entity.BeanTypeConverter;

import com.bh.paperplane_study.entity.HistoryEntity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "HISTORY_ENTITY".
*/
public class HistoryEntityDao extends AbstractDao<HistoryEntity, Long> {

    public static final String TABLENAME = "HISTORY_ENTITY";

    /**
     * Properties of entity HistoryEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ContentId = new Property(1, Integer.class, "contentId", false, "CONTENT_ID");
        public final static Property News = new Property(2, String.class, "news", false, "NEWS");
        public final static Property Content = new Property(3, String.class, "content", false, "CONTENT");
        public final static Property Date = new Property(4, java.util.Date.class, "date", false, "DATE");
        public final static Property Bookmark = new Property(5, Integer.class, "bookmark", false, "BOOKMARK");
        public final static Property Type = new Property(6, Integer.class, "type", false, "TYPE");
    }

    private final BeanTypeConverter typeConverter = new BeanTypeConverter();

    public HistoryEntityDao(DaoConfig config) {
        super(config);
    }
    
    public HistoryEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"HISTORY_ENTITY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"CONTENT_ID\" INTEGER NOT NULL ," + // 1: contentId
                "\"NEWS\" TEXT," + // 2: news
                "\"CONTENT\" TEXT," + // 3: content
                "\"DATE\" INTEGER," + // 4: date
                "\"BOOKMARK\" INTEGER," + // 5: bookmark
                "\"TYPE\" INTEGER);"); // 6: type
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"HISTORY_ENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, HistoryEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getContentId());
 
        String news = entity.getNews();
        if (news != null) {
            stmt.bindString(3, news);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(4, content);
        }
 
        java.util.Date date = entity.getDate();
        if (date != null) {
            stmt.bindLong(5, date.getTime());
        }
 
        Integer bookmark = entity.getBookmark();
        if (bookmark != null) {
            stmt.bindLong(6, bookmark);
        }
 
        BeanType type = entity.getType();
        if (type != null) {
            stmt.bindLong(7, typeConverter.convertToDatabaseValue(type));
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, HistoryEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getContentId());
 
        String news = entity.getNews();
        if (news != null) {
            stmt.bindString(3, news);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(4, content);
        }
 
        java.util.Date date = entity.getDate();
        if (date != null) {
            stmt.bindLong(5, date.getTime());
        }
 
        Integer bookmark = entity.getBookmark();
        if (bookmark != null) {
            stmt.bindLong(6, bookmark);
        }
 
        BeanType type = entity.getType();
        if (type != null) {
            stmt.bindLong(7, typeConverter.convertToDatabaseValue(type));
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public HistoryEntity readEntity(Cursor cursor, int offset) {
        HistoryEntity entity = new HistoryEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // contentId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // news
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // content
            cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)), // date
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // bookmark
            cursor.isNull(offset + 6) ? null : typeConverter.convertToEntityProperty(cursor.getInt(offset + 6)) // type
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, HistoryEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setContentId(cursor.getInt(offset + 1));
        entity.setNews(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setContent(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setDate(cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)));
        entity.setBookmark(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setType(cursor.isNull(offset + 6) ? null : typeConverter.convertToEntityProperty(cursor.getInt(offset + 6)));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(HistoryEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(HistoryEntity entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(HistoryEntity entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
