(ns duct-ethereum-dapp.views
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [duct-ethereum-dapp.subs :as subs]
            [duct-ethereum-dapp.events :as events]
            [soda-ash.core :as sa]
            [cljsjs.semantic-ui-react]
            [cljsjs.react-transition-group]))

(defn home-panel []
  (let [number (re-frame/subscribe [::subs/number])]
    [:div
     (when-let  [{:keys [type message]} nil]
       (if (= type :failure)
         [sa/Message {:as "h3" :color "red" :error true} message]
         [sa/Message {:as "h3" :color "blue"} message]))
     [:div @number]
     [sa/Button {:primary true
                 :onClick #(re-frame/dispatch [::events/increment])}
      "Increment"]]))

(defn about-panel []
  [:div "About"])

(defn none-panel []
  [:div])

(defmulti panels identity)
(defmethod panels :home-panel [] #'home-panel)
(defmethod panels :about-panel [] #'about-panel)
(defmethod panels :none [] #'none-panel)
(defmethod panels :default [] [:div "This page does not exist."])

(def transition-group
  (reagent/adapt-react-class js/ReactTransitionGroup.TransitionGroup))
(def css-transition
  (reagent/adapt-react-class js/ReactTransitionGroup.CSSTransition))

(defn main-panel []
  (reagent/create-class
   {:reagent-render
    (fn []
      (let [active-panel (re-frame/subscribe [::subs/active-panel])]
        [:div
         [sa/Menu {:fixed "top" :inverted true}
          [sa/Container
           [sa/MenuItem {:as "a" :header true  :href "/"}
            "SimpleStorage"]
           [sa/MenuItem {:as "a" :href "/about"} "About"]]]
         [sa/Container {:className "mainContainer" :style {:marginTop "7em"}}
          [transition-group
           [css-transition {:key @active-panel
                            :classNames "pageChange"
                            :timeout 500
                            :className "transition"}
            [(panels @active-panel)]]]]]))}))
