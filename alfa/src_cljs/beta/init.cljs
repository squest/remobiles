(ns beta.init
  "Mainly functions to initialize the app"
  (:require
   [reagent.core :as re :refer [atom render-component]]
   [common.util :as util :refer [selid animate]]
   [reagent.cursor :as rec :refer [cur]]
   [common.config :refer [config]]
   [beta.comp :as bcomp :refer [main-page]]
   [common.stm :as stm :refer [local-database]]
   [beta.navigation :as navi :refer [set-page!]]))

(defn mount-app
  "If there exist a current user, then we can just mount the app"
  [app]
  ;; What to do when we mount the app
  ;; Basically we render the main page for the app
  ;; Setup the current page
  ;; and make them available for clicks
  (let [available-drills (:available-drills @app)]
    (.setTimeout js/window
                 (fn []
                   (do (stm/init-drills! app available-drills)
                       (set-page! app :drills)
                       (render-component [main-page app]
                                         (selid "main"))))
                 500)))

(defn init-app
  "Initialize the app with data"
  [app]
  (let [dbase (:database @app)
        available-drills (:available-drills @app)]
    ;; Mount the drills data from database unto the app-state
    (do (stm/init-drills! dbase available-drills)
        (stm/set-current-user! app)
        (mount-app app))))

;; This is an ugly way to do it since we complect the component and the app-logic
;; However so far we haven't find a way to separate the view with logic yet
;; TODO Create an architecture that handles callback in a clojurely idiomatic way
(defn comp-login
  "Main login page, to be used for the first time"
  [app]
  (let [user (atom {})
        {:keys [database]} @app
        what-to-do-when-the-user-click-submit-button
        (fn [_]
          (if-let [user-data (stm/get-user database @user)]
            ;; set current user to this user and mount the app
            (do (stm/set-current-user! database user-data)
                (mount-app app))
            ;; add user to database and app-state
            ;; set current user to this user
            ;; and initialize the app
            (do (stm/add-user! database @user)
                (stm/set-current-user! database @user)
                (init-app app))))]
    [:div
     [:div {:class "bar bar-header bar-positive"}
      [:h3 {:class "title"}
       "Zenius Vocab App"]]
     (repeat 3 [:br])
     [:div {:class "row"}
      [:div {:class "col col-25"}]
      [:h3 {:class "col col-50"} "Please login"]]
     [:div {:class "content"}
      [:br]
      [:div {:class "list list-inset"}
       [:label {:class "item item-input"}
        [:input {:type        "text"
                 :placeholder "username di zeniusnet"
                 :on-change   (fn [e] (swap! user assoc :username
                                            (.-value (.-target e))))}]]
       [:label {:class "item item-input"}
        [:input {:type        "password"
                 :placeholder "password di zeniusnet"
                 :on-change   (fn [e] (swap! user assoc :password
                                            (.-value (.-target e))))}]]]]
     (repeat 2 [:br])
     [:div {:class "row"}
      [:div {:class "col col-25"}]
      [:button {:class        "col col-50 button button-outline button-positive"
                :on-click     what-to-do-when-the-user-click-submit-button
                :on-touch-end what-to-do-when-the-user-click-submit-button}
       "Login"]]]))

(defn launch-login
  "Launch the login page, and determine whether to init app or simply
  mount the app based on user existence in the database" [app]
  (render-component [comp-login app]
                    (selid "main")))

(defn start
  "Start the application, no in-memory data yet, thus an empty state,
	but we can check whether data exist in localstorage, if data
	exist in database then we need to mount them into app-state."
  [app conf which-profile?]
  (do ;; Initialize the database
    (->> (which-profile? conf)
         (:database)
         (local-database)
         (swap! app assoc :database))
    ;; Initialize available drills from conf
    (stm/set-available-drills! app conf)
    ;; Check whether there exist a current user in database, if exist
    ;; then just mount the app Otherwise we need to initialize the app
    ;; by generating necessary data into database
    (let [dbase (:database @app)
          current-user (stm/get-current-user dbase)]
      (if current-user
        (init-app app)
        (launch-login app)))))

(def app-state (atom {}))

(start app-state config :development)











