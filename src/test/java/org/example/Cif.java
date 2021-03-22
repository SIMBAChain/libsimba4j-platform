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

import java.math.BigInteger;

import com.simbachain.SimbaException;
import com.simbachain.simba.CallResponse;
import com.simbachain.simba.JsonData;
import com.simbachain.simba.SimbaClient.UploadFile;
import com.simbachain.simba.platform.ContractClient;
import com.simbachain.simba.platform.ContractService;

/**
 * Class that represents the Cif contract.
 */
public class Cif extends ContractClient {

    public Cif(ContractService simba) {
        super(simba);
    }

    /**
     * Execute the ASN Transaction.
     *
     * @param __SellerSupplier String.
     * @param datetime BigInteger.
     * @param edi856 String.
     * @param files an array of UploadFiles.
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse asn(String __SellerSupplier, BigInteger datetime, String edi856, UploadFile... files) throws SimbaException {
        JsonData data = JsonData.jsonData();
        data = data.and("__SellerSupplier", __SellerSupplier); 
        data = data.and("datetime", datetime); 
        data = data.and("EDI856", edi856); 
        return this.simba.callMethod("ASN", data, files);
    }

    /**
     * Execute the Bank Transaction.
     *
     * @param __Bank String.
     * @param __Broker String.
     * @param name String.
     * @param account String.
     * @param files an array of UploadFiles.
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse newBank(String __Bank, String __Broker, String name, String account, UploadFile... files) throws SimbaException {
        JsonData data = JsonData.jsonData();
        data = data.and("__Bank", __Bank); 
        data = data.and("__Broker", __Broker); 
        data = data.and("name", name); 
        data = data.and("account", account); 
        return this.simba.callMethod("Bank", data, files);
    }

    /**
     * Execute the Buyer Transaction.
     *
     * @param __Buyer String.
     * @param name String.
     * @param account String.
     * @param files an array of UploadFiles.
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse newBuyer(String __Buyer, String name, String account, UploadFile... files) throws SimbaException {
        JsonData data = JsonData.jsonData();
        data = data.and("__Buyer", __Buyer); 
        data = data.and("name", name); 
        data = data.and("account", account); 
        return this.simba.callMethod("Buyer", data, files);
    }

    /**
     * Execute the Broker Transaction.
     *
     * @param __Broker String.
     * @param __Carrier String.
     * @param name String.
     * @param account String.
     * @param files an array of UploadFiles.
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse newBroker(String __Broker, String __Carrier, String name, String account, UploadFile... files) throws SimbaException {
        JsonData data = JsonData.jsonData();
        data = data.and("__Broker", __Broker); 
        data = data.and("__Carrier", __Carrier); 
        data = data.and("name", name); 
        data = data.and("account", account); 
        return this.simba.callMethod("Broker", data, files);
    }

    /**
     * Execute the Carrier Transaction.
     *
     * @param __Carrier String.
     * @param __Shipper String.
     * @param name String.
     * @param account String.
     * @param files an array of UploadFiles.
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse newCarrier(String __Carrier, String __Shipper, String name, String account, UploadFile... files) throws SimbaException {
        JsonData data = JsonData.jsonData();
        data = data.and("__Carrier", __Carrier); 
        data = data.and("__Shipper", __Shipper); 
        data = data.and("name", name); 
        data = data.and("account", account); 
        return this.simba.callMethod("Carrier", data, files);
    }

    /**
     * Execute the Invoice Transaction.
     *
     * @param __SellerSupplier String.
     * @param datetime BigInteger.
     * @param edi810 String.
     * @param files an array of UploadFiles.
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse invoice(String __SellerSupplier, BigInteger datetime, String edi810, UploadFile... files) throws SimbaException {
        JsonData data = JsonData.jsonData();
        data = data.and("__SellerSupplier", __SellerSupplier); 
        data = data.and("datetime", datetime); 
        data = data.and("EDI810", edi810); 
        return this.simba.callMethod("Invoice", data, files);
    }

    /**
     * Execute the Lockbox Transaction.
     *
     * @param __Bank String.
     * @param datetime BigInteger.
     * @param edi823 String.
     * @param files an array of UploadFiles.
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse lockbox(String __Bank, BigInteger datetime, String edi823, UploadFile... files) throws SimbaException {
        JsonData data = JsonData.jsonData();
        data = data.and("__Bank", __Bank); 
        data = data.and("datetime", datetime); 
        data = data.and("EDI823", edi823); 
        return this.simba.callMethod("Lockbox", data, files);
    }

    /**
     * Execute the Payment Transaction.
     *
     * @param __Buyer String.
     * @param __Bank String.
     * @param datetime BigInteger.
     * @param edi820 String.
     * @param files an array of UploadFiles.
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse payment(String __Buyer, String __Bank, BigInteger datetime, String edi820, UploadFile... files) throws SimbaException {
        JsonData data = JsonData.jsonData();
        data = data.and("__Buyer", __Buyer); 
        data = data.and("__Bank", __Bank); 
        data = data.and("datetime", datetime); 
        data = data.and("EDI820", edi820); 
        return this.simba.callMethod("Payment", data, files);
    }

    /**
     * Execute the Product Transaction.
     *
     * @param __Product String.
     * @param __SellerSupplier String.
     * @param description String.
     * @param serialnumber BigInteger.
     * @param category String.
     * @param ioTDeviceTemp String.
     * @param ioTDeviceVibration String.
     * @param files an array of UploadFiles.
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse newProduct(String __Product, String __SellerSupplier, String description, BigInteger serialnumber, String category, String ioTDeviceTemp, String ioTDeviceVibration, UploadFile... files) throws SimbaException {
        JsonData data = JsonData.jsonData();
        data = data.and("__Product", __Product); 
        data = data.and("__SellerSupplier", __SellerSupplier); 
        data = data.and("description", description); 
        data = data.and("serialnumber", serialnumber); 
        data = data.and("category", category); 
        data = data.and("IoTDeviceTemp", ioTDeviceTemp); 
        data = data.and("IoTDeviceVibration", ioTDeviceVibration); 
        return this.simba.callMethod("Product", data, files);
    }

    /**
     * Execute the Shipper Transaction.
     *
     * @param __Shipper String.
     * @param __SellerSupplier String.
     * @param name String.
     * @param account String.
     * @param files an array of UploadFiles.
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse newShipper(String __Shipper, String __SellerSupplier, String name, String account, UploadFile... files) throws SimbaException {
        JsonData data = JsonData.jsonData();
        data = data.and("__Shipper", __Shipper); 
        data = data.and("__SellerSupplier", __SellerSupplier); 
        data = data.and("name", name); 
        data = data.and("account", account); 
        return this.simba.callMethod("Shipper", data, files);
    }

    /**
     * Execute the LoadBoard Transaction.
     *
     * @param __LoadBoard String.
     * @param __Shipper String.
     * @param datetime BigInteger.
     * @param order BigInteger.
     * @param files an array of UploadFiles.
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse newLoadBoard(String __LoadBoard, String __Shipper, BigInteger datetime, BigInteger order, UploadFile... files) throws SimbaException {
        JsonData data = JsonData.jsonData();
        data = data.and("__LoadBoard", __LoadBoard); 
        data = data.and("__Shipper", __Shipper); 
        data = data.and("datetime", datetime); 
        data = data.and("order", order); 
        return this.simba.callMethod("LoadBoard", data, files);
    }

    /**
     * Execute the PurchaseOrder Transaction.
     *
     * @param __Buyer String.
     * @param datetime BigInteger.
     * @param edi850 String.
     * @param files an array of UploadFiles.
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse purchaseOrder(String __Buyer, BigInteger datetime, String edi850, UploadFile... files) throws SimbaException {
        JsonData data = JsonData.jsonData();
        data = data.and("__Buyer", __Buyer); 
        data = data.and("datetime", datetime); 
        data = data.and("EDI850", edi850); 
        return this.simba.callMethod("PurchaseOrder", data, files);
    }

    /**
     * Execute the SellerSupplier Transaction.
     *
     * @param __SellerSupplier String.
     * @param __Buyer String.
     * @param name String.
     * @param account String.
     * @param files an array of UploadFiles.
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse newSellerSupplier(String __SellerSupplier, String __Buyer, String name, String account, UploadFile... files) throws SimbaException {
        JsonData data = JsonData.jsonData();
        data = data.and("__SellerSupplier", __SellerSupplier); 
        data = data.and("__Buyer", __Buyer); 
        data = data.and("name", name); 
        data = data.and("account", account); 
        return this.simba.callMethod("SellerSupplier", data, files);
    }

}
