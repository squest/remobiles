(ns beta.quiz
  "Mainly concern with logic part of quiz management"
  (:require
   [common.stm :as stm]
   [reagent.cursor :refer [cur]]
   [common.util :refer [selid animate]]))

(defn generate-soal
  "Generate the quiz from the available current-drill"
  [app]
  (let [lists (cur app [:current-drill])
        drills (:list @lists)
        n (count drills)]
    (loop [res #{}]
      (if (= 5 (count res))
        {:answer (nth (vec res) (rand-int 5))
         :choices (map #(nth drills %) (vec res))}
        (recur (conj res (rand-int n)))))))

(defn check-answer
  "Check user's answer against the soal"
  [app answer soal]
  (let [check (= answer soal)]
    (do (stm/update-user-score! app check)
        {:message
         (if check
           (str "You answered correctly, your score is now "
                (stm/get-user-score app))
           "Waduh salah boy!")
         :answer
         soal})))






