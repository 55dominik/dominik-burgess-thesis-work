package gb.dom55.bme.smarthome.login

import android.content.res.Resources
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.login.fragments.LoginFragment
import org.hamcrest.CoreMatchers.containsString
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class LogonActivityInstrumentationTest {

    val testEmail = "address@example.com"
    val testPass = "LongAcceptedPass17154."
    var noPasswordError = ""
    var noEmailError = ""


    @get:Rule
    var activityActivityTestRule: ActivityTestRule<LogonActivity> =
            ActivityTestRule<LogonActivity>(LogonActivity::class.java)

    @Before
    fun init() {
        activityActivityTestRule.activity
                .supportFragmentManager.beginTransaction()
                .replace(R.id.logonContentFrame, LoginFragment())
                .addToBackStack(null)
                .commit()
        val resources: Resources = activityActivityTestRule.activity.resources
        noPasswordError = resources.getString(R.string.password_at_least__num_char_error)
        noEmailError = resources.getString(R.string.invalid_email_address)
    }

    @Test
    fun writeCredentialsAndNavigateToForgotPassword() {

        // Write email into email field
        onView(withId(R.id.loginEmailEdit)).check(matches((isDisplayed())))
        onView(withId(R.id.loginEmailEdit)).perform(typeText(testEmail), closeSoftKeyboard())
        // Write password into password field
        onView(withId(R.id.loginPasswordEdit)).check(matches((isDisplayed())))
        onView(withId(R.id.loginPasswordEdit)).perform(typeText(testPass), closeSoftKeyboard())

        // Navigate to forgotten password fragment
        onView(withId(R.id.loginForgotPasswordLink)).perform(click())

        // Check email is still in box
        onView(withId(R.id.forgotEmailEdit)).check(matches(isDisplayed()))
        onView(withId(R.id.forgotEmailEdit)).check(matches(withText(containsString(testEmail))))

        // Navigate back to login fragment
        onView(withId(R.id.forgotLinkToLogin)).perform(click())

        // Check email is still in box
        onView(withId(R.id.loginEmailEdit)).check(matches(isDisplayed()))
        onView(withId(R.id.loginEmailEdit)).check(matches(withText(containsString(testEmail))))
        // Check password is still in box
        onView(withId(R.id.loginPasswordEdit)).check(matches(isDisplayed()))
        onView(withId(R.id.loginPasswordEdit)).check(matches(withText(containsString(testPass))))

    }

    @Test
    fun writeCredentialAndNavigateToRegisterFragment() {
        // Write email into email field
        onView(withId(R.id.loginEmailEdit)).check(matches((isDisplayed())))
        onView(withId(R.id.loginEmailEdit)).perform(typeText(testEmail), closeSoftKeyboard())
        // Write password into password field
        onView(withId(R.id.loginPasswordEdit)).check(matches((isDisplayed())))
        onView(withId(R.id.loginPasswordEdit)).perform(typeText(testPass), closeSoftKeyboard())

        // Navigate to register fragment
        onView(withId(R.id.loginSignUpLink)).perform(click())

        // Check email is still in box
        onView(withId(R.id.regEmailEdittext)).check(matches(isDisplayed()))
        onView(withId(R.id.regEmailEdittext)).check(matches(withText(containsString(testEmail))))
        // Check password is still in box
        onView(withId(R.id.regPasswordEdit)).check(matches(isDisplayed()))
        onView(withId(R.id.regPasswordEdit)).check(matches(withText(containsString(testPass))))

        // Navigate back to login fragment
        onView(withId(R.id.regLinkToLogin)).perform(click())

        // Check email is still in box
        onView(withId(R.id.loginEmailEdit)).check(matches(isDisplayed()))
        onView(withId(R.id.loginEmailEdit)).check(matches(withText(containsString(testEmail))))
        // Check password is still in box
        onView(withId(R.id.loginPasswordEdit)).check(matches(isDisplayed()))
        onView(withId(R.id.loginPasswordEdit)).check(matches(withText(containsString(testPass))))
    }

    @Test
    fun writeEmailOnForgotFragmentAndNavigateToLogin() {
        // Navigate to forgotten password fragment
        onView(withId(R.id.loginForgotPasswordLink)).perform(click())

        // Write email into email field
        onView(withId(R.id.forgotEmailEdit)).check(matches((isDisplayed())))
        onView(withId(R.id.forgotEmailEdit)).perform(typeText(testEmail), closeSoftKeyboard())

        // Navigate back to login fragment
        onView(withId(R.id.forgotLinkToLogin)).perform(click())

        // Check email is still in box
        onView(withId(R.id.loginEmailEdit)).check(matches(isDisplayed()))
        onView(withId(R.id.loginEmailEdit)).check(matches(withText(containsString(testEmail))))
    }

    @Test
    fun writeCredentialOnRegisterFragmentAndSwitchToLogin() {
        // Navigate to register fragment
        onView(withId(R.id.loginSignUpLink)).perform(click())

        // Write email into email field
        onView(withId(R.id.regEmailEdittext)).check(matches((isDisplayed())))
        onView(withId(R.id.regEmailEdittext)).perform(typeText(testEmail), closeSoftKeyboard())
        // Write password into password field
        onView(withId(R.id.regPasswordEdit)).check(matches((isDisplayed())))
        onView(withId(R.id.regPasswordEdit)).perform(typeText(testPass), closeSoftKeyboard())

        // Navigate back to login fragment
        onView(withId(R.id.regLinkToLogin)).perform(click())

        // Check email is still in box
        onView(withId(R.id.loginEmailEdit)).check(matches(isDisplayed()))
        onView(withId(R.id.loginEmailEdit)).check(matches(withText(containsString(testEmail))))
        // Check password is still in box
        onView(withId(R.id.loginPasswordEdit)).check(matches(isDisplayed()))
        onView(withId(R.id.loginPasswordEdit)).check(matches(withText(containsString(testPass))))
    }

    @Test
    fun attemptLoginWithoutPassword() {
        // Write email into email field
        onView(withId(R.id.loginEmailEdit)).check(matches((isDisplayed())))
        onView(withId(R.id.loginEmailEdit)).perform(typeText(testEmail), closeSoftKeyboard())

        onView(withId(R.id.loginButton)).perform(click())

        // Check edit text error
        onView(withId(R.id.loginPasswordEdit)).check(matches(hasErrorText(noPasswordError)))
    }

    @Test
    fun attemptLoginWithoutEmail() {
        // Write password into password field
        onView(withId(R.id.loginPasswordEdit)).check(matches((isDisplayed())))
        onView(withId(R.id.loginPasswordEdit)).perform(typeText(testPass), closeSoftKeyboard())

        onView(withId(R.id.loginButton)).perform(click())

        // Check edit text error
        onView(withId(R.id.loginEmailEdit)).check(matches(hasErrorText(noEmailError)))
    }

    @Test
    fun attemptPasswordResetWithoutEmail() {
        // Navigate to forgotten password fragment
        onView(withId(R.id.loginForgotPasswordLink)).perform(click())

        // Click on confirmation button
        onView(withId(R.id.forgotConfirmButton)).perform(click())

        // Check edit text error
        onView(withId(R.id.forgotEmailEdit)).check(matches(hasErrorText(noEmailError)))
    }

    @Test
    fun attemptRegisterWithoutEmail() {
        // Navigate to register fragment by clicking text view
        onView(withId(R.id.loginSignUpLink)).perform(click())
        // Click on confirmation button
        onView(withId(R.id.regConfirmButton)).perform(click())
        // Check edit text error
        onView(withId(R.id.regEmailEdittext)).check(matches(hasErrorText(noEmailError)))
    }

}