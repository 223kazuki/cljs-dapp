(ns cljs-dapp.module.web3
  (:require [integrant.core :as ig]
            [re-frame.core :as re-frame]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [district0x.re-frame.web3-fx]
            [cljs-web3.eth :as web3-eth]
            [cljs-dapp.utils :refer [reg-event-fxs reg-subs
                                     clear-re-frame-handlers clear-re-frame-db]]))

(defn- load-subs []
  (reg-subs
   {::web3-instance
    (fn [db] (::web3-instance db))

    ::my-address
    (fn [db] (::my-address db))

    ::data
    (fn [db] (::data db))}))

(defn- load-events []
  (reg-event-fxs
   {::init
    (fn-traced [{:keys [db]} [web3]]
               {:db (merge db web3)})

    ::halt
    (fn-traced [{:keys [db]} _]
               {:db (clear-re-frame-db db (namespace ::module))
                :web3/stop-watching-all {}})

    ::get-data
    (fn-traced [{:keys [db]} _]
               {:web3/call {:web3 (::web3-instance db)
                            :fns [{:instance (::contract-instance db)
                                   :fn :get
                                   :on-success [::get-data-success]
                                   :on-error [::api-failure]}]}})

    ::get-data-success
    (fn-traced [{:keys [db]} [result]]
               {:db (assoc db ::data (js-invoke result "toNumber"))})

    ::set-data
    (fn-traced [{:keys [db]} [data]]
               {:db (assoc db ::loading? true)
                :web3/call {:web3 (::web3-instance db)
                            :fns [{:instance (::contract-instance db)
                                   :fn :set
                                   :args [data]
                                   :tx-opts {:gas 4500000}
                                   :on-tx-success [::get-data]
                                   :on-tx-error [::api-failure]}]}})

    ::watch-updated
    (fn-traced [{:keys [db]} [_ _]]
               {:web3/watch-events {:events [{:id :watch-updated
                                              :event :Updated
                                              :instance (::contract-instance db)
                                              :block-filter-opts {:from-block 0
                                                                  :to-block "latest"}
                                              :on-success [::get-data]
                                              :on-error [::api-failure]}]}})

    ::api-failure
    (fn-traced [{:keys [db]} [result]]
               {:db (assoc db ::loading? false)})}))

(defmethod ig/init-key ::module
  [_ {:keys [network-id contract dev]}]
  (js/console.log (str "Initializing " (pr-str ::module)))
  (load-subs)
  (load-events)
  (when-let [web3-instance (aget js/window "web3")]
    (let [{:keys [:abi :networks]} contract
          network-id-key (keyword (str network-id))
          address (-> networks network-id-key :address)
          web3 {::web3-instance web3-instance
                ::contract-instance (web3-eth/contract-at web3-instance abi address)
                ::my-address (aget web3-instance "eth" "defaultAccount")}]
      (re-frame/dispatch-sync [::init web3])
      (re-frame/dispatch-sync [::watch-updated]))))

(defmethod ig/halt-key! ::module
  [_ _]
  (js/console.log (str "Halting " (pr-str ::module)))
  (re-frame/dispatch-sync [::halt])
  (clear-re-frame-handlers (namespace ::module)))
