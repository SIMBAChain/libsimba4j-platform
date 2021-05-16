package ${jc.packageName};

import java.util.concurrent.Future;

import com.simbachain.simba.JsonData;
import com.simbachain.simba.CallResponse;
import com.simbachain.SimbaException;
import com.simbachain.simba.platform.DeployedContractInstance;
import com.simbachain.simba.platform.ContractService;
import com.simbachain.simba.platform.ContractClient;
import com.simbachain.simba.platform.InstanceId;
import com.simbachain.simba.platform.NewInstanceResponse;
import com.simbachain.simba.PagedResult;
import com.simbachain.simba.Query;
import com.simbachain.simba.Transaction;
#foreach( $import in ${jc.imports} )
import ${import};
#end

/**
 * Class that represents an instance of the stateful ${jc.className} contract.
 */
public class ${jc.className} extends ContractClient {

    private final InstanceId instanceId;

    public ${jc.className}(ContractService simba, InstanceId instanceId) {
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
        JsonData ${method.jsonDataName} = JsonData.with("${method.parameters.get(0).name}", list);
#else
        JsonData ${method.jsonDataName} = JsonData.with("${method.parameters.get(0).name}", ${method.parameters.get(0).javaName}.toJsonData());
#end 
#else
        JsonData ${method.jsonDataName} = JsonData.with("${method.parameters.get(0).name}", ${method.parameters.get(0).javaName}); 
#end
#else
#if( ${method.accessor} && ${method.parameters.size()} == 0)
#else
        JsonData ${method.jsonDataName} = JsonData.jsonData();
#end
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
#if( $method.accessor )
        return this.simba.callGetter("${method.name}", this.instanceId, ${method.returnType});
#else   
#if ( ${method.files} )
        return this.simba.callInstanceMethod("${method.name}", this.instanceId, ${method.jsonDataName}, files);
#else
        return this.simba.callInstanceMethod("${method.name}", this.instanceId, ${method.jsonDataName});
#end
#end
    }
    
    /**
     * Get transactions for the ${method.name} transaction.
     *
     * @param params Query.Params.
     * @return PagedResult of Transaction objects.
     */
    public PagedResult<Transaction> get${method.getterName}Transactions(Query.Params params) throws SimbaException {
        return this.getTransactions("${method.name}", params);
    }
#end

    /**
     * Create a new instance of this contract. If a key in the json data
     * is named 'assetId', this will be used as the asset Id of the contract.
     * 
#foreach( $param in ${jc.constructor.parameters} )
     * @param ${param.javaName} ${param.type}.
#end
     * @return DeployedContractInstance containing address and assetId if specified. 
     * @throws SimbaException if an error occurs. 
     */
    public DeployedContractInstance new${jc.className}(${jc.constructor.parameterList}) throws SimbaException {
#if( ${jc.constructor.parameters.size()} == 1)
#if( $jc.constructor.parameters.get(0).structType )
#if( $jc.constructor.parameters.get(0).dimensions > 0 )
        java.util.List<JsonData> list = new java.util.ArrayList<>();
        for (${jc.constructor.parameters.get(0).componentType} element : ${jc.constructor.parameters.get(0).javaName}) {
            list.add(element.toJsonData());
        }
        JsonData ${jc.constructor.jsonDataName} = JsonData.with("${jc.constructor.parameters.get(0).name}", list);
#else
        JsonData ${jc.constructor.jsonDataName} = JsonData.with("${jc.constructor.parameters.get(0).name}", ${jc.constructor.parameters.get(0).javaName}.toJsonData());
#end 
#else
        JsonData ${jc.constructor.jsonDataName} = JsonData.with("${jc.constructor.parameters.get(0).name}", ${jc.constructor.parameters.get(0).javaName}); 
#end
#else
#if( ${jc.constructor.parameters.size()} == 0)
        JsonData ${jc.constructor.jsonDataName} = JsonData.jsonData();
#else
        JsonData ${jc.constructor.jsonDataName} = JsonData.jsonData();
#end
#foreach( $param in ${jc.constructor.parameters} )
#if( $param.structType )
#if( $param.dimensions > 0 )
        java.util.List<JsonData> list = new java.util.ArrayList<>();
        for (${param.componentType} element : ${param.javaName}) {
            list.add(element.toJsonData());
        }
        ${jc.constructor.jsonDataName} = ${jc.constructor.jsonDataName}.and("${param.name}", list);
#else
        ${jc.constructor.jsonDataName} = ${jc.constructor.jsonDataName}.and("${param.name}", ${param.javaName}.toJsonData());
#end 
#else
        ${jc.constructor.jsonDataName} = ${jc.constructor.jsonDataName}.and("${param.name}", ${param.javaName}); 
#end
#end
#end
        NewInstanceResponse response = this.simba.callNewInstance(${jc.constructor.jsonDataName});
        Future<DeployedContractInstance> future = simba.waitForContractInstanceDeployment(response.getInstanceId());
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
            JsonData ${struct.jsonDataName} = JsonData.jsonData();
#foreach( $comp in ${struct.components} )
#if( $comp.structType )
#if( $comp.dimensions > 0 )
            java.util.List<JsonData> list = new java.util.ArrayList<>();
            for (${comp.componentType} element : ${comp.javaName}) {
                list.add(element.toJsonData());
            }
            ${struct.jsonDataName} = ${struct.jsonDataName}.and("${comp.name}", list);
#else
            ${struct.jsonDataName} = ${struct.jsonDataName}.and("${comp.name}", ${comp.javaName}.toJsonData());
#end 
#else
            ${struct.jsonDataName} = ${struct.jsonDataName}.and("${comp.name}", ${comp.javaName}); 
#end
#end        
            return ${struct.jsonDataName};
        }
    }

#end
}