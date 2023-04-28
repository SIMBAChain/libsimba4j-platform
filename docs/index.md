# LibSimba4J Blocks Platform Documentation

This page provides usage examples of the Java SIMBA Chain Blocks Platform client.

JavaDocs are available <a href="./api-doc/index.html" target="_blank">here</a>

Some notes on the Blocks API (to be extended) are available <a href="./api.md" target="_blank">here</a>

## Getting Started

To use LibSimba4J include it as a dependency. For Maven builds add the following dependency
to your pom file:

```
<dependency>
    <groupId>com.simbachain</groupId>
    <artifactId>libsimba4j-platform</artifactId>
    <version>1.0.0</version>
</dependency>
```

or for Gradle:

```
repositories {
    mavenCentral()
}

dependencies {
    implementation 'com.simbachain:libsimba4j-platform:1.0.0'
}
```

To build from source, LibSimba4J builds with maven. You will need to have maven 3.* installed. Once
you have maven, cd into the top level directory and type:

```shell
mvn install
```

## Configuration File

Configuration is loaded consistent with other SIMBA client tools.
The config file should be in dotenv format and should be called `.simbachain.env` or `simbachain.env`
(i.e. a visible variant) or `.env`.

This can be placed on the classpath, or can be placed anywhere if the
environment variable `SIMBA_HOME` is set. This variable should point to the directory containing the
dotenv file. The `SIMBA_HOME` variable defaults to the user's home directory, e.g. `~/`

The search order for this file is:

* `.simbachain.env` on the classpath
* `simbachain.env` on the classpath
* `.env` on the classpath
* `SIMBA_HOME/.simbachain.env`
* `SIMBA_HOME/simbachain.env`
* `SIMBA_HOME/.env`

The config setup supports in memory env vars taking precedence over values in the file.
All environment variables for Libsimba4J are prefixed with `SIMBA_`.

### Auth Configuration

SIMBA Blocks Platform uses OAuth 2.
Two auth providers are currently supported: Blocks and Keycloak.

*NOTE: The Blocks provider is currently the default and should be used in all cases. The Keycloak
provider **may** be used in the future.*

For Blocks the configuration will look something like
below, i.e., the `SIMBA_AUTH_BASE_URL` and `SIMBA_API_BASE_URL` are the same:

```shell
SIMBA_AUTH_CLIENT_SECRET=...
SIMBA_AUTH_CLIENT_ID=...
SIMBA_AUTH_BASE_URL=https://my.blocks.server
SIMBA_API_BASE_URL=https://my.blocks.server
```

For keycloak, the configuration will look more like, below:

```shell
SIMBA_AUTH_CLIENT_SECRET=...
SIMBA_AUTH_CLIENT_ID=...
SIMBA_AUTH_REALM=simbachain
SIMBA_API_BASE_URL=https://my.blocks.server
SIMBA_AUTH_BASE_URL=https://my.keycloak.server
```
                                                                                                 
Additionally, a `SIMBA_TOKEN_DIR` file location can be set to save tokens to file.
Combined with setting `writeToFile` on the auth config will cause the token to
be written to file.
This is useful if you are making many calls but are closing down the application
and cannot therefore cache in memory.


These values can also be directly set an environment variables if you don't use a dot env file.

Other values can be stored in the env file if required. Any values set directly as environment
variables will override values in the file.

The config is loaded using the `SimbaConfigFile` class. For example:

```java
import com.simbachain.SimbaConfigFile;

SimbaConfigFile config = new SimbaConfigFile();

String clientId = config.getAuthClientId();
String clientSecret = config.getAuthClientSecret();
String authHost = config.getAuthBAseUrl();
String host = config.getApiBaseUrl();

BlocksConfig authConfig = new BlocksConfig(clientId, clientSecret, authHost);
AuthenticatedUser user = new AuthenticatedUser(host, authConfig);

System.out.println("Authenticated user: " + user.whoami());
```

## Logging

Libsimba4J uses SLF4J for logging. All logging is at Debug level.

## Setting up a SIMBA Blocks Client

The first thing to do is configure authentication. See above for configuring auth.

Once you have an auth config established, you can create services. Let's start with the
`OrganisationService`. This provides management tasks to list resources, and deploy code and
contracts. The following example shows how this is done.

First create an `OrganisationConfig` instance. This takes your auth config and the name of an
organisation you are a member of.

Then create an `OrganisationService` passing in the org config and the root endpoint of the SEP
service.

```java
BlocksConfig authConfig = new BlocksConfig(clientId, clientSecret, authHost);
OrganisationConfig orgConfig = new OrganisationConfig("simbachain", config);
OrganisationService orgService = new OrganisationService(host, orgConfig);
```

The `OrganisationService` class provides access to general org-level APIs: 
List applications, contract design, artifacts and deployed contracts:

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
    
PagedResult<Application> apps = orgService.getApplications();
List<? extends Application> results = apps.getResults();
for (Application result : results) {
    System.out.println(result.getName());
}

PagedResult<ContractDesign> cds = orgService.getContractDesigns();
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

You can also iterate over pages, e.g.:

```java
List<? extends ContractDesign> cdsResults = cds.getResults();
for (ContractDesign result : cdsResults) {
    System.out.println(result.getName());
}
while (cds.getNext() != null) {
    cds = orgService.nextContractDesigns(cds);
    cdsResults = cds.getResults();
    for (ContractDesign result : cdsResults) {
        System.out.printf("next returned contract design: %s%n", result);
    }
}
```
                 
## Deploying Contracts

In the simple case, deploying a contract involves supplying solidity code and a contract name.
The name is not required to be unique. You create a design and compile it using a `CompilationSpec`
object. This takes a number of fields:

* `language`: Optional. Defaults to 'solidity'
* `name`: A non-unique name for the design. This does not have to match the name of the contract in the solidity.
* `target_contract`: `<CONTRACT_NAME>`. Optional. This field identifies the contract you want an HTTP API for. In the case
  of a simple contract deployment, this is not required as there is only a single contract. However, if there
  are related contracts in the compilation, this ensures the correct contract is the primary contract to be exposed
  via the Blocks API.
* `binary_targets`: A list containing the `<CONTRACT_NAME>`. Optional. Again, this is optional, but worth including if you want to
  restrict the artifacts that should be deployed at deployment time. It defines
  the contracts for which binary info should be returned. Without this info, the artifact cannot be deployed. Consider
  a contract that depends on external libraries, for which you already know the addresses. In this case you want to
  not have those deployed when deploying the contract as they are already deployed. In this case, the binary targets
  list can be used to restrict deployment to only the main contract.
* `libraries`: Optional dictionary of external libraries that are already deployed and should be linked to the contract.
  The key values of the dictionary are `<LIBRARY_NAME>` key and value of `<ADDRESS>`. If libraries are included, they
  are linked into the binary content of the compiled contract.
* `encodeCode`: boolean. Defaults to true. If true, his tells LibSimba4J to base64 encode the source code
  as the API expects the source to be Base64 encoded. 

Along with the compilation spec, supply a String or an InputStream which is the code itself. 

```java
InputStream in = CompileDeployTest.class.getResourceAsStream("/supply.sol");

CompilationSpec compSpec = new CompilationSpec().withName(apiName);
ContractDesign design = orgService.compileContract(in, compSpec);
System.out.println(design);
```
         
Once a design is created, you can continue to modify it. Before deploying a contract, you should
create a contract artifact. This is a frozen snapshot of a design and is used as the information
to deploy.

To create a snapshot of the code as an artifact pass in the identifier of the contract design:

```java
ContractArtifact artifact = orgService.createArtifact(design.getId());
```

To deploy the artifact, use a `DeploymentSpec`. This is used to spell out the configuration
of the deployment including:

* Contract API name. This is the path the contract will deployed to in the REST API.
* An optional display name.
* The name of the Application to add the deployed contract to.
* The name of the blockchain to deploy to.
* The name of the storage backend for off-chain storage.
* The name of the application to deploy the contract into.
* An optional map of arguments in the case where the contract constructor takes arguments.

```java


DeploymentSpec spec=new DeploymentSpec()
    .withApiName(apiName)
    .withBlockchain("Quorum")
    .withStorage("azure")
    .withAppName("neo-supplychain");


Future<DeployedContract> future = orgService.deployContract(artifact.getId(),spec);
DeployedContract contract = future.get();
System.out.println(contract);
```

Once you have a deployed contract, you can create a `ContractService` to interact directly with it.

The `ContractService` creation method of `OrganisationService` takes an `Application` name, and the
API name you gave to the `DeploedContract`.

When creating the contract service, the auth config is passed to it.

```java
ContractService contractService = orgService.newContractService("supplychain", apiName);
```

## Calling and Querying Contract Methods

You can now call methods on the contract:

```java
JsonData supplyData=JsonData.with("price", 120)
    .and("dateTime", System.currentTimeMillis())
    .and("supplier", JsonData.with("__Supplier", "Supplier3.32"))
    .and("purchaser", JsonData.with("__Supplier", "Supplier2.11"))
    .and("part", JsonData.with("__Part", "Part542"));

CallResponse ret=contractService.callMethod("supply", supplyData);
System.out.println("Got back response: " + ret);
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

For methods that are `getters`, i.e., do not create transactions and do return values, the `callGetter`
function can be used. The json data here is converted to query paramters and the class defines
the expected return type.

```java
CallReturn<String> getterResponse = contractService.callGetter("getSupplier", String.class, JsonData.with("pk", "1234567890"));
System.out.println("getter response: " + getterResponse.getReturnValue());
```
              
For methods that do create transactions, you can query for them and select a subset of fields to return:

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

### Methods with File Uploads

The special `_bundleHash` parameter name is used to determine whether a method has been defined to accept file uploads.
This field should not be populated by the client side. Instead, if files are present in the current
request and the field is defined on the method, then the files are written to off-chain storage
with the JSON manifest being constructed and written out alongside the files.
The manifest contains the content hashes of the files along with metadata and timestamp.
The content hash of the JSON manifest is then set to be the value of the `_bundleHash` field.
This hash value is then what ends up on the chain in the transaction.

To create a bundle, the function must have a `_bundleHash` parameter in the function.

Below is an example of submitting files and getting the bundle hash back. The `UploadFile`class is used
to define the data to upload.

```java
JsonData nonConformanceData = JsonData.with("dateTime", System.currentTimeMillis())
    .and("reason", "Non conformant")
    .and("source", JsonData.with("__DataSource", "DataSource2.11"))
    .and("part", JsonData.with("__Part", "Part542"));

headers = new HashMap<>();

InputStream report = CompileDeployExample.class.getResourceAsStream("/ConformanceReport.pdf");
SimbaClient.UploadFile uploadFile = new SimbaClient.UploadFile("ConformanceReport", "application/pdf", report); 
CallResponse bundleCall = contractService.callMethod("nonConformance", nonConformanceData, headers, uploadFile);
Future<Transaction> ftxn = contractService.waitForTransactionCompletion(bundleCall.getRequestIdentitier());
Transaction txn = ftxn.get();
Map<String, Object> inputs = txn.getInputs();
String bundleHash = (String) inputs.get("_bundleHash");
System.out.println("Bundle Hash: " + bundleHash);
```
   
Using the bundle hash, you can query for the entire bundle which will be in gzip format, or files
inside it, or the JSON manifest describing the bundle. When downloading files, specify the
location to write locally to.

```java
File file = new File(dataDir, "MyReport.pdf");
BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(file)); 
contractService.getBundleFileForTransaction(bundleHash, "ConformanceReport.pdf", bout);

Manifest manifest = contractService.getBundleMetadataForTransaction(bundleHash);
System.out.println("Manifest: " + manifest);
```

## Generated Classes

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
import com.simbachain.simba.ContractService;
import com.simbachain.simba.ContractClient;
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
    public CallResponse supply(Supplier supplier,
        Supplier purchaser,
        Part part,
        BigInteger price,
        BigInteger dateTime) throws SimbaException {
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
    public PagedResult<Transaction> getSupplyTransactions(Query.Params params)
        throws SimbaException {
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
    public CallResponse assemble(Part part, Part[] subParts, String assemblyId, BigInteger dateTime)
        throws SimbaException {
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
    public PagedResult<Transaction> getAssembleTransactions(Query.Params params)
        throws SimbaException {
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
    public CallResponse distribute(Supplier distributor,
        Depot[] depots,
        Part part,
        BigInteger dateTime) throws SimbaException {
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
    public PagedResult<Transaction> getDistributeTransactions(Query.Params params)
        throws SimbaException {
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
    public CallResponse nonConformance(Part part,
        DataSource source,
        String reason,
        BigInteger dateTime) throws SimbaException {
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
    public PagedResult<Transaction> getNonConformanceTransactions(Query.Params params)
        throws SimbaException {
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
    public PagedResult<Transaction> getSystemAssemblyTransactions(Query.Params params)
        throws SimbaException {
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
