(ns alfa.react
  (:require [ajax.core :as http]
            [cljs.reader :refer [read-string]]
            [jayq.core :as jayq]
            [reagent.core :as re :refer [render-component atom]]
            [alfa.reconfig :refer [conf]]))


(defn selid
  "Document getElementById shortened"
  [id]
  (.getElementById js/document id))

(def anim-duration 1200)

(defn animate
  "Animate the content when popped up, to be applied when
  performing any transition, elmt is the id of a certain html
  element"
  [elmt]
  (.fadeIn js/Jacked (selid elmt)
           (clj->js {:duration anim-duration})))

(.initializeTouchEvents js/React true)

(defn comp-main-header
  "The main header for the app"
  [title]
  [TopUI.NavBar {:id "main-header"}
   [TopUI.NavBarItem {:center nil :half nil}
    title]])

(defn comp-menu-item
  "A component for each item in the menu"
  [item-map label]
  [TopUI.ListItem item-map label])

(defn nekat
  [nama]
  [TopUI.NavBar
   [TopUI.NavBarItem {:center :half} nama]])

(defn comp-menu-list
  "A list of menu items"
  [menus]
  (map #(let [fns (fn [e]
                    (do (render-component [nekat %]
                                          (selid "main"))
                        (animate "main")))]
         (comp-menu-item
           {:on-click     fns
            :on-touch-end fns
            :id           %}
           %))
       menus))

(defn main-page
  "The main page, consists of lists of menus"
  []
  [:div
   [comp-main-header "Hello world"]
   [TopUI.List {:id "main-navigation"}
    [TopUI.ListHeader "First menu"]
    [TopUI.ListContainer
     (comp-menu-list ["First" "Second" "Wharever"])]]])


(def word-list (atom []))

(defn comp-word-list
  []
  [TopUI.List
   [TopUI.ListHeader
    "Word list of"]
   (map #(vector TopUI.ListItem
                 (:word %))
        @word-list)])

(defn start
  []
  (do (http/ajax-request
        {:uri     "data/one.edn"
         :method  :get
         :handler (fn [[stat data]]
                    (do (reset! word-list
                                (:list data))
                        (js/alert @word-list)))
         :response-format (http/edn-response-format)})
      (render-component [main-page]
                        (selid "main"))
      (render-component [comp-word-list]
                        (selid "main-stuff"))))

(start)












































