pragma solidity ^0.8.3;

contract SupplyChain {

    event SupplyEvent(
        address indexed sender,
        string supplier,
        uint indexed dateTime
    );

    struct Supplier {
        string __Supplier;
    }

    struct Depot {
        string __Depot;
    }

    struct Part {
        string __Part;
    }

    struct DataSource {
        string __DataSource;
    }

    struct WeaponSystem {
        string __WeaponSystem;
    }

    constructor() public {
    }

    function supply (
        Supplier memory supplier,
        Supplier memory purchaser,
        Part memory part,
        uint price,
        uint dateTime
    ) public {
        emit SupplyEvent(msg.sender, supplier.__Supplier, dateTime);
    }

    function distribute (
        Supplier memory distributor,
        Depot[] memory depots,
        Part memory part,
        uint dateTime
    ) public {
    }

    function nonConformance (
        Part memory part,
        DataSource memory source,
        string memory reason,
        uint256 dateTime,
        string memory _bundleHash
    ) public {
    }

    function assemble (
        Part memory part,
        Part[] memory subParts,
        string memory assemblyId,
        uint dateTime
    ) public {
    }

    function systemAssembly (
        WeaponSystem memory system,
        Part[] memory parts
    ) public {
    }
    
    function getSupplier (
        string memory pk
    ) public view returns (string memory){
        return pk;
    }
}
