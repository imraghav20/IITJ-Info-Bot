package com.example.iitjinfobot;

import androidx.test.core.app.ActivityScenario;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class LoginActivityTest {

    @Test
    public void isActicivityInView(){
        ActivityScenario activityScenario = ActivityScenario.launch(LoginActivity.class);

        onView(withId(R.id.login_activity)).check(matches(isDisplayed()));
    }

    @Test
    public void isLogoInView(){
        ActivityScenario activityScenario = ActivityScenario.launch(LoginActivity.class);

        onView(withId(R.id.launch_logo)).check(matches(isDisplayed()));
    }

    @Test
    public void isTextInView(){
        ActivityScenario activityScenario = ActivityScenario.launch(LoginActivity.class);

        onView(withId(R.id.sign_in_text)).check(matches(withText("Sign In to Continue")));
    }

    @Test
    public void isButtonInView(){
        ActivityScenario activityScenario = ActivityScenario.launch(LoginActivity.class);

        onView(withId(R.id.sign_in_button)).check(matches(isDisplayed()));
    }
}