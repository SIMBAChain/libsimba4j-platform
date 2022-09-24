package ${jc.packageName};

import java.util.Map;
import com.simbachain.simba.JsonData;
import com.simbachain.simba.CallResponse;
import com.simbachain.SimbaException;
import com.simbachain.simba.ContractService;
import com.simbachain.simba.ContractClient;
import com.simbachain.simba.PagedResult;
import com.simbachain.simba.Query;
import com.simbachain.simba.Transaction;
#foreach( $import in ${jc.imports} )
import ${import};
#end

/**
 * Class that represents the ${jc.className} contract.
 */
public class ${jc.className} extends ContractClient {

    public ${jc.className}(ContractService simba) {
        super(simba);
    }
#foreach( $method in ${jc.methods} )
#if( $method.accessor )
#parse("method-get")
#else
#parse("method-post")
#end
    
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