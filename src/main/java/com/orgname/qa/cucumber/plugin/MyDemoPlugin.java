package com.orgname.qa.cucumber.plugin;

import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.*;

//For using add to test runner plugin = { "com.orgname.qa.cucumber.plugin.MyDemoPlugin" }
public class MyDemoPlugin implements EventListener {

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestCaseFinished.class, this::EHTestCaseEnded);
    }

    public void EHTestCaseEnded(TestCaseFinished event){
        TestCase testcase = event.getTestCase();
        System.out.println("Scenario Name :: " + testcase.getName());

        Result result =event.getResult();
        Status status = result.getStatus();
        switch(status) {
            case PASSED:
                System.out.println("My Test Case Passed!");
                System.out.println();
                break;
            case FAILED:
                System.out.println("My Test Case Failed!");
                System.out.println();
                break;
        }
    }
}
