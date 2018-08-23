(ns cljs-dapp.core
  (:require [integrant.core :as ig]
            [cljs-dapp.config :as config]))

(defn- dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defonce system (atom nil))

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
