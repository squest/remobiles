(ns alfa.dashboard
  (:require [reagent.core :as re :refer [render-component atom]]
            [alfa.db :as db]
            [ajax.core :as http]
            [jayq.core :as jayq]
            [alfa.util :refer [selid animate]]))

(def word-list
  "The list of words available in this app"
  (atom []))

(def chosen-menu
  "The state of which of the main-menu chosen by user"
  (atom {:home true
         :account false
         :zenius false}))

(defn drill-set
  "The component for each drill-set menu"
  [drill-name]
  (list [:div {:class "item item-divider"}
         drill-name]
        [:a {:class "item"}
         "Test yourself"]
        [:a {:class "item"}
         "Browse flash-card"]))

(defn menu
  "The menu for all drill-sets available in the app"
  [menus]
  [:div
   (repeat 3 [:br])
   (->> menus
        (map drill-set)
        (concat [:div {:class "list"}])
        vec)])

(defn account
  "Available menu for managing one's zenius account"
  []
  [:div
   (repeat 3 [:br])
   [:h3 "This is the account bar"]])

(defn zenius
  "Fun stuffs, available menu to access zenius tweets and website"
  []
  [:div
   (repeat 3 [:br])
   [:h3 "This is the zenius bar"]])

(defn footer
  "The main footer bar, where the main menu resides"
  []
  [:div {:class "tabs-striped tabs-color-positive tabs-icon-top"}
   [:div {:class "tabs"}
    [:a {:class (if (:home @chosen-menu)
                  "tab-item active"
                  "tab-item")
         :id "home-bar"
         :on-click (fn [e]
                     (do (render-component [menu (map :name @word-list)]
                                           (selid "container"))
                         (reset! chosen-menu
                                 {:home true
                                  :account false
                                  :zenius false})
                         (animate "container")))}
     [:i {:class "icon ion-home"}]
     "Home"]
    [:a {:class (if (:account @chosen-menu)
                  "tab-item active"
                  "tab-item")
         :id "account-bar"
         :on-click (fn [e]
                     (do (render-component [account]
                                           (selid "container"))
                         (reset! chosen-menu
                                 {:home false
                                  :account true
                                  :zenius false})
                         (animate "container")))}
     [:i {:class "icon ion-star"}]
     "Account"]
    [:a {:class (if (:zenius @chosen-menu)
                  "tab-item active"
                  "tab-item")
         :id "zenius-bar"
         :on-click (fn []
                     (do (render-component [zenius]
                                           (selid "container"))
                         (reset! chosen-menu
                                 {:home false
                                  :account false
                                  :zenius true})
                         (animate "container")))}
     [:i {:class "icon ion-gear-a"}]
     "Tweets"]]])

(defn main-page
  "Main page for the app"
  []
  (do (->> ["one" "two" "three"]
           (map #(db/get-item (str "word-list-" %)))
           (reset! word-list))
      (render-component [menu (map :name @word-list)]
                        (selid "container"))
      (render-component [footer]
                        (selid "footer"))
      (animate "container")))










