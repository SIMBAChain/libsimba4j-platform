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

package com.simbachain.simba;

import java.math.BigInteger;
import java.util.Map;

import org.web3j.crypto.RawTransaction;
import org.web3j.utils.Numeric;

/**
 *
 */
public class Signing {

    private Signing() {
    }

    public static BigInteger getBitInt(String value) {
        if (value == null
            || value.trim()
                    .length() == 0) {
            return BigInteger.ZERO;
        }
        value = value.trim();
        if (value.startsWith("0x")) {
            return Numeric.toBigInt(value);
        }
        return new BigInteger(value);
    }

    public static RawTransaction createSigningTransaction(Map<String, String> raw) {

        String nonce = raw.get("nonce");
        String gasPrice = raw.get("gasPrice");
        String gasLimit = raw.getOrDefault("gas", raw.get("gasLimit"));
        BigInteger value = getBitInt(raw.getOrDefault("value", ""));
        String to = raw.get("to");
        String data = raw.getOrDefault("data", null);

        return RawTransaction.createTransaction(getBitInt(nonce), getBitInt(gasPrice),
            getBitInt(gasLimit), to, value, data);
    }
}
