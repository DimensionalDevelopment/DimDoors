/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Robijnvogel
 */
public class DDStringUtils {

    public static char flipCase(char in) {
        if (Character.isUpperCase(in)) {
            return Character.toLowerCase(in);
        } else {
            return Character.toUpperCase(in);
        }
    }

    public static List<String> getAsStringList(Integer[] integers) {
        List<String> list = new ArrayList();
        for (int integer : integers) {
            list.add("" + integer);
        }
        return list;
    }

    public static List<String> getMatchingStrings(String template, List<String> comparables, boolean caseSensitive) {
        if (template.equals("")) {
            return comparables;
        }
        List<String> results = new ArrayList();
        for (String comparable : comparables) {
            if (isStartOfString(template, comparable, caseSensitive)) {
                results.add(comparable);
            }
        }
        return results;
    }

    public static boolean isStartOfString(String template, String comparable, boolean caseSensitive) {
        if (comparable.length() < template.length()) {
            return false;
        } else if (template.equals("")) {
            return true;
        }

        for (int i = 0; i < template.length(); i++) {
            char tChar = template.charAt(i);
            char cChar = comparable.charAt(i);
            if (tChar == cChar) {
                //this matches, so continue the loop
            } else if (!caseSensitive && flipCase(tChar) == cChar) {
                //this matches, so continue the loop
            } else {
                return false;
            }
        }
        return true;
    }
}
