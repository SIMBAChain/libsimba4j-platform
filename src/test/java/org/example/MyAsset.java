package org.example;

import java.math.BigInteger;
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
 * Class that represents an instance of the stateful MyAsset contract.
 */
public class MyAsset extends ContractClient {

    private final InstanceId instanceId;

    public MyAsset(SimbaPlatform simba, InstanceId instanceId) {
        super(simba);
        this.instanceId = instanceId;
    }

    /**
     * Execute the getBoth Transaction.
     */
    public CallReturn<java.util.List> getBoth() throws SimbaException {
        return this.simba.callGetter("getBoth", this.instanceId, java.util.List.class);
    }
    /**
     * Execute the getAssetId Transaction.
     */
    public CallReturn<String> getAssetId() throws SimbaException {
        return this.simba.callGetter("getAssetId", this.instanceId, String.class);
    }
    /**
     * Execute the getBalance Transaction.
     */
    public CallReturn<BigInteger> getBalance() throws SimbaException {
        return this.simba.callGetter("getBalance", this.instanceId, BigInteger.class);
    }
    /**
     * Execute the setBalance Transaction.
     */
    public CallResponse setBalance(BigInteger balance) throws SimbaException {
        JsonData data = JsonData.with("balance", balance); 
        return this.simba.callInstanceMethod("setBalance", this.instanceId, data);
    }
    /**
     * Create a new instance of this contract. If a key in the json data
     * is named 'assetId', this will be used as the asset Id of the contract.  
     */
    public DeployedContract newInstance(BigInteger balance, String assetId) throws SimbaException {
        JsonData data = JsonData.jsonData();
        data = data.and("balance", balance); 
        data = data.and("assetId", assetId); 
        NewInstanceResponse response = this.simba.callNewInstance(data);
        Future<DeployedContract> future = simba.waitForContractInstanceDeployment(response.getId());
        try {
            return future.get();
        } catch (Exception e) {
            throw new SimbaException("Exception creating instance.", SimbaException.SimbaError.EXECUTION_ERROR, e);
        } 
    }

}
