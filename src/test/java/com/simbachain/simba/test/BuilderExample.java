/*
 * Copyright (c) 2021 SIMBA Chain Inc.
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

package com.simbachain.simba.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simbachain.simba.Metadata;
import com.simbachain.simba.gen.Builder;

/**
 *
 */
public class BuilderExample {
    
    public static void main(String[] args) throws IOException, URISyntaxException {
        URL resource = BuilderExample.class.getResource("/supplychain.json");
        System.out.println(resource);
        InputStream in = resource.openStream();
        ObjectMapper mapper = new ObjectMapper();
        Metadata info = mapper.readValue(in, Metadata.class);
        File file = Paths.get(resource.toURI()).toFile();
        
        String output = file.getParentFile().getParentFile().getParent();
        
        Builder builder = new Builder("com.supplychain", output, info);
        String s = builder.build();
        System.out.println(s);

        resource = BuilderExample.class.getResource("/test-contract.json");
        in = resource.openStream();
        mapper = new ObjectMapper();
        info = mapper.readValue(in, Metadata.class);
        file = Paths.get(resource.toURI()).toFile();

        output = file.getParentFile().getParentFile().getParent();

        builder = new Builder("com.supplychain", output, info);
        s = builder.build();
        System.out.println(s);
    }

}
