(ns cljs-dapp.utils
  (:refer-clojure :exclude [slurp])
  (:require [re-frame.core :as re-frame]
            [re-frame.registrar :refer [kinds get-handler clear-handlers]]))

(defmacro slurp [file]
  #?(:clj (clojure.core/slurp file)))

(defn clear-re-frame-handlers [ns]
  (letfn [(clear [kind]
            (->> (get-handler kind)
                 keys
                 (filter #(= (namespace %) ns))
                 (map #(clear-handlers kind %))
                 doall))]
    (doall
     (for [kind kinds]
       (clear kind)))))

(defn clear-re-frame-db [db ns]
  (->> db
       (filter #(not= (namespace (key %)) ns))
       (into {})))

(def interceptors [re-frame/trim-v])

(defn reg-event-fxs [event-map]
  (->> event-map
       (map (fn [[id event]]
              (re-frame/reg-event-fx id interceptors event)))
       doall))

(defn reg-subs [sub-map]
  (->> sub-map
       (map (fn [[id sub]]
              (re-frame/reg-sub id sub)))
       doall))
