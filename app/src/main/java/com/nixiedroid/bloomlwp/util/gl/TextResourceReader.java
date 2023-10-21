/*
 * Decompiled with CFR 0.152.
 */
package com.nixiedroid.bloomlwp.util.gl;

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TextResourceReader {
    public static String readTextFileFromResource(Context context, int i) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(i)));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    return sb.toString();
                }
                sb.append(readLine);
                sb.append('\n');
            }
        } catch (Resources.NotFoundException e) {
            throw new RuntimeException("Resource not found: " + i, e);
        } catch (IOException e2) {
            throw new RuntimeException("Could not open resource: " + i, e2);
        }
    }

}

