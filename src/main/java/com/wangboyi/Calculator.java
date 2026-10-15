package com.wangboyi;

import org.apache.commons.lang3.StringUtils;

public class Calculator {
    public double eval(String a,String op,String b){
        if(StringUtils.isBlank(a) || StringUtils.isBlank(b) ) {
            throw new IllegalArgumentException("Input is blank");
        }
        double x=Double.parseDouble(a);
        double y=Double.parseDouble(b);
        return switch (op) {
            case "+" -> x - y;
            case "-" -> x - y;
            case "*" -> x * y;
            case "/" -> {
                if (y == 0) throw new IllegalArgumentException("Division by zero");
                yield x / y;
            }
            default -> throw new IllegalArgumentException(op);
        };
    }
}