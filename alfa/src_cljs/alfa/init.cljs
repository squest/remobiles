(ns alfa.init
	(:require [alfa.db :as db]
						[alfa.user :as user]
						[ajax.core :refer [ajax-request edn-response-format]]
						[reagent.core :refer [render-component atom]]
						[alfa.util :refer [selid animate]]
						[alfa.dashboard :refer [main-page]]))

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
	(do (ajax-request {:uri (str "data/one.edn")
										 :method :get
										 :response-format (edn-response-format)
										 :handler
										 (fn [[_ data]]
											 (js/alert data))})
			(js/alert (db/get-item "word-list-one"))
			(main-page)))

(defn comp-login
	"Main login page, to be used for the first time"
	[]
	(let [user (atom {})]
		[:div
		 [TopUI.NavBar
			[TopUI.NavBarItem {:center :half}
			 "Login"]]
		 [:div {:align "center"}
			[TopUI.TextInput {:placeholder "username"
												:on-change   #(swap! user assoc :username
																						 (.-value (.-target %)))}]
			[:br]
			[:br]
			[TopUI.TextInput {:placeholder "password"
												:type        "password"
												:on-change   #(swap! user assoc :password
																						 (.-value (.-target %)))}]
			[:br]
			[TopUI.Button {:on-click
										 (fn [e]
											 (init-app @user))
										 :on-touch-end
										 (fn [e]
											 (init-app @user))}
			 "Login"]]]))

(defn start
	"Start the app"
	[]
	(do (.initializeTouchEvents js/React true)
			(if (user/exists?)
				(launch-app)
				(do (render-component [comp-login]
															(selid "main"))
						(animate "main")))))

(start)

