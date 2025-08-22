/*
 * Copyright (c) 2025 SIMBA Chain Inc.
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

package com.simbachain.wallet;

import com.simbachain.SimbaException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.utils.Numeric;

/**
 * Abstract base class for Wallet implementations.
 * A Wallet is a public private key pair and is unlocked via a password.
 * It can also be created with a mnemonic - either passed in, or generated. This can be used to recreate
 * the wallet elsewhere and can be retrieved using the getMnemonic method.
 */
public abstract class Wallet {

    /**
     * For subclasses.
     */
    protected Wallet() {
    }

    /**
     * Sign a RawTransaction object and include the chain Id.
     *
     * @param rawTransaction a raw transaction.
     * @param chainId The chain ID of the transaction.
     * @return The signed transaction as a hex string.
     * @throws SimbaException if an error occurs.
     * @see org.web3j.crypto.RawTransaction
     */
    public String sign(RawTransaction rawTransaction, long chainId) throws SimbaException {
        try {
            Credentials credentials = getCredentials();
            if (credentials == null) {
                throw new SimbaException("No credentials available",
                    SimbaException.SimbaError.WALLET_NOT_FOUND);
            }
            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId, credentials);
            return Numeric.toHexString(signedMessage);
        } catch (Exception e) {
            throw new SimbaException(SimbaException.SimbaError.SIGN_FAILED, e);
        }
    }

    /**
     * Sign a RawTransaction object.
     *
     * @param rawTransaction a raw transaction.
     * @return The signed transaction as a hex string.
     * @throws SimbaException if an error occurs.
     * @see org.web3j.crypto.RawTransaction
     */
    public String sign(RawTransaction rawTransaction) throws SimbaException {
        try {
            Credentials credentials = getCredentials();
            if (credentials == null) {
                throw new SimbaException("No credentials available",
                    SimbaException.SimbaError.WALLET_NOT_FOUND);
            }
            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            return Numeric.toHexString(signedMessage);
        } catch (Exception e) {
            throw new SimbaException(SimbaException.SimbaError.SIGN_FAILED, e);
        }
    }
    
    /**
     * Get the address of the Wallet.
     *
     * @return The address of the Wallet as a string.
     * @throws SimbaException if an error occurs
     */
    public String getAddress() throws SimbaException {
        Credentials credentials = getCredentials();
        if (credentials == null) {
            throw new SimbaException("No credentials available",
                SimbaException.SimbaError.WALLET_NOT_FOUND);
        }
        return credentials.getAddress();
    }

    /**
     * Returns the private key for the wallet in hex format.
     * Note: this is without the '0x' prefix.
     *
     * @return a hex string representation of the private key.
     */
    public String getPrivateKey() {
        Credentials c = getCredentials();
        return c.getEcKeyPair()
                .getPrivateKey()
                .toString(16);
    }

    /**
     * Method for subclasses to implement. Return the loaded credentials.
     *
     * @return the Credentials from a loaded Wallet.
     * @see org.web3j.crypto.Credentials
     */
    protected abstract Credentials getCredentials();

}
