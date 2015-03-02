package androidTest.com.zuzhili.activitytest;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;

import com.zuzhili.R;
import com.zuzhili.ui.activity.loginreg.LoginActivity;

/**
 * Created by liutao on 14-6-15.
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    private LoginActivity loginActivity;

    private EditText userEtxt;

    private EditText passwordEtxt;

    private Button loginBtn;

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        loginActivity = getActivity();
        userEtxt = (EditText) loginActivity.findViewById(R.id.txt_login_username);
        passwordEtxt = (EditText) loginActivity.findViewById(R.id.txt_login_password);
        loginBtn = (Button) loginActivity.findViewById(R.id.btn_login_login);

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testPreconditions() {
        assertNotNull("login activity is null", loginActivity);
        assertNotNull("userEtxt is null", userEtxt);
        assertNotNull("passwordEtxt is null", passwordEtxt);
        assertNotNull("loginBtn is null", loginBtn);
    }
}
