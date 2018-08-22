var HDWalletProvider = require("truffle-hdwallet-provider");
var path = require('path');
var mnemonic = process.env.MNEMONIC;
var accessToken = process.env.INFURA_ACCESS_TOKEN;

module.exports = {
  contracts_build_directory: path.resolve(__dirname, 'resources/public/contracts'),
  networks: {
    rinkeby: {
      provider: function() {
        var provider = new HDWalletProvider(
          mnemonic,
          "https://rinkeby.infura.io/" + accessToken
        );
        console.log(provider.addresses)
        return provider;
      },
      network_id: '*',
      gas: 6000000
    },
    development: {
      host: '127.0.0.1',
      port: 8545,
      network_id: 1533140371286,
      gas: 6000000
    }
  }
};
