#parse("method-doc")
    public ${method.returnValue} ${method.javaName}(${method.parameterList}) throws SimbaException {
#if( ${method.parameters.size()} == 1)
#if( $method.parameters.get(0).structType )
#if( $method.parameters.get(0).dimensions > 0 )
        java.util.List<JsonData> list = new java.util.ArrayList<>();
        for (${method.parameters.get(0).componentType} element : ${method.parameters.get(0).javaName}) {
            list.add(element.toJsonData());
        }
        JsonData ${method.jsonDataName} = JsonData.with("${method.parameters.get(0).name}", list);
#else
        JsonData ${method.jsonDataName} = JsonData.with("${method.parameters.get(0).name}", ${method.parameters.get(0).javaName}.toJsonData());
#end
#else
        JsonData ${method.jsonDataName} = JsonData.with("${method.parameters.get(0).name}", ${method.parameters.get(0).javaName}); 
#end
#else
        JsonData ${method.jsonDataName} = JsonData.jsonData();
#foreach( $param in ${method.parameters} )
#if( $param.structType )
#if( $param.dimensions > 0 )
        java.util.List<JsonData> list = new java.util.ArrayList<>();
        for (${param.componentType} element : ${param.javaName}) {
            list.add(element.toJsonData());
        }
        ${method.jsonDataName} = ${method.jsonDataName}.and("${param.name}", list);
#else
        ${method.jsonDataName} = ${method.jsonDataName}.and("${param.name}", ${param.javaName}.toJsonData());
#end
#else
        ${method.jsonDataName} = ${method.jsonDataName}.and("${param.name}", ${param.javaName}); 
#end
#end
#end
#if( ${method.parameters.size()} == 0)
        return this.simba.callGetter("${method.name}", ${method.returnType});
#else
        return this.simba.callGetter("${method.name}", ${method.returnType}, ${method.jsonDataName});
#end
    }