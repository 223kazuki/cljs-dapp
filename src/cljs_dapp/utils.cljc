(ns cljs-dapp.utils
  (:refer-clojure :exclude [slurp])
  (:require [re-frame.registrar :refer [kinds get-handler clear-handlers]]))

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
