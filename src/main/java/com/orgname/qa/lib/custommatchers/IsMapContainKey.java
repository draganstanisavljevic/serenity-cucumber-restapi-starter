package com.orgname.qa.lib.custommatchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Map;

//this is template demo to create custom matchers
//you can call this matcher on this way assertThat(map, containKey("dva"));
public class IsMapContainKey extends TypeSafeMatcher<Map> {

    private final String key;

    public IsMapContainKey(final String key) {
        this.key = key;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("not contain key " + key);
    }

    // matcher method you can call on this matcher class
    public static IsMapContainKey containKey(final String key) {
        return new IsMapContainKey(key);
    }

    @Override
    protected boolean matchesSafely(Map item) {

        Object object = item.get(key);
        if(object == null){
            return false;
        }else{
            return true;
        }
    }
}
