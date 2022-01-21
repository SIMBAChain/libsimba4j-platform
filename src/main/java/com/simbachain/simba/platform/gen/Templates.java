/*
 * Copyright (c) 2022 SIMBA Chain Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.simbachain.simba.platform.gen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import com.simbachain.SimbaException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.implement.IncludeRelativePath;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResource;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;

/**
 *
 */
public class Templates {

    private static StringResourceRepository repository;
    private static VelocityEngine engine;

    static {
        try {
            initVelocityEngine();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void initVelocityEngine() throws Exception {
        Properties p = new Properties();
        p.setProperty("resource.loader", "string");
        p.setProperty("string.resource.loader.class",
            "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
        p.setProperty(RuntimeConstants.EVENTHANDLER_INCLUDE, IncludeRelativePath.class.getName());
        engine = new VelocityEngine();
        engine.init(p);
        repository = StringResourceLoader.getRepository();
    }

    public static String output(Map<String, Object> parameters, String templateType) throws SimbaException {
        VelocityContext context = new VelocityContext();
        for (String s : parameters.keySet()) {
            context.put(s, parameters.get(s));
        }
        try {
            StringWriter stringWriter = new StringWriter();
            BufferedWriter writer = new BufferedWriter(stringWriter);
            org.apache.velocity.Template template = engine.getTemplate(templateType);
            template.merge(context, writer);
            writer.close();
            return stringWriter.toString();
        } catch (Exception ex) {
            throw new SimbaException(SimbaException.SimbaError.PROCESSING_ERROR, ex);
        }
    }

    protected static String readTemplate(String path) throws IOException {
        InputStream in = getInputStream(path);
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line)
                         .append("\n");
        }
        return stringBuilder.toString();
    }

    protected static InputStream getInputStream(String path) throws SimbaException {
        try {
            File f = new File(path.replace("/", File.separator));
            if (f.exists() && f.length() > 0) {
                return new FileInputStream(f);
            } else {
                InputStream in = Thread.currentThread()
                                       .getContextClassLoader()
                                       .getResourceAsStream(path);
                if (in == null) {
                    if (path.startsWith("/")) {
                        path = path.substring(1);
                    } else {
                        path = "/" + path;
                    }
                    in = Thread.currentThread()
                               .getContextClassLoader()
                               .getResourceAsStream(path);
                }
                return in;
            }
        } catch (Exception e) {
            throw new SimbaException(SimbaException.SimbaError.PROCESSING_ERROR, e);
        }
    }

    public static void registerTemplate(String name, String path) throws IOException {
        StringResource existing = repository.getStringResource(name);
        if (existing == null) {
            repository.putStringResource(name, readTemplate(path));
        }
    }
}

