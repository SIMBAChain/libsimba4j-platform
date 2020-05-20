package ${jc.packageName};

import com.simbachain.simba.JsonData;
import com.simbachain.simba.CallResponse;
import com.simbachain.SimbaException;
import com.simbachain.simba.platform.SimbaPlatform;
import com.simbachain.simba.platform.ContractClient;
#foreach( $import in ${jc.imports} )
import ${import};
#end

/**
 * Class that represents the ${jc.className} contract.
 */
public class ${jc.className} extends ContractClient {

    public ${jc.className}(SimbaPlatform simba) {
        super(simba);
    }
#foreach( $method in ${jc.methods} )

#parse("method-doc")
    public ${method.returnValue} ${method.javaName}(${method.parameterList}) throws SimbaException {
#if( ${method.parameters.size()} == 1)
#if( $method.parameters.get(0).structType )
#if( $method.parameters.get(0).dimensions > 0 )
        java.util.List<JsonData> list = new java.util.ArrayList<>();
        for (${method.parameters.get(0).componentType} element : ${method.parameters.get(0).javaName}) {
            list.add(element.toJsonData());
        }
        JsonData data = JsonData.with("${method.parameters.get(0).name}", list);
#else
        JsonData data = JsonData.with("${method.parameters.get(0).name}", ${method.parameters.get(0).javaName}.toJsonData());
#end 
#else
        JsonData data = JsonData.with("${method.parameters.get(0).name}", ${method.parameters.get(0).javaName}); 
#end
#else
        JsonData data = JsonData.jsonData();
#foreach( $param in ${method.parameters} )
#if( $param.structType )
#if( $param.dimensions > 0 )
        java.util.List<JsonData> list = new java.util.ArrayList<>();
        for (${param.componentType} element : ${param.javaName}) {
            list.add(element.toJsonData());
        }
        data = data.and("${param.name}", list);
#else
        data = data.and("${param.name}", ${param.javaName}.toJsonData());
#end 
#else
        data = data.and("${param.name}", ${param.javaName}); 
#end
#end
#end
#if ( ${method.files} )
        return this.simba.callMethod("${method.name}", data, files);
#else
        return this.simba.callMethod("${method.name}", data);
#end
    }
#end

#foreach( $struct in ${jc.structs} )
    /**
     * The ${struct.javaName} class used as inputs to functions.
     */
    public static class ${struct.javaName} implements Jsonable {
#foreach( $comp in ${struct.components} )        
        private ${comp.type} ${comp.javaName};
#end
#foreach( $comp in ${struct.components} )

        /**
         * Getter for ${comp.javaName}.
         * @return ${comp.javaName}
         */         
        public ${comp.type} get${comp.getterName}() {
            return ${comp.javaName};
        }

        /**
         * Setter for ${comp.javaName}
         * @param ${comp.javaName} of type ${comp.type}.
         */        
        public void set${comp.getterName}(${comp.type} ${comp.javaName}) {
            this.${comp.javaName} = ${comp.javaName};
        }
#end

        @Override
        public JsonData toJsonData() {
            JsonData data = JsonData.jsonData();
#foreach( $comp in ${struct.components} )
#if( $comp.structType )
#if( $comp.dimensions > 0 )
            java.util.List<JsonData> list = new java.util.ArrayList<>();
            for (${comp.componentType} element : ${comp.javaName}) {
                list.add(element.toJsonData());
            }
            data = data.and("${comp.name}", list);
#else
            data = data.and("${comp.name}", ${comp.javaName}.toJsonData());
#end 
#else
            data = data.and("${comp.name}", ${comp.javaName}); 
#end
#end        
            return data;
        }
    }

#end
}