package uj.wmii.pwj.anns;

import uj.wmii.pwj.anns.annotations.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MyTestEngine {

    private final String className;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please specify test class name");
            System.exit(-1);
        }

        AsciiArt ascii = new AsciiArt();
        ascii.printAsciiArt();

        String className = args[0].trim();
        System.out.println("\n------------------------BEGIN---------------------------------");
        System.out.printf("Testing class: %s\n", className);
        MyTestEngine engine = new MyTestEngine(className);
        engine.runTests();
    }

    public MyTestEngine(String className) {
        this.className = className;
    }

    public void runTests() {
        final Object unit = getObject(className);
        List<Method> testMethods = getTestMethods(unit);
        List<TestResult> testMethodsResults = new ArrayList<>();

        System.out.println("---------------------------------------------------------");
        System.out.println("\nTesting those methods : \n");
        for (Method m: testMethods) {
            System.out.println(  " -> " + m.getName());
        }

        for (Method m: testMethods) {
            System.out.println("\n-------------------------"+ m.getName() +"--------------------------------");
            int[] result = launchSingleMethodExpectedValue(m, unit);
            if (result[1] == 0 && result[2] == 0 ) {testMethodsResults.add(TestResult.SUCCESS);}
            else if ( result[2] > 0 ) {testMethodsResults.add(TestResult.ERROR);}
            else if ( result[1] > 0 ) {testMethodsResults.add(TestResult.FAIL);}
        }
        System.out.println("---------------------------------------------------------");
        System.out.printf("\nEngine launched %d following tests :\n\n", testMethods.size());
        long successCount = testMethodsResults.stream().filter(x -> x == TestResult.SUCCESS).count();
        long failCount = testMethodsResults.stream().filter(x -> x == TestResult.FAIL).count();
        long errorCount = testMethodsResults.stream().filter(x -> x == TestResult.ERROR).count();

        for (int i = 0 ; i < testMethods.size() ; i++){
            System.out.println( "[ " + testMethodsResults.get(i).toString() + " ] " + testMethods.get(i).getName().toString()   );
        }
        //System.out.printf("\nTOTAL : %d\n%d passed\n%d failed\n%d had errors\n",  testMethodsResults.size(), successCount , failCount, errorCount );
        System.out.println("\n------------------------END---------------------------------");
    }

    private int[] launchSingleMethodExpectedValue(Method m, Object unit) {

        TestCase[] testCases = m.getAnnotationsByType(TestCase.class);
        int successCount = 0;
        int failCount = 0;
        int errorCount = 0;

        Class<?>[] parameterTypes = m.getParameterTypes();
        Class<?> returnType = m.getReturnType();



        for (TestCase testCase : testCases) {
            try {
                PrintStream originalOut = System.out;
                ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
                System.setOut(new PrintStream(tempOut));

                String[] paramsTestCase = testCase.params();
                String expectedTestCase = testCase.expected();
                Class<? extends Throwable> expectedException = testCase.expectedException();

                try {
                    Object expectedTestCaseConverted = null;
                    Object[] paramsTestCaseConverted = convertParameters(paramsTestCase, parameterTypes);
                    if (!returnType.getName().equals("void") && expectedException == TestCase.None.class) {
                        expectedTestCaseConverted = convertParameters(new String[]{expectedTestCase}, new Class<?>[]{returnType})[0];
                    }

                    m.setAccessible(true);
                    Object result = paramsTestCase.length == 0 ? m.invoke(unit) : m.invoke(unit, paramsTestCaseConverted);
                    System.setOut(originalOut);

                    if (expectedException != TestCase.None.class) {
                        System.out.printf("  Test FAILED: %s -> Expected exception %s but no exception was thrown.\n",
                                m.getName(), expectedException.getName());
                        failCount++;
                    } else if (result != null && !result.equals(expectedTestCaseConverted)) {
                        System.out.printf("  Test FAILED: %s -> Expected %s but got %s.\n",
                                m.getName(), expectedTestCaseConverted, result);
                        failCount++;
                    } else {
                        System.out.printf("  Test PASSED: %s -> Result matches expected value %s.\n",
                                m.getName(), expectedTestCaseConverted);
                        successCount++;
                    }

                } catch (Throwable e) {
                    System.setOut(originalOut);

                    Throwable actualException = (e instanceof InvocationTargetException && e.getCause() != null)
                            ? e.getCause()
                            : e;

                    if (expectedException != TestCase.None.class) {
                        if (!expectedException.isAssignableFrom(actualException.getClass())) {
                            System.out.printf("  Test FAILED: %s -> Expected exception %s but got %s.\n",
                                    m.getName(), expectedException.getName(), actualException.getClass().getName());
                            failCount++;
                        } else {
                            System.out.printf("  Test PASSED: %s -> Expected exception %s was thrown.\n",
                                    m.getName(), expectedException.getName());
                            successCount++;
                        }
                    } else {
                        System.out.printf("  Test ERROR: %s -> Unexpected exception %s was thrown.\n",
                                m.getName(), actualException.getClass().getName());
                        errorCount++;
                    }
                }
            } catch (RuntimeException e) {
                System.out.printf("  Test ERROR: %s -> Runtime exception occurred: %s.\n",
                        m.getName(), e.getMessage());
                errorCount++;
            }

        }
        return new int[]{successCount, failCount, errorCount};
    }

    private static List<Method> getTestMethods(Object unit) {
        Method[] methods = unit.getClass().getDeclaredMethods();
        return Arrays.stream(methods).filter(
                m -> m.getAnnotation(TestCases.class) != null || m.getAnnotation(TestCase.class) != null ).collect(Collectors.toList());
    }

    private static Object getObject(String className) {
        try {
            Class<?> unitClass = Class.forName(className);
            return unitClass.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return new Object();
        }
    }

    public static Object[] convertParameters(String[] params, Class<?>[] parameterTypes) {
        if (params.length != parameterTypes.length) {
            throw new IllegalArgumentException("Number of provided parameters does not match method parameter types.");
        }

        Object[] convertedParams = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            convertedParams[i] = TypeConverter.convert(params[i], parameterTypes[i]);
        }
        return convertedParams;
    }
}

class TypeConverter {
    public static Object convert(String value, Class<?> targetType) {
        if (targetType == String.class) {
            return value;
        } else if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(value);
        } else if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(value);
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (targetType == long.class || targetType == Long.class) {
            return Long.parseLong(value);
        } else if (targetType == float.class || targetType == Float.class) {
            return Float.parseFloat(value);
        } else if (targetType == short.class || targetType == Short.class) {
            return Short.parseShort(value);
        } else if (targetType == byte.class || targetType == Byte.class) {
            return Byte.parseByte(value);
        } else if (targetType == char.class || targetType == Character.class) {
            if (value.length() != 1) {
                throw new IllegalArgumentException("Cannot convert to char: " + value);
            }
            return value.charAt(0);
        } else {
            throw new IllegalArgumentException("Unsupported target type: " + targetType.getName());
        }
    }

}


class AsciiArt{

    String[] art1 = {
            "  __   __        _____         _     _____             _            ",
            " |  \\/  |_   _  |_   _|__  ___| |_  | ____|_ __   __ _(_)_ __   ___ ",
            " | |\\/| | | | |   | |/ _ \\/ __| __| |  _| | '_ \\ / _` | | '_ \\ / _ \\",
            " | |  | | |_| |   | |  __/\\__ \\ |_  | |___| | | | (_| | | | | |  __/",
            " |_|  |_|\\__, |   |_|\\___||___/\\__| |_____|_| |_|\\__, |_|_| |_|\\___|",
            "         |___/                                   |___/               "
    };

    String[] art = art1;

    String reset = "\u001B[0m";
    String[] colors = {
            "\u001B[31m", // Czerwony
            "\u001B[32m", // Zielony
            "\u001B[33m", // Żółty
            "\u001B[34m", // Niebieski
            "\u001B[35m", // Fioletowy
            "\u001B[36m"  // Turkusowy
    };

    void printAsciiArt()
    {
        for (int i = 0; i < art.length; i++) {
            System.out.println(colors[i % colors.length] + art[i] + reset);
        }
    }
}
