(ns duct-ethereum-dapp.client
  (:require [duct-ethereum-dapp.client.core :as core]))

(if-not @core/system
  (core/start)
  (do (core/stop) (core/start)))
