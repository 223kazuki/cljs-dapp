(ns duct-ethereum-dapp.web3
  (:require [integrant.core :as ig]
            [re-frame.core :as re-frame]
            [day8.re-frame.http-fx]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [district0x.re-frame.web3-fx]
            [ajax.core :as ajax]
            [ajax.protocols :as protocol]
            [cljs-web3.core :as web3]
            [cljs-web3.eth :as web3-eth]
            [goog.string :as gstring]
            [goog.string.format]
            [duct-ethereum-dapp.utils :refer [clear-namespace]]))

(defn load-subs []
  (re-frame/reg-sub
   ::number
   (fn [db] (::number db))))

(defn load-events []
  (re-frame/reg-event-db
   ::init
   (fn-traced [db [_ {:keys [network-id contract-json dev]}]]
              (let [{:keys [:abi :networks] :as contract} contract-json
                    network-id-key (keyword (str network-id))
                    address (-> networks network-id-key :address)
                    web3 {::network-id network-id
                          ::contract contract
                          ::contract-address address}
                    web3 (if-let [web3-instance (aget js/window "web3")]
                           (assoc web3
                                  ::web3-instance web3-instance
                                  ::contract-instance (web3-eth/contract-at web3-instance abi address)
                                  ::my-address (aget web3-instance "eth" "defaultAccount"))
                           web3)]
                (merge db web3))))

  (re-frame/reg-event-db
   ::halt
   (fn-traced [db _] db))

  (re-frame/reg-event-fx
   ::load-number
   (fn-traced [{:keys [db]} _]
              {:web3/call {:web3 (::web3-instance db)
                           :fns [{:instance (::contract-instance db)
                                  :fn :get
                                  :on-success [::load-number-success]
                                  :on-error [::api-failure]}]}}))

  (re-frame/reg-event-db
   ::load-number-success
   (fn-traced [db [_ result]]
              (assoc db ::number (first (aget result "c")))))

  (re-frame/reg-event-fx
   ::increment
   (fn-traced [{:keys [db]} [_ _]]
              {:web3/call {:web3 (::web3-instance db)
                           :fns [{:instance (::contract-instance db)
                                  :fn :set
                                  :args [(inc (::number db))]
                                  :tx-opts {:gas 4500000}
                                  :on-tx-success [::load-number]
                                  :on-tx-error [::api-failure]}]}}))

  (re-frame/reg-event-fx
   ::watch-update
   (fn-traced [{:keys [db]} [_ _]]
              {:web3/watch-events {:events [{:id :update
                                             :event :Update
                                             :instance (::contract-instance db)
                                             :block-filter-opts {:from-block 0 :to-block "latest"}
                                             :on-success [::load-number]
                                             :on-error [::api-failure]}]}}))

  (re-frame/reg-event-db
   ::api-failure
   (fn-traced [db [_ result]]
              (if (== (:status result) 401)
                (aset js/window.location "href" "/login")
                (assoc db ::loading? false)))))

(defmethod ig/init-key ::module
  [_ {:keys [network-id contract-json dev]}]
  (load-subs)
  (load-events)
  (re-frame/dispatch-sync [::init {:network-id network-id
                                   :contract-json contract-json
                                   :dev dev}]))

(defmethod ig/halt-key! ::module
  [_ {:keys []}]
  (re-frame/dispatch-sync [::halt])
  (clear-namespace (namespace ::module)))
