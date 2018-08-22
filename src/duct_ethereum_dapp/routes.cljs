(ns duct-ethereum-dapp.routes
  (:require [integrant.core :as ig]
            [re-frame.core :as re-frame]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [bidi.bidi :as bidi]
            [pushy.core :as pushy]
            [duct-ethereum-dapp.utils :refer [clear-namespace]]))

(defn load-subs []
  (re-frame/reg-sub
   ::active-panel
   (fn [db]
     (::active-panel db))))

(defn load-events []
  (re-frame/reg-event-db
   ::init
   (fn-traced [db _]
              (assoc db ::active-panel :none)))

  (re-frame/reg-event-db
   ::halt
   (fn-traced [db _] db))

  (re-frame/reg-event-db
   ::set-active-panel
   (fn-traced [db [_ panel-name]]
              (assoc db ::active-panel panel-name))))

(defn app-routes [routes]
  (letfn [(dispatch-route [{:keys [:handler :route-params]}]
            (let [panel-name (keyword (str (name handler) "-panel"))]
              (re-frame/dispatch [::set-active-panel panel-name])))
          (parse-url [url]
            (when (empty? url)
              (set! js/window.location (str js/location.pathname "#/")))
            (let [url (-> url
                          (clojure.string/split #"&")
                          (first))]
              (bidi/match-route routes url)))]
    (let [history (pushy/pushy dispatch-route parse-url)]
      (.setUseFragment (aget history "history") true)
      (pushy/start! history)
      {:history history :routes routes})))

(defn go-to-page [{:keys [history routes]} route]
  (pushy/set-token! history (bidi/path-for routes route)))

(defmethod ig/init-key ::module
  [_ {:keys [routes] :as opts}]
  (load-subs)
  (load-events)
  (re-frame/dispatch-sync [::init])
  (app-routes routes))

(defmethod ig/halt-key! ::module
  [_ {:keys [history] :as a}]
  (re-frame/dispatch-sync [::halt])
  (clear-namespace (namespace ::module))
  (pushy/stop! history))
