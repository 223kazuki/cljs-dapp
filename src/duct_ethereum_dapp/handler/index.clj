(ns duct-ethereum-dapp.handler.index
  (:require [ataraxy.core :as ataraxy]
            [ataraxy.response :as response]
            [clojure.java.io :as io]
            [integrant.core :as ig]))

(defmethod ig/init-key :duct-ethereum-dapp.handler/index [_ options]
  (fn [{[_] :ataraxy/result}]
    [::response/ok (slurp (io/resource "duct_ethereum_dapp/public/index.html"))]))
