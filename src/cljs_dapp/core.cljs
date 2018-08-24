(ns cljs-dapp.core
  (:require [integrant.core :as ig]
            [cljs-dapp.module.app]
            [cljs-dapp.module.router]
            [cljs-dapp.module.web3])
  (:require-macros [cljs-dapp.utils :refer [read-config]]))

(def debug? ^boolean goog.DEBUG)

(defn- dev-setup []
  (when debug?
    (enable-console-print!)
    (println "dev mode")))

(defonce system (atom nil))

(defn start []
  (reset! system (ig/init (read-config "resources/config.edn"))))

(defn stop []
  (ig/halt! @system)
  (reset! system nil))

(defn reset []
  (stop)
  (start))

(defn ^export init []
  (dev-setup)
  (start))
