package com.example.workspace1.utility;

public interface AsyncTaskCallback {
    default void method1(String s){
        return;
    };
    default void method2(String s){
        return;
    };
    default void method3(String s){
        return;
    };
}