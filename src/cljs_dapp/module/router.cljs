(ns cljs-dapp.module.router
  (:require [integrant.core :as ig]
            [re-frame.core :as re-frame]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [bidi.bidi :as bidi]
            [pushy.core :as pushy]
            [cljs-dapp.utils :refer [reg-event-fxs reg-subs
                                     clear-re-frame-handlers clear-re-frame-db]]))

(defn- load-subs []
  (reg-subs
   {::active-panel
    (fn [db]
      (::active-panel db))}))

(defn- load-events []
  (reg-event-fxs
   {::init
    (fn-traced [{:keys [:db]} _]
               {:db
                (assoc db ::active-panel :none)})

    ::halt
    (fn-traced [{:keys [:db]} _]
               {:db (clear-re-frame-db db (namespace ::module))})

    ::set-active-panel
    (fn-traced [{:keys [:db]} [panel-name]]
               {:db
                (assoc db ::active-panel panel-name)})}))

(defn- app-routes [routes]
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
      (js-invoke (aget history "history") "setUseFragment" true)
      (pushy/start! history)
      {:history history :routes routes})))

(defn go-to-page [{:keys [history routes]} route]
  (pushy/set-token! history (bidi/path-for routes route)))

(defmethod ig/init-key ::module
  [_ routes]
  (js/console.log (str "Initializing " (pr-str ::module)))
  (load-subs)
  (load-events)
  (re-frame/dispatch-sync [::init])
  (app-routes routes))

(defmethod ig/halt-key! ::module
  [_ {:keys [history]}]
  (js/console.log (str "Halting " (pr-str ::module)))
  (re-frame/dispatch-sync [::halt])
  (clear-re-frame-handlers (namespace ::module))
  (pushy/stop! history))
