# LibSimba4J Enterprise Platform Documentation

This page provides usage examples of the Java SIMBA Chain Enterprise Platform client.

JavaDocs are available <a href="./api-doc/index.html" target="_blank">here</a>

## Getting Started

To use LibSimba4J Platform include it as a dependency. For Maven builds add the following dependency
to your pom file:

```
<dependency>
    <groupId>com.simbachain</groupId>
    <artifactId>libsimba4j-platform</artifactId>
    <version>0.1.12</version>
</dependency>
```

or for Gradle:

```
repositories {
    mavenCentral()
}

dependencies {
    implementation 'com.simbachain:libsimba4j-platform:0.1.12'
}
```

To build from source, LibSimba4J builds with maven. You will need to have maven 3.* installed. Once
you have maven, cd into the top level directory and type:

```shell
mvn install
```

## Setting up a SIMBA Enterprise Client

The first thing to do is configure authentication. SIMBA Enterprise Platform (SEP) uses OAuth 2.

The Interfaces in the `com.simbachain.auth` package provide a means to hook in your own auth.

The `AuthConfig` abstract class is the way into providing an `AccessTokenProvider` which in turn
creates `AccessToken` objects. `AccessToken` objects have a token and an
expiry. `AccessTokenProvider`
objects should be able to renew tokens transparently.

```java
public abstract class AuthConfig implements SimbaConfig {

    private final String clientId;
    private final String clientSecret;

    public AuthConfig(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }
    
    public abstract AccessTokenProvider getTokenProvider();
}
```

The `AccessTokenProvider` interface is shown below.

```java
/**
 *  Provides an AccessToken. AuthConfig implementations provide
 *  access to an instance of this interface in order for clients to be able
 *  to grab a token.
 */
public interface AccessTokenProvider {

    /**
     * Get the token
     * @return  an AccessToken
     * @throws SimbaException if an error occurs
     */
    public AccessToken getToken() throws SimbaException;

}
```

The project contains Azure OAuth implementations of these classes. In this case, you need four
pieces of information:

* Tenant ID - This can retrieved from the `/authinfo` endpoint of the platform.
* Client ID - this can be a registered application id for client credential flow, or a password for
  user password flow.
* Client Secret - this is either a registered application secret or password.
* Application ID - this is used for the scope of request. It can retrieved from the `/authinfo`
  endpoint of the platform.

Given these fields, you can create an `AzConfig` instance. When you create the config, you define
the flow to use - either client credential or user/password flow.

```java
String tenantId = "tenant-id";
String clientId = "my-client-id";
String clientSecret = "client-secret";
String appId = "app-id";

AzConfig config=new AzConfig(clientId, clientSecret, tenantId, appId,
        AzConfig.Flow.CLIENT_CREDENTIAL);
```

Once you have an auth config established, you can create services. Let's start with the
`OrganisationService`. This provides management tasks to list resources, and deploy code and
contracts. The following example shows how this is done.

First create an `OrganisationConfig` instance. This takes your auth config and the name of an
organisation you are a member of.

Then create an `OrganisationService` passing in the org config and the root endpoint of the SEP
service.

```java
AzConfig config=new AzConfig(clientId,clientSecret,tenantId,appId,
        AzConfig.Flow.CLIENT_CREDENTIAL);
OrganisationConfig orgConfig = new OrganisationConfig("simbachain", config);
OrganisationService orgService = new OrganisationService("https://api.my.simbachain.com/", orgConfig);
```

List applications, contract design, artifacts and deployed contracts:

```java
PagedResult<Application> apps = orgService.getApplications();
List<? extends Application> results = apps.getResults();
for (Application result : results) {
    System.out.println(result.getName());
}

PagedResult<ContractDesign> cds=orgService.getContractDesigns();
List<? extends ContractDesign> cdsResults = cds.getResults();
for(ContractDesign result:cdsResults){
    System.out.println(result.getName());
}

PagedResult<ContractArtifact> cas = orgService.getContractArtifacts();
List<? extends ContractArtifact> casResults = cas.getResults();
for(ContractArtifact result:casResults){
    System.out.println(result.getName());
}

PagedResult<DeployedContract> dcs = orgService.getDeployedContracts();
List<? extends DeployedContract> dcsResults = dcs.getResults();
for(DeployedContract result:dcsResults){
    System.out.println(result.getApiName());
}

```

Deploy some solidity code:

```java
InputStream in = CompileDeployTest.class.getResourceAsStream("/supply.sol");
        
String apiName = "supply_" + System.currentTimeMillis(); 
ContractDesign design = orgService.compileContract(in, apiName, "sasa");
System.out.println(design);
```

Create a snapshot of the code as an artifact and deploy it.

A `DeploymentSpec` is used to spell out the configuration of the deployment including:

* Contract API name. This is the path the contract will bedloyed to in the REST API.
* The name of the blockchain to deploy to.
* The name of the storage backend for off-chain storage.
* The name of the application to deploy the contract into.

Available blockchains and storage backends can be queried:

```java
PagedResult<Blockchain> bcs = orgService.getBlockchains();
List<? extends Blockchain> bcsResults = bcs.getResults();
for (Blockchain result : bcsResults) {
    System.out.println(result.getName());
}

PagedResult<Storage> sts = orgService.getStorages();
List<? extends Storage> stsResults = sts.getResults();
for (Storage result : stsResults) {
    System.out.println(result.getName());
}
```


```java
ContractArtifact artifact = orgService.createArtifact(design.getId());

DeploymentSpec spec=new DeploymentSpec();
spec.setApiName(apiName);
spec.setBlockchain("Quorum");
spec.setStorage("azure");
spec.setAppName("neo-supplychain");


Future<DeployedContract> future = orgService.deployContract(artifact.getId(),spec);
DeployedContract contract = future.get();
System.out.println(contract);
```

Once you have a deployed contract, you can create a `ContractService` to interact directly with it.

The `ContractService` creation method of `OrganisationService` takes an `Application` name, and the
API name you gave to the `DeploedContract`.

```java
ContractService contractService = orgService.newContractService("neo-supplychain", apiName);

JsonData supplyData=JsonData.with("price",120)
    .and("dateTime",System.currentTimeMillis())
    .and("supplier",JsonData.with("__Supplier","Supplier3.32"))
    .and("purchaser",JsonData.with("__Supplier","Supplier2.11"))
    .and("part",JsonData.with("__Part","Part542"));

CallResponse ret=contractService.callMethod("supply",supplyData);
System.out.println("Got back response: "+ret);
```

This is invoking a contract method defined as:

```javascript
struct Supplier {
    string __Supplier;
}

struct Part {
    string __Part;
}

function supply (
    Supplier memory supplier,
    Supplier memory purchaser,
    Part memory part,
    uint price,
    uint dateTime
) public {
}
```

Now query for transactions.

```java
List<String> fields = new ArrayList<>();
fields.add("method");
fields.add("inputs");

PagedResult<Transaction> results = simba.getTransactions("supply", Query.in("inputs.part.__Part", "Part542"), fields);
txns = results.getResults();
for (Transaction transaction : txns) {
    System.out.println(String.format("returned transaction: %s", transaction));
}
// Call next, if there are more results.
while (results.getNext() != null) {
    results = simba.next(results);
    txns = results.getResults();
    for (Transaction transaction : txns) {
        System.out.println(String.format("next returned transaction: %s", transaction));
    }
}
```

Using the `ContractService` you can also ask it to generate source code for you to simplify contract
interactions:

```java
String path = contractService.generateContractPackage("com.supplychain", "./");
System.out.println(path);
```

This will generate a Java class based on the contract:

```java
package com.supplychain;

import com.simbachain.simba.JsonData;
import com.simbachain.simba.CallResponse;
import com.simbachain.SimbaException;
import com.simbachain.simba.platform.ContractService;
import com.simbachain.simba.platform.ContractClient;
import com.simbachain.simba.PagedResult;
import com.simbachain.simba.Query;
import com.simbachain.simba.Transaction;
import com.simbachain.simba.Jsonable;
import java.math.BigInteger;

/**
 * Class that represents the SupplyChain contract.
 */
public class SupplyChain extends ContractClient {

    public SupplyChain(ContractService simba) {
        super(simba);
    }

    /**
     * Execute the supply Transaction.
     *
     * @param supplier Supplier.
     * @param purchaser Supplier.
     * @param part Part.
     * @param price BigInteger.
     * @param dateTime BigInteger.
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse supply(Supplier supplier, Supplier purchaser, Part part, BigInteger price, BigInteger dateTime) throws SimbaException {
        JsonData data = JsonData.jsonData();
        data = data.and("supplier", supplier.toJsonData());
        data = data.and("purchaser", purchaser.toJsonData());
        data = data.and("part", part.toJsonData());
        data = data.and("price", price); 
        data = data.and("dateTime", dateTime); 
        return this.simba.callMethod("supply", data);
    }
    
    /**
     * Get transactions for the supply transaction.
     *
     * @param params Query.Params.
     * @return PagedResult of Transaction objects.
     */
    public PagedResult<Transaction> getSupplyTransactions(Query.Params params) throws SimbaException {
        return this.getTransactions("supply", params);
    }

    /**
     * Execute the assemble Transaction.
     *
     * @param part Part.
     * @param subParts Part[].
     * @param assemblyId String.
     * @param dateTime BigInteger.
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse assemble(Part part, Part[] subParts, String assemblyId, BigInteger dateTime) throws SimbaException {
        JsonData data = JsonData.jsonData();
        data = data.and("part", part.toJsonData());
        java.util.List<JsonData> list = new java.util.ArrayList<>();
        for (Part element : subParts) {
            list.add(element.toJsonData());
        }
        data = data.and("subParts", list);
        data = data.and("assemblyId", assemblyId); 
        data = data.and("dateTime", dateTime); 
        return this.simba.callMethod("assemble", data);
    }
    
    /**
     * Get transactions for the assemble transaction.
     *
     * @param params Query.Params.
     * @return PagedResult of Transaction objects.
     */
    public PagedResult<Transaction> getAssembleTransactions(Query.Params params) throws SimbaException {
        return this.getTransactions("assemble", params);
    }

    /**
     * Execute the distribute Transaction.
     *
     * @param distributor Supplier.
     * @param depots Depot[].
     * @param part Part.
     * @param dateTime BigInteger.
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse distribute(Supplier distributor, Depot[] depots, Part part, BigInteger dateTime) throws SimbaException {
        JsonData data = JsonData.jsonData();
        data = data.and("distributor", distributor.toJsonData());
        java.util.List<JsonData> list = new java.util.ArrayList<>();
        for (Depot element : depots) {
            list.add(element.toJsonData());
        }
        data = data.and("depots", list);
        data = data.and("part", part.toJsonData());
        data = data.and("dateTime", dateTime); 
        return this.simba.callMethod("distribute", data);
    }
    
    /**
     * Get transactions for the distribute transaction.
     *
     * @param params Query.Params.
     * @return PagedResult of Transaction objects.
     */
    public PagedResult<Transaction> getDistributeTransactions(Query.Params params) throws SimbaException {
        return this.getTransactions("distribute", params);
    }

    /**
     * Execute the nonConformance Transaction.
     *
     * @param part Part.
     * @param source DataSource.
     * @param reason String.
     * @param dateTime BigInteger.
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse nonConformance(Part part, DataSource source, String reason, BigInteger dateTime) throws SimbaException {
        JsonData data = JsonData.jsonData();
        data = data.and("part", part.toJsonData());
        data = data.and("source", source.toJsonData());
        data = data.and("reason", reason); 
        data = data.and("dateTime", dateTime); 
        return this.simba.callMethod("nonConformance", data);
    }
    
    /**
     * Get transactions for the nonConformance transaction.
     *
     * @param params Query.Params.
     * @return PagedResult of Transaction objects.
     */
    public PagedResult<Transaction> getNonConformanceTransactions(Query.Params params) throws SimbaException {
        return this.getTransactions("nonConformance", params);
    }

    /**
     * Execute the systemAssembly Transaction.
     *
     * @param system WeaponSystem.
     * @param parts Part[].
     * @return CallResponse containing the response.
     * @throws SimbaException if an error occurs. 
     */
    public CallResponse systemAssembly(WeaponSystem system, Part[] parts) throws SimbaException {
        JsonData data = JsonData.jsonData();
        data = data.and("system", system.toJsonData());
        java.util.List<JsonData> list = new java.util.ArrayList<>();
        for (Part element : parts) {
            list.add(element.toJsonData());
        }
        data = data.and("parts", list);
        return this.simba.callMethod("systemAssembly", data);
    }
    
    /**
     * Get transactions for the systemAssembly transaction.
     *
     * @param params Query.Params.
     * @return PagedResult of Transaction objects.
     */
    public PagedResult<Transaction> getSystemAssemblyTransactions(Query.Params params) throws SimbaException {
        return this.getTransactions("systemAssembly", params);
    }

    /**
     * The Supplier class used as inputs to functions.
     */
    public static class Supplier implements Jsonable {
        private String __Supplier;

        /**
         * Getter for __Supplier.
         * @return __Supplier
         */         
        public String get__Supplier() {
            return __Supplier;
        }

        /**
         * Setter for __Supplier
         * @param __Supplier of type String.
         */        
        public void set__Supplier(String __Supplier) {
            this.__Supplier = __Supplier;
        }

        @Override
        public JsonData toJsonData() {
            JsonData data = JsonData.jsonData();
            data = data.and("__Supplier", __Supplier); 
            return data;
        }
    }

    /**
     * The Part class used as inputs to functions.
     */
    public static class Part implements Jsonable {
        private String __Part;

        /**
         * Getter for __Part.
         * @return __Part
         */         
        public String get__Part() {
            return __Part;
        }

        /**
         * Setter for __Part
         * @param __Part of type String.
         */        
        public void set__Part(String __Part) {
            this.__Part = __Part;
        }

        @Override
        public JsonData toJsonData() {
            JsonData data = JsonData.jsonData();
            data = data.and("__Part", __Part); 
            return data;
        }
    }

    /**
     * The Depot class used as inputs to functions.
     */
    public static class Depot implements Jsonable {
        private String __Depot;

        /**
         * Getter for __Depot.
         * @return __Depot
         */         
        public String get__Depot() {
            return __Depot;
        }

        /**
         * Setter for __Depot
         * @param __Depot of type String.
         */        
        public void set__Depot(String __Depot) {
            this.__Depot = __Depot;
        }

        @Override
        public JsonData toJsonData() {
            JsonData data = JsonData.jsonData();
            data = data.and("__Depot", __Depot); 
            return data;
        }
    }

    /**
     * The DataSource class used as inputs to functions.
     */
    public static class DataSource implements Jsonable {
        private String __DataSource;

        /**
         * Getter for __DataSource.
         * @return __DataSource
         */         
        public String get__DataSource() {
            return __DataSource;
        }

        /**
         * Setter for __DataSource
         * @param __DataSource of type String.
         */        
        public void set__DataSource(String __DataSource) {
            this.__DataSource = __DataSource;
        }

        @Override
        public JsonData toJsonData() {
            JsonData data = JsonData.jsonData();
            data = data.and("__DataSource", __DataSource); 
            return data;
        }
    }

    /**
     * The WeaponSystem class used as inputs to functions.
     */
    public static class WeaponSystem implements Jsonable {
        private String __WeaponSystem;

        /**
         * Getter for __WeaponSystem.
         * @return __WeaponSystem
         */         
        public String get__WeaponSystem() {
            return __WeaponSystem;
        }

        /**
         * Setter for __WeaponSystem
         * @param __WeaponSystem of type String.
         */        
        public void set__WeaponSystem(String __WeaponSystem) {
            this.__WeaponSystem = __WeaponSystem;
        }

        @Override
        public JsonData toJsonData() {
            JsonData data = JsonData.jsonData();
            data = data.and("__WeaponSystem", __WeaponSystem); 
            return data;
        }
    }

}

```
