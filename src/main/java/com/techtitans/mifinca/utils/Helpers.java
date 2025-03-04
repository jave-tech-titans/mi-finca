package com.techtitans.mifinca.utils;

import java.util.List;

public class Helpers {
    public static boolean validateStrings(List<String> parameters){
        return parameters.stream().allMatch((p)->p != null && !p.isEmpty());
    }
}
