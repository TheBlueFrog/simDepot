package com.mike.util;

/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Util
{
    static public String flatten(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        for (String s : map.keySet())
            sb.append("(").append(s).append(", ").append(map.get(s)).append("), ");
        if (sb.length() > 2)
            sb.delete(sb.length() - 2, sb.length());
        return sb.toString().replaceAll("[<]", "{").replaceAll("[>]", "}");
    }

    static public String flatten(List<String> v) {
        StringBuilder sb = new StringBuilder();
        for (String s : v)
            sb.append(s).append(", ");
        if (sb.length() > 2)
            sb.delete(sb.length() - 2, sb.length());
        return sb.toString().replaceAll("[<]", "{").replaceAll("[>]", "}");
    }

}
