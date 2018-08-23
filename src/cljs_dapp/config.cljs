(ns cljs-dapp.config
  (:require [integrant.core :as ig]
            [cljs-dapp.module.app :as app]
            [cljs-dapp.module.routes :as routes]
            [cljs-dapp.module.web3 :as web3])
  (:require-macros [cljs-dapp.utils :refer [slurp]]))

(def debug? ^boolean goog.DEBUG)

(def system-conf
  {::routes/module
   ["/" {""       :home
         "about"  :about}]

   ::web3/module
   {:network-id (if debug? 1533140371286 4)
    :contract (-> (slurp "resources/public/contracts/Simplestorage.json")
                  (js/JSON.parse)
                  (js->clj :keywordize-keys true))}

   ::app/module
   {:routes (ig/ref ::routes/module)
    :web3 (ig/ref ::web3/module)}})
