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

package com.simbachain.simba.platform;

import java.io.OutputStream;
import java.util.concurrent.Future;

import com.simbachain.SimbaException;
import com.simbachain.simba.Manifest;
import com.simbachain.simba.PagedResult;
import com.simbachain.simba.Query;
import com.simbachain.simba.Transaction;

/**
 *  Base class for generated clients. Mostly just to wrap general simba functionality.
 */
public class ContractClient {
    
    protected final ContractService simba;

    public ContractClient(ContractService simba) {
        this.simba = simba;
    }

    public Transaction getTransaction(String txnId) throws SimbaException {
        return simba.getTransaction(txnId);
    }

    /**
     * Get the metadata JSON file for a bundle as a string.
     *
     * @param transactionIdOrHash The transaction ID or hash
     * @return a String that is the JSON manifest file.
     * @throws SimbaException if an error occurs
     */
    public Manifest getBundleMetadataForTransaction(String transactionIdOrHash)
        throws SimbaException {
        return simba.getBundleMetadataForTransaction(transactionIdOrHash);
    }

    /**
     * Get the bundle file itself for a given transaction. The output stream is closed once
     * complete.
     *
     * @param transactionIdOrHash The transaction ID or hash
     * @param outputStream        An output stream to write the bundle file to.
     * @return the number of bytes written to the output stream
     * @throws SimbaException if an error occurs
     */
    public long getBundleForTransaction(String transactionIdOrHash,
        OutputStream outputStream) throws SimbaException {
        return simba.getBundleForTransaction(transactionIdOrHash, outputStream);
    }

    /**
     * Get a file from the bundle file for a given transaction with the given index. The output
     * stream is closed once complete.
     *
     * @param transactionIdOrHash The transaction ID or hash
     * @param fileName            the file name
     * @param outputStream        An output stream to write the bundle file to.
     * @return the number of bytes written to the output stream
     * @throws SimbaException if an error occurs
     */
    public long getBundleFileForTransaction(String transactionIdOrHash,
        String fileName,
        OutputStream outputStream) throws SimbaException {
        return simba.getBundleFileForTransaction(transactionIdOrHash, fileName, outputStream);
    }

    /**
     * Get the bundle file itself for a given transaction. The output stream is closed once
     * complete.
     *
     * @param transactionIdOrHash The transaction ID or hash
     * @param outputStream        An output stream to write the bundle file to.
     * @param close               Whether or not to close the output stream on completion.
     * @return the number of bytes written to the output stream
     * @throws SimbaException if an error occurs
     */
    public long getBundleForTransaction(String transactionIdOrHash,
        OutputStream outputStream,
        boolean close) throws SimbaException {
        return simba.getBundleForTransaction(transactionIdOrHash, outputStream, close);
    }

    /**
     * Get a file from the bundle file for a given transaction with the given index. The output
     * stream is closed once complete.
     *
     * @param transactionIdOrHash The transaction ID or hash
     * @param fileName            the file name
     * @param outputStream        An output stream to write the bundle file to.
     * @param close               Whether or not to close the output stream on completion.
     * @return the number of bytes written to the output stream
     * @throws SimbaException if an error occurs
     */
    public long getBundleFileForTransaction(String transactionIdOrHash,
        String fileName,
        OutputStream outputStream,
        boolean close) throws SimbaException {
        return simba.getBundleFileForTransaction(transactionIdOrHash, fileName, outputStream, close);
    }

    /**
     * Query for a transactions and get back a paged result.
     *
     * @return A PagedResult containing zero or more results.
     * @throws SimbaException if an error occurs
     */
    public PagedResult<Transaction> getTransactions() throws SimbaException {
        return simba.getTransactions();
    }

    /**
     * Query for a transactions on a particular method and get back a paged result.
     *
     * @param method the contract method to get transactions for.
     * @param params the query parameters.
     * @return A PagedResult containing zero or more results.
     * @throws SimbaException if an error occurs
     */
    public PagedResult<Transaction> getTransactions(String method, Query.Params params)
        throws SimbaException {
        return simba.getTransactions(method, params);
    }

    /**
     * Get the next paged result if available.
     *
     * @param result the current paged result.
     * @return a new paged result from calling getNext() or null if there is no next URL.
     * @throws SimbaException if an error occurs
     */
    public PagedResult<Transaction> next(PagedResult<Transaction> result)
        throws SimbaException {
        return simba.next(result);
    }

    /**
     * Get the previous paged result if available.
     *
     * @param result the current paged result.
     * @return a new paged result from calling getPrevious() or null if there is no previous URL.
     * @throws SimbaException if an error occurs
     */
    public PagedResult<Transaction> previous(PagedResult<Transaction> result)
        throws SimbaException {
        return simba.previous(result);
    }

    /**
     * Wait for a transaction to reach COMPLETED stage.
     *
     * @param txnId        The transaction or requiest ID.
     * @param interval     Interval to poll the server.
     * @param totalSeconds Total time to wait.
     * @return a Future object that returns a Transaction.
     * @throws SimbaException if something goes wrong.
     */
    public Future<Transaction> waitForTransactionCompletion(String txnId,
        long interval,
        int totalSeconds) throws SimbaException {
        return simba.waitForTransactionCompletion(txnId, interval, totalSeconds);
    }

    /**
     * Wait for a transaction to reach COMPLETED stage. Polling interval is set to 1 second and
     * total wait time is set to 10 seconds.
     *
     * @param txnId The transaction or requiest ID.
     * @return a Future object that returns a Transaction.
     * @throws SimbaException if something goes wrong.
     */
    public Future<Transaction> waitForTransactionCompletion(String txnId) throws SimbaException {
        return simba.waitForTransactionCompletion(txnId);
    }
    
}
