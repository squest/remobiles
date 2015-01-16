(ns alfa.init
  (:require [alfa.db :as db]
            [alfa.user :as user]
            [ajax.core :refer [ajax-request edn-response-format edn-request-format]]
            [reagent.core :refer [render-component atom]]
            [alfa.util :refer [selid animate]]
            [alfa.dashboard :refer [main-page]]))

(defn header
  "Main header"
  [title]
  [:div {:class "bar bar-header bar-positive"}
   [:h3 {:class "title"}
    title]])

(defn init-app
  [usermap]
  (let [{:keys [username password]} usermap]
    (do (user/save! username password)
        (doseq [item ["one" "two" "three"]]
          (ajax-request
           {:uri (str "data/" item ".edn")
            :method :get
            :response-format (edn-response-format)
            :handler
            (fn [[_ data]]
              (db/set-item! (str "word-list-" item) data))}))
        (main-page))))

(defn launch-app
  "Launch the app when the user already login"
  []
  (do (render-component [header "Zenius Vocab App"]
                        (selid "header"))
      (main-page)))

(defn comp-login
  "Main login page, to be used for the first time"
  []
  (let [user (atom {})]
    [:div
     (repeat 3 [:br])
     [:div {:class "row"}
      [:div {:class "col col-25"}]
      [:h3 {:class "col col-50"} "Please login : "]] 
     [:div {:class "content"}
      [:br]
      [:div {:class "list list-inset"}
       [:label {:class "item item-input"}
        [:input {:type "text"
                 :placeholder "username di zeniusnet"
                 :on-change (fn [e] (swap! user assoc :username
                                          (.-value (.-target e))))}]]
       [:label {:class "item item-input"}
        [:input {:type "password"
                 :placeholder "password di zeniusnet"
                 :on-change (fn [e] (swap! user assoc :password
                                          (.-value (.-target e))))}]]]]
     (repeat 2 [:br])
     [:div {:class "row"}
      [:div {:class "col col-25"}]
      [:button {:class "col col-50 button button-outline button-positive"
                :on-click (fn []
                            (init-app @user))
                :on-touch-end (fn []
                                (init-app @user))}
       "Login"]]]))

(defn start
  "Start the app"
  []
  (do (.initializeTouchEvents js/React true)
      (if (user/exists?)
        (launch-app) 
        (do (render-component [header "Zenius Vocab App"]
                              (selid "header"))
            (render-component [comp-login]
                              (selid "container"))
            (animate "menu-container")))))

(start)



