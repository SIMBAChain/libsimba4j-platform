pragma solidity ^0.8.3;

contract SupplyChain {

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
        uint dateTime
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
}