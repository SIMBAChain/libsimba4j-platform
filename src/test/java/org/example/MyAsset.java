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
     *
     * @return CallReturn containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallReturn<java.util.List> getBoth() throws SimbaException {
        return this.simba.callGetter("getBoth", this.instanceId, java.util.List.class);
    }

    /**
     * Execute the getAssetId Transaction.
     *
     * @return CallReturn containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallReturn<String> getAssetId() throws SimbaException {
        return this.simba.callGetter("getAssetId", this.instanceId, String.class);
    }

    /**
     * Execute the getBalance Transaction.
     *
     * @return CallReturn containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallReturn<BigInteger> getBalance() throws SimbaException {
        return this.simba.callGetter("getBalance", this.instanceId, BigInteger.class);
    }

    /**
     * Execute the setBalance Transaction.
     *
     * @param balance BigInteger.
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse setBalance(BigInteger balance) throws SimbaException {
        JsonData data = JsonData.with("balance", balance); 
        return this.simba.callInstanceMethod("setBalance", this.instanceId, data);
    }

    /**
     * Create a new instance of this contract. If a key in the json data
     * is named 'assetId', this will be used as the asset Id of the contract.
     * @param balance BigInteger.
     * @param assetId String.
     * @return DeployedContract containing address and assetId if specified. 
     * @throws SimbaException if an error occurs. 
     */
    public DeployedContract newMyAsset(BigInteger balance, String assetId) throws SimbaException {
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
