(ns duct-ethereum-dapp.client.db)

(def default-db
  {:active-panel :none
   :loading? true
   :number nil
   :web3 (aget js/window "web3")
   :provides-web3? (aget js/window "web3")
   :contract {:name "SimpleStorage"
              :abi nil
              :instance nil
              :address nil}})
