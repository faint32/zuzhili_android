package androidTest.com.zuzhili.dbtest;

import android.test.AndroidTestCase;

import com.zuzhili.db.DBHelper;
import com.zuzhili.db.Table;
import com.zuzhili.model.im.IMConversation;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by liutao on 14-6-27.
 */
public class TableTest extends AndroidTestCase {
    private DBHelper dbHelper;

    private Table table;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        dbHelper = DBHelper.getInstance(getContext());
        table = dbHelper.getTable();
        table.setDbUtils(dbHelper.getDbUtils());
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testPreconditions() throws Exception {
        assertNotNull("message table is null", table);
        assertNotNull("dbUtils is null", table.getDbUtils());
    }

    public void testQueryIMConversation() throws Exception {
        try {
            List<IMConversation> imConversations = table.queryIMConversation("63");
            assertNotNull("query result is null", imConversations);
            assertTrue("query result is empty", imConversations.size() > 0);
        } catch (SQLException e) {
            assertTrue(e.getMessage(), false);
        }

    }
}
