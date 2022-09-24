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

package com.simbachain.simba.management;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simbachain.simba.Ided;

/**
 *  A Blockchain object.
 *
 */
public class Blockchain implements Ided {
    
    @JsonProperty
    private String id;
    
    @JsonProperty
    private String name;
    
    @JsonProperty("display_name")
    private String displayName;
    
    @JsonProperty("global_id")
    private String globalId;
    
    @JsonProperty("blockchain_type")
    private String blockchainYType;

    @JsonProperty ("poa")
    private Boolean poa;

    @JsonProperty
    private String faucet;

    @JsonProperty ("currency_unit")
    private String currencyUnit;
    
    @JsonProperty("supported_contract_types")
    private List<String> supportedContractTypes;

    @JsonProperty ("consensus_alg")
    private String consensusAlg;

    @JsonProperty ("gas_price_multiplier")
    private String gasPriceMultiplier;

    @JsonProperty ("gas_estimate_multiplier")
    private String gasEstimateMultiplier;

    @JsonProperty ("block_time")
    private Integer blockTime;

    @JsonProperty ("min_confirms_reqd")
    private Integer minConfirmsReqd;

    @JsonProperty ("min_confirms_adjustable")
    private Boolean minConfirmsAdjustable;

    @JsonProperty ("disable_gap_analysis")
    private Boolean disableGapAnalysis;

    @JsonProperty ("disable_new_blocks")
    private Boolean disableNewBlocks;

    @JsonProperty ("perform_initial_import")
    private Boolean performInitialImport;

    @JsonProperty ("dynamic_pricing")
    private Boolean dynamicPricing;

    @JsonProperty ("base_price_multiplier")
    private Double basePriceMultiplier;

    @JsonProperty ("priority_fee_multiplier")
    private Double priorityFeeMultiplier;

    @JsonProperty ("low_fee_percentile")
    private Integer lowFeePercentile;

    @JsonProperty ("high_fee_percentile")
    private Integer highFeePercentile;
    
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getGlobalId() {
        return globalId;
    }

    public void setGlobalId(String globalId) {
        this.globalId = globalId;
    }

    public String getBlockchainYType() {
        return blockchainYType;
    }

    public void setBlockchainYType(String blockchainYType) {
        this.blockchainYType = blockchainYType;
    }

    public Boolean getPoa() {
        return poa;
    }

    public void setPoa(Boolean poa) {
        this.poa = poa;
    }

    public String getFaucet() {
        return faucet;
    }

    public void setFaucet(String faucet) {
        this.faucet = faucet;
    }

    public String getCurrencyUnit() {
        return currencyUnit;
    }

    public void setCurrencyUnit(String currencyUnit) {
        this.currencyUnit = currencyUnit;
    }

    public List<String> getSupportedContractTypes() {
        return supportedContractTypes;
    }

    public void setSupportedContractTypes(List<String> supportedContractTypes) {
        this.supportedContractTypes = supportedContractTypes;
    }

    public String getConsensusAlg() {
        return consensusAlg;
    }

    public void setConsensusAlg(String consensusAlg) {
        this.consensusAlg = consensusAlg;
    }

    public String getGasPriceMultiplier() {
        return gasPriceMultiplier;
    }

    public void setGasPriceMultiplier(String gasPriceMultiplier) {
        this.gasPriceMultiplier = gasPriceMultiplier;
    }

    public String getGasEstimateMultiplier() {
        return gasEstimateMultiplier;
    }

    public void setGasEstimateMultiplier(String gasEstimateMultiplier) {
        this.gasEstimateMultiplier = gasEstimateMultiplier;
    }

    public Integer getBlockTime() {
        return blockTime;
    }

    public Integer getMinConfirmsReqd() {
        return minConfirmsReqd;
    }

    public Boolean getMinConfirmsAdjustable() {
        return minConfirmsAdjustable;
    }

    public Boolean getDisableGapAnalysis() {
        return disableGapAnalysis;
    }

    public Boolean getDisableNewBlocks() {
        return disableNewBlocks;
    }

    public Boolean getPerformInitialImport() {
        return performInitialImport;
    }

    public Boolean getDynamicPricing() {
        return dynamicPricing;
    }

    public Double getBasePriceMultiplier() {
        return basePriceMultiplier;
    }

    public Double getPriorityFeeMultiplier() {
        return priorityFeeMultiplier;
    }

    public Integer getLowFeePercentile() {
        return lowFeePercentile;
    }

    public Integer getHighFeePercentile() {
        return highFeePercentile;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Blockchain{");
        sb.append("id='")
          .append(id)
          .append('\'');
        sb.append(", name='")
          .append(name)
          .append('\'');
        sb.append(", displayName='")
          .append(displayName)
          .append('\'');
        sb.append(", globalId='")
          .append(globalId)
          .append('\'');
        sb.append(", blockchainYType='")
          .append(blockchainYType)
          .append('\'');
        sb.append(", poa=")
          .append(poa);
        sb.append(", faucet='")
          .append(faucet)
          .append('\'');
        sb.append(", currencyUnit='")
          .append(currencyUnit)
          .append('\'');
        sb.append(", supportedContractTypes=")
          .append(supportedContractTypes);
        sb.append(", consensusAlg='")
          .append(consensusAlg)
          .append('\'');
        sb.append(", gasPriceMultiplier='")
          .append(gasPriceMultiplier)
          .append('\'');
        sb.append(", gasEstimateMultiplier='")
          .append(gasEstimateMultiplier)
          .append('\'');
        sb.append(", blockTime=")
          .append(blockTime);
        sb.append(", minConfirmsReqd=")
          .append(minConfirmsReqd);
        sb.append(", minConfirmsAdjustable=")
          .append(minConfirmsAdjustable);
        sb.append(", disableGapAnalysis=")
          .append(disableGapAnalysis);
        sb.append(", disableNewBlocks=")
          .append(disableNewBlocks);
        sb.append(", performInitialImport=")
          .append(performInitialImport);
        sb.append(", dynamicPricing=")
          .append(dynamicPricing);
        sb.append(", basePriceMultiplier=")
          .append(basePriceMultiplier);
        sb.append(", priorityFeeMultiplier=")
          .append(priorityFeeMultiplier);
        sb.append(", lowFeePercentile=")
          .append(lowFeePercentile);
        sb.append(", highFeePercentile=")
          .append(highFeePercentile);
        sb.append('}');
        return sb.toString();
    }
}
