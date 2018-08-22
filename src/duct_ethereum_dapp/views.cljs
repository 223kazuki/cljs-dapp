(ns duct-ethereum-dapp.views
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [cljsjs.semantic-ui-react]
            [cljsjs.react-transition-group]
            [soda-ash.core :as sa]
            [duct-ethereum-dapp.routes :as routes]
            [duct-ethereum-dapp.web3 :as web3]))

(defn home-panel []
  (let [number (re-frame/subscribe [::web3/number])]
    (reagent/create-class
     {:component-will-mount
      #(re-frame/dispatch [::web3/load-number])

      :reagent-render
      (fn []
        [:div
         [:div @number]
         [sa/Button {:primary true
                     :on-click #(re-frame/dispatch [::web3/increment])}
          "Increment"]])})))

(defn about-panel []
  [:div "About"])

(defn none-panel []
  [:div])

(defmulti panels identity)
(defmethod panels :home-panel [] #'home-panel)
(defmethod panels :about-panel [] #'about-panel)
(defmethod panels :none [] #'none-panel)

(def transition-group
  (reagent/adapt-react-class js/ReactTransitionGroup.TransitionGroup))
(def css-transition
  (reagent/adapt-react-class js/ReactTransitionGroup.CSSTransition))

(defn main-panel []
  (let [active-panel (re-frame/subscribe [::routes/active-panel])]
    (fn []
      [:div
       [sa/Menu {:fixed "top" :inverted true}
        [sa/Container
         [sa/MenuItem {:as "a" :header true  :href "/"}
          "SimpleStorage"]
         [sa/MenuItem {:as "a" :href "/about"} "About"]]]
       [sa/Container {:className "mainContainer" :style {:marginTop "7em"}}
        (let [panel @active-panel]
          [transition-group
           [css-transition {:key panel
                            :classNames "pageChange"
                            :timeout 500
                            :className "transition"}
            [(panels panel)]]])]])))
