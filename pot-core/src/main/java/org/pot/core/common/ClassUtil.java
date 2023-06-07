package org.pot.core.common;

public class ClassUtil {
    public static String getAbbreviatedName(String stacktrace, int depth) {
        String[] packages = stacktrace.split("\\.");
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (int i = packages.length - 1; i >= 0; i--) {
            if (count == depth) {
                break;
            }
            if (packages[i].length() > 0) {
                sb.insert(0, packages[i].charAt(0));
                count++;
            }
        }
        return sb.toString();
    }

    public static void main(String args[]) {
        System.out.print(getAbbreviatedName("com.eyu.kylin.commons.utils.CLassUtil", 3));
    }
}