(ns alfa.quiz
  (:require [reagent.core :as re :refer [render-component atom]]
            [alfa.util :refer [selid animate]]
            [alfa.db :as db]))

(def drill-set
  (atom {}))

(def current-question
  (atom {}))

(defn pick-question
  [lst]
  (loop [l lst res []]
    (if (= 5 (count res))
      res
      (let [rnd (rand-int (count l))]
        (recur (remove #{(nth l rnd)} l)
               (conj res (nth l rnd)))))))

(defn comp-quiz
  []
  [:div 
   (repeat 2 [:br])
   [:div {:class "card"}
    [:h4 {:class "col col-offset-5"} (:question @current-question)]]
   [:ul {:class "list list-inset"}
    (map #(vector :li {:class "item"} %)
         (:choices @current-question))]
   [:div {:class "row"}
    [:div {:class "col col-20"}]
    [:button {:class "col col-50 button button-positive"}
     "Submit"]]])

(defn start-question
  []
  (let [choices (pick-question (:list @drill-set))
        answer (rand-int 5)]
    (do (reset! current-question
                {:question (:def (nth choices answer))
                 :answer (:word (nth choices answer))
                 :choices (map :word choices)})
        (render-component [comp-quiz]
                          (selid "container"))
        (animate "container"))))

(defn quiz-init
  [idx]
  (let [drills (db/get-item (str "word-list-"
                                 (cond (= idx 1) "one"
                                       (= idx 2) "two"
                                       (= idx 3) "three")))]
    (do (reset! drill-set
                drills)
        (start-question))))






