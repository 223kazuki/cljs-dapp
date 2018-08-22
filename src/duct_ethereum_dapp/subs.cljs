(ns duct-ethereum-dapp.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::active-panel
 (fn [db]
   (:active-panel db)))

(re-frame/reg-sub
 ::number
 (fn [db]
   (:number db)))
