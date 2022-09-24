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

package com.simbachain.simba.gen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.simbachain.SimbaException;
import com.simbachain.simba.ContractMetadata;
import com.simbachain.simba.ContractType;
import com.simbachain.simba.Metadata;
import com.simbachain.simba.Method;
import com.simbachain.simba.Parameter;
import org.apache.commons.text.CaseUtils;

/**
 *
 */
public class Builder {
    
    public static final String BUNDLE_HASH = "_bundleHash";
    
    private final JavaClass javaClass;
    private final String destination;
    private ContractMetadata metadata = null;

    public Builder(String packageName, String destination, Metadata info) {
        try {
            Templates.registerTemplate("contract", "contract.tpl");
            Templates.registerTemplate("method-doc", "method-doc.tpl");
            Templates.registerTemplate("method-header-doc", "method-header-doc.tpl");
            Templates.registerTemplate("method-get", "method-get.tpl");
            Templates.registerTemplate("method-post", "method-post.tpl");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.metadata = info.getContract();
        this.javaClass = new JavaClass();
        this.javaClass.setPackageName(packageName);
        this.javaClass.setClassName(getJavaClassName(metadata.getName()));
        this.destination = destination;
    }
    
    public String build() throws SimbaException {
        boolean abstrakt = metadata.getAbstract();
        if(abstrakt) {
            return null;
        }
        Map<String, Method> methods = metadata.getMethods();
        Method construct = metadata.getConstructor();
        JavaMethod constructor = new JavaMethod(this.javaClass.getClassName(), this.javaClass.getClassName(), false);
        List<Parameter> cParams = construct.getParams();
        List<String> args = new ArrayList<>();
        for (Parameter param : cParams) {
            args.add(param.getName());
            JavaObject p = createComponent(param);
            constructor.addParameter(p);
        }
        constructor.setJsonDataName(getDataName(args));
        javaClass.setConstructor(constructor);
        for (String s : methods.keySet()) {
            Method meth = methods.get(s);
            String viz = meth.getVisibility();
            if (!"public".equals(viz)) {
                continue;
            }
            String methodName = getJavaMethodName(s, meth.getParameterMap());
            boolean access = meth.getAccessor();
            
            List<Parameter> returns = meth.getReturns();
            JavaMethod javaMethod = new JavaMethod(s, methodName, access);
            List<Parameter> params = meth.getParams();
            List<String> paramNames = new ArrayList<>();
            for (Parameter param : params) {
                paramNames.add(param.getName());
                JavaObject p = createComponent(param);
                if(p.isStructType()) {
                    javaClass.addImport("com.simbachain.simba.Jsonable");
                }
                javaMethod.addParameter(p);
            }
            String dataName = getDataName(paramNames);
            javaMethod.setJsonDataName(dataName);
            String headersName = getHeadersName(paramNames);
            javaMethod.setHeadersName(headersName);
            for (Parameter aReturn : returns) {
                javaMethod.addReturn(createComponent(aReturn));
            }
            if(javaMethod.isAccessor()) {
                javaClass.addImport("com.simbachain.simba.CallReturn");
            }
            if(javaMethod.isFiles()) {
                javaClass.addImport("com.simbachain.simba.SimbaClient.UploadFile");
            }
            javaClass.addMethod(javaMethod);
        }
        Map<String, Object> context = new HashMap<>();
        context.put("jc", javaClass);
        String template = "contract";
        String output = Templates.output(context, template);
        File root = new File(destination);
        root.mkdirs();
        String[] parts = javaClass.packageName.split("\\.");
        File curr = root;
        for (String part : parts) {
            File next = new File(curr, part);
            next.mkdir();
            curr = next;
        }
        return writeToFile(output, javaClass.getClassName(), curr);
    }
    
    private String getDataName(List<String> names) {
        String start = "data";
        while (names.contains(start)) {
            start = "_" + start;
        }
        return start;
    }

    private String getHeadersName(List<String> names) {
        String start = "headers";
        while (names.contains(start)) {
            start = "_" + start;
        }
        return start;
    }

    private String writeToFile(String template, String name, File parent) throws SimbaException {
        try {
            File out = new File(parent, name + ".java");
            BufferedWriter writer = new BufferedWriter(new FileWriter(out));
            writer.write(template);
            writer.flush();
            writer.close();
            return out.getAbsolutePath();
        } catch (IOException e) {
            throw new SimbaException(e.getMessage(), SimbaException.SimbaError.PROCESSING_ERROR);
        }
    }
    
    private TypeInfo getType(String type) {
        TypeAndDims tad = getDimensions(type);
        type = tad.type;
        int dims = tad.dims;
        boolean isStruct = false;
        if (type.startsWith("bool")) {
            type = "Boolean";
        } else if (type.startsWith("int")) {
            type = extractNumber(type);
        } else if (type.startsWith("uint")) {
            type = extractNumber(type);
        } else if (type.startsWith("struct ")) {
            type = createStruct(type, metadata.getTypes(), dims);
            isStruct = true;
        } else {
            type = "String";
        }
        StringBuilder sb = new StringBuilder(type);
        for (int i = 0; i < dims; i++) {
            sb.append("[]");
        }
        return new TypeInfo(sb.toString(), dims, isStruct);
    }
    
    private JavaObject createComponent(Parameter component) {
        String javaName = getJavaParamName(component.getName());
        TypeInfo javaType = getType(component.getType());
        return new JavaObject(component.getName(), javaName, javaType.type, javaType.structType, javaType.dims);
    }
    
    private String createStruct(String struct, Map<String, ContractType> types, int dimensions) {
        if (struct.startsWith("struct ")) {
            struct = struct.substring("struct ".length()).trim();
        }
        Struct structure = javaClass.getStruct(struct);
        if(structure != null) {
            return structure.getJavaName();
        }
        boolean save = true;
        ContractType type = types.get(struct);
        if (type == null) {
            save = false;
        }
        String structName = getJavaClassName(struct);
        String[] parts = struct.split("\\.");
        if(parts.length == 2) {
            String contract = getJavaClassName(parts[0]);
            if(!contract.equals(this.javaClass.getClassName())) {
                save = false;
            } else {
                structName = getJavaClassName(parts[1]);    
            }
        }
        if(save) {
            structure = new Struct(struct, structName, dimensions);
            List<Parameter> components = type.getComponents();
            List<String> comps = new ArrayList<>();
            for (Parameter component : components) {
                comps.add(component.getName());
                structure.addComponent(createComponent(component));
            }
            structure.setJsonDataName(getDataName(comps));
            this.javaClass.addStruct(structure);
        }
        return structName;
    }
    
    private TypeAndDims getDimensions(String type) {
        String componentType = type;
        int dims = type.indexOf("[");
        int dimCount = 0;
        if (dims > -1) {
            componentType = type.substring(0, dims);
            String arrs = type.substring(dims);
            int lastIndex = 0;
            while (lastIndex != -1) {
                lastIndex = arrs.indexOf("[", lastIndex);
                if (lastIndex != -1) {
                    dimCount++;
                    lastIndex += 1;
                }
            }
        }
        return new TypeAndDims(componentType, dimCount);
    }
    
    private String extractNumber(String componentType) {
        
        int len = componentType.startsWith("int") ? 3 : 4; 
        int size = componentType.length() > len ? Integer.parseInt(componentType.substring(len)) : 256;
        String returnType = "Integer";
        int integerVal = componentType.startsWith("int") ? 32 : 16;
        int longVal = componentType.startsWith("int") ? 64 : 32;
        if(size > integerVal && size <= longVal) {
            returnType = "Long";
        } else if (size > longVal) {
            this.javaClass.addImport("java.math.BigInteger");
            returnType = "BigInteger";
        }
        return returnType;
    }

    private String getJavaMethodName(String name, Map<String, com.simbachain.simba.Parameter> parameters) {
        if(parameters.get("__" + name) != null) {
            name = toCamelCase(name, true);
            return "new" + name;
        }
        return toCamelCase(name, false);
    }
    
    private String getJavaParamName(String name) {
        try {
            Integer.parseInt(name);
            return "_" + name;
        } catch (NumberFormatException e) {
        }
        return toCamelCase(name, false);
    }

    private String getJavaClassName(String name) {
        String[] parts = name.split("\\.");
        List<String> names = new ArrayList<>();
        for (String part : parts) {
            names.add(toCamelCase(part, true));
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < names.size(); i++) {
            String s = names.get(i);
            sb.append(s);
            if(i < names.size() - 1) {
                sb.append(".");
            }
        }
        return sb.toString();
    }
    
    private String toCamelCase(String value, boolean capFirst) {
        if (!value.contains("_")) {
            if(capFirst) {
                value = value.substring(0, 1).toUpperCase() + value.substring(1);
            } else {
                if(value.equals(value.toUpperCase())) {
                    value = value.toLowerCase();
                } else {
                    value = value.substring(0, 1)
                                 .toLowerCase() + value.substring(1);
                }
            }
            return value;
        } else {
            if(value.startsWith("_")) {
                return value;
            }
            return CaseUtils.toCamelCase(value, capFirst, '_'); 
        }
    }

    public static class JavaObject {
        private final String name;
        private final String javaName;
        private final String type;
        private final boolean structType;
        private final int dimensions;
        private String jsonDataName = "data";

        public JavaObject(String name,
            String javaName,
            String type,
            boolean structType,
            int dimensions) {
            this.name = name;
            this.javaName = javaName;
            this.type = type;
            this.structType = structType;
            this.dimensions = dimensions;
        }
        
        public String getName() {
            return name;
        }

        public String getJavaName() {
            return javaName;
        }

        public String getType() {
            return type;
        }
        
        public boolean isStructType() {
            return structType;
        }

        public int getDimensions() {
            return dimensions;
        }

        public String getJsonDataName() {
            return jsonDataName;
        }

        public void setJsonDataName(String jsonDataName) {
            this.jsonDataName = jsonDataName;
        }

        public String getComponentType() {
            if(dimensions > 0) {
                return type.substring(0, type.indexOf("["));
            }
            return type;
        }

        public String getGetterName() {
            return javaName.substring(0, 1).toUpperCase() + javaName.substring(1);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("JavaObject{");
            sb.append("name='")
              .append(name)
              .append('\'');
            sb.append(", javaName='")
              .append(javaName)
              .append('\'');
            sb.append(", type='")
              .append(type)
              .append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    public static class JavaMethod extends JavaObject {

        private final List<JavaObject> parameters = new ArrayList<>();
        private final List<JavaObject> returns = new ArrayList<>();
        private final boolean accessor;
        private JavaObject bundleHash = null;
        private String headersName = "headers";

        public JavaMethod(String name, String javaName, boolean accessor) {
            super(name, javaName, "method", false, 0);
            this.accessor = accessor;
        }
        
        public boolean isEmpty() {
            return parameters.size() == 0;
        }

        public List<JavaObject> getParameters() {
            return parameters;
        }

        public void addParameter(JavaObject parameter) {
            if (parameter.getName().equals(BUNDLE_HASH)) {
                bundleHash = parameter;
            } else {
                this.parameters.add(parameter);
            }
        }
        
        public boolean isFiles() {
            return bundleHash != null;
        }
        
        public String getParameterList() {
            StringBuilder sb = new StringBuilder();
            int count = 0;
            for (JavaObject param : parameters) {
                sb.append(param.type).append(" ").append(param.javaName);
                if (count < parameters.size() - 1) {
                    sb.append(", ");
                }
                count++;
            }
            if(bundleHash != null) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append("UploadFile... files");
            }
            return sb.toString();
        }

        public String getHeaderParameterList() {
            StringBuilder sb = new StringBuilder();
            int count = 0;
            for (JavaObject param : parameters) {
                sb.append(param.type)
                  .append(" ")
                  .append(param.javaName);
                if (count < parameters.size() - 1) {
                    sb.append(", ");
                }
                count++;
            }
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append("Map<String, String> ").append(getHeadersName());
            if (bundleHash != null) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append("UploadFile... files");
            }
            return sb.toString();
        }

        public List<JavaObject> getReturns() {
            return returns;
        }

        public void addReturn(JavaObject ret) {
            this.returns.add(ret);
        }
        
        public String getReturnValue() {
            if (this.returns.size() == 0) {
                return "CallResponse";
            } else if (this.returns.size() == 1) {
                return "CallReturn<" + this.returns.get(0).type + ">";
            } else {
                return "CallReturn<java.util.List>";
            }
        }

        public String getReturnDoc() {
            if (this.returns.size() == 0) {
                return "CallResponse";
            } else  {
                return "CallReturn";
            }
        }

        public String getReturnType() {
            if (this.returns.size() == 0) {
                return "void";
            } else if (this.returns.size() == 1) {
                return this.returns.get(0).type + ".class";
            } else {
                return "java.util.List.class";
            }
        }
        
        public String getHeadersName() {
            return headersName;
        }

        public void setHeadersName(String headersName) {
            this.headersName = headersName;
        }

        public boolean isAccessor() {
            return accessor;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("JavaMethod{");
            sb.append("name='")
              .append(getName())
              .append('\'');
            sb.append(", javaName='")
              .append(getJavaName())
              .append('\'');
            sb.append(", type='")
              .append(getType())
              .append('\'');
            sb.append(",\nparameters=")
              .append(parameters);
            sb.append(",\nreturns=")
              .append(returns);
            sb.append('}');
            return sb.toString();
        }
    }
    
    public static class Struct extends JavaObject {

        private final Map<String, JavaObject> components = new HashMap<>();
        
        public Struct(String name, String javaName, int dimensions) {
            super(name, javaName, "struct", true, dimensions);
        }

        public Map<String, JavaObject> getComponents() {
            return components;
        }
        
        public void addComponent(JavaObject component) {
            this.components.put(component.getName(), component);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Struct{");
            sb.append("components=")
              .append(components);
            sb.append(", name='")
              .append(getName())
              .append('\'');
            sb.append(", javaName='")
              .append(getJavaName())
              .append('\'');
            sb.append(", type='")
              .append(getType())
              .append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
    
    public static class JavaClass {
        private String packageName;
        private String className;
        private JavaObject constructor;
        private List<JavaMethod> methods = new ArrayList<>();
        private List<Struct> structs = new ArrayList<>();
        private List<String> imports = new ArrayList<>();

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public JavaObject getConstructor() {
            return constructor;
        }

        public void setConstructor(JavaObject constructor) {
            this.constructor = constructor;
        }

        public List<JavaMethod> getMethods() {
            return methods;
        }

        public void addMethod(JavaMethod method) {
            this.methods.add(method);
        }

        public List<Struct> getStructs() {
            return structs;
        }
        
        public Struct getStruct(String name) {
            for (Struct struct : structs) {
                if(struct.getName().equals(name)) {
                    return struct;
                }
            }
            return null;
        }

        public void addStruct(Struct struct) {
            this.structs.add(struct);
        }

        public List<String> getImports() {
            return imports;
        }

        public void addImport(String imp) {
            if(!this.imports.contains(imp)) {
                this.imports.add(imp);
            }
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("JavaClass{");
            sb.append("packageName='")
              .append(packageName)
              .append('\'');
            sb.append(", className='")
              .append(className)
              .append('\'');
            sb.append(",\nmethods=")
              .append(methods);
            sb.append(",\nstructs=")
              .append(structs);
            sb.append('}');
            return sb.toString();
        }
    }
    
    static class TypeAndDims {
        
        public String type;
        public int dims;

        public TypeAndDims(String type, int dims) {
            this.type = type;
            this.dims = dims;
        }
    }

    static class TypeInfo {

        public String type;
        public int dims;
        public boolean structType;

        public TypeInfo(String type, int dims, boolean structType) {
            this.type = type;
            this.dims = dims;
            this.structType = structType;
        }
    }

}
