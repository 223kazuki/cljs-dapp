(ns cljs-dapp.module.app
  (:require [integrant.core :as ig]
            [reagent.core :as reagent]
            [cljs-dapp.views :as views]))

(defmethod ig/init-key ::module
  [_ _]
  (js/console.log (str "Initializing " (pr-str ::module)))
  (let [container (.getElementById js/document "app")]
    (reagent/render [views/app-container] container)
    container))

(defmethod ig/halt-key! ::module
  [_ container]
  (js/console.log (str "Halting  " (pr-str ::module)))
  (reagent/unmount-component-at-node container))
