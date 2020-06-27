package ${jc.packageName};

import java.util.concurrent.Future;

import com.simbachain.simba.JsonData;
import com.simbachain.simba.CallResponse;
import com.simbachain.SimbaException;
import com.simbachain.simba.platform.DeployedContract;
import com.simbachain.simba.platform.SimbaPlatform;
import com.simbachain.simba.platform.ContractClient;
import com.simbachain.simba.platform.InstanceId;
import com.simbachain.simba.platform.NewInstanceResponse;
#foreach( $import in ${jc.imports} )
import ${import};
#end

/**
 * Class that represents an instance of the stateful ${jc.className} contract.
 */
public class ${jc.className} extends ContractClient {

    private final InstanceId instanceId;

    public ${jc.className}(SimbaPlatform simba, InstanceId instanceId) {
        super(simba);
        this.instanceId = instanceId;
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
#if( ${method.accessor} && ${method.parameters.size()} == 0)
#else
        JsonData data = JsonData.jsonData();
#end
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
        return this.simba.callInstanceMethod("${method.name}", this.instanceId, data, files);
#else
#if( $method.accessor )
        return this.simba.callGetter("${method.name}", this.instanceId, ${method.returnType});
#else
        return this.simba.callInstanceMethod("${method.name}", this.instanceId, data);
#end
#end
    }
#end

    /**
     * Create a new instance of this contract. If a key in the json data
     * is named 'assetId', this will be used as the asset Id of the contract.
     * 
#foreach( $param in ${jc.constructor.parameters} )
     * @param ${param.javaName} ${param.type}.
#end
     * @return DeployedContract containing address and assetId if specified. 
     * @throws SimbaException if an error occurs. 
     */
    public DeployedContract new${jc.className}(${jc.constructor.parameterList}) throws SimbaException {
#if( ${jc.constructor.parameters.size()} == 1)
#if( $jc.constructor.parameters.get(0).structType )
#if( $jc.constructor.parameters.get(0).dimensions > 0 )
        java.util.List<JsonData> list = new java.util.ArrayList<>();
        for (${jc.constructor.parameters.get(0).componentType} element : ${jc.constructor.parameters.get(0).javaName}) {
            list.add(element.toJsonData());
        }
        JsonData data = JsonData.with("${jc.constructor.parameters.get(0).name}", list);
#else
        JsonData data = JsonData.with("${jc.constructor.parameters.get(0).name}", ${jc.constructor.parameters.get(0).javaName}.toJsonData());
#end 
#else
        JsonData data = JsonData.with("${jc.constructor.parameters.get(0).name}", ${jc.constructor.parameters.get(0).javaName}); 
#end
#else
#if( ${jc.constructor.parameters.size()} == 0)
#else
        JsonData data = JsonData.jsonData();
#end
#foreach( $param in ${jc.constructor.parameters} )
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
        NewInstanceResponse response = this.simba.callNewInstance(data);
        Future<DeployedContract> future = simba.waitForContractInstanceDeployment(response.getInstanceId());
        try {
            return future.get();
        } catch (Exception e) {
            throw new SimbaException("Exception creating instance.", SimbaException.SimbaError.EXECUTION_ERROR, e);
        } 
    }

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
         * Setter for ${comp.javaName}.
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