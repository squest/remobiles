(ns alfa.dashboard
	(:require [reagent.core :as re :refer [render-component atom]]
						[alfa.db :as db]
						[ajax.core :as http]
						[jayq.core :as jayq]
						[alfa.util :refer [selid animate]]))

(defn header
	"Main page header"
	[title]
	[TopUI.NavBar
	 [TopUI.NavBarItem
		{:center :half}
		title]])

(defn menu
	"Main page menu"
	[title menus]
	[TopUI.List
	 [TopUI.ListHeader title]
	 (map #(vector TopUI.ListItem
								 {:on-click (fn [e]
															(set! (.-innerHTML (.-target e))
																		(->> (.-target e)
																				 (.-innerHTML)
																				 (reverse)
																				 (apply str))))
									:on-touch-end (fn [e]
																	(set! (.-innerHTML (.-target e))
																				(->> (.-target e)
																						 (.-innerHTML)
																						 (reverse)
																						 (apply str))))}
								 %)
				menus)])

(def word-list
	(atom []))

(defn comp-main-page
	[]
	[:div
	 [header "Main page"]
	 [menu "Welcome" (map :name @word-list)]])

(defn main-page
	"Main page for the app"
	[]
	(do (->> ["one" "two" "three"]
					 (map #(db/get-item (str "word-list-" %)))
					 (reset! word-list))
			(render-component [comp-main-page]
												(selid "main"))
			(animate "main")))


