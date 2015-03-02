package androidTest.com.zuzhili.dbtest;

import android.database.SQLException;
import android.test.AndroidTestCase;

import com.zuzhili.db.DBHelper;

/**
 * Created by liutao on 14-6-15.
 */
public class DBHelperTest extends AndroidTestCase {

    private DBHelper dbHelper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dbHelper = DBHelper.getInstance(getContext());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testPreconditions() {
        assertNotNull("dbHelper is null", dbHelper);
    }

    public void testCreateTables() {
        try {
            dbHelper.createDB();
            assertTrue("tables created", true);
        } catch (SQLException e) {
            assertTrue("tables not created", false);
        }
    }
}
