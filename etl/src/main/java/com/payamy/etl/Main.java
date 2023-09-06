package com.payamy.etl;

import com.payamy.etl.execute.Executor;

public class Main {
    public static void main( String[] args ) throws Exception {
        Class<?> thisClass = Class.forName("com.payamy.etl.Main");
        Executor.run(thisClass.getPackage().toString().split(" ")[1]);
    }
}
