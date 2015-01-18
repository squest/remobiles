(ns beta.init
  (:require
    [reagent.core :refer [atom render-component]]
    [common.util :refer [selid animate]]
    [common.config :refer [config]]
    [common.state :refer [app-state]]
    [common.stm :as stm :refer [make-app-state make-local-storage]]))

(defn mount-app
  "If there exist a current user, then we can just mount the app"
  [app]
  ;; TODO Rally the troops, mount the application
  )

(defn init-app
  "Initialize the app with data"
  [app]
  (let [dbase (:database app)
        available-drills (:available-drills app)]
    (do (stm/init-drills! dbase available-drills)
        (stm/init-drills! app available-drills)
        (mount-app app))))

(defn comp-login
  "Main login page, to be used for the first time"
  [app]
  (let [user (atom {})
        {:keys [database]} app
        what-to-do-when-the-user-click-submit-button
        (fn [e]
          (if (stm/user-exists? database @user)
            ;; set current user to this user and mount the app
            (do (stm/set-current-user! app @user)
                (stm/set-current-user! database @user)
                (mount-app app))
            ;; add user to database and app-state
            ;; set current user to this user
            ;; and initialize the app
            (do (stm/add-user! app @user)
                (stm/add-user! database @user)
                (stm/set-current-user! app @user)
                (stm/set-current-user! database @user)
                (init-app app))))]
    [:div
     (repeat 3 [:br])
     [:div {:class "row"}
      [:div {:class "col col-25"}]
      [:h3 {:class "col col-50"} "Please login : "]]
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
  "Launch the login page, and determine whether to init app or simply mount the app based on user existence in the
  database"
  [app]
  (render-component [comp-login app]
                    (selid "main")))

(defn start
  "Start the application, no in-memory data yet, thus an empty state, but we can check whether data exist in
	localstorage, if data exist in database then we need to mount them into app-state."
  [app conf which-profile?]
  ;; Initialize the database
  (do (->> (:database (which-profile? conf))
           (make-local-storage)
           (swap! app assoc :database))
      ;; Initialize available drills from conf
      (swap! app assoc :available-drills
             (:available-drills conf))
      ;; Check whether there exist a current user in database, if exist then just mount the app
      ;; Otherwise we need to initialize the app by generating necessary data into database
      (let [dbase (:database @app)
            current-user (stm/get-current-user dbase)]
        (if current-user
          (mount-app app)
          (launch-login app)))))

(start app-state config :development)











