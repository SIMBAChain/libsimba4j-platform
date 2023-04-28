/*
 * Copyright (c) 2023 SIMBA Chain Inc.
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

package com.simbachain.simba;

import java.math.BigInteger;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.RawTransaction;
import org.web3j.utils.Numeric;

/**
 *
 */
public class Signing {
    
    protected static Logger log = LoggerFactory.getLogger(Signing.class.getName());

    private Signing() {
    }

    public static BigInteger getBigInt(Object value) {
        if (value == null) {
            return null;
        }
        if(value instanceof BigInteger) {
            return (BigInteger) value;
        }
        if (value instanceof String) {
            String stringVal = ((String)value).trim(); 
            if (stringVal.length() == 0) {
                return BigInteger.ZERO;
            }
            if (stringVal.startsWith("0x")) {
                return Numeric.toBigInt(stringVal);
            }
            return new BigInteger(stringVal);
        } else if (value instanceof Number) {
            long longVal = ((Number)value).longValue();
            return new BigInteger(String.valueOf(longVal));
        }
        return null;
    }

    public static RawTransaction createSigningTransaction(Map<String, Object> raw) {
        Object chainId = raw.get("chainId");
        Object nonce = raw.get("nonce");
        Object gasLimit = raw.getOrDefault("gas", raw.get("gasLimit"));
        BigInteger value = getBigInt(raw.getOrDefault("value", ""));
        Object to = raw.get("to");
        Object data = raw.getOrDefault("data", null);
        Object gasPrice = raw.get("gasPrice");
        Object maxPriorityFeePerGas = raw.get("maxPriorityFeePerGas");
        Object maxFeePerGas = raw.get("maxFeePerGas");
        if (gasPrice != null) {
            if (log.isDebugEnabled()) {
                log.debug("EXIT: Signing.createSigningTransaction: "
                    + " legacy transaction signed");
            }
            return RawTransaction.createTransaction(getBigInt(nonce), getBigInt(gasPrice),
                getBigInt(gasLimit), (String) to, value, (String) data);
        }  else {
            if (log.isDebugEnabled()) {
                log.debug("EXIT: Signing.createSigningTransaction: "
                    + " EIP 1559 transaction signed");
            }
            return RawTransaction.createTransaction(getBigInt(chainId).longValue(), getBigInt(nonce),
                getBigInt(gasLimit), (String) to, value, (String) data, getBigInt(maxPriorityFeePerGas),
                getBigInt(maxFeePerGas));
        }
    }
}
