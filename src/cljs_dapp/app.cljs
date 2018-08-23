(ns cljs-dapp.app
  (:require [integrant.core :as ig]
            [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [cljs-dapp.views :as views]
            [cljs-dapp.utils :refer [clear-namespace]]))

(defmethod ig/init-key ::module
  [_ {:keys []}]
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defmethod ig/halt-key! ::module
  [_ {:keys []}]
  (clear-namespace (namespace ::module)))
