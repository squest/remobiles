(ns beta.comp
  (:require
    [reagent.core :refer [atom render-component]]
    [reagent.cursor :refer [cur]]
    [common.stm :as stm]
    [common.util :refer [selid animate]]
    [beta.navigation :refer [set-page!]]
    [beta.quiz :as quiz]))

(defn page-drills
  [app]
  (let [drills (cur app [:drills])
        what-to-do-when-user-click-item
        (fn [e]
          (let [code (.-id (.-target e))]
            (do (stm/set-current-drill! app code)
                (set-page! app :quiz))))]
    [:div
     [:ul {:class "list"}
      (map #(vector :li
                    {:class "item"
                     :id (:code %)
                     :on-click what-to-do-when-user-click-item
                     :on-touch-end what-to-do-when-user-click-item}
                    (:name %))
           @drills)]]))

(defn page-account
  [app]
  (let [user (stm/get-current-user app)]
    [:div
     [:div {:class "app-content"}
      [:h3 "This is the account"]
      [:br]
      [:p (str "Existing user : "
               (:username user))]]]))

(defn page-zenius
  [app]
  [:div {:class "app-content"}
   [:h3 "This is the zenius"]])

(defn page-quiz
  [app]
  (let [current-drill
        (:list (stm/get-current-drill app))]
    [:div {:class "app-content"}
     [:h3 "This is the drill quiz"]
     [:ul {:class "list"}
      (map #(vector :li {:class "item"}
                    (:word %))
           (:list (stm/get-current-drill app)))]]))

(defn page-flash-card
  [app]
  [:div
   [:h1 "This is the drill flash"]])

(defn main-page
  "This is the main-page component, consisting of header, page-container, and a footer"
  [app]
  (let [current-page (cur app [:current-page])
        ;; the tab-class is the state of class for footer
        tab-class (atom {:drills "tab-item active"
                         :account "tab-item"
                         :zenius "tab-item"})
        ;; These tab-clicks functions are used to manage the state of the click
        ;; and determine whether the icon would be striped
        tab-drill-click
        (fn [e]
          (do (set-page! app :drills)
              (reset! tab-class
                      {:drills "tab-item active"
                       :account "tab-item"
                       :zenius "tab-item"})
              (js/console.log (:drills @app))))
        tab-account-click
        (fn [e]
          (do (set-page! app :account)
              (reset! tab-class
                      {:drills "tab-item"
                       :account "tab-item active"
                       :zenius "tab-item"})))
        tab-zenius-click
        (fn [e]
          (do (set-page! app :zenius)
              (reset! tab-class
                      {:drills "tab-item"
                       :account "tab-item"
                       :zenius "tab-item active"})))]
    (fn []
      [:div
       [:div {:class "tabs-striped tabs-top"}
        [:div {:class "bar bar-header bar-positive app-header"}
         [:h3 {:class "title"}
          "Zenius Vocab App"]]]
       [:div {:class "app-page"}
        (cond (= @current-page :drills)
              (page-drills app)
              (= @current-page :account)
              (page-account app)
              (= @current-page :zenius)
              (page-zenius app)
              (= @current-page :quiz)
              (page-quiz app)
              (= @current-page :flash-card)
              (page-flash-card app)
              :else [:div (repeat 3 [:br])
                     [:h1 "Kagak ada apa apaan"]])]
       [:div {:class "tabs-striped tabs-color-positive"}
        [:div {:class "tabs app-footer"}
         [:a {:class        (:drills @tab-class)
              :on-touch-end tab-drill-click
              :on-click     tab-drill-click}
          [:i {:class "icon ion-home"}]
          "Drills"]
         [:a {:class        (:account @tab-class)
              :on-touch-end tab-account-click
              :on-click     tab-account-click}
          [:i {:class "icon ion-star"}]
          "Account"]
         [:a {:class        (:zenius @tab-class)
              :on-touch-end tab-zenius-click
              :on-click     tab-zenius-click}
          [:i {:class "icon ion-gear-a"}]
          "Zenius"]]]])))







