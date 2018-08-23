pragma solidity ^0.4.23;

import "openzeppelin-solidity/contracts/math/SafeMath.sol";

/** 
 * @title SimpleStorage
 * @author Kazuki Tsutusmi
 */
contract SimpleStorage {
  // Libraries
  using SafeMath for uint;

  // Events
  event Updated(uint _data);

  // Variables
  uint public storedData;

  /**
   * @param _initData initial data.
   */
  constructor(uint _initData) public {
    storedData = _initData;
  }

  /**
   * @param _data data to set.
   */
  function set(uint _data) public {
    storedData = _data;
    emit Updated(_data);
  }

  /**
   * @return _data : stored data.
   */
  function get() view external returns (uint _data) {
    _data = storedData;
  }
}