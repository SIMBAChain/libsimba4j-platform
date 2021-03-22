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

package org.example;

import java.util.concurrent.Future;

import com.simbachain.SimbaException;
import com.simbachain.simba.CallResponse;
import com.simbachain.simba.JsonData;
import com.simbachain.simba.platform.CallReturn;
import com.simbachain.simba.platform.ContractClient;
import com.simbachain.simba.platform.ContractService;
import com.simbachain.simba.platform.DeployedContractInstance;
import com.simbachain.simba.platform.InstanceId;
import com.simbachain.simba.platform.NewInstanceResponse;

/**
 * Class that represents an instance of the stateful Component contract.
 */
public class Component extends ContractClient {

    private final InstanceId instanceId;

    public Component(ContractService simba, InstanceId instanceId) {
        super(simba);
        this.instanceId = instanceId;
    }

    /**
     * Execute the getUid Transaction.
     *
     * @return CallReturn containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallReturn<String> getUid() throws SimbaException {
        return this.simba.callGetter("getUid", this.instanceId, String.class);
    }

    /**
     * Execute the getDesign Transaction.
     *
     * @return CallReturn containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallReturn<String> getDesign() throws SimbaException {
        return this.simba.callGetter("getDesign", this.instanceId, String.class);
    }

    /**
     * Execute the getSerialNum Transaction.
     *
     * @return CallReturn containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallReturn<Integer> getSerialNum() throws SimbaException {
        return this.simba.callGetter("getSerialNum", this.instanceId, Integer.class);
    }

    /**
     * Execute the delivered Transaction.
     *
     * @param datetime Integer.
     * @param location String.
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse delivered(Integer datetime, String location) throws SimbaException {
        JsonData data = JsonData.jsonData();
        data = data.and("datetime", datetime); 
        data = data.and("location", location); 
        return this.simba.callInstanceMethod("delivered", this.instanceId, data);
    }

    /**
     * Execute the manufactured Transaction.
     *
     * @param datetime Integer.
     * @param location String.
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse manufactured(Integer datetime, String location) throws SimbaException {
        JsonData data = JsonData.jsonData();
        data = data.and("datetime", datetime); 
        data = data.and("location", location); 
        return this.simba.callInstanceMethod("manufactured", this.instanceId, data);
    }

    /**
     * Create a new instance of this contract. If a key in the json data
     * is named 'assetId', this will be used as the asset Id of the contract.
     * 
     * @param assetId String.
     * @param design String.
     * @param serialNum Integer.
     * @return DeployedContract containing address and assetId if specified. 
     * @throws SimbaException if an error occurs. 
     */
    public DeployedContractInstance newComponent(String assetId, String design, Integer serialNum) throws SimbaException {
        JsonData data = JsonData.jsonData();
        data = data.and("assetId", assetId); 
        data = data.and("design", design); 
        data = data.and("serialNum", serialNum); 
        NewInstanceResponse response = this.simba.callNewInstance(data);
        Future<DeployedContractInstance> future = simba.waitForContractInstanceDeployment(response.getInstanceId());
        try {
            return future.get();
        } catch (Exception e) {
            throw new SimbaException("Exception creating instance.", SimbaException.SimbaError.EXECUTION_ERROR, e);
        } 
    }

}
