package androidTest.com.zuzhili.dbtest;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import com.zuzhili.db.DBHelper;
import com.zuzhili.db.IMMessageTable;
import com.zuzhili.model.im.IMChatMessageDetail;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by liutao on 14-6-15.
 */
public class IMMessageTableTest extends AndroidTestCase {

    private DBHelper dbHelper;

    private IMMessageTable messageTable;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        dbHelper = DBHelper.getInstance(getContext());
        messageTable = dbHelper.getMessageTable();
        messageTable.setDbUtils(dbHelper.getDbUtils());
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testPreconditions() throws Exception {
        assertNotNull("message table is null", messageTable);
        assertNotNull("dbUtils is null", messageTable.getDbUtils());
    }

    public void testInsertIMMessage() throws Exception {
        IMChatMessageDetail detail = buildDummyIMChatMessageDetail("1", 0, String.valueOf(System.currentTimeMillis()), IMChatMessageDetail.TYPE_MSG_TEXT, "this is for test!", "63", null, null, null, 0);
        try {
            boolean result = messageTable.insertIMMessage(detail);
            assertTrue("insert data failed", result);
        } catch (Exception e) {
            assertTrue("insert data failed", false);
        }
    }

    public void testQueryMessageExistence() throws Exception {
        String existsIMmessageId = messageTable.isExistsIMmessageId("1");
        assertNotNull("message not exist", existsIMmessageId);
    }

    public void testQueryIMMessages() throws Exception {
//        messageTable.insertIMMessage(buildDummyIMChatMessageDetail("1", 0, String.valueOf(System.currentTimeMillis()), IMChatMessageDetail.TYPE_MSG_TEXT, "hello world " + System.currentTimeMillis(), "63", null, null, null);
//        messageTable.insertIMMessage(buildDummyIMChatMessageDetail("1", 0, String.valueOf(System.currentTimeMillis()), IMChatMessageDetail.TYPE_MSG_TEXT, "big boy " + System.currentTimeMillis(), "63", null, null, null);

        try {
            ArrayList<IMChatMessageDetail> imChatMessageDetails = messageTable.queryIMMessages("1", "63", String.valueOf(System.currentTimeMillis()),"");
            assertTrue("found nothing", imChatMessageDetails != null && imChatMessageDetails.size() > 0);
        } catch (Exception e) {
            assertTrue("execute query encounters error", false);
        }
    }

    @MediumTest
    public void testQueryNewIMMessages() {
        try {
            messageTable.insertIMMessage(buildDummyIMChatMessageDetail("1", IMChatMessageDetail.STATE_READED, String.valueOf(System.currentTimeMillis()), IMChatMessageDetail.TYPE_MSG_TEXT, "test unread msg " + System.currentTimeMillis(), "63", null, null, null, 0));
            ArrayList<IMChatMessageDetail> imChatMessageDetails = messageTable.queryNewIMMessages("1", "63");
            assertTrue("bad results! " , imChatMessageDetails.size() > 0);
        } catch (IllegalArgumentException e) {
            assertTrue("IllegalArgumentException, sessionId is null!", false);
        } catch (SQLException e) {
            assertTrue("SQLException, insert error!", false);
        }
    }

    @MediumTest
    public void testDeleteAllIMMessage() {
        try {
            messageTable.deleteAllIMMessage();
        } catch (SQLException e) {
            assertTrue("SQLException, delete error!", false);
        }
    }

    public void testDeleteIMMessage() {
        try {
            messageTable.deleteIMMessage("1", "63");
            assertTrue("delete message by session id encounters error!", true);
        } catch (SQLException e) {
            assertTrue("SQLException, delete error!", false);
        }
    }

    public void testQueryIMMessageFileLocalPath() {
        try {
            messageTable.insertIMMessage(buildDummyIMChatMessageDetail("1", IMChatMessageDetail.STATE_READED, String.valueOf(System.currentTimeMillis()), IMChatMessageDetail.TYPE_MSG_FILE, null, "63", "www.zuzhili.com", "/sdcard/0", "test1.jpg", 0));
            messageTable.insertIMMessage(buildDummyIMChatMessageDetail("1", IMChatMessageDetail.STATE_READED, String.valueOf(System.currentTimeMillis()), IMChatMessageDetail.TYPE_MSG_FILE, null, "63", "www.zuzhili.com", "/sdcard/0", "test2.jpg", 0));
            ArrayList<String> fileLocalPathList = messageTable.queryIMMessageFileLocalPath("1", "63");
            assertNotNull(fileLocalPathList);
            assertEquals(2, fileLocalPathList.size());

        } catch (SQLException e) {
            assertTrue("sqliteException: " + e.getMessage(), false);
        }

    }

    public void testUpdateIMMessageSendStatusByMessageId() {

        try {
            messageTable.insertIMMessage(buildDummyIMChatMessageDetail("1", IMChatMessageDetail.STATE_UNREAD, "12345", IMChatMessageDetail.TYPE_MSG_FILE, null, "63", "www.zuzhili.com", "/sdcard/0", "test1.jpg", 0));
            messageTable.updateIMMessageSendStatusByMessageId("123", 1);
            assertTrue("update runs into exception", true);
        } catch (SQLException e) {
            assertTrue("sqliteException: " + e.getMessage(), false);
        }
    }

    public void testUpdateAllIMMessageSendFailed() {
        try {
            messageTable.updateAllIMMessageSendFailed();
            assertTrue("update runs into exception", true);

        } catch (SQLException e) {
            assertTrue("sqliteException: " + e.getMessage(), false);
        }

    }

    public void testUpdateIMMessageUnreadStatusToRead() {
        try {
            messageTable.insertIMMessage(buildDummyIMChatMessageDetail("1", IMChatMessageDetail.STATE_UNREAD, "12345", IMChatMessageDetail.TYPE_MSG_FILE, null, "63", "www.zuzhili.com", "/sdcard/0", "test1.jpg", 0));
            messageTable.updateIMMessageUnreadStatusToRead("1", "63");
            assertTrue(true);
        } catch (SQLException e) {
            assertTrue(e.getMessage(), false);
        }

    }

    public void testUpdateIMMessageUnreadStatusToRead2() {
        try {
            messageTable.insertIMMessage(buildDummyIMChatMessageDetail("1", IMChatMessageDetail.STATE_UNREAD, "12345", IMChatMessageDetail.TYPE_MSG_FILE, null, "63", "www.zuzhili.com", "/sdcard/0", "test1.jpg", 1));
            messageTable.insertIMMessage(buildDummyIMChatMessageDetail("1", IMChatMessageDetail.STATE_UNREAD, "12345", IMChatMessageDetail.TYPE_MSG_FILE, null, "63", "www.zuzhili.com", "/sdcard/0", "test2.jpg", 1));
            ArrayList<IMChatMessageDetail> unreadMessageList = messageTable.queryNewIMMessages("1", "63");
            assertNotNull("quary failed ", unreadMessageList);
            assertTrue("query unread messages failed!", unreadMessageList.size() > 0);
            messageTable.updateIMMessageUnreadStatusToRead(unreadMessageList);
            assertTrue(true);
        } catch (SQLException e) {
            assertTrue(e.getMessage(), false);
        }

    }

    public void testQueryIMChatMessageByMessageId() throws Exception {
        try {
            IMChatMessageDetail imChatMessageDetail = messageTable.queryIMChatMessageByMessageId("1338202819-5b217e58-1403609823");
            assertNotNull("could‘t find chat message", imChatMessageDetail);
        } catch (SQLException e) {
            assertTrue(e.getMessage(), false);
        }

    }

    private IMChatMessageDetail buildDummyIMChatMessageDetail(String sessionId, int readStatus, String messageId, int messageType, String messageContent, String listId, String fileUrl, String filePath, String fileExt, int status) {

        IMChatMessageDetail detail = new IMChatMessageDetail();
        detail.setCurDate(String.valueOf(System.currentTimeMillis()));
        detail.setDateCreated(String.valueOf(System.currentTimeMillis()));
        detail.setDuration(0);
        detail.setGroupSender("taoliuh@gmail.com");
        detail.setImState(status);
        detail.setMessageContent(messageContent);
        detail.setMessageType(messageType);
        detail.setSessionId(sessionId);
        detail.setUserData("userData");
        detail.setReadStatus(readStatus);
        detail.setMessageId(messageId);
        detail.setListId(listId);
        detail.setFileUrl(fileUrl);
        detail.setFilePath(filePath);
        detail.setFileExt(fileExt);

        return detail;
    }

}
