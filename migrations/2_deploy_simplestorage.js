var SimpleStorage = artifacts.require("SimpleStorage");

module.exports = function(deployer) {
  // Pass 123 to the contract as the first constructor parameter
  deployer.deploy(SimpleStorage, 123, {privateFor: ["AQNPV7cpCge4p9OAkZOaauKaPDIT0o3QThtj1kXQrRc="]})
};