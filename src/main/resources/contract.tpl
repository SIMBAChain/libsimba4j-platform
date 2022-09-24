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