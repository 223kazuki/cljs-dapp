pragma solidity ^0.4.18;

contract SimpleStorage {
    uint public storedData;
    event Update(uint x);

    constructor(uint initVal) public {
        storedData = initVal;
    }

    function set(uint x) public {
        emit Update(x);
        storedData = x;
    }

    function get() view public returns (uint retVal) {
        return storedData;
    }
}