(ns cljs-dapp.views
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [cljsjs.semantic-ui-react]
            [cljsjs.react-transition-group]
            [soda-ash.core :as sa]
            [cljs-dapp.module.routes :as routes]
            [cljs-dapp.module.web3 :as web3]))

(defn data-form [{:keys [:configs :handlers]}]
  (let [{:keys [:initial-data]} configs
        {:keys [:update-handler]} handlers
        form (reagent/atom {:data initial-data})
        input-handler
        (fn [el]
          (let [n (aget el "target" "name")
                v (aget el "target" "value")]
            (swap! form assoc (keyword n) v)))]
    (fn []
      [:div
       [sa/Segment {:style {:width "300px"}}
        [sa/Form
         [sa/FormField
          [:label "Data"]
          [:input {:name "data" :type "number"
                   :value (:data @form) :on-change input-handler}]
          [sa/Divider {:hidden true}]
          [sa/Button {:on-click #(update-handler (js/parseInt (:data @form)))
                      :disabled (or (empty? (str (:data @form)))
                                    (neg? (:data @form)))}
           "Update"]]]]])))

(defn home-panel []
  (let [my-address (re-frame/subscribe [::web3/my-address])
        data (re-frame/subscribe [::web3/data])]
    (reagent/create-class
     {:component-will-mount
      #(re-frame/dispatch [::web3/get-data])

      :reagent-render
      (fn []
        [:div
         [sa/Segment
          [sa/Table {:celled true}
           [sa/TableBody
            [sa/TableRow
             [sa/TableCell {:style {:width "200px" :background-color "#F9FAFB"}}
              "Your address"]
             [sa/TableCell @my-address]]
            [sa/TableRow
             [sa/TableCell {:style {:background-color "#F9FAFB"}} "Stored data"]
             [sa/TableCell @data]]]]
          [sa/Divider {:hidden true}]
          (when @data
            [data-form {:configs {:initial-data @data}
                        :handlers {:update-handler
                                   #(re-frame/dispatch [::web3/set-data %])}}])]])})))

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

(defn app-container []
  (let [active-panel (re-frame/subscribe [::routes/active-panel])
        web3-instance (re-frame/subscribe [::web3/web3-instance])]
    (fn []
      [:div
       [sa/Menu {:fixed "top" :inverted true}
        [sa/Container
         [sa/MenuItem {:as "a" :header true  :href "/"} "SimpleStorage"]
         [sa/MenuItem {:as "a" :href "/about"} "About"]]]
       [sa/Container {:className "mainContainer" :style {:margin-top "7em"}}
        (if-not @web3-instance
          [sa/Modal {:open true :size "small"}
           [sa/ModalContent
            "You must connect to "
            [:a {:href "https://metamask.io/"} "Metamask"] "."]]
          (let [panel @active-panel]
            [transition-group
             [css-transition {:key panel
                              :classNames "pageChange" :timeout 500 :className "transition"}
              [(panels panel)]]]))]])))
