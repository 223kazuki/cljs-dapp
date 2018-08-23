(ns cljs-dapp.config
  (:require [integrant.core :as ig]
            [cljs-dapp.app :as app]
            [cljs-dapp.routes :as routes]
            [cljs-dapp.web3 :as web3])
  (:require-macros [cljs-dapp.macro :refer [slurp]]))

(def debug? ^boolean goog.DEBUG)

(defn- get-contract-json []
  (-> (slurp "resources/public/contracts/Simplestorage.json")
      (js/JSON.parse)
      (js->clj :keywordize-keys true)))

(def system-conf
  {::routes/module
   {:routes ["/" {""       :home
                  "about"  :about}]}

   ::web3/module
   {:network-id (if debug? 1533140371286 4)
    :contract-json (get-contract-json)
    :dev debug?}

   ::app/module
   {:routes (ig/ref ::routes/module)
    :web3 (ig/ref ::web3/module)}})
