(ns cljs-dapp.utils
  (:require [re-frame.registrar :refer [kinds get-handler clear-handlers]]))

(defn clear-namespace [ns]
  (letfn [(clear [kind]
            (->> (get-handler kind)
                 keys
                 (filter #(= (namespace %) ns))
                 (map #(clear-handlers kind %))
                 doall))]
    (doall
     (for [kind kinds]
       (clear kind)))))
