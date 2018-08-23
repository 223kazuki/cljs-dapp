(ns cljs-dapp.core
  (:require [integrant.core :as ig]
            [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [cljs-dapp.config :as config]))

(defonce system (atom nil))

(defn- dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn start []
  (reset! system (ig/init config/system-conf)))

(defn stop []
  (ig/halt! @system)
  (reset! system nil))

(defn reset []
  (stop)
  (start))

(defn ^export init []
  (dev-setup)
  (start))
