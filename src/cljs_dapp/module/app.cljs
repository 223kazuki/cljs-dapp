(ns cljs-dapp.module.app
  (:require [integrant.core :as ig]
            [reagent.core :as reagent]
            [cljs-dapp.views :as views]))

(defmethod ig/init-key :cljs-dapp.module/app
  [_ {:keys [mount-point-id]}]
  (js/console.log (str "Initializing " (pr-str ::module)))
  (let [container (.getElementById js/document mount-point-id)]
    (reagent/render [views/app-container] container)
    container))

(defmethod ig/halt-key! :cljs-dapp.module/app
  [_ container]
  (js/console.log (str "Halting  " (pr-str ::module)))
  (reagent/unmount-component-at-node container))
