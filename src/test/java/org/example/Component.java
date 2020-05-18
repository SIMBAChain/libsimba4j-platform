package org.example;

import java.util.concurrent.Future;

import com.simbachain.SimbaException;
import com.simbachain.simba.CallResponse;
import com.simbachain.simba.JsonData;
import com.simbachain.simba.platform.CallReturn;
import com.simbachain.simba.platform.ContractClient;
import com.simbachain.simba.platform.DeployedContract;
import com.simbachain.simba.platform.InstanceId;
import com.simbachain.simba.platform.NewInstanceResponse;
import com.simbachain.simba.platform.SimbaPlatform;

/**
 * Class that represents an instance of the stateful Component contract.
 */
public class Component extends ContractClient {

    private final InstanceId instanceId;

    public Component(SimbaPlatform simba, InstanceId instanceId) {
        super(simba);
        this.instanceId = instanceId;
    }

    /**
     * Execute the getUid Transaction.
     */
    public CallReturn<String> getUid() throws SimbaException {
        return this.simba.callGetter("getUid", this.instanceId, String.class);
    }
    /**
     * Execute the getDesign Transaction.
     */
    public CallReturn<String> getDesign() throws SimbaException {
        return this.simba.callGetter("getDesign", this.instanceId, String.class);
    }
    /**
     * Execute the getSerialNum Transaction.
     */
    public CallReturn<Integer> getSerialNum() throws SimbaException {
        return this.simba.callGetter("getSerialNum", this.instanceId, Integer.class);
    }
    /**
     * Execute the delivered Transaction.
     */
    public CallResponse delivered(Integer datetime, String location) throws SimbaException {
        JsonData data = JsonData.jsonData();
        data = data.and("datetime", datetime); 
        data = data.and("location", location); 
        return this.simba.callInstanceMethod("delivered", this.instanceId, data);
    }
    /**
     * Execute the manufactured Transaction.
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
     */
    public DeployedContract newInstance(String assetId, String design, Integer serialNum) throws SimbaException {
        JsonData data = JsonData.jsonData();
        data = data.and("assetId", assetId); 
        data = data.and("design", design); 
        data = data.and("serialNum", serialNum); 
        NewInstanceResponse response = this.simba.callNewInstance(data);
        Future<DeployedContract> future = simba.waitForContractInstanceDeployment(response.getId());
        try {
            return future.get();
        } catch (Exception e) {
            throw new SimbaException("Exception creating instance.", SimbaException.SimbaError.EXECUTION_ERROR, e);
        } 
    }

}
