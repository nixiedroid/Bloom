package com.nixiedroid.bloomlwp.util.gl;

import android.content.res.Resources;
import com.nixiedroid.bloomlwp.App;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TextResourceReader {
    public static String readTextFileFromResource(int resourceId) {
        StringBuilder resourceString = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(App.get().getResources().openRawResource(resourceId)))){
            String line;
            while (true) {
                line = bufferedReader.readLine();
                if (line == null) {
                    return resourceString.toString();
                }
                resourceString.append(line);
                resourceString.append('\n');
            }
        } catch (Resources.NotFoundException e) {
            throw new RuntimeException("Resource not found: " + resourceId, e);
        } catch (IOException e) {
            throw new RuntimeException("Could not open resource: " + resourceId, e);
        }
    }
}

