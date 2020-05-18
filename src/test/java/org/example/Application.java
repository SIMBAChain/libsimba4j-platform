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
     */
    public CallResponse anArr(BigInteger[] first) throws SimbaException {
        JsonData data = JsonData.with("first", first); 
        return this.simba.callMethod("anArr", data);
    }
    /**
     * Execute the two_arrs Transaction.
     */
    public CallResponse twoArrs(BigInteger[] first, BigInteger[] second) throws SimbaException {
        JsonData data = JsonData.jsonData();
        data = data.and("first", first); 
        data = data.and("second", second); 
        return this.simba.callMethod("twoArrs", data);
    }
    /**
     * Execute the address_arr Transaction.
     */
    public CallResponse addressArr(String[] first) throws SimbaException {
        JsonData data = JsonData.with("first", first); 
        return this.simba.callMethod("addressArr", data);
    }
    /**
     * Execute the nested_arr_0 Transaction.
     */
    public CallResponse nestedArr0(BigInteger[][] first) throws SimbaException {
        JsonData data = JsonData.with("first", first); 
        return this.simba.callMethod("nestedArr0", data);
    }
    /**
     * Execute the nested_arr_1 Transaction.
     */
    public CallResponse nestedArr1(BigInteger[][] first) throws SimbaException {
        JsonData data = JsonData.with("first", first); 
        return this.simba.callMethod("nestedArr1", data);
    }
    /**
     * Execute the nested_arr_2 Transaction.
     */
    public CallResponse nestedArr2(BigInteger[][] first) throws SimbaException {
        JsonData data = JsonData.with("first", first); 
        return this.simba.callMethod("nestedArr2", data);
    }
    /**
     * Execute the nested_arr_3 Transaction.
     */
    public CallResponse nestedArr3(BigInteger[][] first) throws SimbaException {
        JsonData data = JsonData.with("first", first); 
        return this.simba.callMethod("nestedArr3", data);
    }
    /**
     * Execute the nested_arr_4 Transaction.
     */
    public CallResponse nestedArr4(BigInteger[][] first, UploadFile... files) throws SimbaException {
        JsonData data = JsonData.with("first", first); 
        return this.simba.callMethod("nestedArr4", data, files);
    }
    /**
     * Execute the struct_test_1 Transaction.
     */
    public CallResponse structTest1(Person[] people, Boolean testBool) throws SimbaException {
        JsonData data = JsonData.jsonData();
        java.util.List<JsonData> list = new java.util.ArrayList<>();
        for (Person element : people) {
            list.add(element.toJsonData());
        }
        data = data.and("people", list);
        data = data.and("test_bool", testBool); 
        return this.simba.callMethod("structTest1", data);
    }
    /**
     * Execute the struct_test_2 Transaction.
     */
    public CallResponse structTest2(Person person, Boolean testBool) throws SimbaException {
        JsonData data = JsonData.jsonData();
        data = data.and("person", person.toJsonData());
        data = data.and("test_bool", testBool); 
        return this.simba.callMethod("structTest2", data);
    }
    /**
     * Execute the struct_test_3 Transaction.
     */
    public CallResponse structTest3(AddressPerson person, UploadFile... files) throws SimbaException {
        JsonData data = JsonData.with("person", person.toJsonData());
        return this.simba.callMethod("structTest3", data, files);
    }
    /**
     * Execute the struct_test_4 Transaction.
     */
    public CallResponse structTest4(AddressPerson[] persons, UploadFile... files) throws SimbaException {
        java.util.List<JsonData> list = new java.util.ArrayList<>();
        for (AddressPerson element : persons) {
            list.add(element.toJsonData());
        }
        JsonData data = JsonData.with("persons", list);
        return this.simba.callMethod("structTest4", data, files);
    }
    /**
     * Execute the struct_test_5 Transaction.
     */
    public CallResponse structTest5(Person person, UploadFile... files) throws SimbaException {
        JsonData data = JsonData.with("person", person.toJsonData());
        return this.simba.callMethod("structTest5", data, files);
    }
    /**
     * Execute the nowt Transaction.
     */
    public CallResponse nowt() throws SimbaException {
        JsonData data = JsonData.jsonData();
        return this.simba.callMethod("nowt", data);
    }

    public static class Addr implements Jsonable {
        private BigInteger number;
        private String town;
        private String street;

        public BigInteger getNumber() {
            return number;
        }
        
        public void setNumber(BigInteger number) {
            this.number = number;
        }
        public String getTown() {
            return town;
        }
        
        public void setTown(String town) {
            this.town = town;
        }
        public String getStreet() {
            return street;
        }
        
        public void setStreet(String street) {
            this.street = street;
        }

        public JsonData toJsonData() {
            JsonData data = JsonData.jsonData();
            data = data.and("number", number); 
            data = data.and("town", town); 
            data = data.and("street", street); 
            return data;
        }
    }

    public static class Person implements Jsonable {
        private String name;
        private Addr addr;
        private BigInteger age;

        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        public Addr getAddr() {
            return addr;
        }
        
        public void setAddr(Addr addr) {
            this.addr = addr;
        }
        public BigInteger getAge() {
            return age;
        }
        
        public void setAge(BigInteger age) {
            this.age = age;
        }

        public JsonData toJsonData() {
            JsonData data = JsonData.jsonData();
            data = data.and("name", name); 
            data = data.and("addr", addr.toJsonData());
            data = data.and("age", age); 
            return data;
        }
    }

    public static class AddressPerson implements Jsonable {
        private Addr[] addrs;
        private String name;
        private BigInteger age;

        public Addr[] getAddrs() {
            return addrs;
        }
        
        public void setAddrs(Addr[] addrs) {
            this.addrs = addrs;
        }
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        public BigInteger getAge() {
            return age;
        }
        
        public void setAge(BigInteger age) {
            this.age = age;
        }

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
