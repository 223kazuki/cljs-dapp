(ns duct-ethereum-dapp.client.events
  (:require [re-frame.core :as re-frame]
            [duct-ethereum-dapp.client.db :as db]
            [day8.re-frame.http-fx]
            [district0x.re-frame.web3-fx]
            [ajax.core :as ajax]
            [ajax.protocols :as protocol]
            [cljsjs.web3]
            [cljs-web3.core :as web3]
            [cljs-web3.eth :as web3-eth]
            [goog.string :as gstring]
            [goog.string.format]))

(re-frame/reg-event-fx
 ::initialize-db
 (fn [_ _]
   {:db db/default-db
    :http-xhrio {:method :get
                 :uri (gstring/format "./build/contracts/%s.json"
                                      (get-in db/default-db [:contract :name]))
                 :timeout 6000
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [::abi-loaded]
                 :on-failure [::api-failure]}}))

(re-frame/reg-event-db
 ::abi-loaded
 (fn  [db [_ {:keys [abi networks]}]]
   (let [web3 (:web3 db)
         address (get-in networks [:3 :address])
         instance (web3-eth/contract-at web3 abi address)]
     (re-frame/dispatch [::load-number])
     (re-frame/dispatch [::watch-update])
     (-> db
         (assoc-in [:contract :abi] abi)
         (assoc-in [:contract :address] address)
         (assoc-in [:contract :instance] instance)))))

(re-frame/reg-event-fx
 ::load-number
 (fn  [{:keys [db]} [_ _]]
   {:web3/call {:web3 (:web3 db)
                :fns [{:instance (get-in db [:contract :instance])
                       :fn :get
                       :on-success [::get-success]
                       :on-error [::api-failure]}]}}))

(re-frame/reg-event-db
 ::get-success
 (fn  [db [_ result]]
   (assoc db :number (first (aget result "c")))))

(re-frame/reg-event-fx
 ::increment
 (fn  [{:keys [db]} [_ _]]
   {:web3/call {:web3 (:web3 db)
                :fns [{:instance (get-in db [:contract :instance])
                       :fn :set
                       :args [(inc (:number db))]
                       :tx-opts {:gas 4500000}
                       :on-tx-success [::load-number]
                       :on-tx-error [::api-failure]}]}}))

(re-frame/reg-event-fx
 ::watch-update
 (fn [{:keys [db]} [_ _]]
   {:web3/watch-events {:events [{:id :update
                                  :event :Update
                                  :instance (get-in db [:contract :instance])
                                  :block-filter-opts {:from-block 0 :to-block "latest"}
                                  :on-success [::load-number]
                                  :on-error [::api-failure]}]}}))

(re-frame/reg-event-db
 ::set-active-panel
 (fn  [db [_ active-panel]]
   (-> db
       (assoc :active-panel active-panel))))

(re-frame/reg-event-db
 ::api-failure
 (fn [db [_ result]]
   (if (== (:status result) 401)
     (aset js/window.location "href" "/login")
     (assoc db :loading? false))))
