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

package com.simbachain.simba.platform;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.simbachain.simba.Parameter;
import com.simbachain.simba.Transaction;
import com.simbachain.simba.com.Payload;

/**
 *
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public class PlatformTransaction implements Transaction {

    @JsonProperty
    private String id;
    @JsonProperty("request_id")
    private String requestId;
    @JsonProperty
    private String method;
    @JsonProperty
    private Payload payload;
    @JsonProperty
    private Map<String, Object> receipt = new HashMap<>();
    @JsonProperty
    private Map<String, Object> inputs = new HashMap<>();
    @JsonProperty ("transaction_hash")
    private String txnHash;
    @JsonProperty
    private String error;
    @JsonProperty
    private long nonce;
    @JsonProperty
    private long confirmations;
    @JsonProperty
    private long value;
    @JsonProperty("contract_address")
    private String contractAddress;
    @JsonProperty
    private String origin;
    @JsonProperty("transaction_type")
    private String transactionType;
    @JsonProperty("from_address")
    private String sender;
    @JsonProperty
    private State state;
    @JsonProperty("created_on")
    @JsonFormat (shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date created;

    @JsonProperty("raw_transaction")
    private Map<String, String> rawTransaction;

    @JsonIgnore
    private String app;
    @JsonIgnore
    private Map<String, Parameter> methodParameters = new HashMap<>();

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public String getBlock() {
        return receipt.getOrDefault("blockNumber", "").toString();
    }

    @Override
    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    @Override
    public String getBundleHash() {
        return getInputs().getOrDefault("_bundleHash", "").toString();
    }

    @Override
    public Map<String, Object> getInputs() {
        return inputs;
    }

    public void setInputs(Map<String, Object> inputs) {
        this.inputs = inputs;
    }

    @Override
    public Map<String, Parameter> getMethodParameters() {
        return methodParameters;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public Map<String, Object> getReceipt() {
        return receipt;
    }

    public void setReceipt(Map<String, Object> receipt) {
        this.receipt = receipt;
    }

    @Override
    public String getTxnHash() {
        return txnHash;
    }

    public void setTxnHash(String txnHash) {
        this.txnHash = txnHash;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public long getNonce() {
        return nonce;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

    public long getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(long confirmations) {
        this.confirmations = confirmations;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    @Override
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @Override
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setMethodParameters(Map<String, Parameter> methodParameters) {
        this.methodParameters = methodParameters;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public Map<String, String> getRawTransaction() {
        return rawTransaction;
    }

    public void setRawTransaction(Map<String, String> rawTransaction) {
        this.rawTransaction = rawTransaction;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PlatformTransaction{");
        sb.append("id='")
          .append(id)
          .append('\'');
        sb.append(", requestId='")
          .append(requestId)
          .append('\'');
        sb.append(", method='")
          .append(method)
          .append('\'');
        sb.append(", payload=")
          .append(payload);
        sb.append(", receipt=")
          .append(receipt);
        sb.append(", inputs=")
          .append(inputs);
        sb.append(", txnHash='")
          .append(txnHash)
          .append('\'');
        sb.append(", error='")
          .append(error)
          .append('\'');
        sb.append(", nonce=")
          .append(nonce);
        sb.append(", confirmations=")
          .append(confirmations);
        sb.append(", value=")
          .append(value);
        sb.append(", contractAddress='")
          .append(contractAddress)
          .append('\'');
        sb.append(", origin='")
          .append(origin)
          .append('\'');
        sb.append(", transactionType='")
          .append(transactionType)
          .append('\'');
        sb.append(", sender='")
          .append(sender)
          .append('\'');
        sb.append(", state=")
          .append(state);
        sb.append(", created=")
          .append(created);
        sb.append(", rawTransaction=")
          .append(rawTransaction);
        sb.append(", app='")
          .append(app)
          .append('\'');
        sb.append(", methodParameters=")
          .append(methodParameters);
        sb.append('}');
        return sb.toString();
    }
}
