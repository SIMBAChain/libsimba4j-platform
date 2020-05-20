package org.example;

import java.math.BigInteger;

import com.simbachain.SimbaException;
import com.simbachain.simba.CallResponse;
import com.simbachain.simba.JsonData;
import com.simbachain.simba.Jsonable;
import com.simbachain.simba.Simba.UploadFile;
import com.simbachain.simba.platform.ContractClient;
import com.simbachain.simba.platform.SimbaPlatform;

/**
 * Class that represents the Application contract.
 */
public class Application extends ContractClient {

    public Application(SimbaPlatform simba) {
        super(simba);
    }

    /**
     * Execute the an_arr Transaction.
     *
     * @param first BigInteger[].
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse anArr(BigInteger[] first) throws SimbaException {
        JsonData data = JsonData.with("first", first); 
        return this.simba.callMethod("an_arr", data);
    }

    /**
     * Execute the two_arrs Transaction.
     *
     * @param first BigInteger[].
     * @param second BigInteger[].
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse twoArrs(BigInteger[] first, BigInteger[] second) throws SimbaException {
        JsonData data = JsonData.jsonData();
        data = data.and("first", first); 
        data = data.and("second", second); 
        return this.simba.callMethod("two_arrs", data);
    }

    /**
     * Execute the address_arr Transaction.
     *
     * @param first String[].
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse addressArr(String[] first) throws SimbaException {
        JsonData data = JsonData.with("first", first); 
        return this.simba.callMethod("address_arr", data);
    }

    /**
     * Execute the nested_arr_0 Transaction.
     *
     * @param first BigInteger[][].
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse nestedArr0(BigInteger[][] first) throws SimbaException {
        JsonData data = JsonData.with("first", first); 
        return this.simba.callMethod("nested_arr_0", data);
    }

    /**
     * Execute the nested_arr_1 Transaction.
     *
     * @param first BigInteger[][].
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse nestedArr1(BigInteger[][] first) throws SimbaException {
        JsonData data = JsonData.with("first", first); 
        return this.simba.callMethod("nested_arr_1", data);
    }

    /**
     * Execute the nested_arr_2 Transaction.
     *
     * @param first BigInteger[][].
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse nestedArr2(BigInteger[][] first) throws SimbaException {
        JsonData data = JsonData.with("first", first); 
        return this.simba.callMethod("nested_arr_2", data);
    }

    /**
     * Execute the nested_arr_3 Transaction.
     *
     * @param first BigInteger[][].
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse nestedArr3(BigInteger[][] first) throws SimbaException {
        JsonData data = JsonData.with("first", first); 
        return this.simba.callMethod("nested_arr_3", data);
    }

    /**
     * Execute the nested_arr_4 Transaction.
     *
     * @param first BigInteger[][].
     * @param files an array of UploadFiles.
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse nestedArr4(BigInteger[][] first, UploadFile... files) throws SimbaException {
        JsonData data = JsonData.with("first", first); 
        return this.simba.callMethod("nested_arr_4", data, files);
    }

    /**
     * Execute the struct_test_1 Transaction.
     *
     * @param people Person[].
     * @param testBool Boolean.
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse structTest1(Person[] people, Boolean testBool) throws SimbaException {
        JsonData data = JsonData.jsonData();
        java.util.List<JsonData> list = new java.util.ArrayList<>();
        for (Person element : people) {
            list.add(element.toJsonData());
        }
        data = data.and("people", list);
        data = data.and("test_bool", testBool); 
        return this.simba.callMethod("struct_test_1", data);
    }

    /**
     * Execute the struct_test_2 Transaction.
     *
     * @param person Person.
     * @param testBool Boolean.
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse structTest2(Person person, Boolean testBool) throws SimbaException {
        JsonData data = JsonData.jsonData();
        data = data.and("person", person.toJsonData());
        data = data.and("test_bool", testBool); 
        return this.simba.callMethod("struct_test_2", data);
    }

    /**
     * Execute the struct_test_3 Transaction.
     *
     * @param person AddressPerson.
     * @param files an array of UploadFiles.
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse structTest3(AddressPerson person, UploadFile... files) throws SimbaException {
        JsonData data = JsonData.with("person", person.toJsonData());
        return this.simba.callMethod("struct_test_3", data, files);
    }

    /**
     * Execute the struct_test_4 Transaction.
     *
     * @param persons AddressPerson[].
     * @param files an array of UploadFiles.
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse structTest4(AddressPerson[] persons, UploadFile... files) throws SimbaException {
        java.util.List<JsonData> list = new java.util.ArrayList<>();
        for (AddressPerson element : persons) {
            list.add(element.toJsonData());
        }
        JsonData data = JsonData.with("persons", list);
        return this.simba.callMethod("struct_test_4", data, files);
    }

    /**
     * Execute the struct_test_5 Transaction.
     *
     * @param person Person.
     * @param files an array of UploadFiles.
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse structTest5(Person person, UploadFile... files) throws SimbaException {
        JsonData data = JsonData.with("person", person.toJsonData());
        return this.simba.callMethod("struct_test_5", data, files);
    }

    /**
     * Execute the nowt Transaction.
     *
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse nowt() throws SimbaException {
        JsonData data = JsonData.jsonData();
        return this.simba.callMethod("nowt", data);
    }

    /**
     * The Addr class used as inputs to functions.
     */
    public static class Addr implements Jsonable {
        private BigInteger number;
        private String town;
        private String street;

        /**
         * Getter for number.
         * @return number
         */         
        public BigInteger getNumber() {
            return number;
        }

        /**
         * Setter for number
         * @param number of type BigInteger.
         */        
        public void setNumber(BigInteger number) {
            this.number = number;
        }

        /**
         * Getter for town.
         * @return town
         */         
        public String getTown() {
            return town;
        }

        /**
         * Setter for town
         * @param town of type String.
         */        
        public void setTown(String town) {
            this.town = town;
        }

        /**
         * Getter for street.
         * @return street
         */         
        public String getStreet() {
            return street;
        }

        /**
         * Setter for street
         * @param street of type String.
         */        
        public void setStreet(String street) {
            this.street = street;
        }

        @Override
        public JsonData toJsonData() {
            JsonData data = JsonData.jsonData();
            data = data.and("number", number); 
            data = data.and("town", town); 
            data = data.and("street", street); 
            return data;
        }
    }

    /**
     * The Person class used as inputs to functions.
     */
    public static class Person implements Jsonable {
        private String name;
        private Addr addr;
        private BigInteger age;

        /**
         * Getter for name.
         * @return name
         */         
        public String getName() {
            return name;
        }

        /**
         * Setter for name
         * @param name of type String.
         */        
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Getter for addr.
         * @return addr
         */         
        public Addr getAddr() {
            return addr;
        }

        /**
         * Setter for addr
         * @param addr of type Addr.
         */        
        public void setAddr(Addr addr) {
            this.addr = addr;
        }

        /**
         * Getter for age.
         * @return age
         */         
        public BigInteger getAge() {
            return age;
        }

        /**
         * Setter for age
         * @param age of type BigInteger.
         */        
        public void setAge(BigInteger age) {
            this.age = age;
        }

        @Override
        public JsonData toJsonData() {
            JsonData data = JsonData.jsonData();
            data = data.and("name", name); 
            data = data.and("addr", addr.toJsonData());
            data = data.and("age", age); 
            return data;
        }
    }

    /**
     * The AddressPerson class used as inputs to functions.
     */
    public static class AddressPerson implements Jsonable {
        private Addr[] addrs;
        private String name;
        private BigInteger age;

        /**
         * Getter for addrs.
         * @return addrs
         */         
        public Addr[] getAddrs() {
            return addrs;
        }

        /**
         * Setter for addrs
         * @param addrs of type Addr[].
         */        
        public void setAddrs(Addr[] addrs) {
            this.addrs = addrs;
        }

        /**
         * Getter for name.
         * @return name
         */         
        public String getName() {
            return name;
        }

        /**
         * Setter for name
         * @param name of type String.
         */        
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Getter for age.
         * @return age
         */         
        public BigInteger getAge() {
            return age;
        }

        /**
         * Setter for age
         * @param age of type BigInteger.
         */        
        public void setAge(BigInteger age) {
            this.age = age;
        }

        @Override
        public JsonData toJsonData() {
            JsonData data = JsonData.jsonData();
            java.util.List<JsonData> list = new java.util.ArrayList<>();
            for (Addr element : addrs) {
                list.add(element.toJsonData());
            }
            data = data.and("addrs", list);
            data = data.and("name", name); 
            data = data.and("age", age); 
            return data;
        }
    }

}
