(ns beta.comp
	(:require
		[reagent.core :refer [atom render-component]]
		[reagent.cursor :refer [cur]]
		[common.stm :as stm]
		[common.util :refer [selid animate]]))

(defn page-drills
	[app]
	[:div (repeat 3 [:br])
	 [:ul {:class "list"}
		(map #(vector :li
									{:class "item"}
									(:name %))
				 (vals (:drills @app)))]])

(defn page-account
	[app]
	[:div (repeat 3 [:br])
	 [:div {:id "anim"}
		[:h1 "This is the account"]]])

(defn page-zenius
	[app]
	[:div [:h1 "This is the zenius"]])

(defn page-quiz
	[app]
	[:div [:h1 "This is the drill quiz"]])

(defn page-flash-card
	[app]
	[:div [:h1 "This is the drill flash"]])

(defn main-page
	[app]
	(let [current-page (cur app [:current-page])
				drills (cur app [:drills])
				tab-class (atom {:drills "tab-item active"
												 :account "tab-item"
												 :zenius "tab-item"})
				tab-drill-click
				(fn [e]
					(do (reset! current-page :drills)
							(reset! tab-class
											{:drills "tab-item active"
											 :account "tab-item"
											 :zenius "tab-item"})
							(animate "main")
							(js/alert (:name (first (:drills @app))))))
				tab-account-click
				(fn [e]
					(do (reset! current-page :account)
							(reset! tab-class
											{:drills "tab-item"
											 :account "tab-item active"
											 :zenius "tab-item"})
							(animate "main")))
				tab-zenius-click
				(fn [e]
					(do (reset! current-page :zenius)
							(reset! tab-class
											{:drills "tab-item"
											 :account "tab-item"
											 :zenius "tab-item active"})
							(animate "main")))]
		(fn []
			[:div
			 [:div {:class "tabs-striped tabs-top"}
				[:div {:class "bar bar-header bar-positive"}
				 [:h3 {:class "title"}
					"Zenius Vocab App"]]]
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
										[:h1 "Kagak ada apa apaan"]])
			 [:div {:class "tabs-striped tabs-color-positive"}
				[:div {:class "tabs"}
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






