package androidTest.com.zuzhili.dbtest;

import android.test.AndroidTestCase;

import com.zuzhili.db.DBHelper;
import com.zuzhili.db.IMGroupInfoTable;
import com.zuzhili.model.im.IMGroup;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liutao on 14-6-19.
 */
public class GroupInfoTableTest extends AndroidTestCase {

    private DBHelper dbHelper;

    private IMGroupInfoTable groupInfoTable;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        dbHelper = DBHelper.getInstance(getContext());
        groupInfoTable = dbHelper.getGroupInfoTable();
        groupInfoTable.setDbUtils(dbHelper.getDbUtils());
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testPreconditions() throws Exception {
        assertNotNull("groupInfoTable is null", groupInfoTable);
        assertNotNull("dbUtils is null", groupInfoTable.getDbUtils());
    }

    public void testInsertIMGroupInfo() throws Exception {
        try {
//            groupInfoTable.insertIMGroupInfo(buildDummyIMGroup("1", "123", "Test", "taoliuh@gmail.com", "63", "hello world"));
            assertTrue(true);
        } catch (Exception e) {
            assertTrue("insert runs into exception: " + e.getMessage(), false);
        }

    }

//    public void testInsertIMGroupInfos() throws Exception {
//        try {
//            List<IMGroup> groupList = new ArrayList<IMGroup>();
//            groupList.add(buildDummyIMGroup("2", "1230", "Test", "taoliuh@gmail.com", "63", "hello world 1"));
//            groupList.add(buildDummyIMGroup("3", "1231", "Test", "taoliuh@gmail.com", "63", "hello world 2"));
//            groupList.add(buildDummyIMGroup("4", "1232", "Test", "taoliuh@gmail.com", "63", "hello world 3"));
//            groupList.add(buildDummyIMGroup("5", "1233", "Test", "taoliuh@gmail.com", "63", "hello world 4"));
//            groupList.add(buildDummyIMGroup("6", "1234", "Test", "taoliuh@gmail.com", "63", "hello world 5"));
//
//            groupInfoTable.insertIMGroupInfos(groupList);
//            assertTrue(true);
//        } catch (SQLException e) {
//            assertTrue("insert runs into exception: " + e.getMessage(), false);
//        }
//
//    }

    public void testIsExistsGroupId() throws Exception {
        try {

//            groupInfoTable.insertIMGroupInfo(buildDummyIMGroup("7", "1235", "Test", "taoliuh@gmail.com", "63", "hello world 6"));
            String existsGroupId = groupInfoTable.isExistsGroupId("1234");
            assertNotNull("groupId not existed", existsGroupId);
        } catch (SQLException e) {
            assertTrue("query runs into exception: " + e.getMessage(), false);
        }
    }

//    public void testUpdateGroupInfo() throws Exception {
//        try {
//            groupInfoTable.updateGroupInfo(buildDummyIMGroup("8", "1234", "Test", "taoliuh@gmail.com", "63", "hello world 6"));
//            assertTrue(true);
//        } catch (SQLException e) {
//            assertTrue("update runs into exception: " + e.getMessage(), false);
//        }
//
//    }

    public void testQueryGroup() throws Exception {
//        try {
//            IMGroup imGroup = groupInfoTable.queryGroup("1234");
//            assertNotNull("query nothing", imGroup);
//        } catch (SQLException e) {
//            assertTrue("query runs into exception: " + e.getMessage(), false);
//
//        }

    }

//    public void testQueryGroups() throws Exception {
//        try {
//            List<IMGroup> imGroups = groupInfoTable.queryGroups("63");
//            assertNotNull("query nothing", imGroups);
//            assertTrue("group list is empty", imGroups.size() > 0);
//        } catch (SQLException e) {
//            assertTrue("query runs into exception: " + e.getMessage(), false);
//        }
//    }

//    public void testDeleteGroupByGroupId() throws Exception {
//        try {
//            groupInfoTable.insertIMGroupInfo(buildDummyIMGroup("118", "1234", "Test", "taoliuh@gmail.com", "63", "hello world 6"));
//            groupInfoTable.deleteGroupByGroupId("118");
//            assertTrue(true);
//        } catch (SQLException e) {
//            assertTrue("update runs into exception: " + e.getMessage(), false);
//        }
//
//    }

    private IMGroup buildDummyIMGroup(String id, String groupId, String name, String owner, String listId, String lastMessage) {
        IMGroup data = new IMGroup();
        data.setId(id);
        data.setGroupId(groupId);
        data.setName(name);
        data.setOwner(owner);
        data.setListId(listId);
        data.setLastMessage(lastMessage);
        data.setCount("10");
        data.setPermission("1");
        data.setCreatedDate(String.valueOf(System.currentTimeMillis()));
        data.setType("1");
        data.setDeclared("测试");
        return data;
    }
}
