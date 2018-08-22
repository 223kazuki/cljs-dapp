(ns duct-ethereum-dapp.routes
  (:require [bidi.bidi :as bidi]
            [pushy.core :as pushy]
            [re-frame.core :as re-frame]
            [duct-ethereum-dapp.events :as events]))

(def routes ["/" {""       :home
                  "about"  :about}])

(defn- parse-url [url]
  (bidi/match-route routes url))

(defn- dispatch-route [matched-route]
  (let [panel-name (keyword (str (name (:handler matched-route)) "-panel"))]
    (re-frame/dispatch [::events/set-active-panel panel-name])))

(def history (pushy/pushy dispatch-route parse-url))

(defn app-routes []
  (pushy/start! history))

(def url-for (partial bidi/path-for routes))

(defn go-to-page [route]
  (pushy/set-token! history (url-for route)))
