package uj.wmii.pwj.anns.tests;

import uj.wmii.pwj.anns.annotations.TestCase;
import uj.wmii.pwj.anns.annotations.TestConfig;
import uj.wmii.pwj.anns.annotations.TestFixture;


@TestFixture( config = @TestConfig( repeat = 1) )
public class MyBeautifulTestSuite {

    @TestCase
    public void testSoemthing() {
        System.out.println("I'm testing something!");
    }


    @TestCase(params = {"a param"})
    @TestCase(params = {"b param"})
    @TestCase(params = {"c param. Long, long C param."})
    public void testWithParam(String param) {
        System.out.printf("I was invoked with parameter: %s\n", param);
    }

    public void notATest1() {
        System.out.println("I'm not a test.");
    }
    public void notATest2() {
        System.out.println("I'm not a test.");
    }

    @TestCase(expectedException = NullPointerException.class)
    public void imFailue() {
        System.out.println("I AM EVIL.");
        throw new NullPointerException();
    }

    @TestCase( params = {"1","3"} , expected = "4")
    @TestCase( params = {"1","2"} , expectedException = IllegalArgumentException.class )
    @TestCase( params = {"1","3"} , expected = "5")
    int addVluesInteger(int a , int b  )
    {
        return a + b;
    }

    @TestCase( params = {"1","3"} , expected = "13")
    @TestCase( params = {"1","2"} , expected = "12")
    String addVluesString(int a , String b   )
    {
        return String.valueOf(a) + b ;
    }

}
